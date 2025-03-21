package org.example.service;

import org.example.dto.BackgroundDTO;
import org.example.entity.Users;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BackgroundService {
    CompletableFuture<BackgroundDTO> uploadTemporary(MultipartFile file);

    CompletableFuture<String> confirmSave(String resourceId, Integer userId) throws Exception;

    CompletableFuture<BackgroundDTO> uploadTemporary(String gradient);

    List<String> getUserBackgroundsUrl(Users user);

    String getBackgroundUrl(Integer resourceId) throws Exception;
}
