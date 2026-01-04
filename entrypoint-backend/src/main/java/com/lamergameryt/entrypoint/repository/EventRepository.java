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

package com.lamergameryt.entrypoint.repository;

import com.lamergameryt.entrypoint.model.EventModel;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<EventModel, Long> {
    @EntityGraph(attributePaths = "performers")
    List<EventModel> findAllByNameContainingIgnoreCaseAndStartDateAfter(String name, LocalDateTime startDate);

    @EntityGraph(attributePaths = "performers")
    List<EventModel> findAllByNameContainingIgnoreCaseAndStartDateBetween(
            String name, LocalDateTime startDateAfter, LocalDateTime startDateBefore);

    @EntityGraph(attributePaths = "performers")
    List<EventModel> findAllByStartDateAfter(LocalDateTime startDateAfter);

    @EntityGraph(attributePaths = "performers")
    List<EventModel> findAllByStartDateBetween(LocalDateTime startDateAfter, LocalDateTime startDateBefore);

    default List<EventModel> filterEvents(@NotNull LocalDateTime startDateAfter) {
        return this.findAllByStartDateAfter(startDateAfter);
    }

    default List<EventModel> filterEvents(
            @NotNull LocalDateTime startDateAfter, @NotNull LocalDateTime startDateBefore) {
        return this.findAllByStartDateBetween(startDateAfter, startDateBefore);
    }

    default List<EventModel> filterEvents(@NotNull String name, @NotNull LocalDateTime startDate) {
        return this.findAllByNameContainingIgnoreCaseAndStartDateAfter(name, startDate);
    }

    default List<EventModel> filterEvents(
            @NotNull String name, @NotNull LocalDateTime startDateAfter, @NotNull LocalDateTime startDateBefore) {
        return this.findAllByNameContainingIgnoreCaseAndStartDateBetween(name, startDateAfter, startDateBefore);
    }
}
