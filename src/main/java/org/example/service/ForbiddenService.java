package org.example.service;

import org.example.entity.ForbiddenWord;

public interface ForbiddenService {
    public ForbiddenWord addWord(String word);

    public void removeWord(String id);

    public void manualRefresh();
}