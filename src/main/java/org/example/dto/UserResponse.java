package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Data
@Builder
@Relation(
        itemRelation = "user",
        collectionRelation = "users"
)
@Schema(description = "Информация о пользователе")
public class UserResponse {

    @Schema(
            description = "ID пользователя",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Имя пользователя",
            example = "Иван"
    )
    private String name;

    @Schema(
            description = "Email пользователя",
            example = "ivan@example.com"
    )
    private String email;

    @Schema(
            description = "Возраст пользователя",
            example = "25"
    )
    private Integer age;

    @Schema(
            description = "Дата создания пользователя"
    )
    private LocalDateTime createdAt;
}