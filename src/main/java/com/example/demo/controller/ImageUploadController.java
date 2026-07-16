package com.example.demo.controller;

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
import java.util.List;

@RestController
@RequestMapping("image-upload")
@CrossOrigin(origins = {
        "http://localhost:2626",
        "http://13.127.244.157:2626/"})
public class ImageUploadController {
    @Autowired
    private ImageUploadService s3Client;

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
        return ResponseEntity.ok("Deleted.");
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<String>> getAllImage(){
        List<String> images = s3Client.getAllImages();
        return ResponseEntity.ok(images);
    }


}
