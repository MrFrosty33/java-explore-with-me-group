package ru.practicum.explore.with.me.model.category;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCategoryDto {
    @NotEmpty(message = "must not be blank")
    @Size(max = 50, message = "max size is 100")
    private String name;
}