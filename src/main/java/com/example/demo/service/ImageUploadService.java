package com.example.demo.service;

import com.example.demo.dto.DashboardStatsDto;
import com.example.demo.dto.ImageResponse;
import com.example.demo.dto.ImageUploadResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.MultipartUpload;

import java.io.IOException;
import java.util.List;

@Service
public interface ImageUploadService {
    List<ImageUploadResponse> imageUpload(MultipartFile[] file) throws IOException;

    byte[] download(String fileName);

    void delete(String fileName);

    Page<ImageResponse> getImages(
            int page,
            int size,
            String search,
            String sortBy,
            String direction
    );

    DashboardStatsDto getDashboardStats();
}
