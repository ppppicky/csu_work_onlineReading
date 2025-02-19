package org.example.util;


import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubReader;
import org.example.dto.ChapterDTO;
import org.example.entity.BookChapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Component
public class EpubDealer {
    @Autowired
    ContentFilter contentFilter;

//    /**
//     * 保存封面图片到本地
//     *
//     * @param imageData
//     * @return
//     * @throws IOException
//     */
//    public String saveCoverImage(byte[] imageData) throws IOException {
//        if (imageData == null || imageData.length == 0) {
//            log.info("封面图片数据不能为空");
//            return null;
//        }
//        String baseDir = System.getProperty("user.dir") + "/src/main/resources/static/covers/";
//        // 生成唯一的封面图片名称
//        String fileName = UUID.randomUUID() + ".jpg";
//        Files.write(Paths.get(baseDir, fileName), imageData);
//        return "/covers/" + fileName;//返回相对路径（供前端访问）
//    }

    /**
     * 统计章节数
     *
     * @param epubBook
     * @return
     */
    public int countChapters(Book epubBook) {
        return countChaptersRecursive(epubBook.getTableOfContents().getTocReferences());
    }

    private int countChaptersRecursive(List<TOCReference> tocRefs) {
        int count = 0;
        for (TOCReference ref : tocRefs) {
            count += 1 + countChaptersRecursive(ref.getChildren());
        }
        return count;
    }


    /**
     * 解析章节内容
     *
     * @param bookId
     * @param epubStream
     * @return
     * @throws IOException
     */
    public List<ChapterDTO> parseChapters(Integer bookId, InputStream epubStream) throws IOException {
        Book epubBook = new EpubReader().readEpub(epubStream);
        List<ChapterDTO> chapters = new ArrayList<>();
        int chapterNum = 1;

        TableOfContents toc = epubBook.getTableOfContents();

        for (TOCReference ref : toc.getTocReferences()) {
            processChapterRecursive(epubBook, ref, bookId, chapters, chapterNum++);
        }
        return chapters;
    }

    private void processChapterRecursive(Book epubBook, TOCReference ref, Integer bookId, List<ChapterDTO> chapters, int chapterNum) {
        // 获取章节对应的 Resource
        Resource res = epubBook.getResources().getByHref(ref.getCompleteHref());
        if (res == null) return;

        // 创建 Chapter 对象
        ChapterDTO chapter = new ChapterDTO();
        chapter.setChapterId(chapterNum);
        chapter.setChapterName(extractChapterName(epubBook, res));
        chapter.setContent(extractChapterContent(res));
        // 添加到章节列表
        chapters.add(chapter);

        // 递归处理子章节
//        for (TOCReference child : ref.getChildren()) {
//            processChapterRecursive(epubBook,child, bookId, chapters,chapterNum);
//        }
    }

    private String extractChapterName(Book epubBook, Resource res) {
        // 尝试从 EPUB 目录匹配章节名
        Optional<String> tocTitle = findChapterTitleFromTOC(epubBook, res);
        if (tocTitle.isPresent()) {
            return tocTitle.get();
        }

        // 如果 TOC 查找失败，解析 HTML <h1> 或 <title>
        String htmlTitle = extractTitleFromHtml(res);
        if (!htmlTitle.isEmpty()) {
            return htmlTitle;
        }

        // 仍然找不到，返回默认值
        System.out.println("无法获取章节名: " + res.getHref());
        return "Unknown Chapter";
    }

    /**
     * @param epubBook
     * @param res
     * @return
     */
    private Optional<String> findChapterTitleFromTOC(Book epubBook, Resource res) {
        TableOfContents toc = epubBook.getTableOfContents();
        return findChapterInTOC(toc.getTocReferences(), res.getHref());
    }

    /**
     * @param references
     * @param href
     * @return
     */
    private Optional<String> findChapterInTOC(List<TOCReference> references, String href) {
        for (TOCReference ref : references) {
            if (ref.getCompleteHref().contains(href)) {
                return Optional.of(ref.getTitle().trim());
            }
            // 递归检查子章节
            Optional<String> childTitle = findChapterInTOC(ref.getChildren(), href);
            if (childTitle.isPresent()) {
                return childTitle;
            }
        }
        return Optional.empty();
    }

