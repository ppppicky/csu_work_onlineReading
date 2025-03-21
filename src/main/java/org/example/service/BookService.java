package org.example.service;

import org.example.dto.BookInfoDTO;
import org.example.dto.BookChapterCombinationDTO;
import org.example.dto.ChapterDTO;
import org.example.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface BookService {


    Page<Book> getAllBook(int page, int size);

    // public Book addBook(File bookFile, String typeId, Byte isVip) throws IOException;

    void deleteBook(Integer bookId);

    Page<BookInfoDTO> getBooksByType(Pageable pageable, String bookTypeId);

    Page<BookInfoDTO> getBooksList(String keyword, Pageable pageable);

    List<ChapterDTO> getBookTOC(Integer bookId);

    BookInfoDTO getBook(Integer bookId);

    void updateBook(BookInfoDTO bookInfoDTO);

    void createBook(BookChapterCombinationDTO bookInfoDTO) throws IOException;

    @Async("ioThreadPool")
        // 使用自定义线程池
    CompletableFuture<BookChapterCombinationDTO> parseEpubAsync(File bookFile);

    BookChapterCombinationDTO parseEpub(File tempFile) throws FileNotFoundException, IOException;

}
