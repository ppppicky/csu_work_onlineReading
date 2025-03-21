package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.ForbiddenWord;
import org.example.repository.ForbiddenWordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Component
public class ContentFilter {
    @Autowired
    private ForbiddenWordRepo forbiddenWordRepo;


    // 存储文件中的禁用词
    private final CopyOnWriteArrayList<String> fileWords = new CopyOnWriteArrayList<>();
    public final AtomicReference<List<String>> dbWords = new AtomicReference<>(new CopyOnWriteArrayList<>());

    private String fileRegexPattern = "";
    public final AtomicReference<String> dbRegexPattern = new AtomicReference<>("");


    // 在Bean初始化完成后执行该方法，用于初始化禁用词库
    @PostConstruct
    public void init() {
        loadFileWords();
        loadDbWords();
    }

    // 定时任务，每分钟执行一次，用于重新加载数据库中的禁用词库
    @Scheduled(fixedRate = 60000) // 10 分钟执行一次
    public void scheduledReloadDbWords() {
        loadDbWords();
    }

    /**
     * 从文件中加载禁用词
     */
    private void loadFileWords() {
        List<String> words = new ArrayList<>();
        Path dirPath = Paths.get("src/main/resources/sensitive");

        try (Stream<Path> paths = Files.walk(dirPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .forEach(path -> {
                        try {
                            List<String> lines = Files.readAllLines(path);
                            words.addAll(lines.stream().map(String::trim).filter(line -> !line.isEmpty()).collect(Collectors.toList()));
                        } catch (IOException e) {
                            log.error("读取本地敏感词文件失败: {}", path, e);
                        }
                    });
        } catch (IOException e) {
            log.error("无法扫描本地敏感词文件夹", e);
        }

        fileWords.addAll(words);
        fileRegexPattern = fileWords.isEmpty() ? "" :
                fileWords.stream().map(Pattern::quote).collect(Collectors.joining("|"));

        log.info("已加载本地敏感词，总数: {}", words.size());
    }

    /**
     * 从数据库中加载禁用词
     */
    public void loadDbWords() {
        List<String> newDbWords = forbiddenWordRepo.findAll()
                .stream()
                .map(ForbiddenWord::getWord)
                .collect(Collectors.toList());

        String newDbRegexPattern = newDbWords.isEmpty() ? ""
                : newDbWords.stream().map(Pattern::quote).collect(Collectors.joining("|"));

        // 仅当新数据与旧数据不同时，才进行更新，减少不必要的赋值操作
        if (!newDbWords.equals(dbWords) || !newDbRegexPattern.equals(dbRegexPattern)) {
            synchronized (this) {
                dbWords.set(new CopyOnWriteArrayList<>(newDbWords));
                dbRegexPattern.set(newDbRegexPattern);
                log.info("数据库禁用词库已更新，总数：{}", newDbWords.size());
            }
        } else {
            log.debug("数据库禁用词库无变化，无需更新");
        }
    }


    /**
     * 对输入的内容进行过滤，替换其中的禁用词为 ***
     *
     * @param content 待过滤的内容
     * @return 过滤后的内容
     */
    public String filterFromDB(String content) {
        if (content == null || dbRegexPattern.get().isEmpty()) {
            return content;
        }
        return content = content.replaceAll(dbRegexPattern.get(), "***");
    }

    public String filterFromFile(String content) {
        if (content == null) return null;
        String cleaned = cleanGarbledText(content);
        if (!fileRegexPattern.isEmpty()) {
            cleaned = cleaned.replaceAll(fileRegexPattern, "***");
        }
        return cleaned;
    }


    /**
     * 清理内容中的乱码，去除控制字符、非基本多文种平面字符和其他非法字符
     *
     * @param text 待清理的文本
     * @return 清理后的文本
     */
    private String cleanGarbledText(String text) {
        return text.replaceAll("\\p{Cntrl}", "")
                .replaceAll("[^\\u0000-\\uFFFF]", "")
                .replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9\\p{P}\\s]", "");
    }
}