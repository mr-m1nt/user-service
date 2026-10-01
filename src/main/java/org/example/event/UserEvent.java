package org.example.event;

public record UserEvent(
        String operation, // "CREATE" или "DELETE"
        String email
) {}
