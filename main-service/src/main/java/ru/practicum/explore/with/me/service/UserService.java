package ru.practicum.explore.with.me.service;

import ru.practicum.explore.with.me.model.user.AdminFindUserParam;
import ru.practicum.explore.with.me.model.user.NewUserRequest;
import ru.practicum.explore.with.me.model.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> find(AdminFindUserParam param);

    UserDto create(NewUserRequest newUserRequest);

    void delete(Long userId);
}
