package ru.practicum.explore.with.me.model.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.explore.with.me.model.event.Location;
import ru.practicum.explore.with.me.model.event.StateAction;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateEventAdminRequestDto {
    private String annotation;
    private Long category;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    private String title;
}
