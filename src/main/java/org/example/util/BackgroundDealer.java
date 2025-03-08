package org.example.util;

import cn.hutool.core.lang.UUID;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;

import net.coobird.thumbnailator.Thumbnails;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.example.entity.BackgroundType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class BackgroundDealer {
      @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.temp}")
    private String tempBucket;

    @Value("${minio.bucket.backgrounds}")
    private String permanentBucket;
    /**
     * 存储文件到临时目录
     */
    public String saveTemporary(MultipartFile file) throws Exception {
        String objectName = "bg_"+ UUID.randomUUID() + "_" + file.getOriginalFilename();
        log.info("objectname:   "+objectName);
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(tempBucket)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return objectName;
        }
    }
    // BackgroundFileDealer.java
    public String saveTemporary(byte[] fileData, String fileName, String contentType) throws Exception {
        String objectName = "bg_" + UUID.randomUUID() + "_" + fileName;
        try (InputStream inputStream = new ByteArrayInputStream(fileData)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(tempBucket)
                            .object(objectName)
                            .stream(inputStream, fileData.length, -1)
                            .contentType(contentType)
                            .build()
            );
            return objectName;
        }
    }
//    public byte[] saveTemporary(MultipartFile file) throws IOException {
////        String filePath = TEMP_DIR + UUID.randomUUID() + "_" + file.getOriginalFilename();
////        File tmp = new File(filePath);
////        try (InputStream is = file.getInputStream();
////             OutputStream os = new FileOutputStream(tmp)) {
////            byte[] buffer = new byte[8192];
////            int bytesRead;
////            while ((bytesRead = is.read(buffer)) != -1) {
////                os.write(buffer, 0, bytesRead);
////            }
////            return filePath;
////        } catch (IOException e) {
////            throw new RuntimeException("文件存储失败", e);
////        }
//
//        return file.getBytes(); // 直接返回文件的 byte[]
//
//    }

    /**
     * 将文件从临时存储移动到永久存储
     */
    public String moveToPermanent(String tempObjectName) throws Exception {
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .source(CopySource.builder().bucket(tempBucket).object(tempObjectName).build())
                        .bucket(permanentBucket)
                        .object(tempObjectName)
                        .build()
        );
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(tempBucket)
                        .object(tempObjectName)
                        .build()
        );

        return tempObjectName;
    }

    public String getPresignedUrl(String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET).bucket(permanentBucket).object(objectName)
                        .expiry(2, TimeUnit.HOURS).build()
        );
    }


    /**
     * 生成缩略图
     */
//    public byte[] generateThumbnail(byte[] fileData, BackgroundType resourceType) throws IOException {
//
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

//        if (!resourceType.equals(BackgroundType.VIDEO)) {
//            // 生成图片缩略图
//            Thumbnails.of(inputStream)
//                    .size(200, 200)
//                    .toOutputStream(outputStream);
//        } else {
//            // 生成视频缩略图
//            File tempVideoFile = File.createTempFile("video", ".mp4");
//            Files.write(tempVideoFile.toPath(), fileData);
//            String thumbnailPath = tempVideoFile.getAbsolutePath().replace(".mp4", ".jpg");
//            extractVideoThumbnail(tempVideoFile.getAbsolutePath(), thumbnailPath);
//
//            // 读取缩略图文件
//            byte[] thumbnailData = Files.readAllBytes(new File(thumbnailPath).toPath());
//            outputStream.write(thumbnailData);
//
//        return outputStream.toByteArray(); // 返回缩略图的 byte[]
//    }

//    public static void extractVideoThumbnail(String filePath, String outputPath) {
//        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath);
//        try {
//            grabber.start();
//            grabber.setVideoFrameNumber(0);
//            Frame frame = grabber.grabImage();
//            if (frame == null) {
//                throw new IOException("无法从视频中获取有效帧");
//            }  // 转换帧为BufferedImage
//            Java2DFrameConverter converter = new Java2DFrameConverter();
//            BufferedImage image = converter.getBufferedImage(frame);
//
//            // 使用Thumbnailator生成缩略图（保持宽高比）
//            Thumbnails.of(image)
//                    .size(200, 200)
//                    .toFile(new File(outputPath));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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