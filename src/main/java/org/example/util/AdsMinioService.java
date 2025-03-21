package org.example.util;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Service
public class AdsMinioService {
    @Autowired
    private MinioClient minioClient;
    @Value("${minio.bucket.adverts}")
    private String bucket;
    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 上传文件到 MinIO 存储桶
     *
     * @param
     * @param name 文件在MinIO存储桶中的名称
     * @return 返回文件在MinIO中的完整访问URL
     * @throws Exception
     */
    public String uploadFile(MultipartFile file, String name) throws Exception {
        // 如果存储桶不存在，则创建该存储桶
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket).object(name).
                        stream(file.getInputStream(), file.getSize(), -1)// 传入文件输入流和文件大小
                        .contentType(file.getContentType())
                        .build()
        );

        return URI.create(endpoint).resolve(String.format("%s/%s", bucket, name)).toString();

    }

    /**
     * 获取文件的预签名 URL，用于临时访问MinIO中的文件
     *
     * @param objectName
     * @return 返回文件的预签名URL
     * @throws Exception
     */
    public String getPresignedUrl(String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET).bucket(bucket).object(objectName)
                        .expiry(2, TimeUnit.HOURS)// URL的有效期为2小时
                        .build()
        );
    }

    /**
     * 从 MinIO 存储桶中删除指定的文件
     *
     * @param objectName
     * @throws Exception
     */
    public void deleteFile(String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build()
        );

    }
}
