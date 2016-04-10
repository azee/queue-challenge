package com.example.beans;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by azee on 10.04.16.
 */
public class Message<T> implements Serializable {

    private T object;
    private String uuid;

    public Message() {
        uuid = UUID.randomUUID().toString();
    }

    public Message(T object) {
        this.object = object;
        uuid = UUID.randomUUID().toString();
    }

    public Object getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public String getUuid() {
        return uuid;
    }
}
