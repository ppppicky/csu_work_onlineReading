package org.example.service.Impl;

import org.example.entity.Manager;
import org.example.mapper.ManagerMapper;
import org.example.repository.ManagerRepository;
import org.example.service.ManagerService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagerSImpl implements ManagerService {
     ManagerRepository managerRepository;
    ManagerMapper managerMapper;

    @Autowired
    public ManagerSImpl(ManagerRepository repository, ManagerMapper mapper) {
        managerMapper = mapper;
        managerRepository = repository;
    }

    @Override
    public void register(Manager manager) {
        manager.setManagerPassword(BCrypt.hashpw(manager.getManagerPassword(), BCrypt.gensalt()));
        managerRepository.save(manager);
    }

    @Override
    public Manager login(String name, String password) {
        Manager manager = (Manager) managerRepository.findByManagerName(name);
       if(manager==null)throw  new RuntimeException("manager existed");
        if (!BCrypt.checkpw(password, manager.getManagerPassword())) {
            throw new RuntimeException("incorrect password");
        }
        return manager;
    }

    @Override
    public boolean findManager(String name) {
        return managerRepository.findByManagerName(name)
                == null ? false : true;
    }

}