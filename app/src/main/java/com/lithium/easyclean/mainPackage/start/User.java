package com.lithium.easyclean.mainPackage.start;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String password;
    private String email;
    private String uid = null;

    public User(){

    }
    public User(String name, String password,  String email){
        this.name = name;
        this.password = password;
        this.email = email;
    }
    public User(String name, String password,  String email,String uid){
        this.name = name;
        this.password = password;
        this.email = email;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}


