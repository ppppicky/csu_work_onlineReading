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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReadRecordSImpl implements ReadRecordService {
    @Autowired
    ReadRepository readRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ReadMapper readMapper;
    @Autowired
    BookRepository bookRepository;


    @Override
    public List<ReadRecordDTO> getAllRecordsByUserId(int userId) {
       Users users=  userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("user not existed"));
       List<ReadRecord>readRecords= readRepository.findByUser(users)
               .orElseThrow(()->new IllegalArgumentException("book not been read yet"));
        List<ReadRecordDTO> recordDTOS= readRecords.stream().map((item)->{
        ReadRecordDTO dto=new ReadRecordDTO();
        BeanUtils.copyProperties(item,dto);

        dto.setUserId(item.getUser().getUserId());
        dto.setLastReadPage(item.getLastReadPage());
        dto.setLastReadTime(item.getLastReadTime());
        dto.setBookId(item.getBook().getBookId());
        return dto;
    }).collect(Collectors.toList());
        return recordDTOS;

    }

    @Override
    public ReadRecordDTO getLastRecordByUserId(int userId,int bookId) {
        Users users=  userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("user not existed"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new GlobalException.BookNotFoundException("book not existed"));
        ReadRecord record = readRepository.findByUserAndBook(users, book)
                .orElseThrow(()->new IllegalArgumentException("book not been read yet"));
        ReadRecordDTO dto=new ReadRecordDTO();
        dto.setBookId(bookId);
        dto.setUserId(userId);
        dto.setLastReadTime(record.getLastReadTime());
        dto.setLastReadPage(record.getLastReadPage());
        return  dto;
    }

    /**
     * 处理新的阅读记录
     * @param dto
     */
    @Transactional
    @Override
    public void processNewRecord(ReadRecordDTO dto) {
        Users user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new GlobalException.UserNotFoundException("user not existed"));
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new GlobalException.BookNotFoundException("book not existed"));
        if (dto.getLastReadPage() != null && dto.getLastReadPage() > book.getBookPage()) {
            throw new GlobalException.InvalidPageException("page out of index");
        }

        ReadRecord record = readRepository.findByUserAndBook(user, book)
                .orElse(new ReadRecord(user, book));
        record.setUser(record.getUser());
        record.setBook(record.getBook());
        record.setLastReadPage(dto.getLastReadPage());
        record.setLastReadTime(dto.getLastReadTime() != null
                ? dto.getLastReadTime() : LocalDateTime.now());
        log.info(record.toString());
        readRepository.save(record);
    }
}
