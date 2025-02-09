package org.example.dto;

import lombok.Data;

@Data
public class ProgressDto {
    private String userId;
    private Integer bookId;
    private Integer chapterId;
    private Integer pageIndex;
}


