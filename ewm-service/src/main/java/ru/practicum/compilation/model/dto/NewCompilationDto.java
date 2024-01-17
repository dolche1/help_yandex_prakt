package ru.practicum.compilation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class NewCompilationDto {
    private Boolean pinned;
    @NotBlank
    @Size(min = 1, max = 50, message = "Размер заголовка подборки должен составлять от до 50 символов")
    private final String title;
    private final List<Integer> events;
}
