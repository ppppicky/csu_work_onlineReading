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

    public ForbiddenWord addWord(String word) {
//        ForbiddenWord forbiddenWord = new ForbiddenWord();
//
//        forbiddenWord.setWord(word);
//        ForbiddenWord saved = forbiddenWordRepo.save(forbiddenWord);
//        contentFilter.loadDbWords(); // 立即刷新
//        return saved;
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
                List<String> updatedDbWords = new CopyOnWriteArrayList<>(contentFilter.dbWords.get());
                updatedDbWords.remove(word);
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
//        try {
//            log.info(word);
//            forbiddenWordRepo.delete(forbiddenWordRepo.findByWord(word).get());
//            contentFilter.loadDbWords();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e.getLocalizedMessage());
//        }
    }


    public void manualRefresh() {
        contentFilter.loadDbWords();
    }
}
