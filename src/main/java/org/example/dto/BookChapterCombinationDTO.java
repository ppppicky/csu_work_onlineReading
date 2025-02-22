package org.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookChapterCombinationDTO {
    private BookInfoDTO bookInfo;

    private List<ChapterDTO> chapters;

    private String tempEpubKey; // 用于后续提交时标识临时文件
}