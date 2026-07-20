package com.example.demo.service.storage;

import com.example.demo.dto.storage.StorageUploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StorageService {

    StorageUploadResponse upload(MultipartFile file) throws IOException;

    byte[] download(String fileName);

    void delete(String fileName);

}
