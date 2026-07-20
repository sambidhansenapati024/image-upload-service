package com.example.demo.service.storage;

import com.example.demo.dto.storage.StorageUploadResponse;
import com.example.demo.exception.InvalidFileException;
import com.example.demo.util.ImageConstants;
import com.example.demo.util.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {

    @Value("${file.max.size}")
    private DataSize maxFileSize;

    @Autowired
    private S3Client s3Client;

    @Value("${aws.bucket-name}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    private static final Logger logger =
            LoggerFactory.getLogger(StorageServiceImpl.class);

    @Override
    public StorageUploadResponse upload(MultipartFile file) throws IOException {

        if(file.isEmpty()){
            throw new InvalidFileException(MessageConstants.EMPTY_FILE);
        }
        if(file.getSize() > maxFileSize.toBytes()){
            throw  new InvalidFileException(MessageConstants.FILE_SIZE_LIMIT_EXCEED);
        }


        String contentType = file.getContentType();

        if (contentType == null ||
                !ImageConstants.ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new InvalidFileException(MessageConstants.INVALID_IMAGE_FORMAT);
        }

        String originalFileName = file.getOriginalFilename();

        String fileName =
                UUID.randomUUID() + "_" + originalFileName;

        PutObjectRequest request =
                PutObjectRequest.builder().bucket(bucketName).key(fileName).contentType(file.getContentType()).build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        String url = String.format(
                "https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                region,
                fileName);
        logger.info(
                "Uploaded file '{}' to S3 with key '{}'",
                file.getOriginalFilename(),
                fileName);

        return new StorageUploadResponse(
                fileName,
                url
        );
    }

    @Override
    public byte[] download(String fileName) {
        GetObjectRequest request =
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build();

        ResponseBytes<GetObjectResponse> response =
                s3Client.getObjectAsBytes(request);

        return response.asByteArray();
    }

    @Override
    public void delete(String fileName) {
        DeleteObjectRequest request =
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build();

        s3Client.deleteObject(request);

    }

}
