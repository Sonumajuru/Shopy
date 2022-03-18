package com.genesistech.njangi.model;

public class Message {

    private String message;
    private User sender;
    private long createdAt;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Message(String message, User sender, long createdAt) {
        this.message = message;
        this.sender = sender;
        this.createdAt = createdAt;
    }
}
