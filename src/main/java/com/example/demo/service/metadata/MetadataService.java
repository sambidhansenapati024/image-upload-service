package com.example.demo.service.metadata;

import com.example.demo.dto.ImageResponse;
import com.example.demo.entity.ImageMetadata;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MetadataService {

    ImageMetadata save(ImageMetadata image);

    Page<ImageMetadata> findByUser(
            User user,
            Pageable pageable
    );

    Page<ImageMetadata> findByUserAndSearch(
            User user,
            String search,
            Pageable pageable
    );

    Optional<ImageMetadata> findByS3Key(String key);


    List<ImageMetadata> findByUser(User user);

    Page<ImageMetadata> findDeletedByUser(
            User user,
            Pageable pageable);

    Page<ImageMetadata> findDeletedByUserAndSearch(
            User user,
            String search,
            Pageable pageable);

    Optional<ImageMetadata> findByIdAndUser(Long id, User user);



    void permanentDelete(ImageMetadata image);

}