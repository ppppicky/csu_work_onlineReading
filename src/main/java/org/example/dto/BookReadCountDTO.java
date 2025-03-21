package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookReadCountDTO {
    private String bookName;
    private long readCount;
}
