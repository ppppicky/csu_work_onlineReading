package org.example.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
public class ForbiddenWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String word;

    private String replacement = "***";
}