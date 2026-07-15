package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.MultipartUpload;

import java.io.IOException;
import java.util.List;

@Service
public interface ImageUploadService {
    String imageUpload(MultipartFile file) throws IOException;

    byte[] download(String fileName);

    void delete(String fileName);

    List<String> getAllImages();
}
