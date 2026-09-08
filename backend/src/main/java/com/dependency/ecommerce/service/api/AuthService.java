package com.dependency.ecommerce.service.api;

import com.dependency.ecommerce.dto.*;
import com.dependency.ecommerce.model.User;

import java.util.List;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    List<UserResponse> getAllUsers();
}
