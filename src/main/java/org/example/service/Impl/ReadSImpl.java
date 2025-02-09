//package org.example.service.Impl;
//
//import org.example.entity.ReadRecord;
//import org.example.mapper.ReadRMapper;
//import org.example.repository.ReadRRepository;
//import org.example.service.ReadRecordService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@Service
//public class ReadRSImpl implements ReadRecordService {
//    private ReadRMapper readRMapper;
//    private ReadRRepository readRRepository;
//
//    @Autowired
//    public ReadRSImpl(ReadRMapper mapper, ReadRRepository repository) {
//        readRMapper = mapper;
//        readRRepository = repository;
//    }
//
//    @Override
//    public void updateReadRecord(ReadRecord readRecord) {
//        readRMapper.updateReadRecord(readRecord.getReadRId(),readRecord.getLastReadPage());
//    }
//
//    @Override
//    public ReadRecord getLastRecordByUserId(int userId) {
//        return readRMapper.getLastReadRecordByUser(userId);
//    }
//}
