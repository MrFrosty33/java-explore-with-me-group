package ru.practicum.explore.with.me.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewUserRequest {
    @Size(max = 255, message = "max size = 512")
    @NotBlank(message = "must not be blank")
    @Email(message = "must be a valid email")
    private String email;
    @Size(max = 255, message = "max size = 255")
    @NotBlank(message = "must not be blank")
    private String name;
}
