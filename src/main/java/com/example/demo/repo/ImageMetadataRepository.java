package com.example.demo.repo;

import com.example.demo.entity.ImageMetadata;
import com.example.demo.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageMetadataRepository
        extends JpaRepository<ImageMetadata, Long> {

    Page<ImageMetadata> findByUser(
            User user,
            Pageable pageable
    );

    Page<ImageMetadata> findByUserAndOriginalFileNameContainingIgnoreCase(
            User user,
            String search,
            Pageable pageable
    );

    ImageMetadata findByS3Key(String s3Key);

    List<ImageMetadata> findByUser(User user);

}
