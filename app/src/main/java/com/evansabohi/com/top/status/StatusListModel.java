package com.evansabohi.com.top.status;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ABOHI CHRISTIAN on 09/06/2018.
 */

public class StatusListModel {

    private  ArrayList<String>statusmessages;
    private String uid;

    private long timestamp;
    public StatusListModel(){

    }
    public StatusListModel(ArrayList<String>statusmessages,String uid,long timestamp){
        this.statusmessages = statusmessages;
        this.uid = uid;
        this.timestamp = timestamp;


    }


    public ArrayList<String> getStatusmessages() {
        return statusmessages;
    }

    public void setStatusmessages(ArrayList<String> statusmessages) {
        this.statusmessages = statusmessages;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
