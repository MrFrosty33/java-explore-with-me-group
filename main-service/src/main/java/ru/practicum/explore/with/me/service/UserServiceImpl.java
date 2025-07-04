package ru.practicum.explore.with.me.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore.with.me.mapper.UserMapper;
import ru.practicum.explore.with.me.model.user.NewUserRequest;
import ru.practicum.explore.with.me.model.user.User;
import ru.practicum.explore.with.me.model.user.UserDto;
import ru.practicum.explore.with.me.model.user.UserShortDto;
import ru.practicum.explore.with.me.repository.UserRepository;
import ru.practicum.explore.with.me.util.DataProvider;
import ru.practicum.explore.with.me.util.ExistenceValidator;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService, ExistenceValidator<User>, DataProvider<UserShortDto, User> {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> find(List<Long> ids, int from, int size) {
        List<UserDto> result;

        if (ids != null && !ids.isEmpty()) {
            result = userRepository.findByIdIn(ids).stream()
                    .map(this::getUserDto)
                    .toList();
        } else {
            PageRequest pageRequest = PageRequest.of(from, size);
            result = userRepository.findAll(pageRequest).get()
                    .map(this::getUserDto)
                    .toList();
        }

        log.info("UserServiceImpl: result of find(): {}", result);
        return result;
    }

    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        return null;
    }

    @Override
    public void delete(Long userId) {

    }

    private User getEntity(NewUserRequest newUserRequest) {
        return userMapper.toEntity(newUserRequest);
    }

    private UserDto getUserDto(User user) {
        return userMapper.toDto(user);
    }

    @Override
    public UserShortDto getDto(User entity) {
        return userMapper.toShortDto(entity);
    }

    @Override
    public void validateExists(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            log.info("attempt to find user with id: {}", id);
            //throw new NotFoundException("reason", "message");
            // ждём ветку main_svc_exceptions
        }
    }
}
