package org.example.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderQueryDTO {
    private Integer userId; // 必须传递的用户ID
    private String name; // 订单名称（可选）

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime; // 开始时间（可选）

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime; // 结束时间（可选）

    private String state; // 订单状态（可选）
    private Integer bookId; // 书籍ID（可选）
}
