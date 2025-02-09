package org.example.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.ReadRecordDTO;
import org.example.entity.Book;
import org.example.entity.ReadRecord;
import org.example.entity.Users;
import org.example.mapper.ReadMapper;
import org.example.repository.BookRepository;
import org.example.repository.ReadRepository;
import org.example.repository.UserRepository;
import org.example.service.ReadRecordService;
import org.example.util.GlobalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ReadSImpl implements ReadRecordService {
    @Autowired
    private ReadMapper readMapper;
    @Autowired
    private ReadRepository readRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public ReadSImpl(ReadMapper mapper, ReadRepository repository,UserRepository userRepository,BookRepository bookRepository) {
        readMapper = mapper;
        readRepository = repository;
        this.userRepository=userRepository;
        this.bookRepository=bookRepository;
    }

//    @Override
//    public void updateReadRecord(ReadRecord readRecord) {
//        readMapper.updateReadRecord(readRecord.getReadId(), readRecord.getLastReadPage());
//    }
//
//    @Override
//    public ReadRecord getLastRecordByUser(Users users){
//        return readMapper.getLastReadRecordByUser(users.getUserId());
//    }

    /**
     * 保存阅读记录
     * @param dto
     */
    @Transactional
    @Override
    public void processNewRecord(ReadRecordDTO dto) {
        Users user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new GlobalException.UserNotFoundException("user not existed"));
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new GlobalException.BookNotFoundException("book not existed"));
        if(dto.getLastReadPage() != null&&dto.getLastReadPage()>book.getBookPage()){
            throw new GlobalException.InvalidPageException("page out of index");
        }
        ReadRecord record = readRepository.findByUsersAndBook(user, book)
                .orElse(new ReadRecord(user,book));

        record.setUsers(record.getUsers());
        record.setBook(record.getBook());
        record.setLastReadPage(dto.getLastReadPage());
        record.setLastReadTime(dto.getLastReadTime() != null
                ? dto.getLastReadTime() : LocalDateTime.now());
        readRepository.save(record);
    }

}
