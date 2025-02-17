package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.ForbiddenWord;
import org.example.repository.ForbiddenWordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ContentFilter {
    @Autowired
    ForbiddenWordRepo forbiddenWordRepo;

    private List<String> words = new ArrayList<>();

    @PostConstruct
    public void init() {
        System.out.println("ContentFilter instance: " + this);
        reloadForbiddenWords();
    }

    public void reloadForbiddenWords() {

        words = forbiddenWordRepo.findAll().stream()
                .map(ForbiddenWord::getWord)
                .collect(Collectors.toList());
        log.info(words.toString());

    }

    public String filter(String content) {
        if (content == null) {
            return null;
        }
        String cleaned = cleanGarbledText(content);
        for (String str : words) {
            cleaned = cleaned.replaceAll(str, "***");
        }
        return cleaned;
    }

    // 改进后的乱码过滤方法
    private String cleanGarbledText(String text) {
        // 1. 移除控制字符
        String cleaned = text.replaceAll("\\p{Cntrl}", "");

        // 2. 移除四字节UTF-8字符（很多乱码来自这些字符）
        cleaned = cleaned.replaceAll("[^\\u0000-\\uFFFF]", "");

        // 3. 移除非常用符号
        cleaned = cleaned.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9\\p{P}\\s]", "");

        return cleaned;
    }
}
