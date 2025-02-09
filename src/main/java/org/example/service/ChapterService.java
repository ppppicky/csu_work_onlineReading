package org.example.service;

import org.example.dto.PaginatedContent;
import org.springframework.stereotype.Service;

@Service
public interface ChapterService {


    PaginatedContent getChapterContent(Integer chapterId, int pageSize);
     String getChapterContent(Integer chapterId);
}
