package com.mymobile.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mymobile.config.MinioProperties;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;

/**
 * Reads and stores phone photos in the MinIO bucket. Phone.imgSrc / PhoneLine.imgSrc
 * hold the object key (e.g. "blackMotoX.jpg").
 */
@Service
public class PhotoService {

	private final MinioClient minioClient;
	private final String bucket;

	public PhotoService(MinioClient minioClient, MinioProperties properties) {
		this.minioClient = minioClient;
		this.bucket = properties.bucket();
	}

	/**
	 * Returns the photo stream, or null if no object exists for the key.
	 * The caller must close the returned stream.
	 */
	public Photo getPhoto(String key) {
		try {
			GetObjectResponse response = minioClient.getObject(
					GetObjectArgs.builder().bucket(bucket).object(key).build());
			return new Photo(response, response.headers().get("Content-Type"));
		} catch (ErrorResponseException e) {
			if ("NoSuchKey".equals(e.errorResponse().code()))
				return null;
			throw new IllegalStateException("Could not read photo " + key + " from MinIO", e);
		} catch (Exception e) {
			throw new IllegalStateException("Could not read photo " + key + " from MinIO", e);
		}
	}

	/**
	 * Stores an uploaded photo under a new random key and returns the key.
	 */
	public String uploadPhoto(byte[] data, ImageType type) {
		String key = UUID.randomUUID() + "." + type.extension();
		try {
			minioClient.putObject(PutObjectArgs.builder()
					.bucket(bucket)
					.object(key)
					.stream(new ByteArrayInputStream(data), data.length, -1)
					.contentType(type.contentType())
					.build());
			return key;
		} catch (Exception e) {
			throw new IllegalStateException("Could not upload photo to MinIO", e);
		}
	}

	public void deletePhoto(String key) {
		try {
			minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(key).build());
		} catch (Exception e) {
			throw new IllegalStateException("Could not delete photo " + key + " from MinIO", e);
		}
	}

	public record Photo(InputStream content, String contentType) {
	}

	/**
	 * Image formats accepted for uploads. SVG is deliberately not allowed, since it can contain scripts.
	 */
	public enum ImageType {
		JPEG("jpg", "image/jpeg"),
		PNG("png", "image/png"),
		WEBP("webp", "image/webp");

		private final String extension;
		private final String contentType;

		ImageType(String extension, String contentType) {
			this.extension = extension;
			this.contentType = contentType;
		}

		public String extension() {
			return extension;
		}

		public String contentType() {
			return contentType;
		}

		/**
		 * Detects the format from the file's first bytes (not its name or the browser's content type).
		 * Returns null if it isn't a supported image.
		 */
		public static ImageType detect(byte[] data) {
			if (startsWith(data, 0, 0xFF, 0xD8, 0xFF))
				return JPEG;
			if (startsWith(data, 0, 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A))
				return PNG;
			if (startsWith(data, 0, 'R', 'I', 'F', 'F') && startsWith(data, 8, 'W', 'E', 'B', 'P'))
				return WEBP;
			return null;
		}

		private static boolean startsWith(byte[] data, int offset, int... expected) {
			if (data.length < offset + expected.length)
				return false;
			for (int i = 0; i < expected.length; i++) {
				if ((data[offset + i] & 0xFF) != expected[i])
					return false;
			}
			return true;
		}
	}
}
