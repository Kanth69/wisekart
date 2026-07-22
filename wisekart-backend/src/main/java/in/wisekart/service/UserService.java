package in.wisekart.service;

import in.wisekart.dto.CreateUserRequest;
import in.wisekart.dto.UserResponse;
import in.wisekart.dto.RegisterRequest;
import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse registerCustomer(RegisterRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);
}
