package org.example.service;

import org.example.entity.Users;

public interface UserService {


    boolean findUser(String userName);

    void register(Users user);

    Users login(String userName, String userPassword);

    Users getUserByUserName(String userName);

}