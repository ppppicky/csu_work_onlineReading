package org.example.service;

import org.example.dto.ReadRecordDTO;
import org.example.entity.ReadRecord;
import org.example.entity.Users;

import java.util.List;

public interface ReadRecordService {
    public List<ReadRecordDTO> getAllRecordsByUserId(int userId) ;


    public ReadRecordDTO getLastRecordByUserId(int userId,int bookId) ;



    public void processNewRecord(ReadRecordDTO record);

}
