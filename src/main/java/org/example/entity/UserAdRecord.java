package org.example.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
public class UserAdRecord implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recordId;
    private Integer userId;
    private String advertId;
    private LocalDateTime watchTime;

    }
    //  private Integer remainingViews; // 当日剩余观看次数（冗余字段，便于快速查询）



