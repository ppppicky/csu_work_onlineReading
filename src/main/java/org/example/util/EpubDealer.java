package org.example.util;


import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.EpubReader;
import org.example.entity.BookChapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
public class EpubDealer {
    /**
     * 保存封面图片到本地
     *
     * @param imageData
     * @return
     * @throws IOException
     */
    public String saveCoverImage(byte[] imageData) throws IOException {
        if (imageData == null || imageData.length == 0) {
            log.info("封面图片数据不能为空");
            return null;
        }
        String baseDir = System.getProperty("user.dir") + "/src/main/resources/static/covers/";
        // 生成唯一的封面图片名称
        String fileName = UUID.randomUUID() + ".jpg";
        Files.write(Paths.get(baseDir, fileName), imageData);
        return "/covers/" + fileName;//返回相对路径（供前端访问）
    }

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
    public List<BookChapter> parseChapters(Integer bookId, InputStream epubStream) throws IOException {
        Book epubBook = new EpubReader().readEpub(epubStream);
        List<BookChapter> chapters = new ArrayList<>();
        int chapterNum = 1;
//        for (Resource res : epubBook.getContents()) {
//            try {
//                BookChapter chapter = new BookChapter();
//                chapter.setBookId(bookId);
//                chapter.setChapterNum(chapterNum++);
//                chapter.setChapterName(extractChapterName(res));
//                chapter.setContent(cleanContent(res));
//                chapter.setCreateTime(LocalDateTime.now());
//                chapter.setUpdateTime(LocalDateTime.now());
//                chapters.add(chapter);
//            } catch (Exception e) {
//                log.error("解析章节失败: {}", res.getHref(), e);
//            }
//        }
        TableOfContents toc = epubBook.getTableOfContents();

        for (TOCReference ref : toc.getTocReferences()) {
            processChapterRecursive(epubBook, ref, bookId, chapters, chapterNum++);
        }
        return chapters;
    }

    private void processChapterRecursive(Book epubBook, TOCReference ref, Integer bookId, List<BookChapter> chapters, int chapterNum) {
        // 获取章节对应的 Resource
        Resource res = epubBook.getResources().getByHref(ref.getCompleteHref());
        if (res == null) return;

        // 创建 Chapter 对象
        BookChapter chapter = new BookChapter();
        chapter.setBookId(bookId);
        chapter.setChapterNum(chapterNum);
        chapter.setChapterName(extractChapterName(epubBook, res));
        chapter.setContent(extractChapterContent(res));
        chapter.setCreateTime(LocalDateTime.now());
        chapter.setUpdateTime(LocalDateTime.now());// 获取章节内容
        // 添加到章节列表
        chapters.add(chapter);

        // 递归处理子章节
//        for (TOCReference child : ref.getChildren()) {
//            processChapterRecursive(epubBook,child, bookId, chapters,chapterNum);
//        }
    }

    private String extractChapterName(Book epubBook, Resource res) {
        // 1️尝试从 EPUB 目录匹配章节名
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

            return content.toString().trim();
        } catch (Exception e) {
            System.out.println("解析章节内容失败: " + e.getMessage());
            return "";
        }
    }


//    /**
//     *提取章节名称
//     * @param res
//     * @return
//     */
//    private String extractChapterName(Resource res) {
//        // 优先使用资源的标题
//        if (res.getTitle() != null && !res.getTitle().trim().isEmpty()) {
//            return res.getTitle().trim();
//        }
//
//        String href = res.getHref();
//        log.info("Processing href: " + href);
//
//        // 优先匹配包含更详细章节信息的格式，例如 chapter_1_intro.html
//        Pattern detailedPattern = Pattern.compile("chapter_(\\d+)_([a-zA-Z0-9]+)\\.html");
//        Matcher detailedMatcher = detailedPattern.matcher(href);
//        if (detailedMatcher.find()) {
//            return "Chapter " + detailedMatcher.group(1) + ": " + detailedMatcher.group(2);
//        }
//
//        // 匹配 split_1.html 这样的格式
//        Pattern simplePattern = Pattern.compile("split_(\\d+)\\.html");
//        Matcher simpleMatcher = simplePattern.matcher(href);
//        if (simpleMatcher.find()) {
//            return "Chapter " + simpleMatcher.group(1);
//        }
//
//        // 匹配中文章节名，例如 "第1章_引言.html"
//        Pattern chinesePattern = Pattern.compile("第(\\d+)章_?([^.]*)\\.html");
//        Matcher chineseMatcher = chinesePattern.matcher(href);
//        if (chineseMatcher.find()) {
//            return "Chapter " + chineseMatcher.group(1) + (chineseMatcher.group(2).isEmpty() ? "" : ": " + chineseMatcher.group(2));
//        }
//
//        log.warn("Failed to extract chapter name from href: " + href);
//        return "Unknown Chapter";
//    }
}
