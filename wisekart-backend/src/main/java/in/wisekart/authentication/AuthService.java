package in.wisekart.authentication;

import in.wisekart.dto.LoginRequest;
import in.wisekart.dto.LoginResponse;
import in.wisekart.dto.RegisterRequest;
import in.wisekart.dto.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
