package org.example.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.BookType;
import org.example.repository.BookTypeRepository;
import org.example.service.BookTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookTypeSImpl implements BookTypeService {
    @Autowired
    BookTypeRepository bookTypeRepository;

    /**
     *
     * @param typeName
     */
    @Override
    public void addType(String typeName) {

       if( bookTypeRepository.findByBookTypeName(typeName).isPresent()){
           throw new RuntimeException("booktype existed");
       }

        BookType bookType=new BookType();
       bookType.setBookTypeName(typeName);
       log.info(bookType.toString());
       bookTypeRepository.save(bookType);
    }
}
