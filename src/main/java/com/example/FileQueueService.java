package com.example;

import com.example.beans.Message;
import com.example.error.QueueException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.util.TextUtils.isEmpty;

public class FileQueueService extends BaseQueueService {
  //
  // Task 3: Implement me if you have time.
  //

    //Queue will be used as a singleton
    protected static QueueService service;

    private String BASE = String.format("%stmp%sfilequeue", File.separator, File.separator);
    private final String PENDING_PATH = "pending";
    private final String LOCK_DIR_PATH = ".lock";
    private final String MESSAGES_PATH = "messages";

    private final long LOCK_ITERATION_TIMEOUT = 100;

    /**
     * Singleton getter
     */
    public static synchronized QueueService getService() {
        if (service == null) {
            service = new FileQueueService();
        }
        return service;
    }

    /**
     * Constructor
     * Reads property if base queues url is redefined (e.g. parametrized by Jenkins) and keep default if not set.
     * Starts the watchdog
     */
    private FileQueueService() {
        if (!isEmpty(System.getProperty("filequeue.base.directory"))){
            BASE = System.getProperty("filequeue.base.directory");
        }
        File file = new File(BASE);
        if (!file.exists()){
            file.mkdir();
        }
        watchdog.start();
    }

    @Override
    public void push(String queueName, Message message) {
        //We keep reception time of the message to use it as index for FIFO
        long now = System.nanoTime();

        //Create a directory for the queue a new queue name received
        File queueDir = getQueueBaseDir(queueName);
        if (!queueDir.exists()){
            queueDir.mkdir();
        }

        //Lock is implemented thorough a hidden directory
        //Each queue has its own lock
        File lock = getLock(queueName);
        try {
            lock(lock);
            //Serialize message
            writeToFile(queueName, message, now);
        } catch (IOException e){
            throw new QueueException("Error occurred while performing file operations", e);
        }finally {
            unlock(lock);
        }
    }

    @Override
    public Message pull(String queueName) {
        File lock = getLock(queueName);
        try {
            lock(lock);

            //Pull will move file to a pending messages directory
            //Create a directory if not exists
            File pendings = getQueuePendingDir(queueName);
            if (!pendings.exists()){
                pendings.mkdir();
            }
            File queueDir = getQueueDir(queueName);
            File[] files = queueDir.listFiles();
            if (files.length == 0){
                return null;
            }

            //Select the file with lowest receive timestamp
            File messageFile = null;
            for (File file : files){
                if (messageFile == null || file.getName().compareTo(messageFile.getName()) < 0){
                    messageFile = file;
                }
            }

            //Deserialize
            Object message = readFromFile(messageFile);

            //Move message file to pendings
            messageFile.renameTo(new File(getQueuePendingDir(queueName) + File.separator + messageFile.getName()));
            return (Message)message;
        } finally {
            unlock(lock);
        }
    }

    @Override
    public void delete(String queueName, Message message) {
        File lock = getLock(queueName);
        try {
            lock(lock);
            //Each message has an UUID
            //Find a file with corresponding uuid in name and remove it
            for (File file : getQueuePendingDir(queueName).listFiles()){
                if (file.getName().contains(message.getUuid())){
                    file.delete();
                    return;
                }
            }
        } finally {
            unlock(lock);
        }

    }

    @Override
    public long messagesInQueue(String queueName) {
        return getQueueDir(queueName).listFiles().length;
    }

    @Override
    public long pendingMessages(String queueName) {
        return getQueuePendingDir(queueName).listFiles().length;
    }

    @Override
    protected void clearPending() {
        for (File pendingDir : getPendingDirs()) {
            for (File file : pendingDir.listFiles()) {
                if (System.currentTimeMillis() - file.lastModified() > timeout) {
                    file.renameTo(new File(getQueueDir(pendingDir.getParentFile().getName()) + File.separator + file.getName()));
                }
            }
        }
    }

    @Override
    public void clearMessages(String queueName) {
        for (File file : getQueueDir(queueName).listFiles()){
            file.delete();
        }
        for (File file : getQueuePendingDir(queueName).listFiles()){
            file.delete();
        }
    }

    private void lock(File lock){
        while (!lock.mkdir()) {
            try {
                Thread.sleep(LOCK_ITERATION_TIMEOUT);
            } catch (InterruptedException e) {
                throw new QueueException("Thread was interrupted", e);
            }
        }
    }

    private void unlock(File lock) {
        lock.delete();
    }

    /////////////Directory path methods///////////////
    private File getLock(String queueName){
        return new File(getQueueBaseDirPath(queueName + File.separator + LOCK_DIR_PATH));
    }

    private String getQueueBaseDirPath(String queueName){
        return BASE + File.separator + queueName;
    }

    private File getQueueBaseDir(String queueName) {
        return new File(getQueueBaseDirPath(queueName));
    }

    private String getQueueDirPath(String queueName){
        return getQueueBaseDirPath(queueName) + File.separator + MESSAGES_PATH;
    }

    private File getQueueDir(String queueName){
        return new File(getQueueDirPath(queueName));
    }

    private String getQueuePendingDirPath(String queueName){
        return getQueueBaseDirPath(queueName) + File.separator + PENDING_PATH;
    }

    private File getQueuePendingDir(String queueName){
        return new File(getQueuePendingDirPath(queueName));
    }

    private String getMessageDirPath(String queueName, String filename){
        return getQueueDirPath(queueName) + File.separator + filename;
    }
    ////////////////////////////////////////////////

    /**
     * Serialize a message
     */
    private void writeToFile(String queueName, Message message, long time) throws IOException {
        File messagesDir = getQueueDir(queueName);
        if (!messagesDir.exists()){
            messagesDir.mkdir();
        }
        String fileName = time + message.getUuid();
        FileOutputStream fout = new FileOutputStream(getMessageDirPath(queueName, fileName));
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(message);
    }

    /**
     * Deserialize a message
     */
    private Message readFromFile(File file) {
        try {
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fin);
            Object message = ois.readObject();
            ois.close();
            return (Message)message;
        } catch (Exception e){
            throw new QueueException("Couldn't read file", e);
        }
    }

    /**
     * Get all pending directories for all queues
     */
    private List<File> getPendingDirs() {
        List<File> dirs = new ArrayList<>();
        for (File queueDir : new File(BASE).listFiles()){
            if (queueDir.isDirectory()){
                for (File dir : queueDir.listFiles()){
                    if (dir.isDirectory() && dir.getName().equals(PENDING_PATH)){
                        dirs.add(dir);
                    }
                    continue;
                }
            }
        }
        return dirs;
    }
}
