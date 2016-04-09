package com.example;

public class FileQueueService extends BaseQueueService {
  //
  // Task 3: Implement me if you have time.
  //

    @Override
    public void push(String queueName, Object message) {
    }

    @Override
    public Object pull(String queueName) {
        return null;
    }

    @Override
    public void delete(String queueName, Object message) {

    }

    @Override
    public long messagesInQueue(String queueName) {
        return 0;
    }

    @Override
    public long pendingMessages(String queueName) {
        return 0;
    }
}
