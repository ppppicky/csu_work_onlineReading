package org.example.util;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.huaban.analysis.jieba.JiebaSegmenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KeywordNLPExtractor {
    @Value("${keywords.totals}")
    private int totals;

    // 停用词列表（可以从外部文件加载）
    private static final List<String> STOP_WORDS = Arrays.asList("的", "是", "了", "和", "或", "在", "与", "及", "我们", "你们");

    // 需要排除的词性
    private static final List<String> EXCLUDED_POS_TAGS = Arrays.asList(
            "nr",  // 人名
            "m",   // 数词（包含序数词）
            "f",    // 方位词
            "mq",
            "p"
    );

    public List<String> extractKeywords(String text) {
      //  log.info("nlp         " + text);
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<Term> terms = StandardTokenizer.segment(text);

        // 过滤停用词、不合适的词性
        Map<String, Integer> wordCount = new HashMap<>();

        for (Term term : terms) {
            String word = term.word;
            String posTag = term.nature.toString(); // HanLP词性
            // 过滤规则：
            if (word.length() <= 1) continue;  // 过滤单字
            if (STOP_WORDS.contains(word)) continue;  // 过滤停用词
            if (EXCLUDED_POS_TAGS.contains(posTag)) continue;  // 过滤特定词性

            // 计算词频
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
        List<String> ans = wordCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue())) // 按词频降序排序
                .limit(Math.min(totals, wordCount.size()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return ans;
    }
}
