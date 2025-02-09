package org.example.dto;

import lombok.Data;

@Data
public class ChapterVO {
    private Integer chapterId;  // 章节ID
    private String chapterName;  // 章节名称

    public ChapterVO(int chapterId, String chapterName) {
        this.setChapterId(chapterId);
        this.setChapterName(chapterName);
    }
}
