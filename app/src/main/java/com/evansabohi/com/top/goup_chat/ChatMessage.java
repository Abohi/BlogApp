package com.evansabohi.com.top.goup_chat;


public class ChatMessage {

    String name;
    String message;
    String current_uid;
    long time;

   public ChatMessage(){

   }
    public ChatMessage(String name,  String message, long time) {
        this.name = name;
        this.message = message;
        this.time = time;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getCurrent_uid() {
        return current_uid;
    }

    public void setCurrent_uid(String current_uid) {
        this.current_uid = current_uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
