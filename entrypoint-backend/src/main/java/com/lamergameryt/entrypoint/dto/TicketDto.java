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

package com.lamergameryt.entrypoint.dto;

import com.lamergameryt.entrypoint.enums.TicketStatus;
import com.lamergameryt.entrypoint.model.TicketModel;

public record TicketDto(long id, long eventId, String seatNumber, TicketStatus status) {
    public static TicketDto from(TicketModel ticket) {
        return new TicketDto(ticket.getId(), ticket.getEventId(), ticket.getSeatNumber(), ticket.getStatus());
    }
}
