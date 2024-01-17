package ru.practicum.user.service;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.model.dto.NewUserRequest;
import ru.practicum.user.model.dto.UserDto;
import ru.practicum.user.model.dto.UserMapper;
import ru.practicum.exceptions.EntityAlreadyExists;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return repository.findUsers(ids, pageable).stream()
                .map(UserMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        try {
            User user = repository.save(UserMapper.userFromNewUserRequest(newUserRequest));
            return UserMapper.userToUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExists("Пользователь с email '" + newUserRequest.getEmail() + "' уже существует");
        }
    }

    @Override
    @Transactional
    public void deleteUser(int userId) {
        if (!repository.existsById(userId))
            throw new EntityNotFoundException("Пользователь с id=" + userId + " не найден");
        repository.deleteById(userId);
    }
}
