package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.bucket-name}")
    private String bucketName;
    @Value("${aws.region}")
    private String region;

    @Override
    public String imageUpload(MultipartFile file) throws IOException {

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest request =
                PutObjectRequest.builder().bucket(bucketName).key(fileName).contentType(file.getContentType()).build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        return fileName;
    }

    @Override
    public byte[] download(String fileName) {
        GetObjectRequest reuest = GetObjectRequest.builder().bucket(bucketName).key(fileName).build();

        ResponseBytes<GetObjectResponse> responce = s3Client.getObjectAsBytes(reuest);

        return responce.asByteArray();
    }

    @Override
    public void delete(String fileName) {
        DeleteObjectRequest reuest = DeleteObjectRequest.builder().bucket(bucketName).key(fileName).build();
        s3Client.deleteObject(reuest);

    }

    @Override
    public List<String> getAllImages() {
        ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucketName).build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        return response.contents()
                .stream()
                .map(obj -> String.format(
                        "https://%s.s3.%s.amazonaws.com/%s",
                        bucketName,
                        region,
                        obj.key()))
                .collect(Collectors.toList());
    }
}
