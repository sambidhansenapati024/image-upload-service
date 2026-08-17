package com.example.demo.service.reids;

public interface RedisService {

    <T> void save(String key, T value, long timeoutSeconds);

    <T> T get(String key, Class<T> clazz);

    void delete(String key);

    boolean exists(String key);

    long getRemainingTtl(String key);

    long incrementWithExpiry(String key, long timeoutSeconds );

}