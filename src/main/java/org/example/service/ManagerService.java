package org.example.service;

import org.example.entity.Manager;

public interface ManagerService {
    void register(Manager manager);

    Manager login(String name, String password);
    boolean findManager(String name);
}
