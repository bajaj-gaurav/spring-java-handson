/*
package com.threading.scheduledAnnotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TestScheduler {

    private ArrayList<String> list = new ArrayList<>();

    public List<String> search()
    {
        return list;
    }


    public TestScheduler()
    {
        list.add("saurav");
    }


    @Scheduled(initialDelay = 6000L, fixedDelay = 6000L)
    void inventorySync() {

        log.info("gaurav tests initialised");

        list.add("gaurav");

        System.out.println(list.size());

        log.info("gaurav tests finished");
    }
}
*/
