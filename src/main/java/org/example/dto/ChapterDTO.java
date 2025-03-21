package org.example.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChapterDTO {


    private Integer chapterId;

    private String chapterName;

    private String content;

    public ChapterDTO(int chapterId, String chapterName) {
        this.setChapterId(chapterId);
        this.setChapterName(chapterName);
    }

    public ChapterDTO(int chapterId, String chapterName,String content) {
        this.setChapterId(chapterId);
        this.setChapterName(chapterName);
        this.setContent(content);
    }
    public ChapterDTO(){}
}
