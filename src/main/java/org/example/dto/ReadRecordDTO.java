package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
public class ReadRecordDTO {
    @NotNull(message = "userId 不能为空")
    private Integer userId;

    @NotNull(message = "bookId 不能为空")
    private Integer bookId;

    @PositiveOrZero(message = "页码不能为负数")
    private Integer lastReadPage;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastReadTime;
}