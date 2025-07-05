package ru.practicum.explore.with.me.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.with.me.mapper.ParticipationRequestMapper;
import ru.practicum.explore.with.me.model.participation.CancelParticipationRequest;
import ru.practicum.explore.with.me.model.participation.NewParticipationRequest;
import ru.practicum.explore.with.me.model.participation.ParticipationRequest;
import ru.practicum.explore.with.me.model.participation.ParticipationRequestDto;
import ru.practicum.explore.with.me.model.participation.Status;
import ru.practicum.explore.with.me.model.user.User;
import ru.practicum.explore.with.me.repository.ParticipationRequestRepository;
import ru.practicum.explore.with.me.repository.UserRepository;
import ru.practicum.explore.with.me.util.DataProvider;
import ru.practicum.explore.with.me.util.ExistenceValidator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ParticipationRequestServiceImpl implements ParticipationRequestService,
        ExistenceValidator<ParticipationRequest>, DataProvider<ParticipationRequestDto, ParticipationRequest> {

    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final ExistenceValidator<User> userExistenceValidator;
    private final ParticipationRequestMapper participationRequestMapper;


    @Override
    public List<ParticipationRequestDto> find(Long userId) {
        userExistenceValidator.validateExists(userId);

        List<ParticipationRequestDto> result = participationRequestRepository.findAllByRequesterId(userId).stream()
                .map(this::getDto)
                .toList();
        log.info("ParticipationRequestServiceImpl: result of find(): {}", result);
        return result;
    }

    @Override
    @Transactional
    public ParticipationRequestDto create(NewParticipationRequest newParticipationRequest) {
        //todo eventExistenceValidator.validateExists();
        return null;
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(CancelParticipationRequest cancelParticipationRequest) {
        validateExists(cancelParticipationRequest.getRequestId());
        userExistenceValidator.validateExists(cancelParticipationRequest.getUserId());

        ParticipationRequestDto result = participationRequestMapper.toDto(
                participationRequestRepository.findById(cancelParticipationRequest.getRequestId()).get());
        participationRequestRepository.deleteById(cancelParticipationRequest.getRequestId());

        log.info("ParticipationRequestServiceImpl: result of cancel(): {}, which has been deleted", result);
        return result;
    }

    private ParticipationRequest mapEntity(NewParticipationRequest newParticipationRequest) {
        userExistenceValidator.validateExists(newParticipationRequest.getUserId());
        //todo eventExistenceValidator.validateExists();
        //подтянуть также Event в сущность

        ParticipationRequest entity = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .requester(userRepository.findById(newParticipationRequest.getUserId()).get())
                .status(Status.PENDING)
                .build();

        log.trace("ParticipationRequestServiceImpl: result of mapEntity(): {}", entity);
        return entity;


    }

    @Override
    public ParticipationRequestDto getDto(ParticipationRequest entity) {
        return participationRequestMapper.toDto(entity);
    }

    @Override
    public void validateExists(Long id) {
        if (participationRequestRepository.findById(id).isEmpty()) {
            log.info("attempt to find participationRequest with id: {}", id);
            throw new RuntimeException();
            //todo throw new NotFoundException("reason", "message");
            // ждём ветку main_svc_exceptions
        }
    }
}
