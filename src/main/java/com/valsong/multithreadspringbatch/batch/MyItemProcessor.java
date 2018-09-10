package com.valsong.multithreadspringbatch.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class MyItemProcessor implements ItemProcessor<String, String> {
    @Override
    public String process(String item) throws Exception {
        return "#" + item + "#";
    }
}
