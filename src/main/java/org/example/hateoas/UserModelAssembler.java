package org.example.hateoas;

import org.example.controller.UserController;
import org.example.dto.UserResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler
        implements RepresentationModelAssembler<
        UserResponse,
        EntityModel<UserResponse>> {

    @Override
    public EntityModel<UserResponse> toModel(
            UserResponse user) {

        return EntityModel.of(
                user,

                linkTo(
                        methodOn(UserController.class)
                                .getUser(user.getId())
                ).withSelfRel(),

                linkTo(
                        methodOn(UserController.class)
                                .getAllUsers()
                ).withRel("users")
        );
    }
}