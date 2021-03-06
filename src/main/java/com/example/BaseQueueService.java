package com.example;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by azee on 10.04.16.
 */
public abstract class BaseQueueService implements QueueService {

    //Reception (delete) confirmation timeout for a queue
    protected volatile long timeout = 30000;

    Object lock = new Object();

    //Watchdog will clean up pending messages and return them back to the queue if ttl limit reached
    protected Thread watchdog = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true){
                clearPending();
                try {
                    synchronized (lock){
                        lock.wait(Math.max(1, getTimeout()));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    public synchronized void setTimeout(long timeout) {
        this.timeout = timeout;
        //Need to wake watchdog immediately if timeout changed
        synchronized (lock){
            lock.notify();
        }
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void clearMessages(String queueName) {
        throw new NotImplementedException();
    }

    protected abstract void clearPending();
}
