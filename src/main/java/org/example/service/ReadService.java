package org.example.service;

import org.example.dto.ReadRecordDTO;
import org.example.dto.ReadingSettingDTO;
import org.example.entity.FontResource;
import org.example.entity.Users;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReadService {
//    public void updateReadRecord(ReadRecord readRecord);
//
//    public ReadRecord getLastRecordByUser(Users users);

    void processNewRecord(ReadRecordDTO record);
    
    List<FontResource> getAvailableFonts();

    ReadingSettingDTO getUserSettings(Users user);

    void updateUserSetting(Integer userId, ReadingSettingDTO settingDTO);

    void addFont(MultipartFile fontResource);
}
