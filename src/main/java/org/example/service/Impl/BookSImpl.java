package org.example.service.Impl;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.epub.EpubReader;
import org.example.entity.Book;
import org.example.mapper.BookMapper;
import org.example.repository.BookRepository;
import org.example.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookSImpl implements BookService {

    BookRepository bookRepository;
    BookMapper bookMapper;

    @Autowired
    public BookSImpl(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public Page<Book> getAllBook(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAll(pageable);
    }

    @Transactional
    public Book addBook(File bookFile, int typeId) throws IOException {
        nl.siegmann.epublib.domain.Book epubBook = new EpubReader().readEpub(new FileInputStream(bookFile));
        Resources resources = epubBook.getResources();
        Metadata metadata = epubBook.getMetadata();
        String bookName = epubBook.getTitle();
        if (bookRepository.findByBookName(bookName)) {
            throw new RuntimeException("book existed");
        }
        String author = metadata.getAuthors().isEmpty() ? "Unknown" : String.valueOf(metadata.getAuthors().get(0));
//考虑用string还是author类
        String bookCover = null;
        for (Resource resource : resources.getAll()) {
            if (resource.getHref().contains("cover")) {
                bookCover = saveCoverImage(resource.getData());
                break;
            }
        }
//        int pageCount = 0; // 获取页数的逻辑可以依赖于 EPUB 文件的具体结构
//        if (epubBook.getContents() != null) {
//            pageCount = epubBook.getContents().size();
//        }
        Book newBook = new Book();
        newBook.setBookCover(bookCover);
        //  newBook.setBookPage(pageCount);
        newBook.setAuthor(author);
        newBook.setBookName(bookName);
        newBook.setEpubFile(Files.readAllBytes(bookFile.toPath()));
        newBook.setIsCharge((byte) 0);
        newBook.setCreateTime(LocalDateTime.now());
        newBook.setBookTypeId(typeId);//待完善------
        return bookRepository.save(newBook);
    }

    public String saveCoverImage(byte[] imageData) throws IOException {
        String baseDir = System.getProperty("user.dir") + "/src/main/resources/static/covers/";
        // 2. 生成唯一的封面图片名称
        String fileName = UUID.randomUUID().toString() + ".jpg";
        Files.write(Paths.get(baseDir, fileName), imageData);
        return "/covers/" + fileName;//返回相对路径（供前端访问）
    }

    @Override
    public void DeleteBook(Integer bookId) {
        bookMapper.deleteBook(bookId);
    }

    @Override
    public List<Book> getBooksByType(int bookTypeId) {
        return bookMapper.getBooksByCategory(bookTypeId).stream().map(map -> {
            Book book = new Book();
            book.setBookCover((String) map.get("BookCover"));
            book.setAuthor((String) map.get("Author"));
            book.setBookId((Integer) map.get("BookId"));
            book.setBookName((String) map.get("BookName"));
            return book;
        }).collect(Collectors.toList());
    }
}