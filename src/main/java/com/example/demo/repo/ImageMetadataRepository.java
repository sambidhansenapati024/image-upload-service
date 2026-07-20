package com.example.demo.repo;

import com.example.demo.entity.ImageMetadata;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageMetadataRepository
        extends JpaRepository<ImageMetadata, Long> {

    List<ImageMetadata> findByUser(User user);

    ImageMetadata findByS3Key(String s3Key);

}
