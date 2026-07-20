package com.example.demo.mapper;

import com.example.demo.dto.ImageResponse;
import com.example.demo.dto.ImageUploadResponse;
import com.example.demo.entity.ImageMetadata;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

    public ImageUploadResponse toUploadResponse(ImageMetadata image) {

        ImageUploadResponse response =
                new ImageUploadResponse();

        response.setId(image.getId());

        response.setKey(image.getS3Key());

        response.setImageUrl(image.getImageUrl());

        response.setOriginalFileName(image.getOriginalFileName());

        response.setFileSize(image.getFileSize());

        response.setContentType(image.getContentType());

        response.setUploadedAt(image.getUploadedAt());

        return response;
    }

    public ImageResponse toImageResponse(ImageMetadata image) {

        ImageResponse response = new ImageResponse();

        response.setId(image.getId());

        response.setKey(image.getS3Key());

        response.setImageUrl(image.getImageUrl());

        response.setOriginalFileName(image.getOriginalFileName());

        response.setFileSize(image.getFileSize());

        response.setContentType(image.getContentType());

        response.setUploadedAt(image.getUploadedAt());

        return response;
    }
}
