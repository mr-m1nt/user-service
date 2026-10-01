package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.hateoas.UserModelAssembler;
import org.example.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(
        name = "Users",
        description = "API для управления пользователями"
)
public class UserController {

    private final UserService userService;
    private final UserModelAssembler userModelAssembler;

    @Operation(
            summary = "Создать пользователя",
            description = "Создаёт нового пользователя и возвращает его данные"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Пользователь успешно создан"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные пользователя"
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResponse>> createUser(
            @Valid @RequestBody UserRequest request) {

        UserResponse response =
                userService.createUser(request);

        EntityModel<UserResponse> model =
                userModelAssembler.toModel(response);

        URI location =
                linkTo(
                        methodOn(UserController.class)
                                .getUser(response.getId())
                ).toUri();

        return ResponseEntity
                .created(location)
                .body(model);
    }

    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает данные пользователя по его идентификатору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь найден"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>> getUser(
            @PathVariable Long id) {

        UserResponse response =
                userService.getUserById(id);

        return ResponseEntity.ok(
                userModelAssembler.toModel(response)
        );
    }

    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список пользователей получен"
    )
    @GetMapping
    public ResponseEntity<
            CollectionModel<EntityModel<UserResponse>>>
    getAllUsers() {

        List<EntityModel<UserResponse>> users =
                userService.getAllUsers()
                        .stream()
                        .map(userModelAssembler::toModel)
                        .toList();

        CollectionModel<EntityModel<UserResponse>> model =
                CollectionModel.of(
                        users,
                        linkTo(
                                methodOn(UserController.class)
                                        .getAllUsers()
                        ).withSelfRel()
                );

        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Обновить пользователя",
            description = "Обновляет данные существующего пользователя"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно обновлён"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>>
    updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {

        UserResponse response =
                userService.updateUser(id, request);

        return ResponseEntity.ok(
                userModelAssembler.toModel(response)
        );
    }

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Пользователь успешно удалён"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}