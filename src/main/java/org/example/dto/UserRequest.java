package org.example.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "Имя обязательно")
    @Size(max = 100, message = "Имя максимум 100 символов")
    private String name;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;

    @Min(value = 0, message = "Возраст >= 0")
    @Max(value = 100, message = "Возраст <= 100")
    private Integer age;
}
