package org.example.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ChapterDTO;
import org.example.dto.PaginatedContent;
import org.example.entity.Book;
import org.example.entity.BookChapter;
import org.example.repository.BookRepository;
import org.example.repository.ChapterRepo;
import org.example.service.ChapterService;
import org.example.util.ContentFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChapterSImpl implements ChapterService {
    @Autowired
    ChapterRepo chapterRepo;

    @Autowired
    BookRepository bookRepository;
    @Autowired
    ContentFilter contentFilter;

    /**
     * 获取指定章节的内容、进行分页处理
     *
     * @param chapterId
     * @param pageSize
     * @return 返回分页内容和总页数
     */
    @Override
    public PaginatedContent getChapterContent(Integer chapterId, int pageSize) {
        BookChapter chapter = chapterRepo.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("章节不存在"));
        List<String> pages = splitContentToPages(chapter.getContent(), pageSize);
        return new PaginatedContent(pages, pages.size());
    }

    /**
     * 获取章节内容
     *
     * @param chapterId
     * @return
     */
    @Override
    public String getChapterContent(Integer chapterId) {
        BookChapter chapter = chapterRepo.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("章节不存在"));
        return chapter.getContent();
    }

    /**
     * 更新章节内容
     *
     * @param chapterId
     * @param newContent
     */
    @Override
    public void updateChapterContent(Integer chapterId, String newContent) {
        String filteredContent = contentFilter.filter(newContent);
        BookChapter chapter = chapterRepo.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("chapter not existed"));
        chapter.setContent(filteredContent);
        chapter.setUpdateTime(LocalDateTime.now());
        Book book = bookRepository.findById(chapter.getBookId()).get();
        book.setUpdateTime(LocalDateTime.now());
        bookRepository.save(book);
        chapterRepo.save(chapter);
    }

    /**
     * 更新章节名
     *
     * @param chapterId
     * @param newName
     */
    @Override
    public void updateChapterName(Integer chapterId, String newName) {
        String filteredContent = contentFilter.filter(newName);
        BookChapter chapter = chapterRepo.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("chapter not existed"));
        chapter.setChapterName(filteredContent);
        chapter.setUpdateTime(LocalDateTime.now());
        Book book = bookRepository.findById(chapter.getBookId()).get();
        book.setUpdateTime(LocalDateTime.now());
        bookRepository.save(book);
        chapterRepo.save(chapter);
    }

    /**
     * 新建章节
     *
     * @param bookId
     * @param chapterDTO
     */
    @Override
    public void createChapter(Integer bookId, ChapterDTO chapterDTO) {
        if (chapterRepo.findByBookIdAndChapterNum(bookId, chapterDTO.getChapterId()).isPresent()) {
            throw new IllegalArgumentException("chapter already exists");
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("book not existed"));
        BookChapter bookChapter = new BookChapter();
        bookChapter.setBookId(book.getBookId());
        bookChapter.setChapterName(chapterDTO.getChapterName());
        bookChapter.setChapterNum(chapterDTO.getChapterId());
        bookChapter.setContent(contentFilter.filter(chapterDTO.getContent()));
        bookChapter.setCreateTime(LocalDateTime.now());
        bookChapter.setUpdateTime(LocalDateTime.now());
        chapterRepo.save(bookChapter);
    }

    /**
     * 根据页大小分割章节
     *
     * @param content
     * @param pageSize
     * @return 每页内容
     */
    private List<String> splitContentToPages(String content, int pageSize) {
        List<String> pages = new ArrayList<>();  // 用于存储分页后的内容
        int length = content.length();  // 获取内容的长度

        for (int i = 0; i < length; i += pageSize) {
            pages.add(content.substring(i, Math.min(i + pageSize, length)));  // 截取并添加每一页的内容
        }
        return pages;  // 返回分页后的内容列表
    }
}
