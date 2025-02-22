package org.example.service;

import org.example.dto.UserDTO;
import org.example.entity.Manager;

import java.util.List;

public interface ManagerService {
    void register(Manager manager);

    Manager login(String name, String password);

    Manager findManager(String name);

    // 获取用户列表，支持用户名模糊查询
    List<UserDTO> getUsers(String username, int page, int size);

    // 启用/禁用用户
    boolean updateUserStatus(String username, Integer status);

}
