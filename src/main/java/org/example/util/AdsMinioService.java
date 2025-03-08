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

    public String uploadFile(MultipartFile file, String name) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket).object(name).stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
        return URI.create(endpoint).resolve(String.format("%s/%s", bucket, name)).toString();
        //  return String.format("%s/%s/%s", endpoint.replaceAll("/$", ""), bucket, name);

    }

    public String getPresignedUrl(String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET).bucket(bucket).object(objectName)
                        .expiry(2, TimeUnit.HOURS).build()
        );
    }

    public void deleteFile(String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build()
        );

    }
}
