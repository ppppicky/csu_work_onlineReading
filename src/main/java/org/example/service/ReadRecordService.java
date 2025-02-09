package org.example.service;

import org.example.dto.ReadRecordDTO;
import org.example.entity.ReadRecord;
import org.example.entity.Users;

public interface ReadRecordService {
//    public void updateReadRecord(ReadRecord readRecord);
//
//    public ReadRecord getLastRecordByUser(Users users);

    void processNewRecord(ReadRecordDTO record);
}
