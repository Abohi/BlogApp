package com.evansabohi.com.top.new_post;


import java.util.Date;

public class BlogPost{


    public String  image_url;
    public String desc;
    public String image_thumb;
    public String current_uid;
    public long timestamp;

    public BlogPost() {}

    public BlogPost( String image_url, String desc, String image_thumb, long timestamp,String current_uid) {
        this.image_url = image_url;
        this.desc = desc;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
        this.current_uid = current_uid;
    }
    public String getCurrent_uid() {
        return current_uid;
    }

    public void setCurrent_uid(String current_uid) {
        this.current_uid = current_uid;
    }


    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}
