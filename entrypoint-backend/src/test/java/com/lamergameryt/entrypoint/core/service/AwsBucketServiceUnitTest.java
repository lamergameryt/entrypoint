/*
 * Entrypoint - Event Booking and Management Application
 * Copyright (C) 2026 Harsh Patil <ifung230@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.lamergameryt.entrypoint.core.service;

import com.lamergameryt.entrypoint.service.AwsBucketService;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@ExtendWith(MockitoExtension.class)
class AwsBucketServiceUnitTest {
    @Mock
    private S3Client client;

    @Mock
    private S3Presigner presigner;

    private AwsBucketService service;
    private S3Exception s3Exception;
    private RuntimeException unknownException;

    @BeforeEach
    void setup() {
        service = new AwsBucketService(client, presigner);
        s3Exception = (S3Exception) S3Exception.builder()
                .awsErrorDetails(
                        AwsErrorDetails.builder().errorMessage("Access denied").build())
                .build();
        unknownException = new RuntimeException("Unknown error");
    }

    @Test
    @DisplayName("During file upload, if S3Exception occurs, should return false")
    void testFileUpload() {
        Mockito.doThrow(s3Exception)
                .when(client)
                .putObject(Mockito.<Consumer<PutObjectRequest.Builder>>any(), Mockito.any(RequestBody.class));

        var result = service.addImageToBucket("test-bucket", "image-key", new byte[] {1, 2, 3});
        Assertions.assertThat(result).isFalse();
    }

    @Test
    @DisplayName("During file upload, if unknown exception occurs, should return false")
    void testFileUploadUnknownException() {
        Mockito.doThrow(unknownException)
                .when(client)
                .putObject(Mockito.<Consumer<PutObjectRequest.Builder>>any(), Mockito.any(RequestBody.class));

        var result = service.addImageToBucket("test-bucket", "image-key", new byte[] {1, 2, 3});
        Assertions.assertThat(result).isFalse();
    }

    @Test
    @DisplayName("During presigned download url generation, if S3Exception occurs, should get empty optional")
    void testPresignedDownloadUrlGeneration() {
        Mockito.doThrow(s3Exception)
                .when(presigner)
                .presignGetObject(Mockito.<Consumer<GetObjectPresignRequest.Builder>>any());

        var result = service.getDownloadPresignedUrl("test-bucket", "image-key");
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("During presigned download url generation, if unknown exception occurs, should get empty optional")
    void testPresignedDownloadUrlGenerationUnknownException() {
        Mockito.doThrow(unknownException)
                .when(presigner)
                .presignGetObject(Mockito.<Consumer<GetObjectPresignRequest.Builder>>any());

        var result = service.getDownloadPresignedUrl("test-bucket", "image-key");
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("During presigned upload url generation, if S3Exception occurs, should get empty optional")
    void testPresignedUploadUrlGeneration() {
        Mockito.doThrow(s3Exception)
                .when(presigner)
                .presignPutObject(Mockito.<Consumer<PutObjectPresignRequest.Builder>>any());

        var result = service.getUploadPresignedUrl("test-bucket", "image-key");
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("During presigned upload url generation, if unknown exception occurs, should get empty optional")
    void testPresignedUploadUrlGenerationUnknownException() {
        Mockito.doThrow(unknownException)
                .when(presigner)
                .presignPutObject(Mockito.<Consumer<PutObjectPresignRequest.Builder>>any());

        var result = service.getUploadPresignedUrl("test-bucket", "image-key");
        Assertions.assertThat(result).isEmpty();
    }
}
