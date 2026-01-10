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

package com.lamergameryt.entrypoint.core.db.repository;

import com.lamergameryt.entrypoint.core.db.DbTestBase;
import com.lamergameryt.entrypoint.model.EventModel;
import com.lamergameryt.entrypoint.repository.EventRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class EventRepositoryTest extends DbTestBase {
    @Autowired
    private EventRepository repository;

    private LocalDateTime now;
    private EventModel event1;
    private EventModel event2;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        event1 = EventModel.builder()
                .name("Music Concert")
                .description("A great music concert")
                .startDate(now.plusDays(1))
                .build();
        event2 = EventModel.builder()
                .name("Art Exhibition")
                .description("An amazing art exhibition")
                .startDate(now.plusDays(5))
                .build();
    }

    @Test
    @DisplayName("Should save and fetch event successfully")
    void shouldSaveAndFetch() {
        var savedEvent = repository.save(event1);
        Assertions.assertNotNull(savedEvent.getId());

        var eventId = savedEvent.getId();
        var event = repository.findById(eventId);
        Assertions.assertTrue(event.isPresent());

        Assertions.assertEquals(event.get().getId(), savedEvent.getId());
        Assertions.assertEquals(event.get().getName(), savedEvent.getName());
        Assertions.assertEquals(event.get().getDescription(), savedEvent.getDescription());
        Assertions.assertEquals(event.get().getStartDate(), savedEvent.getStartDate());
    }

    @Test
    @DisplayName("Should filter events by start date successfully")
    void shouldFilterByStartDate() {
        repository.save(event1);
        repository.save(event2);

        var events = repository.filterEvents(now.plusDays(2));
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("Art Exhibition", events.get(0).getName());
    }

    @Test
    @DisplayName("Should filter events by date range successfully")
    void shouldFilterByDateRange() {
        repository.save(event1);
        repository.save(event2);

        var events = repository.filterEvents(now.plusDays(0), now.plusDays(3));
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("Music Concert", events.get(0).getName());
    }

    @Test
    @DisplayName("Should filter events by name and start date successfully")
    void shouldFilterByNameAndStartDate() {
        repository.save(event1);
        repository.save(event2);

        var events = repository.filterEvents("music", now);
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("Music Concert", events.get(0).getName());
    }

    @Test
    @DisplayName("Should filter events by name and date range successfully")
    void shouldFilterByNameAndDateRange() {
        repository.save(event1);
        repository.save(event2);

        var events = repository.filterEvents("art", now.plusDays(0), now.plusDays(10));
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("Art Exhibition", events.get(0).getName());
    }
}
