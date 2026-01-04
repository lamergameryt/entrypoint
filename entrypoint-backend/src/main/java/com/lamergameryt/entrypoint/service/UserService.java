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

package com.lamergameryt.entrypoint.service;

import com.lamergameryt.entrypoint.model.UserModel;
import com.lamergameryt.entrypoint.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserModel> findByCredentials(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    @Transactional
    public UserModel createUser(String name, String email, String password) {
        UserModel userModel = new UserModel();

        userModel.setName(name);
        userModel.setEmail(email);
        userModel.setPassword(password);

        return userRepository.save(userModel);
    }
}
