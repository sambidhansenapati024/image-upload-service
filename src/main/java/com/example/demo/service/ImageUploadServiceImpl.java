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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.ArrayList;
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
    public List<ImageUploadResponse> imageUpload(MultipartFile[] files) throws IOException {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(MessageConstants.USER_NOT_FOUND));

        List<ImageUploadResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {

            // Upload to S3
            StorageUploadResponse storageResponse =
                    storageService.upload(file);

            // Create metadata
            ImageMetadata metadata = new ImageMetadata();

            metadata.setOriginalFileName(file.getOriginalFilename());

            metadata.setS3Key(storageResponse.getKey());

            metadata.setImageUrl(storageResponse.getUrl());

            metadata.setFileSize(file.getSize());

            metadata.setContentType(file.getContentType());

            metadata.setUploadedAt(LocalDateTime.now());

            metadata.setUser(user);

            ImageMetadata savedMetadata =
                    metadataService.save(metadata);

            logger.info(
                    "User '{}' uploaded image '{}'",
                    user.getEmail(),
                    metadata.getOriginalFileName());

            responses.add(
                    imageMapper.toUploadResponse(savedMetadata)
            );
        }

        return responses;
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
    public Page<ImageResponse> getImages(
            int page,
            int size,
            String search,
            String sortBy,
            String direction) {

        User user = getLoggedInUser();

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ImageMetadata> images;

        if (search == null || search.isBlank()) {

            images = metadataService.findByUser(user, pageable);

        } else {

            images = metadataService.findByUserAndSearch(
                    user,
                    search,
                    pageable);

        }

        return images.map(imageMapper::toImageResponse);

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
