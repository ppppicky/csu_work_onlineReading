package org.example.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.ForbiddenWord;
import org.example.repository.ForbiddenWordRepo;
import org.example.service.ForbiddenService;
import org.example.util.ContentFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ForbiddenSImpl implements ForbiddenService {

    @Autowired
    ForbiddenWordRepo forbiddenWordRepo;

    @Autowired
    ContentFilter contentFilter;

    /**
     * 添加违禁词
     *
     * @param word
     * @return
     */
    public ForbiddenWord addWord(String word) {

        ForbiddenWord forbiddenWord = new ForbiddenWord();
        forbiddenWord.setWord(word);
        ForbiddenWord saved = forbiddenWordRepo.save(forbiddenWord);

        // 直接更新内存数据，避免重新查询数据库
        List<String> updatedDbWords = new CopyOnWriteArrayList<>(contentFilter.dbWords.get());
        updatedDbWords.add(word);
        contentFilter.dbWords.set(updatedDbWords);
        contentFilter.dbRegexPattern.set(updatedDbWords.stream()
                .map(Pattern::quote)
                .collect(Collectors.joining("|")));

        log.info("新增禁用词: {}", word);
        return saved;
    }

    public void removeWord(String word) {
        try {
            forbiddenWordRepo.findByWord(word).ifPresent(forbiddenWord -> {
                forbiddenWordRepo.delete(forbiddenWord);

                // 更新内存数据
                // 创建一个CopyOnWriteArrayList，其内容为dbWords当前存储的内容
                List<String> updatedDbWords = new CopyOnWriteArrayList<>(contentFilter.dbWords.get());
                // 从更新后的列表中移除指定的违禁词
                updatedDbWords.remove(word);
                // 将更新后的列表设置回dbWords中
                contentFilter.dbWords.set(updatedDbWords);
                contentFilter.dbRegexPattern.set(updatedDbWords.stream()
                        .map(Pattern::quote)
                        .collect(Collectors.joining("|")));

                log.info("删除禁用词: {}", word);
            });
        } catch (Exception e) {
            log.error("删除禁用词失败: {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }


    public void manualRefresh() {
        contentFilter.loadDbWords();
    }
}
