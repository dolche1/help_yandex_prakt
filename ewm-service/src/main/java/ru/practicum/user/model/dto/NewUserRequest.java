package ru.practicum.user.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@RequiredArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Email пользователя должен быть заполнен")
    @Email
    @Size(min = 6, max = 254, message = "Размер email пользователя должен составлять от 6 до 254 символов")
    private final String email;
    @NotBlank(message = "Имя пользователя должно быть заполнено")
    @Size(min = 2, max = 250, message = "Размер имени пользователя должен составлять от 2 до 250 символов")
    private final String name;
}