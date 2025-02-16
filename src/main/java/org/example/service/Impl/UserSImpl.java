package org.example.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.Manager;
import org.example.entity.Users;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class UserSImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Autowired
    public UserSImpl(UserRepository ur, UserMapper um) {
        userRepository = ur;
        userMapper = um;
    }

    @Override
    public boolean findUser(String userName) {
        boolean test = userRepository.findByUserName(userName) == null ? false : true;
        return test;
    }

    @Override
    public void register(Users user) {
        user.setUserPassword(BCrypt.hashpw(user.getUserPassword(), BCrypt.gensalt()));
        user.setUserRegTime(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public Users login(String userName, String userPassword) {
        // 查找用户
        Users user = userRepository.findByUserName(userName);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }

        // 检查用户是否被禁用
        if (user.getStatus() == 0) {
            throw new RuntimeException("Account is disabled");
        }

        // 校验密码
        if (!BCrypt.checkpw(userPassword, user.getUserPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        return user;
    }


}