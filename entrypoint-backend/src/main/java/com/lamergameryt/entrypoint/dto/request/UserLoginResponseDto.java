package com.lamergameryt.entrypoint.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginResponseDto {
    private String token;
    private String name;
    private String email;
}
