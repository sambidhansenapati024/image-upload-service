package com.example.demo.repo;

import com.example.demo.entity.ImageMetadata;
import com.example.demo.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageMetadataRepository
        extends JpaRepository<ImageMetadata, Long> {

    Page<ImageMetadata> findByUserAndDeletedFalse(
            User user,
            Pageable pageable);

    Page<ImageMetadata> findByUserAndDeletedFalseAndOriginalFileNameContainingIgnoreCase(
            User user,
            String search,
            Pageable pageable);

    ImageMetadata findByS3Key(String s3Key);

    List<ImageMetadata> findByUserAndDeletedFalse(User user);

    Page<ImageMetadata> findByUserAndDeletedTrue(
            User user,
            Pageable pageable);

    Page<ImageMetadata> findByUserAndDeletedTrueAndOriginalFileNameContainingIgnoreCase(
            User user,
            String search,
            Pageable pageable);

    Optional<ImageMetadata> findByIdAndUser(Long id, User user);

}
