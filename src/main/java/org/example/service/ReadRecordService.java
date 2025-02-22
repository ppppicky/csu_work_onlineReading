package org.example.service;

import org.example.dto.ReadRecordDTO;
import org.example.entity.ReadRecord;
import org.example.entity.Users;

import java.util.List;

public interface ReadRecordService {
    List<ReadRecordDTO> getAllRecordsByUserId(int userId);

    ReadRecordDTO getLastRecordByUserId(int userId, int bookId);

    void processNewRecord(ReadRecordDTO record);

}