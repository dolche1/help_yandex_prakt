package ru.practicum.category.model.dto;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewCategoryDto {
    @NotBlank(message = "Название категории должно быть указано")
    @Size(min = 1, max = 50, message = "Размер названия категории должен составлять от 1 до 50 символов")
    private String name;
}