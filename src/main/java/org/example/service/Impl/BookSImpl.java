package org.example.service.Impl;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.epub.EpubReader;
import org.example.dto.BookInfoDTO;
import org.example.dto.ChapterDTO;
import org.example.dto.CoverTempDTO;
import org.example.entity.*;
import org.example.mapper.BookMapper;
import org.example.repository.ChapterRepo;
import org.example.repository.BookRepository;
import org.example.repository.BookTypeRepository;
import org.example.service.BookService;
import org.example.service.CoverTempService;
import org.example.util.EpubDealer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookSImpl implements BookService {

    @Autowired
    BookRepository bookRepository;
    @Autowired
    BookTypeRepository bookTypeRepository;
    @Autowired
    ChapterRepo chapterRepo;
    @Autowired
    BookMapper bookMapper;

    @Autowired
    CoverTempService coverTempService;
    @Autowired
     EpubDealer epubDealer;
    private final String STATICDIR = System.getProperty("user.dir") + "/src/main/resources/static";

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
    public Book addBook(File bookFile, String typeName, Byte isVip) throws IOException {
        BookType bookType;

      try {
          bookType = bookTypeRepository.findByBookTypeName(typeName);
      }catch (Exception e){
         throw new IllegalArgumentException("无效书籍类型"+typeName);
      }
        nl.siegmann.epublib.domain.Book epubBook = new EpubReader().readEpub(new FileInputStream(bookFile));
        Resources resources = epubBook.getResources();
        Metadata metadata = epubBook.getMetadata();
        String bookName = epubBook.getTitle();
        if (bookRepository.findByBookName(bookName)!=null) {
            throw new RuntimeException("book existed");
        }
        String author = metadata.getAuthors().isEmpty() ? "Unknown" : String.valueOf(metadata.getAuthors().get(0));
//考虑用string还是author类
        String bookCover = null;
        for (Resource resource : resources.getAll()) {
            if (resource.getHref().contains("cover")) {
                byte[] bytes= resource.getData();
              bookCover=  epubDealer.saveCoverImage(bytes);
                break;
            }
        }

        Book newBook = new Book();
        newBook.setBookCover(bookCover);
        newBook.setAuthor(author);
        newBook.setBookName(bookName);
        newBook.setEpubFile(Files.readAllBytes(bookFile.toPath()));
        newBook.setIsCharge(isVip);
        newBook.setBookDesc(epubDealer.extractBookDescription(epubBook));
        newBook.setBookPage(epubDealer.countChapters(epubBook));
        newBook.setBookType(bookType);
        newBook.setCreateTime(epubDealer.extractBookCreationDate(epubBook));
        newBook.setUpdateTime(LocalDateTime.now());
        bookRepository.save(newBook);
        try (InputStream is =new FileInputStream(bookFile) ) {
            List<BookChapter> chapters = epubDealer.parseChapters(newBook.getBookId(), is);
            chapterRepo.saveAll(chapters);
        }
        return newBook;
    }

    @Override
    @Transactional
    public void deleteBook(Integer bookId) {
        Book book= bookRepository.findById(bookId).get();
        try {
            chapterRepo.deleteByBookId(bookId);
            bookRepository.delete(book);
        }catch (Exception e){
            log.info("-------"+e.getLocalizedMessage());
        }
    }

    /**
     *
     * @param bookTypeId
     * @return
     */
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


    /**
     * 获取章节目录
     * @param bookId
     * @return
     */
    @Override
    public List<ChapterDTO> getBookTOC(Integer bookId) {
        return chapterRepo.findByBookId(bookId).stream()
                .map(chap -> new ChapterDTO(chap.getChapterId(), chap.getChapterName()))  // 将章节实体转为ChapterVO
                .collect(Collectors.toList());
    }

    @Override
    public BookInfoDTO getBook(Integer bookId) {
       Book book= bookRepository.findById(bookId)
               .orElseThrow(()->new IllegalArgumentException("book not existed"));
       BookInfoDTO bookInfoDTO=new BookInfoDTO();
       bookInfoDTO.setBookCover(book.getBookCover());
       bookInfoDTO.setBookDesc(book.getBookDesc());
       bookInfoDTO.setBookPage(book.getBookPage());
       bookInfoDTO.setAuthor(book.getAuthor());
       bookInfoDTO.setBookId(bookId);
       bookInfoDTO.setIsCharge(book.getIsCharge());
       bookInfoDTO.setBookType(book.getBookType());
       bookInfoDTO.setBookName(book.getBookName());
       return bookInfoDTO;
    }

    @Override
    public void updateBook(BookInfoDTO bookInfoDTO) {
        Book book= bookRepository.findById(bookInfoDTO.getBookId())
                .orElseThrow(()->new IllegalArgumentException("book not existed"));
        book.setBookType(bookInfoDTO.getBookType());
        book.setBookPage(bookInfoDTO.getBookPage());
        book.setBookCover(bookInfoDTO.getBookCover());
        book.setBookName(bookInfoDTO.getBookName());
        book.setAuthor(bookInfoDTO.getAuthor());
        book.setBookDesc(bookInfoDTO.getBookDesc());
        book.setIsCharge(bookInfoDTO.getIsCharge());
        book.setUpdateTime(LocalDateTime.now());
        bookRepository.save(book);
    }

    @Override
    public Book createBook(BookInfoDTO bookInfoDTO) throws IOException {
        Book book=new Book();
        if (bookInfoDTO.getBookCover() != null) {
            CoverTempDTO tempDTO = coverTempService.getTempCover(bookInfoDTO.getBookCover());
            book.setBookCover(tempDTO.getPreviewUrl());
         //   log.info("dddddd"+finalPath);
        }

        coverTempService.deleteTempCover(bookInfoDTO.getBookCover()); // 确认保存后删除 Redis 记录

        book.setBookType(bookInfoDTO.getBookType());
        book.setBookPage(bookInfoDTO.getBookPage());

        book.setBookName(bookInfoDTO.getBookName());
        book.setAuthor(bookInfoDTO.getAuthor());
        book.setBookDesc(bookInfoDTO.getBookDesc());
        book.setIsCharge(bookInfoDTO.getIsCharge());
        book.setUpdateTime(LocalDateTime.now());
        book.setCreateTime(LocalDateTime.now());
        bookRepository.save(book);
        return book;
    }

}