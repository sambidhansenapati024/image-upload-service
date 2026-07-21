package com.example.demo.controller;

import com.example.demo.config.VersionConfig;
import com.example.demo.dto.DashboardStatsDto;
import com.example.demo.dto.ImageResponse;
import com.example.demo.dto.ImageUploadResponse;
import com.example.demo.dto.ResponceDto;
import com.example.demo.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("image-upload")
public class ImageUploadController {
    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private VersionConfig versionConfig;

    @Value("${aws.bucket-url}")
    private String bucketUrl;

    @PostMapping("/upload")
    public ResponseEntity<List<ImageUploadResponse>> upload(@RequestParam MultipartFile[] file) throws IOException {
        return ResponseEntity.ok(
                imageUploadService.imageUpload(file)
        );
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> download(@PathVariable String fileName){
        byte[] image = imageUploadService.download(fileName);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @DeleteMapping("/{fileName}")
    public  ResponseEntity<String> delete(@PathVariable String fileName){
        imageUploadService.delete(fileName);
        return ResponseEntity.ok("Deleted");
    }
    @GetMapping
    public ResponseEntity<Page<ImageResponse>> getImages(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "12") int size,

            @RequestParam(defaultValue = "") String search,

            @RequestParam(defaultValue = "uploadedAt") String sortBy,

            @RequestParam(defaultValue = "desc") String direction) {

        return ResponseEntity.ok(

                imageUploadService.getImages(
                        page,
                        size,
                        search,
                        sortBy,
                        direction)

        );

    }
    @GetMapping("/version")
    public Map<String, String> getVersion() {

        Map<String, String> response = new HashMap<>();

        response.put("version", versionConfig.getVersion());
        response.put("buildTime", versionConfig.getBuildTime());

        return response;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {

        return ResponseEntity.ok(
                imageUploadService.getDashboardStats()
        );

    }


}