    /**
     * @param res
     * @return
     */
    private String extractTitleFromHtml(Resource res) {
        try {
            String htmlContent = new String(res.getData(), StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(htmlContent);

            // **优先查找 h1 标签**
            Element h1 = doc.selectFirst("h1");
            if (h1 != null) {
                return h1.text().trim();
            }

            // **备用：获取 title**
            Element title = doc.selectFirst("title");
            if (title != null) {
                return title.text().trim();
            }
        } catch (Exception e) {
            System.out.println("HTML解析失败: " + e.getMessage());
        }
        return "";
    }

    /**
     * @param res
     * @return
     */
    private String extractChapterContent(Resource res) {
        try {
            String htmlContent = new String(res.getData(), StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(htmlContent);

            //去除script和style标签
            doc.select("script, style").remove();

            //获取正文内容
            Elements paragraphs = doc.select("p");
            StringBuilder content = new StringBuilder();
            for (Element p : paragraphs) {
                content.append(p.text()).append("\n\n");
            }

            String rawContent = content.toString().trim();
            return contentFilter.filter(rawContent);
        } catch (Exception e) {
            System.out.println("解析章节内容失败: " + e.getLocalizedMessage());
            return "";
        }
    }

    public String extractBookDescription(Book epubBook) {
        // 1. **尝试从元数据中获取描述**
        String description = epubBook.getMetadata().getDescriptions().stream()
                .findFirst()
                .orElse("");

        if (!description.isEmpty()) {
            log.info("从元数据中找到简介: {}", description);
            return description;
        }

        // 2. **如果元数据中没有描述，尝试从正文内容中提取**
        for (Resource resource : epubBook.getResources().getAll()) {
            if (resource.getMediaType().toString().contains("html")) {
                try {
                    String content = new String(resource.getData(), StandardCharsets.UTF_8);
                    Document doc = Jsoup.parse(content);

                    // **查找 p 标签中的第一段内容**
                    Element firstParagraph = doc.selectFirst("p");
                    if (firstParagraph != null) {
                        String text = firstParagraph.text().trim();
                        if (!text.isEmpty() && text.length() > 50) { // 只选取长度足够的文本
                            log.info("从正文中找到简介: {}", text);
                            return text;
                        }
                    }
                } catch (Exception e) {
                    log.warn("解析 HTML 获取简介失败: {}", e.getMessage());
                }
            }
        }
        log.warn("未找到书籍简介");
        return "暂无简介";
    }

    public LocalDateTime extractBookCreationDate(Book epubBook) {
        List<Date> dates = epubBook.getMetadata().getDates();
        if (dates.isEmpty()) {
            return LocalDateTime.now();
        }

        LocalDateTime creationDate = convertEpubDateToLocalDateTime(dates.get(0));
        return creationDate != null ? creationDate : LocalDateTime.now();
    }

    LocalDateTime convertEpubDateToLocalDateTime(Date epubDate) {
        if (epubDate == null || epubDate.getValue() == null || epubDate.getValue().trim().isEmpty()) {
            return null;
        }

        String dateStr = epubDate.getValue().trim();
        LocalDateTime dateTime = null;

        // 尝试多种格式进行解析
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ISO_DATE_TIME,       // 2023-06-15T14:30:00Z
                DateTimeFormatter.ISO_LOCAL_DATE,      // 2023-06-15
                DateTimeFormatter.ofPattern("yyyy-MM"), // 2023-06
                DateTimeFormatter.ofPattern("yyyy")    // 2023
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                dateTime = LocalDateTime.parse(dateStr, formatter);
                break;
            } catch (Exception ignored) {
            }
        }

        // 处理只有年份的情况
        if (dateTime == null && dateStr.matches("\\d{4}")) {
            dateTime = LocalDateTime.of(Integer.parseInt(dateStr), 1, 1, 0, 0);
        }

        return dateTime;
    }

}
