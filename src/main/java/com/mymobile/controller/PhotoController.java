package com.mymobile.controller;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.mymobile.service.PhotoService;
import com.mymobile.service.PhotoService.Photo;

/**
 * Serves phone photos stored in MinIO, e.g. /photos/blackMotoX.jpg
 */
@RestController
@RequestMapping("/photos")
public class PhotoController {

	private final PhotoService photoService;

	public PhotoController(PhotoService photoService) {
		this.photoService = photoService;
	}

	@GetMapping("/{key}")
	public ResponseEntity<StreamingResponseBody> getPhoto(@PathVariable String key) {
		Photo photo = photoService.getPhoto(key);
		if (photo == null)
			return ResponseEntity.notFound().build();

		MediaType contentType = photo.contentType() != null
				? MediaType.parseMediaType(photo.contentType())
				: MediaType.APPLICATION_OCTET_STREAM;

		StreamingResponseBody body = out -> {
			try (InputStream in = photo.content()) {
				in.transferTo(out);
			}
		};

		return ResponseEntity.ok()
				.contentType(contentType)
				.cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
				// photos can be SVGs; never let one run scripts if it is opened directly
				.header("Content-Security-Policy", "default-src 'none'; style-src 'unsafe-inline'")
				.header("X-Content-Type-Options", "nosniff")
				.body(body);
	}
}
