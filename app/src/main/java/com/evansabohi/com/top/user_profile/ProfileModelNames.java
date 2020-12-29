package com.evansabohi.com.top.user_profile;

/**
 * Created by www.AndroidSquad.Net on 12/14/2016.
 */

public class ProfileModelNames {
    String name;
    String image;
    String native_name;
    String user_age;
    String user_info;
    String uid;
    long time;
    String coverphoto;

    public ProfileModelNames() {
    }

    public ProfileModelNames(String name, String image, String native_name, String user_age, String user_info, String uid,long time,String coverphoto) {
        this.name = name;
        this.image = image;
        this.native_name = native_name;
        this.user_age = user_age;
        this.user_info = user_info;
        this.uid = uid;
        this.time = time;
        this.coverphoto = coverphoto;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNative_name() {
        return native_name;
    }

    public void setNative_name(String native_name) {
        this.native_name = native_name;
    }

    public String getUser_age() {
        return user_age;
    }

    public void setUser_age(String user_age) {
        this.user_age = user_age;
    }

    public String getUser_info() {
        return user_info;
    }

    public void setUser_info(String user_info) {
        this.user_info = user_info;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public String getCoverphoto() {
        return coverphoto;
    }

    public void setCoverphoto(String coverphoto) {
        this.coverphoto = coverphoto;
    }


}

