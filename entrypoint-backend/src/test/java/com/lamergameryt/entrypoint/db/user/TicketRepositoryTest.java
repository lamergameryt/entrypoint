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
import com.lamergameryt.entrypoint.enums.TicketStatus;
import com.lamergameryt.entrypoint.model.EventModel;
import com.lamergameryt.entrypoint.model.TicketModel;
import com.lamergameryt.entrypoint.repository.EventRepository;
import com.lamergameryt.entrypoint.repository.TicketRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TicketRepositoryTest extends DbTestBase {
    @Autowired
    private TicketRepository repository;

    @Autowired
    private EventRepository eventRepository;

    private EventModel testEvent;
    private TicketModel testTicket;

    @BeforeEach
    void setUp() {
        testEvent = eventRepository.save(EventModel.builder()
                .name("Sample Event")
                .description("This is a sample event for testing.")
                .startDate(LocalDateTime.now())
                .build());

        testTicket = TicketModel.builder()
                .seatNumber("A1")
                .status(TicketStatus.NOT_BOOKED)
                .event(testEvent)
                .build();
    }

    @Test
    @DisplayName("Should save and fetch ticket successfully")
    void shouldSaveAndFetch() {
        var savedTicket = repository.save(testTicket);
        Assertions.assertNotNull(savedTicket.getId());

        var ticketId = savedTicket.getId();
        var ticket = repository.findById(ticketId);

        Assertions.assertTrue(ticket.isPresent());
        Assertions.assertEquals(ticket.get().getId(), savedTicket.getId());
        Assertions.assertEquals(ticket.get().getSeatNumber(), savedTicket.getSeatNumber());
        Assertions.assertEquals(
                ticket.get().getEvent().getId(), savedTicket.getEvent().getId());
        Assertions.assertEquals(ticket.get().getStatus(), savedTicket.getStatus());
    }

    @Test
    @DisplayName("Should find tickets by event ID and status successfully")
    void shouldFindByEventIdAndStatus() {
        repository.save(testTicket);
        repository.save(TicketModel.builder()
                .status(TicketStatus.BOOKED)
                .event(testEvent)
                .seatNumber("A2")
                .build());

        var tickets = repository.findAllByEvent_IdAndStatus(testEvent.getId(), testTicket.getStatus());
        Assertions.assertFalse(tickets.isEmpty());
        Assertions.assertEquals(1, tickets.size());
        Assertions.assertEquals(testTicket.getSeatNumber(), tickets.get(0).getSeatNumber());
    }

    @Test
    @DisplayName("Should delete ticket by ID and event ID")
    void shouldDeleteByIdAndEventId() {
        var savedTicket = repository.save(testTicket);
        var deleteCount = repository.deleteByIdAndEventId(savedTicket.getId(), testEvent.getId());

        Assertions.assertEquals(1, deleteCount);

        var ticket = repository.findById(savedTicket.getId());
        Assertions.assertTrue(ticket.isEmpty());
    }
}
