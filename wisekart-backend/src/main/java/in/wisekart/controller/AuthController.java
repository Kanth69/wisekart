package in.wisekart.controller;

import in.wisekart.authentication.AuthService;
import in.wisekart.dto.LoginRequest;
import in.wisekart.dto.LoginResponse;
import in.wisekart.dto.RegisterRequest;
import in.wisekart.dto.UserResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse registeredUser = authService.register(request);
        return ResponseEntity.created(URI.create("/api/users/" + registeredUser.getId()))
                .body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
