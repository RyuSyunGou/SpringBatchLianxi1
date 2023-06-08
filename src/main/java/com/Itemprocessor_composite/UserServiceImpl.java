package com.Itemprocessor_composite;


public class UserServiceImpl {
    public User toUpperCase(User user){
        user.setName(user.getName().toUpperCase());
        return user;
    }
}
