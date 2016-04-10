package com.example;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by azee on 10.04.16.
 */
@RunWith(Parameterized.class)
public class CommonBaseTest {

    protected QueueService service;
    final String QUEUE_NAME = "singleQueueName";

    public CommonBaseTest(QueueService service) {
        this.service = service;
    }

    @Parameterized.Parameters
    public static Collection getServices() {
        return Arrays.asList(new Object[][]{
                {InMemoryQueueService.getService()},
                {FileQueueService.getService()}
        });
    }

    @Before
    public void setUp(){
        service.setTimeout(30000);
        service.clearMessages(QUEUE_NAME);
    }
}
