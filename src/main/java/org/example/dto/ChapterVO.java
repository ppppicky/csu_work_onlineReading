package org.example.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "章节目录信息")
@Data
public class ChapterVO {
    @ApiModelProperty(value = "章节 ID", example = "1")
    private Integer chapterId;  // 章节ID
    @ApiModelProperty(value = "章节标题", example = "第一章")
    private String chapterName;  // 章节名称

    public ChapterVO(int chapterId, String chapterName) {
        this.setChapterId(chapterId);
        this.setChapterName(chapterName);
    }
}
