package org.example.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.ForbiddenWord;
import org.example.repository.ForbiddenWordRepo;
import org.example.service.ForbiddenService;
import org.example.util.ContentFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ForbiddenSImpl implements ForbiddenService {

    @Autowired
    ForbiddenWordRepo forbiddenWordRepo;

    @Autowired
    ContentFilter contentFilter;

    public ForbiddenWord addWord(String word) {
        ForbiddenWord forbiddenWord = new ForbiddenWord();

        forbiddenWord.setWord(word);
        ForbiddenWord saved = forbiddenWordRepo.save(forbiddenWord);
        contentFilter.reloadForbiddenWords(); // 立即刷新
        return saved;

    }


    public void removeWord(String word) {
        try {
            log.info(word);
            forbiddenWordRepo.delete(forbiddenWordRepo.findByWord(word).get());
            contentFilter.reloadForbiddenWords();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }


    public void manualRefresh() {
        contentFilter.reloadForbiddenWords();
    }
}
