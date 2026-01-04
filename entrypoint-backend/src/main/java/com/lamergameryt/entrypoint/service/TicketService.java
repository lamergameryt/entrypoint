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

import com.lamergameryt.entrypoint.enums.TicketStatus;
import com.lamergameryt.entrypoint.exception.ResourceNotFoundException;
import com.lamergameryt.entrypoint.model.EventModel;
import com.lamergameryt.entrypoint.model.TicketModel;
import com.lamergameryt.entrypoint.repository.TicketRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
public class TicketService {
    private final EventService eventService;
    private final TicketRepository repository;

    public TicketService(EventService eventService, TicketRepository ticketRepository) {
        this.eventService = eventService;
        this.repository = ticketRepository;
    }

    public List<TicketModel> getAvailableForEvent(long eventId) {
        return repository.findAllByEvent_IdAndStatus(eventId, TicketStatus.NOT_BOOKED);
    }

    public List<TicketModel> getAllForEvent(long eventId) {
        return repository.findAllByEvent_Id(eventId);
    }

    @Transactional
    public TicketModel createTicket(long eventId, String seatNumber) {
        val event = eventService.getById(eventId);
        if (event.isEmpty()) throw new ResourceNotFoundException("Event with id " + eventId + " does not exist");

        return this.createTicket(event.get(), seatNumber);
    }

    @Transactional
    public TicketModel createTicket(EventModel event, String seatNumber) {
        val ticket = TicketModel.builder().seatNumber(seatNumber).event(event).build();
        return repository.save(ticket);
    }

    @Transactional
    public void deleteTicket(long eventId, long ticketId) {
        val deleteCount = repository.deleteByIdAndEventId(ticketId, eventId);
        if (deleteCount == 0) throw new ResourceNotFoundException("Ticket with id " + ticketId + " does not exist");
    }
}
