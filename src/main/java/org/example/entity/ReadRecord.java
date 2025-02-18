package org.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ReadRecord")
@Data
public class ReadRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer readId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "bookId", nullable = false)
    private Book book;

    private LocalDateTime lastReadTime;

    private Integer lastReadPage;

    public ReadRecord(Users user, Book book) {
        this.book=book;
        this.user=user;
    }
    public ReadRecord() {
    }

}
