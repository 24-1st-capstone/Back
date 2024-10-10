package com.capstone.usa.article.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadImage(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());

        amazonS3.putObject(bucket, uniqueFileName, image.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, uniqueFileName).toString();
    }

    public void deleteImage(String imgUrl) {
        String key = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);

        amazonS3.deleteObject(bucket, key);
    }
}
