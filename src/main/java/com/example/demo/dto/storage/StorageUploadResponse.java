package com.example.demo.dto.storage;

public class StorageUploadResponse {

    private String key;
    private String url;

    public StorageUploadResponse() {
    }

    public StorageUploadResponse(String key, String url) {
        this.key = key;
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}