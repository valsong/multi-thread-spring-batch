package com.valsong.multithreadspringbatch.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class MyStepListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        String str = stepExecution.getJobParameters().toString();
        System.out.println("++++++++++before+++++++++++++");
        System.out.println(str);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("++++++++++after+++++++++++++" + stepExecution);
        return stepExecution.getExitStatus();
    }
}
