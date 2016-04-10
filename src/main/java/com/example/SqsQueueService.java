package com.example;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.QueueAttributeName;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.example.beans.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SqsQueueService extends BaseQueueService {
  //
  // Task 4: Optionally implement parts of me.
  //
  // This file is a placeholder for an AWS-backed implementation of QueueService.  It is included
  // primarily so you can quickly assess your choices for method signatures in QueueService in
  // terms of how well they map to the implementation intended for a production environment.
  //

  private AmazonSQSClient sqsClient;


  @Override
  public void push(String queueName, Message message) {
    sqsClient.sendMessage(sqsClient.getQueueUrl(queueName).getQueueUrl(), serializeMessage(message));
  }

  @Override
  public Message pull(String queueName) {
    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsClient.getQueueUrl(queueName).getQueueUrl());
    receiveMessageRequest.setMaxNumberOfMessages(1);
    List<com.amazonaws.services.sqs.model.Message> messages = sqsClient.receiveMessage(queueName).getMessages();
    return messages.size() < 1 ? null : convert(messages.get(0));
  }

  @Override
  public void delete(String queueName, Message message) {
    //noop - no need to delete from pending - resend is implemented inside sqs
  }

  @Override
  public long messagesInQueue(String queueName) {
    return Long.parseLong(sqsClient.getQueueAttributes(queueName, Arrays.asList(QueueAttributeName.ApproximateNumberOfMessages.toString()))
            .getAttributes().get(QueueAttributeName.ApproximateNumberOfMessages.toString()));


  }

  @Override
  public long pendingMessages(String queueName) {
    Map<String, String> attrs = sqsClient.getQueueAttributes(queueName,
            Arrays.asList(QueueAttributeName.ApproximateNumberOfMessagesNotVisible.toString(),
                    QueueAttributeName.ApproximateNumberOfMessagesDelayed.toString())).getAttributes();
    return Long.parseLong(attrs.get(QueueAttributeName.ApproximateNumberOfMessagesNotVisible.toString())) +
            Long.parseLong(attrs.get(QueueAttributeName.ApproximateNumberOfMessagesDelayed.toString()));

  }

  @Override
  protected void clearPending() {
    //noop - no need to delete from pending - resend is implemented inside sqs
    //we won't even start a watchdog thread
  }

  public SqsQueueService(AmazonSQSClient sqsClient) {
    this.sqsClient = sqsClient;
  }

  private String serializeMessage(Message message){
    //ToDo: add Jackson or ru.greatbit.java-utils to implement serialization to JSON
    return message.toString();
  }

  private String deserializeMessage(Message message){
    //ToDo: add Jackson or ru.greatbit.java-utils to implement serialization to JSON
    return message.toString();
  }

  private Message convert(com.amazonaws.services.sqs.model.Message message) {
    //ToDo: add Jackson or ru.greatbit.java-utils to implement deSerialization to JSON
    return new Message(message.getBody());
  }


}
