package com.example;

import com.example.beans.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by azee on 10.04.16.
 */
@RunWith(Parameterized.class)
public class ConcurrentPullDeleteTest extends CommonBaseTest {

    public ConcurrentPullDeleteTest(QueueService service, String queueClassName) {
        super(service, queueClassName);
    }

    @Test
    public void concurrentPullDeleteTest(){
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        for (int i = 0; i < 1; i++) {
            service.push(QUEUE_NAME, new Message("msg" + i));
            Runnable worker = new Runnable() {
                @Override
                public void run() {
                    service.delete(QUEUE_NAME, service.pull(QUEUE_NAME));
                }
            };
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
        assertThat(getMessage("Messages queue is not empty"), service.messagesInQueue(QUEUE_NAME), is(0L));
        assertThat(getMessage("Pending messages container is not empty"), service.pendingMessages(QUEUE_NAME), is(0L));
    }
}
