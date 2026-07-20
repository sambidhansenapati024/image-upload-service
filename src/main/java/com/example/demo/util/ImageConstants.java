package com.example.demo.util;

import java.util.Set;

public class ImageConstants {

    private ImageConstants() {
    }

    public static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/webp",
            "image/tiff",
            "image/avif"
    );
}
