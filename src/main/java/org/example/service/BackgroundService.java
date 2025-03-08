package org.example.service;

import org.example.dto.BackgroundDTO;
import org.example.entity.Users;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BackgroundService {
    BackgroundDTO uploadTemporary(MultipartFile file);
    void confirmSave(String resourceId, Integer userId) throws Exception;
    BackgroundDTO uploadTemporary(String gradient);
    List<String> getUserBackgroundsUrl(Users user);

    String getBackgroundUrl(Integer resourceId) throws Exception;
}
