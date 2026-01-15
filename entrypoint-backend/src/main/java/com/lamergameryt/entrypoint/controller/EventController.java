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

import com.lamergameryt.entrypoint.dto.EventDto;
import com.lamergameryt.entrypoint.dto.TicketDto;
import com.lamergameryt.entrypoint.dto.request.EventCreateRequestDto;
import com.lamergameryt.entrypoint.dto.request.TicketCreateRequestDto;
import com.lamergameryt.entrypoint.service.EventService;
import com.lamergameryt.entrypoint.service.TicketService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import lombok.val;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
@Tag(name = "events", description = "API routes to manage events and tickets")
public class EventController {
    private final EventService eventService;
    private final TicketService ticketService;

    public EventController(EventService eventService, TicketService ticketService) {
        this.eventService = eventService;
        this.ticketService = ticketService;
    }

    /**
     * Get all events
     *
     * <p>Retrieve all available events which start after the current date.<br>
     * Does not return any past events as the tickets for those cannot be booked.
     *
     * @return A list of available events
     */

    @PreAuthorize("hasAuthority('VIEW_EVENT')")
    @GetMapping()
    public ResponseEntity<List<EventDto>> getAllEvents() {
        val events =
                eventService.getAvailableEvents().stream().map(EventDto::from).toList();
        return ResponseEntity.ok(events);
    }

    /**
     * Create a new event
     *
     * <p>Add an event to the application with the name and description.<br>
     * To add additional information / metadata, use the PUT route.
     *
     * @param eventData The event creation data
     * @return The created event data
     */
    @PreAuthorize("hasAuthority('CREATE_EVENT')")
    @PostMapping()
    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody EventCreateRequestDto eventData) {
        val event = eventService.createEvent(eventData.name(), eventData.description(), eventData.startDate());
        return ResponseEntity.ok(EventDto.from(event));
    }

    /**
     * Filter for particular events
     *
     * <p>Retrieves events which matches the entered filters using an AND type query.<br>
     * Both the filters are required to be entered and are not optional.
     *
     * @param name Name of the event to fuzzy find.
     * @param startsAfter The date after which the event starts.
     * @return The list of events matching the search
     */    
    @PreAuthorize("hasAuthority('VIEW_EVENT')")
    @GetMapping("/search")
    public ResponseEntity<List<EventDto>> getEvents(
            @RequestParam @NotNull String name,
            @Parameter(description = "The date after which the event starts", example = "2025-05-15 12:00:00")
                    @RequestParam(name = "starts_after")
                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                    @PastOrPresent @Nullable LocalDateTime startsAfter) {
        if (startsAfter == null) startsAfter = LocalDateTime.now();

        val events = eventService.searchEvents(name, startsAfter).stream()
                .map(EventDto::from)
                .toList();
        return ResponseEntity.ok(events);
    }

    /**
     * Get tickets for event
     *
     * <p>Retrieves all available tickets for an event.<br>
     * This route does not return tickets which are booked.
     *
     * @param eventId The id of the event
     * @return The list of available tickets
     */
    @PreAuthorize("hasAuthority('VIEW_EVENT')")
    @GetMapping("/{eventId}/tickets")
    public ResponseEntity<List<TicketDto>> getTicketsForEvent(@PathVariable @Positive long eventId) {
        val tickets = ticketService.getAvailableForEvent(eventId).stream()
                .map(TicketDto::from)
                .toList();
        return ResponseEntity.ok(tickets);
    }

    /**
     * Create ticket for event
     *
     * <p>Add a ticket for an event and mark it as available by default.
     *
     * @param eventId The id of the event to add ticket
     * @param ticketRequest The ticket creation data
     * @return The created ticket data
     */
    
    @PreAuthorize("hasAuthority('VIEW_EVENT')")
    @PostMapping("/{eventId}/tickets")
    public ResponseEntity<TicketDto> createTicketForEvent(
            @PathVariable @Positive long eventId, @Valid @RequestBody TicketCreateRequestDto ticketRequest) {
        val ticket = ticketService.createTicket(eventId, ticketRequest.seatNumber());
        return ResponseEntity.ok(TicketDto.from(ticket));
    }

    /**
     * Delete ticket for event
     *
     * <p>Delete a ticket for an event by its id, provided the ticket is not reserved or booked.
     *
     * @param eventId The id of the event
     * @param ticketId The id of the ticket to delete
     * @return A response indicating the deletion status
     */
    @PreAuthorize("hasAuthority('EDIT_EVENT')")
    @DeleteMapping("/{eventId}/tickets/{ticketId}")
    public ResponseEntity<Void> deleteTicketForEvent(
            @PathVariable @Positive long eventId, @PathVariable @Positive long ticketId) {
        ticketService.deleteTicket(eventId, ticketId);
        return ResponseEntity.noContent().build();
    }
}
