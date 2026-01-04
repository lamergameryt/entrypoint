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

import com.scalar.maven.webjar.ScalarProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI getOpenAPIConfig() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ticket Master Backend API")
                        .description("The backend for ticket master used to handle events,"
                                + " performers, and ticket bookings.")
                        .version("v0.0.1")
                        .contact(new Contact().name("Harsh Patil").email("ifung230@gmail.com")));
    }

    @Bean
    @Primary
    public ScalarProperties getScalarConfig() {
        val properties = new ScalarProperties();

        properties.setDarkMode(true);
        properties.setDefaultOpenAllTags(true);
        properties.setHideClientButton(true);

        return properties;
    }
}
