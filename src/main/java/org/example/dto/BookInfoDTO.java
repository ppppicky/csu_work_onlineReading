package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.entity.BookType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
public class BookInfoDTO {
    private Integer bookId;

    private String bookName;

    private String author;

    private String bookCover;

    private String bookDesc;

    private Integer bookPage;//chapter_cnt

    private Byte isCharge;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private BookType bookType;

}
