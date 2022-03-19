package com.genesistech.njangi.model;

public class Message {

    private String message;
    private String sender;
    private String senderUuid;
    private long createdAt;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderUuid() {
        return senderUuid;
    }

    public void setSenderUuid(String senderUuid) {
        this.senderUuid = senderUuid;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Message() {}

    public Message(String message, String sender, String senderUuid, long createdAt) {
        this.message = message;
        this.sender = sender;
        this.senderUuid = senderUuid;
        this.createdAt = createdAt;
    }
}
