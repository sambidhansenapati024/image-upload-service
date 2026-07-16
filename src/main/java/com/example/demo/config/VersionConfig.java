package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:version.properties")
public class VersionConfig {

    @Value("${version}")
    private String version;

    @Value("${buildTime}")
    private String buildTime;

    public String getVersion() {
        return version;
    }

    public String getBuildTime() {
        return buildTime;
    }
}