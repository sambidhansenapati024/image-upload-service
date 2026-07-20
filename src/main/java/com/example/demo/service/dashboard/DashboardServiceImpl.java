package com.example.demo.service.dashboard;

import com.example.demo.dto.DashboardStatsDto;
import com.example.demo.entity.ImageMetadata;
import com.example.demo.entity.User;
import com.example.demo.service.metadata.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private MetadataService metadataService;

    @Override
    public DashboardStatsDto getDashboardStats(User user) {

        List<ImageMetadata> images =
                metadataService.findByUser(user);

        DashboardStatsDto dto =
                new DashboardStatsDto();

        dto.setTotalImages(images.size());

        long totalStorage = images.stream()
                .mapToLong(ImageMetadata::getFileSize)
                .sum();

        dto.setTotalStorage(totalStorage);

        return dto;
    }
}
