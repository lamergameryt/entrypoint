package com.lamergameryt.entrypoint.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginResponseDto(@NotBlank String token,
		@NotBlank String name,
		@Email @NotBlank String email) {
    }
