package com.example.demo.controller;

import com.example.demo.dto.ImageUploadResponse;
import com.example.demo.service.ImageUploadService;
import com.example.demo.service.storage.StorageService;
import com.example.demo.dto.storage.StorageUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/collage")
public class CollageController {

    @Autowired
    private ImageUploadService imageUploadService;

//    @PostMapping("/upload")
//    public ResponseEntity<StorageUploadResponse> uploadCollage(
//            @RequestParam("file") MultipartFile file)
//            throws IOException {
//
//        StorageUploadResponse response =
//                storageService.upload(file);
//
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/upload")
    public ResponseEntity<List<ImageUploadResponse>> upload(@RequestParam MultipartFile[] file) throws IOException {
        return ResponseEntity.ok(
                imageUploadService.imageUpload(file)
        );
    }
}