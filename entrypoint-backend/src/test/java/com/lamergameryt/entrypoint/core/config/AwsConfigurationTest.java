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

package com.lamergameryt.entrypoint.core.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.lamergameryt.entrypoint.config.AwsBucketConfiguration;
import com.lamergameryt.entrypoint.config.AwsBucketProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

class AwsConfigurationTest {
    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner().withUserConfiguration(AwsBucketConfiguration.class);

    private AwsBucketProperties properties;

    @BeforeEach
    void initializeProperties() {
        properties = new AwsBucketProperties();
        properties.setBucket("test-bucket");
        properties.setEndpoint("http://localhost:4566");
        properties.setRegion("us-east-1");
    }

    @Test
    @DisplayName("Should not build beans when AWS S3 is disabled")
    void shouldNotBuildBean() {
        contextRunner
                .withPropertyValues("aws.s3.enabled=false", "aws.key.access=test-access", "aws.key.secret=test-secret")
                .withBean(AwsBucketProperties.class, () -> properties)
                .run(context -> {
                    assertThat(context).doesNotHaveBean(S3Client.class);
                    assertThat(context).doesNotHaveBean(S3Presigner.class);
                });
    }

    @Test
    @DisplayName("Should build beans when AWS S3 is enabled")
    void shouldBuildBean() {
        contextRunner
                .withPropertyValues("aws.s3.enabled=true", "aws.key.access=test-access", "aws.key.secret=test-secret")
                .withBean(AwsBucketProperties.class, () -> properties)
                .run(context -> {
                    assertThat(context).hasSingleBean(S3Client.class);
                    assertThat(context).hasSingleBean(S3Presigner.class);

                    S3Client s3Client = context.getBean(S3Client.class);
                    S3Presigner presigner = context.getBean(S3Presigner.class);

                    assertThat(s3Client).isNotNull();
                    assertThat(presigner).isNotNull();
                });
    }
}
