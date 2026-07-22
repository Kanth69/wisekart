package in.wisekart.authentication;

import in.wisekart.dto.LoginRequest;
import in.wisekart.dto.LoginResponse;
import in.wisekart.dto.RegisterRequest;
import in.wisekart.dto.UserResponse;
import in.wisekart.exception.InvalidCredentialsException;
import in.wisekart.security.JwtUtil;
import in.wisekart.security.UserPrincipal;
import in.wisekart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        return userService.registerCustomer(request);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException exception) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();

        return LoginResponse.builder()
                .token(jwtUtil.generateToken(user))
                .type("Bearer")
                .id(user.getId())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
