package com.example;

import com.example.beans.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by azee on 10.04.16.
 */
@RunWith(Parameterized.class)
public class CommonMessageNotRemovedOnPopTest extends CommonBaseTest {


    public CommonMessageNotRemovedOnPopTest(QueueService service, String queueClassName) {
        super(service, queueClassName);
    }

    @Test(timeout = 1000)
    public void messageNotRemovedOnPopTest(){
        service.setTimeout(0);
        service.push(QUEUE_NAME, new Message("Message1"));
        service.push(QUEUE_NAME, new Message("Message2"));

        assertThat(service.pull(QUEUE_NAME).getObject(), is("Message1"));
        while(service.pendingMessages(QUEUE_NAME) != 0 && service.messagesInQueue(QUEUE_NAME) != 2){}

        assertThat(service.messagesInQueue(QUEUE_NAME), is(2L));
        assertThat(service.pendingMessages(QUEUE_NAME), is(0L));
        assertThat(service.pull(QUEUE_NAME).getObject(), is("Message1"));
    }
}
