package ru.practicum.explore.with.me.model.participation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.with.me.model.event.Event;
import ru.practicum.explore.with.me.model.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "participation_requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User requester;

    @Convert(converter = ParticipationRequestStatusConverter.class)
    private ParticipationRequestStatus status;
}
