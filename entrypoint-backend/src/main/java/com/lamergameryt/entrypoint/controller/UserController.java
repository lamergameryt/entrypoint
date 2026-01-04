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

package com.lamergameryt.entrypoint.controller;

import com.lamergameryt.entrypoint.dto.UserDto;
import com.lamergameryt.entrypoint.dto.request.UserLoginRequestDto;
import com.lamergameryt.entrypoint.dto.request.UserRegisterRequestDto;
import com.lamergameryt.entrypoint.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/user")
@Tag(name = "user", description = "API routes to manage user authentication")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Login a user
     *
     * <p>Performs credentials checks against the entered details.<br>
     * Invalid credentials throw a 403 exception.
     *
     * @param login The login credentials to validate
     * @return The details of the logged-in user
     */
    @PostMapping("/login")
    @Operation(
            responses = {
                @ApiResponse(
                        description = "User credentials validated successfully",
                        responseCode = "200",
                        content = @Content(schema = @Schema(implementation = UserDto.class))),
                @ApiResponse(
                        description = "Invalid user credentials entered",
                        responseCode = "403",
                        content = @Content(schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<UserDto> loginUser(@Valid @RequestBody UserLoginRequestDto login) {
        val user = userService.findByCredentials(login.email(), login.password());
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials entered.");
        }

        return ResponseEntity.ok(UserDto.from(user.get()));
    }

    /**
     * Register a user
     *
     * <p>Create a new user account using the provided credentials.
     *
     * @param register The data to registration a new account
     * @return The details of the registered user
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserRegisterRequestDto register) {
        val user = userService.createUser(register.name(), register.email(), register.password());
        return ResponseEntity.ok(UserDto.from(user));
    }
}
