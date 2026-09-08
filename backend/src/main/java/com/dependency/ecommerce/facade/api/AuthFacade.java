package com.dependency.ecommerce.facade.api;

import com.dependency.ecommerce.dto.*;

import java.util.List;
import java.util.UUID;

public interface AuthFacade {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    List<UserResponse> getAllUsers(UUID userId);
}