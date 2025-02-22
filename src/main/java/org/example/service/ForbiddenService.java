package org.example.service;

import org.example.entity.ForbiddenWord;

public interface ForbiddenService {
     ForbiddenWord addWord(String word);

     void removeWord(String id);

     void manualRefresh();
}