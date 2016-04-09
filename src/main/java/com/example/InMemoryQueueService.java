package com.example;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.stream.Collectors.toList;

public class InMemoryQueueService extends BaseQueueService {

    //Queue will be used as a singleton
    private static QueueService service;

    //Using Deque to be able to place messages at the beginning of the queue
    //Using ConcurrentLinkedDeque to avoid concurrent modification exceptions and locking
    Map<String, Deque> queues = new ConcurrentHashMap<>();

    //Pending map is used as a container for "invisible" (prefetched) messages
    private final Map<String, Map<Object, Long>> pendings = new ConcurrentHashMap();

    //Watchdog will clean up pending messages and return them back to the queue if ttl limit reached
    private Thread watchdog;

    //As size() is of collections is not thread safe - use lock
    Lock updateLock = new ReentrantLock();

    //Private singleton constructor
    private InMemoryQueueService() {
        watchdog = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    clearPending();
                    try {
                        Thread.sleep(timeout);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        watchdog.start();
    }

    /**
     * Singleton getter
     */
    public static synchronized QueueService getService(){
        if (service == null){
            service = new InMemoryQueueService();
        }
        return service;
    }

    /**
     * Just add a message to a concurrent queue
     */
    @Override
    public void push(String queueName, Object message) {
        getQueue(queueName).add(message);
    }


    /**
     * Pull a message will remove it from the queue and place into a "pending confirmation prefetched" container
     */
    @Override
    public Object pull(String queueName) {
        Object message = getQueue(queueName).poll();
        if (message != null){
            getPending(queueName).put(message, System.currentTimeMillis() + timeout);
        }
        return message;
    }

    /**
     * Remove a message from pending - confirm from a consumer
     */
    @Override
    public void delete(String queueName, Object message) {
        getPending(queueName).remove(message);
    }

    /**
     * Get number of messages in queue
     * Will be useful during exploitation and for TDD
     */
    @Override
    public long messagesInQueue(String queueName) {
        try {
            updateLock.lock();
            return getQueue(queueName).size();
        } finally {
            updateLock.unlock();
        }
    }

    /**
     * Get number of pending confirmation messages
     * Will be useful during exploitation and for TDD
     */
    @Override
    public long pendingMessages(String queueName) {
        try {
            updateLock.lock();
            return getPending(queueName).size();
        } finally {
            updateLock.unlock();
        }
    }

    /**
     * Drop a queue
     */
    @Override
    public void clearMessages(String queueName) {
        try {
            updateLock.lock();
            getQueue(queueName).clear();
            getPending(queueName).clear();
        } finally {
            updateLock.unlock();
        }

    }

    /**
     * Goes through pending container to define if ttl of eny is exceeded
     * If so moves messages to the head of the queue
     */
    private void clearPending() {
        long now = System.currentTimeMillis();
        pendings.entrySet().forEach(pending -> {
            pending.getValue().entrySet().stream().filter(entry -> entry.getValue() <= now)
                    .map(entry -> entry.getKey())
                    .collect(toList())
                    .forEach(message -> {
                        try {
                            updateLock.lock();
                            pending.getValue().remove(message);
                            getQueue(pending.getKey()).addFirst(message);
                        } finally {
                            updateLock.unlock();
                        }

                    });
        });
    }

    /**
     * Get a queue by name
     * Will act synchronized if a new queue is created
     */
    private Deque getQueue(String queueName){
        Deque queue = queues.get(queueName);
        if (queue == null){
            synchronized (this) {
                queue = queues.get(queueName);
                if (queue == null) {
                    queue = new ConcurrentLinkedDeque();
                    queues.put(queueName, queue);
                }
            }
        }
        return queue;
    }

    /**
     * Get a pending container by name
     * Will act synchronized if a new queue is created
     */
    private Map<Object, Long> getPending(String queueName){
        Map<Object, Long> pending = pendings.get(queueName);
        if (pending == null){
            synchronized (this){
                pending = pendings.get(queueName);
                if (pending == null){
                    pending = new ConcurrentHashMap<>();
                    pendings.put(queueName, pending);
                }
            }
        }
        return pending;
    }
}
