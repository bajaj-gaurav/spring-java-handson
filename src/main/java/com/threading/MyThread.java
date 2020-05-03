package com.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.threading.common.HttpCalls;

@Component
@Scope("prototype")
public class MyThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyThread.class);

    HttpCalls http;

    @Override
    public void run() {
        System.out.println(("Called from thread"));
        LOGGER.info("Called from thread");

    }
}
