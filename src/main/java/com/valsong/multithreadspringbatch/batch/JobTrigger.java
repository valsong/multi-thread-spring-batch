package com.valsong.multithreadspringbatch.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.builder.JobFlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

@Component
public class JobTrigger {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MyItemWriter myItemWriter;


    @Autowired
    private MyItemProcessor myItemProcessor;

    @Autowired
    private JobCompletionNotificationListener jobCompletionNotificationListener;

    @Autowired
    private Step step1;


    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private TaskExecutor taskExecutor;


    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //如果池中的实际线程数小于corePoolSize,无论是否其中有空闲的线程，都会给新的任务产生新的线程
        taskExecutor.setCorePoolSize(5);
        //连接池中保留的最大连接数。Default: 15 maxPoolSize
        taskExecutor.setMaxPoolSize(10);
        //queueCapacity 线程池所使用的缓冲队列
        taskExecutor.setQueueCapacity(10000);
        taskExecutor.initialize();
        return taskExecutor;

    }


    public void runJob() throws FileNotFoundException, JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobFlowBuilder jobFlowBuilder = jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionNotificationListener)
                .flow(step1);


        FlowBuilder.SplitBuilder<FlowJobBuilder> splitBuilder = jobFlowBuilder.split(taskExecutor);

        Flow[] flows = new Flow[20];

        for (int i = 0; i < 20; i++) {

            Step step = stepBuilderFactory.get("myStep" + i).listener(new MyStepListener())
                    .<String, String>chunk(15)
                    .reader(new MyItemReader())
                    .processor(myItemProcessor)
                    .writer(myItemWriter)
                    .allowStartIfComplete(true)
                    .build();


            Flow flow = new FlowBuilder<Flow>("flow" + i).start(step).build();
            flows[i] = flow;

        }

        Job job = splitBuilder.add(flows).end().build();

        jobLauncher.run(job, new JobParameters());

    }


    public void runJob2() throws FileNotFoundException, JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobFlowBuilder jobFlowBuilder = jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionNotificationListener)
                .flow(step1);

        for (int i = 0; i < 999; i++) {

            Step step = stepBuilderFactory.get("myStep" + i).listener(new MyStepListener())
                    .<String, String>chunk(15)
                    .reader(new MyItemReader())
                    .processor(myItemProcessor)
                    .writer(myItemWriter)
                    .allowStartIfComplete(true)
                    .taskExecutor(taskExecutor)
                    .build();

            jobFlowBuilder.next(step);

        }

        Job job = jobFlowBuilder.end().build();

        jobLauncher.run(job, new JobParameters());

    }


}
