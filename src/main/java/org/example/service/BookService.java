package org.example.service;

import org.example.dto.BookInfoDTO;
import org.example.dto.ChapterVO;
import org.example.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.List;


public interface BookService {


    public Page<Book> getAllBook(int page, int size);

    public Book addBook(File bookFile, String typeId, Byte isVip) throws IOException;

    public void deleteBook(Integer bookId);

    public List<Book> getBooksByType(int bookTypeId);
    List<ChapterVO> getBookTOC(Integer bookId);

    BookInfoDTO getBook(Integer bookId);

    void updateBook(BookInfoDTO bookInfoDTO);

//    public BigDecimal getTotalRevenue() {
//        return dashboardMapper.calculateTotalRevenue();
//    }

//    public List<Map<String, Object>> getTopReadBooks() {
//        return dashboardMapper.getTopReadBooks();
//    }
}
