package com.example.demo.service;

import com.example.demo.dto.ImageResponse;
import com.example.demo.dto.ImageUploadResponse;
import com.example.demo.dto.storage.StorageUploadResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedOperationException;
import com.example.demo.mapper.ImageMapper;
import com.example.demo.service.dashboard.DashboardService;
import com.example.demo.service.storage.StorageService;
import com.example.demo.util.MessageConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.DashboardStatsDto;
import software.amazon.awssdk.services.s3.S3Client;
import com.example.demo.entity.ImageMetadata;
import com.example.demo.entity.User;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.metadata.MetadataService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {

    private static final Logger logger =
            LoggerFactory.getLogger(ImageUploadServiceImpl.class);

    @Autowired
    private StorageService storageService;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ImageMapper imageMapper;

    @Override
    public ImageUploadResponse imageUpload(MultipartFile file) throws IOException {

        // Upload file to S3
        StorageUploadResponse response =
                storageService.upload(file);

        // Get logged-in user's email
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        // Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.USER_NOT_FOUND));

        // Create metadata
        ImageMetadata metadata = new ImageMetadata();

        metadata.setOriginalFileName(file.getOriginalFilename());

        metadata.setS3Key(response.getKey());

        metadata.setImageUrl(response.getUrl());

        metadata.setFileSize(file.getSize());

        metadata.setContentType(file.getContentType());

        metadata.setUploadedAt(LocalDateTime.now());

        metadata.setUser(user);

        // Save to PostgreSQL
        ImageMetadata savedMetadata = metadataService.save(metadata);
        logger.info(
                "User '{}' uploaded image '{}'",
                user.getEmail(),
                metadata.getOriginalFileName());

        return imageMapper.toUploadResponse(savedMetadata);
    }

    @Override
    public byte[] download(String fileName) {
        return storageService.download(fileName);
    }

    @Override
    public void delete(String fileName) {
        User loggedInUser = getLoggedInUser();

        ImageMetadata metadata =
                metadataService.findByS3Key(fileName)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(MessageConstants.IMAGE_NOT_FOUND));

        if (!metadata.getUser().getId().equals(loggedInUser.getId())) {

            throw new UnauthorizedOperationException(MessageConstants.UNAUTHORIZED_DELETE);

        }

        storageService.delete(fileName);

        metadataService.delete(metadata);
        logger.info(
                "User '{}' deleted image '{}'",
                loggedInUser.getEmail(),
                metadata.getOriginalFileName());
    }

    @Override
    public List<ImageResponse> getAllImages() {

        User user = getLoggedInUser();

        return metadataService.findByUser(user)
                .stream()
                .map(imageMapper::toImageResponse)
                .toList();

    }

    @Override
    public DashboardStatsDto getDashboardStats() {

        User user = getLoggedInUser();

        return dashboardService.getDashboardStats(user);

    }

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(MessageConstants.USER_NOT_FOUND));
    }
}
