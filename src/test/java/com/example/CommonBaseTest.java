package com.example;

import org.junit.Before;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by azee on 10.04.16.
 */
public class CommonBaseTest {

    protected QueueService service;
    protected String queueClassName;
    protected final String QUEUE_NAME = "singleQueueName";

    public CommonBaseTest(QueueService service, String queueClassName) {
        this.service = service;
        this.queueClassName = queueClassName;
    }

    @Parameterized.Parameters(name = "{index}: queue {1}")
    public static Collection getServices() {
        return Arrays.asList(new Object[][]{
                {InMemoryQueueService.getService(), InMemoryQueueService.getService().getClass().getSimpleName()},
                {FileQueueService.getService(), FileQueueService.getService().getClass().getSimpleName()}
        });
    }

    @Before
    public void setUp(){
        service.setTimeout(30000);
        service.clearMessages(QUEUE_NAME);
    }

    protected String getMessage(String message){
        return String.format("[%s]: %s", service.getClass().getSimpleName(), message);
    }
}
