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

import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
    java
    jacoco
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "8.1.0"
    id("com.adarshr.test-logger") version "4.0.0"
    id("org.sonarqube") version "7.2.2.6593"
}

group = "com.lamergameryt"
version = "0.0.1"
description = "entrypoint"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("software.amazon.awssdk:bom:2.27.21")
    }
}

dependencies {
    val springDocVersion = "3.0.0"
    val therApiVersion = "0.15.0"
    val testContainersJunitVersion = "1.21.4"
    val testContainersLocalstackVersion = "1.21.4"

    // Spring Boot Dependencies
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // SpringDoc dependencies for OpenAI, Swagger, and Scalar
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocVersion")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-scalar:$springDocVersion")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    

    // AWS S3 SDK
    implementation("software.amazon.awssdk:s3")

    compileOnly("org.projectlombok:lombok")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    

    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("com.github.therapi:therapi-runtime-javadoc-scribe:$therApiVersion")

    runtimeOnly("com.github.therapi:therapi-runtime-javadoc:$therApiVersion")
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersJunitVersion")
    testImplementation("org.testcontainers:localstack:$testContainersLocalstackVersion")
    testImplementation("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

spotless {
    format("misc") {
        target("*.gradle", ".gitattributes", ".gitignore")
        trimTrailingWhitespace()
        leadingSpacesToTabs()
        endWithNewline()
    }

    java {
        trimTrailingWhitespace()
        endWithNewline()

        removeUnusedImports()
        importOrder()

        cleanthat().sourceCompatibility("17").addMutator("SafeButNotConsensual")
        palantirJavaFormat().formatJavadoc(true)

        formatAnnotations()
    }
}

testlogger {
    theme = ThemeType.MOCHA
}

sonar {
    properties {
        property("sonar.projectKey", "lamergameryt_entrypoint")
        property("sonar.organization", "lamergameryt")
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<Test> {
    useJUnitPlatform()
}