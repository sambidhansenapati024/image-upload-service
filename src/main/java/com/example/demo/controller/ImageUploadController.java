package com.example.demo.controller;

import com.example.demo.config.VersionConfig;
import com.example.demo.dto.ResponceDto;
import com.example.demo.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("image-upload")
@CrossOrigin(origins = {
        "http://localhost:2626",
        "http://13.127.244.157:2626/"})
public class ImageUploadController {
    @Autowired
    private ImageUploadService s3Client;

    @Autowired
    private VersionConfig versionConfig;

    @Value("${aws.bucket-url}")
    private String bucketUrl;

    @PostMapping("/upload")
    public ResponseEntity<ResponceDto> upload(@RequestParam MultipartFile file) throws IOException {
        String fileName = s3Client.imageUpload(file);
        ResponceDto response = new ResponceDto();
        response.setFileName(fileName);
        response.setUrl(bucketUrl+fileName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> download(@PathVariable String fileName){
        byte[] image = s3Client.download(fileName);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @DeleteMapping("/{fileName}")
    public  ResponseEntity<String> delete(@PathVariable String fileName){
        s3Client.delete(fileName);
        return ResponseEntity.ok("Deleted");
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<String>> getAllImage(){
        System.out.println("inside gate all Method");
        List<String> images = s3Client.getAllImages();
        return ResponseEntity.ok(images);
    }
    @GetMapping("/version")
    public Map<String, String> getVersion() {

        Map<String, String> response = new HashMap<>();

        response.put("version", versionConfig.getVersion());
        response.put("buildTime", versionConfig.getBuildTime());

        return response;
    }


}
