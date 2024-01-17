package ru.practicum.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.event.model.Location;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank(message = "Для события должна быть заполнена аннотация")
    @Size(min = 20, max = 2000, message = "Размер аннотации должен составлять от 20 до 2000 символов")
    private String annotation;
    @NotNull(message = "Категория события не должна быть пустой")
    private Integer category;
    @NotBlank(message = "Описание события не должно быть пустым")
    @Size(min = 20, max = 7000, message = "Размер описания должен составлять от 20 до 7000 символов")
    private String description;
    @NotNull(message = "Дата и время события должны быть указаны")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull(message = "Местоположение события должно быть указано")
    private Location location;
    private boolean paid;
    @PositiveOrZero
    private int participantLimit;
    private Boolean requestModeration;
    @NotBlank(message = "Заголовок события должен быть указан")
    @Size(min = 3, max = 120, message = "Размер заголовка должен составлять от 3 до 120 символов")
    private String title;

}
