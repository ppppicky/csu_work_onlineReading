package org.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedContent {
    private List<String> pages;  // 分页后的内容列表（每页文本）
    private int totalPages;      // 总页数

    public PaginatedContent(List<String> pages, int totalPages) {
        this.pages = pages;
        this.totalPages = totalPages;
    }
}
