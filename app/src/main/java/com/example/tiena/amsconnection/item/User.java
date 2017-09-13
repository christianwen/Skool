package com.example.tiena.amsconnection.item;

/**
 * Created by tiena on 22/08/2017.
 */

public class User {
    public String key;
    private String name;
    private String role;
    private String info;
    public User(){

    }
    public User(String key,String name,String role,String info){
        this.key=key;
        this.name=name;
        this.role=role;
        this.info=info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
