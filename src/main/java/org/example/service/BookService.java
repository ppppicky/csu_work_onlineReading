package org.example.service;

import org.example.dto.BookInfoDTO;
import org.example.dto.BookChapterCombinationDTO;
import org.example.dto.ChapterDTO;
import org.example.entity.Book;
import org.springframework.data.domain.Page;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


public interface BookService {


    public Page<Book> getAllBook(int page, int size);

   // public Book addBook(File bookFile, String typeId, Byte isVip) throws IOException;

    public void deleteBook(Integer bookId);

   // public List<Book> getBooksByType(int bookTypeId);
    List<ChapterDTO> getBookTOC(Integer bookId);

    BookInfoDTO getBook(Integer bookId);

    void updateBook(BookInfoDTO bookInfoDTO);

    void createBook(BookChapterCombinationDTO bookInfoDTO) throws IOException;

    BookChapterCombinationDTO parseEpub(File tempFile) throws FileNotFoundException, IOException;

//    public BigDecimal getTotalRevenue() {
//        return dashboardMapper.calculateTotalRevenue();
//    }

//    public List<Map<String, Object>> getTopReadBooks() {
//        return dashboardMapper.getTopReadBooks();
//    }
}
