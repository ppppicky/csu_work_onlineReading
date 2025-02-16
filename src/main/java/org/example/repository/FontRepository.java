package org.example.repository;

import org.example.entity.FontResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FontRepository extends JpaRepository<FontResource,Integer> {
     FontResource findByFontName(String fontName);

}
