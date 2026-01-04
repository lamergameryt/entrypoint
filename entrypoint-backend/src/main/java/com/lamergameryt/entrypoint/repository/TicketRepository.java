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

import com.lamergameryt.entrypoint.enums.TicketStatus;
import com.lamergameryt.entrypoint.model.TicketModel;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<TicketModel, Long> {
    /**
     * Find all tickets for the specified event_id and status. <br>
     * <br>
     * Important: eventId is present as an index for faster filtering.
     *
     * @param eventId The id of the event
     * @param status The status of the ticket
     * @return A set of ticket models meeting the criteria
     */
    List<TicketModel> findAllByEvent_IdAndStatus(long eventId, @NotNull TicketStatus status);

    List<TicketModel> findAllByEvent_Id(long eventId);

    @Modifying
    @Query("DELETE FROM TicketModel t WHERE t.id = :ticketId AND t.event.id = :eventId")
    int deleteByIdAndEventId(long ticketId, long eventId);
}
