package in.wisekart.dto;

import in.wisekart.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private final String token;
    private final String type;
    private final Long id;
    private final String firstName;
    private final String email;
    private final Role role;
}
