package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookReadCountDTO {
    private String bookName; // 书籍名称
    private long readCount; // 阅读次数
}
