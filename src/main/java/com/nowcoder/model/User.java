package com.nowcoder.model;

public class User {
    private int id;
    private String name;
    private String password;
    private String salt;
    private String headUrl;
    public User(){

    }
    public User(String name){
        this.name=name;
        this.headUrl="";
        this.password="";
        this.salt="";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHead_url() {
        return headUrl;
    }

    public void setHead_url(String head_url) {
        this.headUrl = head_url;
    }
}
