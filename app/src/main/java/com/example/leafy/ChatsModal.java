package com.example.leafy;

public class ChatsModal {
    private String message;
    private String sender; //user인지 chatbot인지 구분

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

    public ChatsModal(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }
}
