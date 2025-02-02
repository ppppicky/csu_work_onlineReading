package org.example.service;

import org.example.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public interface BookService {


    public Page<Book> getAllBook(int page, int size);

    public Book addBook(File bookFile, int typeId) throws IOException;

    public void DeleteBook(Integer bookId);

    public List<Book> getBooksByType(int bookTypeId);


//    public BigDecimal getTotalRevenue() {
//        return dashboardMapper.calculateTotalRevenue();
//    }

//    public List<Map<String, Object>> getTopReadBooks() {
//        return dashboardMapper.getTopReadBooks();
//    }
}
