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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws.s3")
@Data
public class AwsBucketProperties {
    /**
     * AWS or S3-compatible region where the bucket is hosted.
     *
     * <p>For AWS, this is the standard region identifier, for example:
     *
     * <ul>
     *   <li>{@code us-east-1}</li>
     *   <li>{@code eu-central-1}</li>
     * </ul>
     *
     * <p>For S3-compatible providers (such as Backblaze B2), use the region name defined
     * by that provider.
     *
     * <p>Configured via {@code aws.s3.region}.
     */
    private String region;

    /**
     * HTTP endpoint for the S3 or S3-compatible object storage service.
     *
     * <p>This can point to the default AWS S3 endpoint or to an alternative S3-compatible
     * provider such as Backblaze B2.
     *
     * <p>Example values:
     *
     * <ul>
     *   <li>AWS global endpoint: {@code https://s3.amazonaws.com}</li>
     *   <li>AWS regional endpoint: {@code https://s3.us-east-1.amazonaws.com}</li>
     *   <li>Backblaze B2 endpoint: {@code https://s3.us-west-004.backblazeb2.com}</li>
     * </ul>
     *
     * <p>Configured via {@code aws.s3.endpoint}. Depending on your S3 client configuration,
     * this may be optional when using the default AWS endpoints.
     */
    private String endpoint;

    /**
     * Name of the S3 bucket used by the application to store objects such as uploaded files.
     *
     * <p>Example values:
     *
     * <ul>
     *   <li>{@code entrypoint-uploads}</li>
     *   <li>{@code entrypoint-assets-prod}</li>
     * </ul>
     *
     * <p>Configured via {@code aws.s3.bucket}.
     */
    private String bucket;
}
