package com.evansabohi.com.top.new_post;

/**
 * Created by ABOHI CHRISTIAN on 15/06/2018.
 */

public class CommentModel {
    public CommentModel(){

    }

    public CommentModel(String comment, String uid, long datetime) {
        this.comment = comment;
        this.uid = uid;
        this.datetime = datetime;
    }

    private String comment,uid;
    long datetime;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

}
