package com.evansabohi.com.top.goup_chat;


public class GroupListModel {

    private String name_of_group;

    private String current_uid;
    private long time;


    public GroupListModel(String name_of_group,String current_uid,long time) {
        this.name_of_group = name_of_group;
        this.current_uid = current_uid;
        this.time = time;
    }
    public GroupListModel(){

    }
    public String getName_of_group() {
        return name_of_group;
    }

    public void setName_of_group(String name_of_group) {
        this.name_of_group = name_of_group;
    }
    public String getCurrent_uid() {
        return current_uid;
    }

    public void setCurrent_uid(String current_uid) {
        this.current_uid = current_uid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
