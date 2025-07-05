package ru.practicum.explore.with.me.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore.with.me.model.participation.ParticipationRequest;
import ru.practicum.explore.with.me.model.participation.ParticipationRequestDto;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {
    //todo ждём event
    //@Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto toDto(ParticipationRequest entity);
}
