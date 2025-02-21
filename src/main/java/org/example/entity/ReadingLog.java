package org.example.entity;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class ReadingLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int logId;

    private int userId;
    private int bookId;
    private int chapterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int duration; // in seconds
}