package org.example.service;

import org.example.entity.Users;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface UserService {


    boolean findUser(String userName);

    void register(Users user);

    Users login(String userName, String userPassword);
}
