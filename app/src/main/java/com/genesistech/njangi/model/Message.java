package com.genesistech.njangi.model;
public class Message {
    private String message;
    private String senderName;
    private String senderUuid;
    private String receiverUuid;
    private long createdAt;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getSenderName() {
        return senderName;
    }
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    public String getSenderUuid() {
        return senderUuid;
    }
    public void setSenderUuid(String senderUuid) {
        this.senderUuid = senderUuid;
    }
    public String getReceiverUuid() {
        return receiverUuid;
    }
    public void setReceiverUuid(String receiverUuid) {
        this.receiverUuid = receiverUuid;
    }
    public long getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    public Message() {}
    public Message(String message, String senderName, String senderUuid, String receiverUuid, long createdAt) {
        this.message = message;
        this.senderName = senderName;
        this.senderUuid = senderUuid;
        this.receiverUuid = receiverUuid;
        this.createdAt = createdAt;
    }
}
