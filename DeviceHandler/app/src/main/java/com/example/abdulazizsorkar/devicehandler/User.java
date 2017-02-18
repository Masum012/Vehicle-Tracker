package com.example.abdulazizsorkar.devicehandler;

/**
 * Created by Abdul Aziz Sorkar on 1/14/2016.
 */
public class User {
    String name,userName,password;
    int age;

    public User(String name, String userName, String password, int age) {
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.age = age;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.age = -1;
        this.name="";

    }
}
