// UserBehaviorLog.java
package org.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class UserBehaviorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int logId;

    private int userId;
    private String action; // e.g., "login", "search", "purchase"
    private LocalDateTime actionTime;
    private String details; // e.g., search keywords, purchase details
}