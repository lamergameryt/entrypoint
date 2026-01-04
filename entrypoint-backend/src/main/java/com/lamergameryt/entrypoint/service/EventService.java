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

import com.lamergameryt.entrypoint.model.EventModel;
import com.lamergameryt.entrypoint.repository.EventRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Optional<EventModel> getById(long eventId) {
        return eventRepository.findById(eventId);
    }

    public List<EventModel> getAvailableEvents() {
        return this.getAllEvents(LocalDateTime.now().plusDays(10));
    }

    public List<EventModel> getAllEvents(LocalDateTime to) {
        return eventRepository.filterEvents(LocalDateTime.now(), to);
    }

    public List<EventModel> searchEvents(String name, LocalDateTime startDate) {
        return eventRepository.filterEvents(name, startDate);
    }

    public EventModel createEvent(@NonNull String name, String description, @NonNull LocalDateTime startDate) {
        val event = EventModel.builder()
                .name(name)
                .description(description)
                .startDate(startDate)
                .build();

        eventRepository.save(event);
        return event;
    }
}
