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

import com.lamergameryt.entrypoint.config.AwsBucketProperties;
import com.lamergameryt.entrypoint.service.AwsBucketService;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
class AwsBucketServiceTest {
    @Container
    private static final LocalStackContainer container = new LocalStackContainer(
                    DockerImageName.parse("localstack/localstack:4.12"))
            .withServices(LocalStackContainer.Service.S3);

    @Autowired
    private AwsBucketService awsBucketService;

    @Autowired
    private AwsBucketProperties properties;

    private final HttpClient client = HttpClient.newHttpClient();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        container.start();

        registry.add("aws.key.access", container::getAccessKey);
        registry.add("aws.key.secret", container::getSecretKey);

        registry.add("aws.s3.enabled", () -> true);
        registry.add("aws.s3.region", container::getRegion);
        registry.add("aws.s3.endpoint", container.getEndpointOverride(LocalStackContainer.Service.S3)::toString);
        registry.add("aws.s3.bucket", () -> "test-bucket");
    }

    @BeforeEach
    void setupS3Bucket() throws IOException, InterruptedException {
        container.execInContainer("awslocal", "s3", "mb", "s3://" + properties.getBucket());
    }

    @Test
    @DisplayName("Should get all buckets successfully")
    void testGetAllBuckets() {
        var buckets = awsBucketService.getAllBuckets();

        Assertions.assertThat(buckets).anyMatch(bucket -> bucket.name().equals(properties.getBucket()));
    }

    @Test
    @DisplayName("Should upload file successfully and retrieve presigned URL")
    void testAddImageToBucket() throws IOException, InterruptedException {
        var imageName = "test-image.jpg";
        var imageData = this.readResource(imageName);

        var uploadResult = awsBucketService.addImageToBucket(properties.getBucket(), imageName, imageData);
        Assertions.assertThat(uploadResult).isTrue();

        var presignedUrlOpt = awsBucketService.getDownloadPresignedUrl(properties.getBucket(), imageName);
        Assertions.assertThat(presignedUrlOpt).isPresent();

        var presignedUrl = presignedUrlOpt.get();
        var downloadedData = client.send(
                        HttpRequest.newBuilder(URI.create(presignedUrl)).build(),
                        HttpResponse.BodyHandlers.ofByteArray())
                .body();

        Assertions.assertThat(downloadedData).isEqualTo(imageData);
    }

    @Test
    @DisplayName("Should generate upload presigned URL successfully")
    void testGetUploadPresignedUrl() throws IOException, InterruptedException {
        var imageName = "test-image.jpg";
        var presignedUrlOpt = awsBucketService.getUploadPresignedUrl(properties.getBucket(), imageName);

        Assertions.assertThat(presignedUrlOpt).isPresent();

        var presignedUrl = presignedUrlOpt.get();
        var imageData = this.readResource(imageName);

        var request = HttpRequest.newBuilder(URI.create(presignedUrl))
                .timeout(Duration.ofSeconds(10))
                .PUT(HttpRequest.BodyPublishers.ofByteArray(imageData))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        var downloadUrlOpt = awsBucketService.getDownloadPresignedUrl(properties.getBucket(), imageName);
        Assertions.assertThat(downloadUrlOpt).isPresent();

        var downloadUrl = downloadUrlOpt.get();
        var downloadedData = client.send(
                        HttpRequest.newBuilder(URI.create(downloadUrl)).GET().build(),
                        HttpResponse.BodyHandlers.ofByteArray())
                .body();
        Assertions.assertThat(downloadedData).isEqualTo(imageData);
    }

    private byte[] readResource(String resourceName) {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            Assertions.assertThat(inputStream).isNotNull();
            return inputStream.readAllBytes();
        } catch (IOException e) {
            Assertions.fail("Failed to read resource: " + resourceName, e);
            return new byte[0];
        }
    }
}
