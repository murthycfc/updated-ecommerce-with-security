package com.dependency.ecommerce.controller;

import com.dependency.ecommerce.dto.*;
import com.dependency.ecommerce.facade.api.AuthFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthFacade authFacade;
    public AuthController(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authFacade.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authFacade.login(request));
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam UUID userId){
        return ResponseEntity.ok(authFacade.getAllUsers(userId));
    }
}
