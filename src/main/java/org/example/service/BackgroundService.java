package org.example.service;

import org.example.dto.BackgroundDTO;
import org.example.entity.Users;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BackgroundService {
    public BackgroundDTO uploadTemporary(MultipartFile file);
    public void confirmSave(String resourceId, Integer userId) throws IOException;
    public List<BackgroundDTO> getUserBackgrounds(Users user) ;
    public BackgroundDTO getBackground(Integer resourceId) throws IOException;

    BackgroundDTO uploadTemporary(String gradient);
    //private ResourceType determineResourceType(String mimeType) ;
}
