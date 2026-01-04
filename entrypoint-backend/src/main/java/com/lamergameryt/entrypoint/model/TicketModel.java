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

package com.lamergameryt.entrypoint.model;

import com.lamergameryt.entrypoint.enums.TicketStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(
        name = "tickets",
        indexes = {@Index(columnList = "event_id"), @Index(columnList = "event_id, seat_number", unique = true)})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_number")
    private String seatNumber;

    @Builder.Default
    @NotNull private TicketStatus status = TicketStatus.NOT_BOOKED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @NotNull private EventModel event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchased_by_user_id")
    @Nullable private UserModel purchasedByUser;

    public Long getEventId() {
        if (event instanceof HibernateProxy) {
            return ((EventModel) ((HibernateProxy) event)
                            .getHibernateLazyInitializer()
                            .getImplementation())
                    .getId();
        } else {
            return event.getId();
        }
    }
}
