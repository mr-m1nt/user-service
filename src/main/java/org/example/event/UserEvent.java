package org.example.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private String eventType;  // "USER_CREATED" или "USER_DELETED"
    private String email;
    private Long userId;
    private long timestamp;
}