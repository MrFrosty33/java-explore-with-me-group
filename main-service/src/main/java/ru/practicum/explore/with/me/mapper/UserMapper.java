package ru.practicum.explore.with.me.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explore.with.me.model.user.NewUserRequest;
import ru.practicum.explore.with.me.model.user.User;
import ru.practicum.explore.with.me.model.user.UserDto;
import ru.practicum.explore.with.me.model.user.UserShortDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User entity);

    UserShortDto toShortDto(User entity);

    User toEntity(NewUserRequest newUserRequest);
}
