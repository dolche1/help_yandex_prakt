package ru.practicum.compilation.service;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.compilation.model.dto.CompilationMapper;
import ru.practicum.compilation.model.dto.NewCompilationDto;
import ru.practicum.compilation.model.dto.UpdateCompilationRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.EntityAlreadyExists;
import ru.practicum.exceptions.EntityNotFoundException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return compilationRepository.findByPinned(pinned, pageable).stream()
                .map(CompilationMapper::compilationToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Integer compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка с id=" + compId + " не найдена"));
        return CompilationMapper.compilationToDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        try {
            List<Integer> ids = newCompilationDto.getEvents();
            List<Event> events = (ids == null) ? Collections.emptyList() : eventRepository.findAllById(ids);
            if (newCompilationDto.getPinned() == null) newCompilationDto.setPinned(false);
            Compilation compilation = compilationRepository.save(
                   CompilationMapper.compilationFromNewDto(newCompilationDto, Set.copyOf(events))
            );
            return CompilationMapper.compilationToDto(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExists(
                    "Подборка с заголовком '" + newCompilationDto.getTitle() + "' уже существует"
            );
        }
    }

    @Override
    @Transactional
    public void deleteCompilation(Integer compId) {
        if (!compilationRepository.existsById(compId))
            throw new EntityNotFoundException("Подборка с id=" + compId + " не найдена");
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Integer compId) {
        String title = updateCompilationRequest.getTitle();
        Boolean pinned = updateCompilationRequest.getPinned();
        List<Integer> newEventIds = updateCompilationRequest.getEvents();
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка с id=" + compId + " не найдена"));
        if (title != null) {
            if (
                    !title.equals(compilation.getTitle()) &&
                            compilationRepository.existsByTitle(title)
            )
                throw new EntityAlreadyExists(
                        "Подборка с заголовком '" + updateCompilationRequest.getTitle() + "' уже существует"
                );
            compilation.setTitle(title);
        }
        if (pinned != null) compilation.setPinned(pinned);
        if (newEventIds != null) {
            List<Integer> oldEventIds = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
            if (!newEventIds.equals(oldEventIds)) {
                Set<Event> events = new HashSet<>(eventRepository.findAllById(newEventIds));
                compilation.setEvents(events);
            }
        }
        return CompilationMapper.compilationToDto(compilationRepository.save(compilation));
    }
}
