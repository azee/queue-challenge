package com.example;

import com.example.beans.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by azee on 10.04.16.
 */
@RunWith(Parameterized.class)
public class CommonPushPopTest extends CommonBaseTest {

    public CommonPushPopTest(QueueService service, String queueClassName) {
        super(service, queueClassName);
    }

    @Test
    public void simplePushPopTest(){
        Message message1 = new Message("msg1");
        service.push(QUEUE_NAME, message1);
        Message message2 = new Message("msg2");
        service.push(QUEUE_NAME, message2);

        assertThat(service.pull(QUEUE_NAME).getObject(), is("msg1"));
        assertThat(service.pull(QUEUE_NAME).getObject(), is("msg2"));
        assertNull(service.pull(QUEUE_NAME));

        assertThat(service.messagesInQueue(QUEUE_NAME), is(0L));
        assertThat(service.pendingMessages(QUEUE_NAME), is(2L));

        service.delete(QUEUE_NAME, message1);
        assertThat(service.messagesInQueue(QUEUE_NAME), is(0L));
        assertThat(service.pendingMessages(QUEUE_NAME), is(1L));

        service.delete(QUEUE_NAME, message2);
        assertThat(service.messagesInQueue(QUEUE_NAME), is(0L));
        assertThat(service.pendingMessages(QUEUE_NAME), is(0L));
    }
}
