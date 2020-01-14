package com.example.groupchat;

//This model class defines each chat message where it contains message id, text and a boolean flag (isSelf)to define message owner.
// Using this boolean flag we’ll align message left or right in the list view.
public class Message {
    private String fromName, message;
    private boolean isSelf;
    private int type;
    private String fileName;

    public Message() {
    }

    public Message(String fromName, String message,String fileName, boolean isSelf, int type) {
        this.fromName = fromName;
        this.message = message;
        this.fileName = fileName;
        this.isSelf = isSelf;
        this.type = type;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    public int getType() {
        return type;
    }

    public void setType(String message) {
        this.type = type;
    }


}