package com.techgig;

/**
 * Created by Vishal Mago on 05/05/2017.
 */

public class user_model {
    public String username;
    public String email;
    public String password;
    public int balance;
    public user_model() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public user_model(String username, String email,String password,int balance) {
        this.username = username;
        this.email = email;
        this.password=password;
        this.balance=balance;
    }

}