package in.wisekart.dto;

import in.wisekart.entity.Role;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final Role role;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
