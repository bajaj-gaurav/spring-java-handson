package com.threading;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.threading.service.AsynchronousService;


@Component
public class Scheduler {

    @Autowired
    private AsynchronousService checkAsyncService;

    @Scheduled(fixedDelay = 1000)
    public void checkTheScedule() {
        checkAsyncService.printEmployees();
    }


}
