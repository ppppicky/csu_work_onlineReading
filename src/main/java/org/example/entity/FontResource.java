package org.example.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class FontResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fontId;

    private String fontName;

    @Column(nullable = false)
    private String storagePath;

}
