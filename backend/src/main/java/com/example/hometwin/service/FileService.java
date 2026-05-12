package com.example.hometwin.service;

import io.minio.*;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.UUID;

@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    public String uploadAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请上传头像文件");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("文件名无效");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)")) {
            throw new RuntimeException("仅支持 jpg、png、gif、webp 格式");
        }

        long maxSize = 2 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new RuntimeException("头像文件大小不能超过 2MB");
        }

        try {
            String fileName = "avatars/" + UUID.randomUUID().toString() + extension;
            
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            String avatarUrl = minioUrl + "/" + bucketName + "/" + fileName;
            log.info("头像上传成功: {}", avatarUrl);
            return avatarUrl;
        } catch (Exception e) {
            log.error("头像上传失败", e);
            throw new RuntimeException("头像上传失败: " + e.getMessage());
        }
    }

    public String uploadAvatarFromBase64(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            throw new RuntimeException("请提供图片数据");
        }

        try {
            String extension = ".jpg";
            if (base64Image.startsWith("data:image/png")) {
                extension = ".png";
            } else if (base64Image.startsWith("data:image/gif")) {
                extension = ".gif";
            } else if (base64Image.startsWith("data:image/webp")) {
                extension = ".webp";
            }

            String pureBase64 = base64Image;
            if (base64Image.contains(",")) {
                pureBase64 = base64Image.split(",")[1];
            }

            byte[] imageBytes = Base64.getDecoder().decode(pureBase64);
            String fileName = "avatars/" + UUID.randomUUID().toString() + extension;

            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(new java.io.ByteArrayInputStream(imageBytes), imageBytes.length, -1)
                    .contentType("image/jpeg")
                    .build()
            );

            String avatarUrl = minioUrl + "/" + bucketName + "/" + fileName;
            log.info("头像上传成功(Base64): {}", avatarUrl);
            return avatarUrl;
        } catch (Exception e) {
            log.error("头像上传失败(Base64)", e);
            throw new RuntimeException("头像上传失败: " + e.getMessage());
        }
    }
}