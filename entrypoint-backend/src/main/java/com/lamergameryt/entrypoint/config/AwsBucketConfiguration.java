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

package com.lamergameryt.entrypoint.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsBucketConfiguration {
    @Value("${aws.key.access}")
    private String accessKey;

    @Value("${aws.key.secret}")
    private String secretKey;

    private final AwsBucketProperties properties;

    public AwsBucketConfiguration(AwsBucketProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnBooleanProperty(name = "aws.s3.enabled")
    public S3Client createS3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsSessionCredentials.create(accessKey, secretKey, "")))
                .build();
    }

    @Bean
    @ConditionalOnBooleanProperty(name = "aws.s3.enabled")
    public S3Presigner createS3Presigner() {
        return S3Presigner.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsSessionCredentials.create(accessKey, secretKey, "")))
                .build();
    }
}
