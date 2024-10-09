package com.upbdocs.upbdocs.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(byte[] file, String fileName) {
        String fileKey = UUID.randomUUID().toString() + "-" + fileName;

        InputStream inputStream = new ByteArrayInputStream(file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.length);

        s3Client.putObject(new PutObjectRequest(bucketName, fileKey, inputStream, metadata));

        return s3Client.getUrl(bucketName, fileKey).toString();
    }

    public void deleteFile(String fileUrl) {
        String fileKey = extractFileKeyFromUrl(fileUrl);
        s3Client.deleteObject(bucketName, fileKey);
    }

    private String extractFileKeyFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }
}