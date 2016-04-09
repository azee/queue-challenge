package com.example;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by azee on 08.04.16.
 */
public abstract class BaseQueueService implements QueueService {

    protected volatile long timeout = 30000;

    @Override
    public synchronized void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void clearMessages(String queueName) {
        throw new NotImplementedException();
    }
}
