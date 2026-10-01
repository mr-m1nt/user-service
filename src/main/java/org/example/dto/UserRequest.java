package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Данные для создания или обновления пользователя")
public class UserRequest {

    @Schema(
            description = "Имя пользователя",
            example = "Иван"
    )
    @NotBlank(message = "Имя обязательно")
    @Size(max = 100, message = "Имя максимум 100 символов")
    private String name;

    @Schema(
            description = "Email пользователя",
            example = "ivan@example.com"
    )
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;

    @Schema(
            description = "Возраст пользователя",
            example = "25"
    )
    @Min(value = 0, message = "Возраст >= 0")
    @Max(value = 100, message = "Возраст <= 100")
    private Integer age;
}