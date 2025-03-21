package org.example.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserDTO;
import org.example.entity.Manager;
import org.example.entity.Users;
import org.example.repository.ManagerRepository;
import org.example.repository.UserRepository;
import org.example.service.ManagerService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ManagerSImpl implements ManagerService {

    @Autowired
     ManagerRepository managerRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public void register(Manager manager) {
        manager.setManagerPassword(BCrypt.hashpw(manager.getManagerPassword(), BCrypt.gensalt()));
        managerRepository.save(manager);
    }

    @Override
    public Manager login(String name, String password) {
        Manager manager = managerRepository.findByManagerName(name);
        if (manager == null) log.error("manager existed");
        if (!BCrypt.checkpw(password, manager.getManagerPassword())) {
            log.error("incorrect password");
        }
        return manager;
    }

    @Override
    public Manager findManager(String name) {
        return managerRepository.findByManagerName(name);
    }

    // 获取用户列表，支持用户名模糊查询
    @Override
    public List<UserDTO> getUsers(String username, int page, int size) {
        List<Users> users;
        if (username != null && !username.isEmpty()) {
            // 支持通过用户名进行模糊查询
            users = userRepository.findByUserNameContaining(username, PageRequest.of(page, size)).getContent();
        } else {
            // 如果没有提供用户名，返回所有用户
            users = userRepository.findAll(PageRequest.of(page, size)).getContent();
        }

        // 将 Users 转换为 UserDTO，密码字段统一为 "****"
        return users.stream().map(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getUserId());
            userDTO.setUserName(user.getUserName());
            userDTO.setUserSex(user.getUserSex());
            userDTO.setIsVip(user.getIsVip());
            userDTO.setVipTime(user.getVipTime());
            userDTO.setUserCredit(user.getUserCredit());
            userDTO.setUserRegTime(user.getUserRegTime());
            userDTO.setStatus(user.getStatus());
            return userDTO;
        }).collect(Collectors.toList());
    }


    // 更新用户状态（启用/禁用）
    @Override
    public boolean updateUserStatus(String username, Integer status) {
        Users user = userRepository.findByUserName(username);
        if (user != null) {
            user.setStatus(status.byteValue()); // 设置用户状态
            userRepository.save(user); // 更新用户状态
            return true;
        }
        return false;
    }

}