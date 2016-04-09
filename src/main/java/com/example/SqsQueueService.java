package com.example;

import com.amazonaws.services.sqs.AmazonSQSClient;

public class SqsQueueService extends BaseQueueService {
  //
  // Task 4: Optionally implement parts of me.
  //
  // This file is a placeholder for an AWS-backed implementation of QueueService.  It is included
  // primarily so you can quickly assess your choices for method signatures in QueueService in
  // terms of how well they map to the implementation intended for a production environment.
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

  public SqsQueueService(AmazonSQSClient sqsClient) {
  }
}
