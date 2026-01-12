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

package com.lamergameryt.entrypoint.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@ConditionalOnBooleanProperty(name = "aws.s3.enabled")
@Slf4j
public class AwsBucketService {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public AwsBucketService(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    public List<Bucket> getAllBuckets() {
        return this.s3Client.listBuckets().buckets();
    }

    public boolean addImageToBucket(String bucketName, String imageKey, byte[] imageData) {
        try {
            this.s3Client.putObject(
                    builder -> builder.bucket(bucketName).key(imageKey).build(), RequestBody.fromBytes(imageData));
            return true;
        } catch (Exception e) {
            logS3Exception(e);
        }

        return false;
    }

    public Optional<String> getUploadPresignedUrl(String bucketName, String imageKey) {
        return this.getUploadPresignedUrl(bucketName, imageKey, Duration.ofHours(1));
    }

    public Optional<String> getDownloadPresignedUrl(String bucketName, String imageKey) {
        return this.getDownloadPresignedUrl(bucketName, imageKey, Duration.ofHours(1));
    }

    public Optional<String> getUploadPresignedUrl(String bucketName, String imageKey, Duration duration) {
        try {
            PresignedPutObjectRequest response =
                    s3Presigner.presignPutObject(builder -> builder.signatureDuration(duration)
                            .putObjectRequest(req -> req.bucket(bucketName).key(imageKey))
                            .build());

            return Optional.of(response.url().toExternalForm());
        } catch (Exception e) {
            logS3Exception(e);
        }

        return Optional.empty();
    }

    public Optional<String> getDownloadPresignedUrl(String bucketName, String imageKey, Duration duration) {
        try {
            PresignedGetObjectRequest response =
                    s3Presigner.presignGetObject(builder -> builder.signatureDuration(duration)
                            .getObjectRequest(req -> req.bucket(bucketName).key(imageKey))
                            .build());

            return Optional.of(response.url().toExternalForm());
        } catch (Exception e) {
            logS3Exception(e);
        }

        return Optional.empty();
    }

    private void logS3Exception(Throwable throwable) {
        if (throwable instanceof S3Exception e) {
            log.error(
                    "AWS S3 Error - Code: {}, Message: {}",
                    e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails().errorMessage());
        } else {
            log.error("Unexpected error: {}", throwable.getMessage());
        }
    }
}
