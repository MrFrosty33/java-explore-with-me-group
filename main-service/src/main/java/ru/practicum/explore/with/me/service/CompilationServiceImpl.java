package ru.practicum.explore.with.me.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.with.me.exception.NotFoundException;
import ru.practicum.explore.with.me.mapper.CompilationMapper;
import ru.practicum.explore.with.me.model.compilation.Compilation;
import ru.practicum.explore.with.me.model.compilation.CompilationCreateDto;
import ru.practicum.explore.with.me.model.compilation.CompilationRequestDto;
import ru.practicum.explore.with.me.model.compilation.CompilationUpdateDto;
import ru.practicum.explore.with.me.model.event.Event;
import ru.practicum.explore.with.me.repository.CompilationRepository;
import ru.practicum.explore.with.me.repository.EventRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;


    @Override
    public CompilationRequestDto create(CompilationCreateDto compilationCreateDto) {
        if (compilationCreateDto.getPinned() == null) {
            compilationCreateDto.setPinned(false);
        }

        List<Long> eventIds = compilationCreateDto.getEvents();
        if (eventIds == null) {
            eventIds = Collections.emptyList();
        } else {
            eventIds = eventIds.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        List<Event> events = eventRepository.findAllById(eventIds);

        Compilation compilation = compilationMapper.toEntity(compilationCreateDto, events);

        return compilationMapper.toRequestDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationRequestDto update(CompilationUpdateDto compilationUpdateDto, Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("The required object was not found.", "Compilation with id=" + compId + " was not found"));

        if (compilationUpdateDto.getTitle() != null) {
            compilation.setTitle(compilationUpdateDto.getTitle());
        }

        if (compilationUpdateDto.getPinned() != null) {
            compilation.setPinned(compilationUpdateDto.getPinned());
        }

        if (compilationUpdateDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(compilationUpdateDto.getEvents());

            compilation.setEvents(events);
        }

        return compilationMapper.toRequestDto(
                compilationRepository.save(compilation)
        );
    }

    @Override
    public void delete(Long compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("The required object was not found.", "Compilation with id=" + compId + " was not found"));

        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationRequestDto> get(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        Page<Compilation> page;

        if (pinned == null) {
            page = compilationRepository.findAll(pageable);
        } else {
            page = compilationRepository.findAllByPinned(pinned, pageable);
        }

        return page.stream()
                .map(compilationMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationRequestDto getById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("The required object was not found.", "Compilation with id=" + compId + " was not found"));

        return compilationMapper.toRequestDto(compilation);
    }
}
