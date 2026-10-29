package com.v2.competency.management.service.impl;

import java.io.File;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
public class S3Service {

	private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    
    

    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }
    
    public String uploadTenantImage(
            String bucketName,
            String companyId,
            String fileName,
            File file
    ) {

        String keyName =
                companyId +
                "/zimulate/" +
                fileName;

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .contentType(getContentType(fileName))
                        .build(),
                file.toPath()
        );

        return keyName;
    }
    
    private String getContentType(String fileName) {

        String lowerCaseFileName = fileName.toLowerCase();

        if (lowerCaseFileName.endsWith(".png")) {
            return "image/png";
        }

        if (lowerCaseFileName.endsWith(".webp")) {
            return "image/webp";
        }

        if (lowerCaseFileName.endsWith(".gif")) {
            return "image/gif";
        }

        if (lowerCaseFileName.endsWith(".svg")) {
            return "image/svg+xml";
        }

        return "image/jpeg";
    }
    
}
