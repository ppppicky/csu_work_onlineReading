package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookReadingStatsDTO {
    private long totalReads; // 总阅读量
    private List<BookReadCountDTO> topReadBooks; // 阅读量最高的书籍
}
