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

package com.lamergameryt.entrypoint.db.user;

import com.lamergameryt.entrypoint.db.DbTestBase;
import com.lamergameryt.entrypoint.model.UserModel;
import com.lamergameryt.entrypoint.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserRepositoryTest extends DbTestBase {
    @Autowired
    private UserRepository repository;

    private UserModel testUser;

    @BeforeEach
    void setUp() {
        testUser = UserModel.builder()
                .name("Test User")
                .email("testuser@gmail.com")
                .password("securepassword")
                .build();
    }

    @Test
    @DisplayName("Should save and fetch user successfully")
    void shouldSaveAndFetch() {
        var savedUser = repository.save(testUser);
        Assertions.assertNotNull(savedUser.getId());

        var userId = savedUser.getId();
        var user = repository.findById(userId);
        Assertions.assertTrue(user.isPresent());

        Assertions.assertEquals(user.get().getId(), savedUser.getId());
        Assertions.assertEquals(user.get().getName(), savedUser.getName());
        Assertions.assertEquals(user.get().getEmail(), savedUser.getEmail());
        Assertions.assertEquals(user.get().getPassword(), savedUser.getPassword());
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUser() {
        var savedUser = repository.save(testUser);

        String newName = "Updated Name";
        String newEmail = "updatedemail@gmail.com";

        savedUser.setName(newName);
        savedUser.setEmail(newEmail);

        var user = repository.findById(savedUser.getId());

        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals(newName, user.get().getName());
        Assertions.assertEquals(newEmail, user.get().getEmail());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUser() {
        var savedUser = repository.save(testUser);
        var userId = savedUser.getId();

        repository.deleteById(userId);

        var user = repository.findById(userId);
        Assertions.assertFalse(user.isPresent());
    }

    @Test
    @DisplayName("Should find user by email and password successfully")
    void shouldFindByEmailAndPassword() {
        var savedUser = repository.save(testUser);
        var user = repository.findByEmailAndPassword(savedUser.getEmail(), savedUser.getPassword());

        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals(savedUser.getId(), user.get().getId());
        Assertions.assertEquals(savedUser.getName(), user.get().getName());
        Assertions.assertEquals(savedUser.getEmail(), user.get().getEmail());
        Assertions.assertEquals(savedUser.getPassword(), user.get().getPassword());
    }
}
