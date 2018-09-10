package com.valsong.multithreadspringbatch.configuration;

import com.valsong.multithreadspringbatch.batch.JobCompletionNotificationListener;
import com.valsong.multithreadspringbatch.batch.MyItemProcessor;
import com.valsong.multithreadspringbatch.batch.MyItemWriter;
import com.valsong.multithreadspringbatch.batch.PersonItemProcessor;
import com.valsong.multithreadspringbatch.entity.Person;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {


    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MyItemWriter myItemWriter;


    @Autowired
    private MyItemProcessor myItemProcessor;

    @Autowired
    private JobCompletionNotificationListener jobCompletionNotificationListener;

    @Autowired
    private Step step1;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    JobLauncher jobLauncher;


    // tag::readerwriterprocessor[]
    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer(@Autowired DataSource druidDataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(druidDataSource)
                .build();
    }
    // end::readerwriterprocessor[]

    // tag::jobstep[]

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(@Autowired DataSource druidDataSource) {
        return new DataSourceTransactionManager(druidDataSource);
    }


    @Bean
    protected JobRepository jobRepository(@Autowired DataSource druidDataSource, @Autowired
            DataSourceTransactionManager dataSourceTransactionManager) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(druidDataSource);
        factory.setTransactionManager(dataSourceTransactionManager);
        //factory.setIsolationLevelForCreate("ISOLATION_SERIALIZABLE");
        factory.setTablePrefix("BATCH_");
        factory.setMaxVarCharLength(1000);
        return factory.getObject();
    }


    @Bean
    public JobLauncher jobLauncher(@Autowired JobRepository jobRepository) throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }


    @Bean
    public Step step1(JdbcBatchItemWriter<Person> writer) {
        return stepBuilderFactory.get("step1")
                .<Person, Person>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }
    // end::jobstep[]

}