package ru.practicum.stat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class EndpointHitCreate {
    @NotBlank(message = "must not be blank")
    @Size(max = 100, message = "max size is 100")
    private String app;
    @NotBlank(message = "must not be blank")
    @Size(max = 100, message = "max size is 100")
    private String uri;
    @NotBlank(message = "must not be blank")
    @Size(max = 32, message = "max size is 32")
    private String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
