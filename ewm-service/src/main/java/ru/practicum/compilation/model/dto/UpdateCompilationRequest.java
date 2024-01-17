package ru.practicum.compilation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCompilationRequest {
    private Boolean pinned;
    @Size(min = 1, max = 50, message = "Размер заголовка подборки должен составлять от 1 до 50 символов")
    private String title;
    private List<Integer> events;
}