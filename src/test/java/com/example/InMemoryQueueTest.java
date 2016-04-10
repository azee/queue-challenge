package com.example;

import com.example.beans.Message;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class InMemoryQueueTest {

//    final QueueService service = InMemoryQueueService.getService();
//    final String QUEUE_NAME = "singleQueueName";
//
//    @Before
//    public void setUp(){
//        service.setTimeout(30000);
//        service.clearMessages(QUEUE_NAME);
//    }
//
//    @Test
//    public void simplePushPopTest(){
//        Message message1 = new Message();
//        service.push(QUEUE_NAME, message1);
//        Message message2 = new Message();
//        service.push(QUEUE_NAME, message2);
//
//        assertThat(service.pull(QUEUE_NAME), is(message1));
//        assertThat(service.pull(QUEUE_NAME), is(message2));
//        assertNull(service.pull(QUEUE_NAME));
//
//        assertThat(service.messagesInQueue(QUEUE_NAME), is(0L));
//        assertThat(service.pendingMessages(QUEUE_NAME), is(2L));
//
//        service.delete(QUEUE_NAME, message1);
//        assertThat(service.messagesInQueue(QUEUE_NAME), is(0L));
//        assertThat(service.pendingMessages(QUEUE_NAME), is(1L));
//
//        service.delete(QUEUE_NAME, message2);
//        assertThat(service.messagesInQueue(QUEUE_NAME), is(0L));
//        assertThat(service.pendingMessages(QUEUE_NAME), is(0L));
//    }
//
//    @Test(timeout = 1000)
//    public void messageNotRemovedOnPopTest(){
//        service.setTimeout(0);
//        service.push(QUEUE_NAME, new Message("Message1"));
//        service.push(QUEUE_NAME, new Message("Message2"));
//
//        assertThat(service.pull(QUEUE_NAME).getObject(), is("Message1"));
//        while(service.pendingMessages(QUEUE_NAME) != 0 && service.messagesInQueue(QUEUE_NAME) != 2){}
//
//        assertThat(service.messagesInQueue(QUEUE_NAME), is(2L));
//        assertThat(service.pendingMessages(QUEUE_NAME), is(0L));
//        assertThat(service.pull(QUEUE_NAME).getObject(), is("Message1"));
//    }
//
//    @Test
//    public void concurrentPullDeleteTest(){
//        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
//        for (int i = 0; i < 1; i++) {
//            service.push(QUEUE_NAME, new Message());
//            Runnable worker = new Runnable() {
//                @Override
//                public void run() {
//                    service.delete(QUEUE_NAME, service.pull(QUEUE_NAME));
//                }
//            };
//            executor.execute(worker);
//        }
//        executor.shutdown();
//        while (!executor.isTerminated()) {}
//        assertThat(service.messagesInQueue(QUEUE_NAME), is(0L));
//        assertThat(service.pendingMessages(QUEUE_NAME), is(0L));
//    }
}
