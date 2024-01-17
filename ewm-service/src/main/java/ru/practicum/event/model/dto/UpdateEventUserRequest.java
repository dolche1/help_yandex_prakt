package ru.practicum.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.StateAction;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000, message = "Размер аннотации должен составлять от 20 до 2000 символов")
    private  String annotation;
    private  Integer category;
    @Size(min = 20, max = 7000, message = "Размер описания должен составлять от 20 до 7000 символов")
    private  String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private  LocalDateTime eventDate;
    private  Location location;
    private  Boolean paid;
    private  Integer participantLimit;
    private  Boolean requestModeration;
    @Size(min = 3, max = 120, message = "Размер заголовка должен составлять от 3 до 120 символов")
    private  String title;
    private  StateAction stateAction;
}
