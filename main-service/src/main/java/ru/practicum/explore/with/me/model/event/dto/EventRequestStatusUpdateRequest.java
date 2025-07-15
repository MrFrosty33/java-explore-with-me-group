package ru.practicum.explore.with.me.model.event.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateRequest {
    @NotEmpty(message = "Request IDs must not be empty")
    private List< @NotNull(message = "Request ID cannot be null")Long> requestIds;

    @NotNull(message = "Status must not be null")
    private StatusUpdateRequest status;
}
