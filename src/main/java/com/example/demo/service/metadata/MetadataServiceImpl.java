package com.example.demo.service.metadata;

import com.example.demo.entity.ImageMetadata;
import com.example.demo.entity.User;
import com.example.demo.repo.ImageMetadataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MetadataServiceImpl
        implements MetadataService {

    private final ImageMetadataRepository repository;

    public MetadataServiceImpl(
            ImageMetadataRepository repository) {

        this.repository = repository;
    }

    @Override
    public ImageMetadata save(ImageMetadata image) {

        return repository.save(image);

    }

    @Override
    public Page<ImageMetadata> findByUser(
            User user,
            Pageable pageable) {

        return repository.findByUser(user, pageable);

    }

    @Override
    public Page<ImageMetadata> findByUserAndSearch(
            User user,
            String search,
            Pageable pageable) {

        return repository.findByUserAndOriginalFileNameContainingIgnoreCase(
                user,
                search,
                pageable);

    }

    @Override
    public Optional<ImageMetadata> findByS3Key(String key) {

        return Optional.ofNullable(repository.findByS3Key(key));

    }

    @Override
    public void delete(ImageMetadata image) {

        repository.delete(image);

    }

    @Override
    public List<ImageMetadata> findByUser(User user) {

        return repository.findByUser(user);

    }
}
