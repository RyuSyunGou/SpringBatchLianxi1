package com.Itemprocessor_adapter;

public class UserServiceImpl {
    public User toUpperCase(User user){
        user.setName(user.getName().toUpperCase());
        return user;
    }
}
