package com.example.demo.service.metadata;

import com.example.demo.entity.ImageMetadata;
import com.example.demo.entity.User;

import java.util.List;
import java.util.Optional;

public interface MetadataService {

    ImageMetadata save(ImageMetadata image);

    List<ImageMetadata> findByUser(User user);

    Optional<ImageMetadata> findByS3Key(String key);

    void delete(ImageMetadata image);

}