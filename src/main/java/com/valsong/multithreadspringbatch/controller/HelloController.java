package com.valsong.multithreadspringbatch.controller;

import com.valsong.multithreadspringbatch.batch.JobTrigger;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("")
public class HelloController {

    @Autowired
    private JobTrigger jobTrigger;

    @RequestMapping("/hello")
    public String index() throws FileNotFoundException, JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobTrigger.runJob();
        return "hello";
    }
}
