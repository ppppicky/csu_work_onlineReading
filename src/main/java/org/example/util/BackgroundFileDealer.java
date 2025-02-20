package org.example.util;

import lombok.extern.slf4j.Slf4j;

import net.coobird.thumbnailator.Thumbnails;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.example.entity.BackgroundType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;


@Slf4j

public class BackgroundFileDealer {
    @Value("${myStaticLocation}")
    private String staticLoca;
    private final String TEMP_DIR = System.getProperty("user.dir") + "/src/main/resources/static/backgrounds/tmp/";
    private final String PERM_DIR = System.getProperty("user.dir") + "/src/main/resources/static/backgrounds/";

    /**
     * 存储文件到临时目录
     */
    public byte[] saveTemporary(MultipartFile file) throws IOException {
//        String filePath = TEMP_DIR + UUID.randomUUID() + "_" + file.getOriginalFilename();
//        File tmp = new File(filePath);
//        try (InputStream is = file.getInputStream();
//             OutputStream os = new FileOutputStream(tmp)) {
//            byte[] buffer = new byte[8192];
//            int bytesRead;
//            while ((bytesRead = is.read(buffer)) != -1) {
//                os.write(buffer, 0, bytesRead);
//            }
//            return filePath;
//        } catch (IOException e) {
//            throw new RuntimeException("文件存储失败", e);
//        }

        return file.getBytes(); // 直接返回文件的 byte[]

    }

    /**
     * 移动文件到正式目录
     */
//    public String moveToPermanent(String tempPath) {
//        try {
//            String permanentPath = tempPath.replace(TEMP_DIR, PERM_DIR);
//            File tempFile = new File(tempPath);
//            File newFile = new File(permanentPath);
//            if (tempFile.renameTo(newFile)) {
//                return permanentPath;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info("文件移动失败" + e.getLocalizedMessage());
//        }
//        throw new RuntimeException();
//    }
    public String moveToPermanent(byte[] fileData, String fileName) throws IOException {
        String permanentPath = PERM_DIR + fileName;
        File permanentFile = new File(permanentPath);
        Files.write(permanentFile.toPath(), fileData); // 将 byte[] 写入文件
        return permanentPath;
    }

    /**
     * 生成缩略图
     */
    public byte[] generateThumbnail(byte[] fileData, BackgroundType resourceType) throws IOException {
        //  String thumbnailPath = filePath.replace(TEMP_DIR, TEMP_DIR + "thumbnails/");
        //  try {

//            if (resourceType.equals(ResourceType.GRADIENT)) {
//                // 解析 CSS 渐变色值（从文件路径读取渐变定义）
//                String gradientDefinition = readAllLines(Paths.get(filePath)).toString();
//                log.info("解析的渐变定义: {}", gradientDefinition);
//                // 生成渐变图片
//                BufferedImage gradientImage = generateGradientImage(gradientDefinition, 500, 500);
//                // 生成缩略图
//                Thumbnails.of(gradientImage)
//                        .size(200, 200)
//                        .toFile(thumbnailPath);
//
//            } else if (resourceType.equals(ResourceType.VIDEO)) {
//                thumbnailPath = thumbnailPath.replaceAll("\\.\\w+$", ".jpg"); // 统一转换为 .jpg
//                extractVideoThumbnail(filePath, thumbnailPath);
//            } else {
//                Thumbnails.of(filePath)
//                        .size(200, 200)
//                        .toFile(thumbnailPath);
//            }

//                if (!resourceType.equals(BackgroundType.VIDEO)) {
//                Thumbnails.of(filePath)
//                        .size(200, 200)
//                        .toFile(thumbnailPath);
//            } else {
//
//                thumbnailPath = thumbnailPath.replace(".mp4", ".jpg"); // 视频缩略图后缀
//                extractVideoThumbnail(filePath, thumbnailPath);
//            }
//            return thumbnailPath;
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("缩略图生成失败", e);
//        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (!resourceType.equals(BackgroundType.VIDEO)) {
            // 生成图片缩略图
            Thumbnails.of(inputStream)
                    .size(200, 200)
                    .toOutputStream(outputStream);
        } else {
            // 生成视频缩略图
            File tempVideoFile = File.createTempFile("video", ".mp4");
            Files.write(tempVideoFile.toPath(), fileData);
            String thumbnailPath = tempVideoFile.getAbsolutePath().replace(".mp4", ".jpg");
            extractVideoThumbnail(tempVideoFile.getAbsolutePath(), thumbnailPath);

            // 读取缩略图文件
            byte[] thumbnailData = Files.readAllBytes(new File(thumbnailPath).toPath());
            outputStream.write(thumbnailData);
        }

        return outputStream.toByteArray(); // 返回缩略图的 byte[]
    }

    public static void extractVideoThumbnail(String filePath, String outputPath) {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath);
        try {
            grabber.start();
            grabber.setVideoFrameNumber(0);
            Frame frame = grabber.grabImage();
            if (frame == null) {
                throw new IOException("无法从视频中获取有效帧");
            }  // 转换帧为BufferedImage
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage image = converter.getBufferedImage(frame);

            // 使用Thumbnailator生成缩略图（保持宽高比）
            Thumbnails.of(image)
                    .size(200, 200)
                    .toFile(new File(outputPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BufferedImage generateGradientImage(String gradientCss, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 解析 CSS 颜色（这里简单处理两个颜色）
        String[] colors = extractGradientColors(gradientCss);
        if (colors.length < 2) {
            throw new IllegalArgumentException("渐变定义无效，至少需要两个颜色");
        }

        Color startColor = Color.decode(colors[0]);
        Color endColor = Color.decode(colors[1]);

        // 创建线性渐变
        GradientPaint gradient = new GradientPaint(0, 0, startColor, width, height, endColor);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();

        return image;
    }

    String[] extractGradientColors(String gradientCss) {
        // 只提取颜色部分
        String colorPart = gradientCss.replaceAll("linear-gradient\\(.*?,", "").replace(")", "").trim();
        return colorPart.split(",\\s*");
    }
}