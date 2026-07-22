package com.example.demo.service.metadata;

import com.example.demo.dto.ImageResponse;
import com.example.demo.entity.ImageMetadata;
import com.example.demo.entity.User;
import com.example.demo.repo.ImageMetadataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

        return repository.findByUserAndDeletedFalse(user, pageable);

    }

    @Override
    public Page<ImageMetadata> findByUserAndSearch(
            User user,
            String search,
            Pageable pageable) {

        return repository.findByUserAndDeletedFalseAndOriginalFileNameContainingIgnoreCase(
                user,
                search,
                pageable);

    }

    @Override
    public Optional<ImageMetadata> findByS3Key(String key) {

        return Optional.ofNullable(repository.findByS3Key(key));

    }

    @Override
    public List<ImageMetadata> findByUser(User user) {

        return repository.findByUserAndDeletedFalse(user);

    }

    @Override
    public Page<ImageMetadata> findDeletedByUser(
            User user,
            Pageable pageable) {

        return repository.findByUserAndDeletedTrue(user, pageable);
    }

    @Override
    public Page<ImageMetadata> findDeletedByUserAndSearch(
            User user,
            String search,
            Pageable pageable) {

        return repository
                .findByUserAndDeletedTrueAndOriginalFileNameContainingIgnoreCase(
                        user,
                        search,
                        pageable);
    }

    @Override
    public Optional<ImageMetadata> findByIdAndUser(Long id, User user) {
        return repository.findByIdAndUser(id, user);
    }

//    @Override
//    public Page<ImageResponse> getDeletedImages(
//            int page,
//            int size,
//            String search,
//            String sortBy,
//            String direction) {
//
//        User user = getLoggedInUser();
//
//        Sort sort = direction.equalsIgnoreCase("desc")
//                ? Sort.by(sortBy).descending()
//                : Sort.by(sortBy).ascending();
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Page<ImageMetadata> images;
//
//        if (search == null || search.isBlank()) {
//
//            images = repository.findDeletedByUser(user, pageable);
//
//        } else {
//
//            images = metadataService.findDeletedByUserAndSearch(
//                    user,
//                    search,
//                    pageable);
//        }
//
//        return images.map(imageMapper::toImageResponse);
//    }

//    @Override
//    public void restoreImage(Long imageId) {
//
//        User user = getLoggedInUser();
//
//        ImageMetadata metadata = metadataService
//                .findByIdAndUser(imageId, user)
//                .orElseThrow(() ->
//                        new ResourceNotFoundException(
//                                MessageConstants.IMAGE_NOT_FOUND));
//
//        metadata.setDeleted(false);
//        metadata.setDeletedAt(null);
//
//        metadataService.save(metadata);
//
//        logger.info(
//                "User '{}' restored image '{}'",
//                user.getEmail(),
//                metadata.getOriginalFileName());
//    }

//    @Override
//    public void permanentlyDelete(Long imageId) {
//
//        User user = getLoggedInUser();
//
//        ImageMetadata metadata = metadataService
//                .findByIdAndUser(imageId, user)
//                .orElseThrow(() ->
//                        new ResourceNotFoundException(
//                                MessageConstants.IMAGE_NOT_FOUND));
//
//        storageService.delete(metadata.getS3Key());
//
//        metadataService.permanentDelete(metadata);
//
//        logger.info(
//                "User '{}' permanently deleted image '{}'",
//                user.getEmail(),
//                metadata.getOriginalFileName());
//    }

    @Override
    public void permanentDelete(ImageMetadata image) {
        repository.delete(image);
    }
}
