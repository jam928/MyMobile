package com.mymobile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mymobile.config.MinioProperties;
import com.mymobile.service.PhotoService.ImageType;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

	static final byte[] JPEG = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0x10 };
	static final byte[] PNG = { (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0 };
	static final byte[] WEBP = { 'R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'E', 'B', 'P', 'V', 'P' };

	@Mock
	MinioClient minioClient;

	PhotoService photoService;

	@BeforeEach
	void setUp() {
		photoService = new PhotoService(minioClient, new MinioProperties("http://minio:9000", "key", "secret", "phones"));
	}

	@Test
	void detectsSupportedImageTypesFromTheirBytes() {
		assertThat(ImageType.detect(JPEG)).isEqualTo(ImageType.JPEG);
		assertThat(ImageType.detect(PNG)).isEqualTo(ImageType.PNG);
		assertThat(ImageType.detect(WEBP)).isEqualTo(ImageType.WEBP);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"<svg xmlns=\"http://www.w3.org/2000/svg\"><script>alert(1)</script></svg>",
			"GIF89a......",
			"just some text",
			"RIFF....WAVE",
			"" })
	void rejectsEverythingElse(String content) {
		assertThat(ImageType.detect(content.getBytes(StandardCharsets.UTF_8))).isNull();
	}

	@Test
	void rejectsTruncatedHeaders() {
		assertThat(ImageType.detect(new byte[] { (byte) 0xFF, (byte) 0xD8 })).isNull();
		assertThat(ImageType.detect(new byte[] { 'R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'E' })).isNull();
	}

	@Test
	void uploadsUnderARandomKeyWithTheDetectedContentType() throws Exception {
		String key = photoService.uploadPhoto(PNG, ImageType.PNG);

		ArgumentCaptor<PutObjectArgs> args = ArgumentCaptor.forClass(PutObjectArgs.class);
		verify(minioClient).putObject(args.capture());
		assertThat(key).matches("[0-9a-f-]{36}\\.png");
		assertThat(args.getValue().bucket()).isEqualTo("phones");
		assertThat(args.getValue().object()).isEqualTo(key);
		assertThat(args.getValue().contentType()).isEqualTo("image/png");
	}

	@Test
	void everyUploadGetsItsOwnKey() {
		assertThat(photoService.uploadPhoto(JPEG, ImageType.JPEG)).isNotEqualTo(photoService.uploadPhoto(JPEG, ImageType.JPEG));
	}

	@Test
	void uploadFailuresAreReported() throws Exception {
		when(minioClient.putObject(any())).thenThrow(new RuntimeException("MinIO is down"));

		assertThatThrownBy(() -> photoService.uploadPhoto(JPEG, ImageType.JPEG))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("upload");
	}

	@Test
	void deletesByKey() throws Exception {
		photoService.deletePhoto("abc.jpg");

		ArgumentCaptor<RemoveObjectArgs> args = ArgumentCaptor.forClass(RemoveObjectArgs.class);
		verify(minioClient).removeObject(args.capture());
		assertThat(args.getValue().object()).isEqualTo("abc.jpg");
		assertThat(args.getValue().bucket()).isEqualTo("phones");
	}
}
