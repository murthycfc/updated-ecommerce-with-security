package com.dependency.ecommerce.facade.impl;

import com.dependency.ecommerce.dto.*;
import com.dependency.ecommerce.exception.AccessDeniedException;
import com.dependency.ecommerce.exception.UserNotFoundException;
import com.dependency.ecommerce.facade.api.AuthFacade;
import com.dependency.ecommerce.model.User;
import com.dependency.ecommerce.repo.UserRepository;
import com.dependency.ecommerce.service.api.AuthService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AuthFacadeImpl implements AuthFacade {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthFacadeImpl(AuthService authService, UserRepository userRepository){
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        return authService.register(request);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        return authService.login(request);
    }

    @Override
    public List<UserResponse> getAllUsers(UUID userId) {
        validateAdmin(userId);
        return authService.getAllUsers();
    }

    private void validateAdmin(UUID userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId.toString())
                .orElseThrow(()-> new UserNotFoundException("User not Found"));
        if(!"ADMIN".equals(user.getRole())){
            throw new AccessDeniedException("Access denied. Only admin can perform this operation.");
        }
    }
}
