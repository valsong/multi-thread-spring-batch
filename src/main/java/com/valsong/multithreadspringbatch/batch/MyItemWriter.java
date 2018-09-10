package com.valsong.multithreadspringbatch.batch;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyItemWriter implements ItemWriter<String> {
    @Override
    public void write(List<? extends String> items) throws Exception {
        System.out.println(Thread.currentThread().getName()+"   items size:" + items.size());
        items.forEach(System.out::println);
    }
}
