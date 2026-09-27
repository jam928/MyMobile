package com.mymobile.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Connection settings for the MinIO object store that holds the phone photos.
 */
@ConfigurationProperties(prefix = "minio")
public record MinioProperties(String endpoint, String accessKey, String secretKey, String bucket) {
}
