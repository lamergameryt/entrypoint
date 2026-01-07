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
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class AwsBucketService {
    private final Optional<S3Client> s3Client;
    private final Optional<S3Presigner> s3Presigner;

    public AwsBucketService(Optional<S3Client> s3Client, Optional<S3Presigner> s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    public List<Bucket> getAllBuckets() {
        if (s3Client.isEmpty()) return List.of();

        return this.s3Client.get().listBuckets().buckets();
    }

    public boolean addImageToBucket(String bucketName, String imageKey, byte[] imageData) {
        if (s3Client.isEmpty()) return false;
        this.s3Client
                .get()
                .putObject(
                        builder -> builder.bucket(bucketName).key(imageKey).build(), RequestBody.fromBytes(imageData));
        return true;
    }

    public Optional<String> getImagePresignedUrl(String bucketName, String imageKey) {
        if (s3Presigner.isEmpty()) return Optional.empty();

        PresignedGetObjectRequest response = s3Presigner.get().presignGetObject(builder -> builder.signatureDuration(
                        Duration.ofHours(1))
                .getObjectRequest(req -> req.bucket(bucketName).key(imageKey).build())
                .build());

        return response.url().toExternalForm().describeConstable();
    }
}
