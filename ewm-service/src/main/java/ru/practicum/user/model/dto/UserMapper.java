package ru.practicum.user.model.dto;

import ru.practicum.user.model.User;

public class UserMapper {
    public static User userFromNewUserRequest(NewUserRequest newUserRequest) {
        return User.builder()
                .id(null)
                .email(newUserRequest.getEmail())
                .name(newUserRequest.getName())
                .build();
    }

    public static UserDto userToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static UserShortDto userToShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
