package com.valsong.multithreadspringbatch.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class MyItemReader implements ItemReader<String> {

    private static final Logger logger = LoggerFactory.getLogger(MyItemReader.class);

    private BufferedReader br = new BufferedReader(new FileReader(MyItemReader.class.getClassLoader().getResource("application.yml").getFile()));

    public MyItemReader() throws FileNotFoundException {
    }

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        String s = br.readLine();
        return s;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (br != null) {
            br.close();
            logger.info("######br will be close ...");
        }
    }

}
