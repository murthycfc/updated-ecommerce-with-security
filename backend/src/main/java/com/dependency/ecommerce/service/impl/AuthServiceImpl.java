package com.dependency.ecommerce.service.impl;

import com.dependency.ecommerce.dto.*;
import com.dependency.ecommerce.exception.*;
import com.dependency.ecommerce.model.User;
import com.dependency.ecommerce.repo.UserRepository;
import com.dependency.ecommerce.security.JwtService;
import com.dependency.ecommerce.service.api.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    AuthServiceImpl(UserRepository userRepo, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {

        try {
            if(userRepo.isEmailExisted(request.getEmail())>0) {
                log.error("Registration failed: email already exists {}",request.getEmail());
                throw new EmailAlreadyRegisteredException("Email already registered");
            }

            User user = new User();
            BeanUtils.copyProperties(request, user);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole("MEMBER");
            User savedUser = userRepo.save(user);

            RegisterResponse response = new RegisterResponse();
            BeanUtils.copyProperties(savedUser, response);
            response.setMessage("Registration successful");
            response.setUserId(savedUser.getId());
            return response;
        }
        catch (EmailAlreadyRegisteredException ex){
            throw ex;
        }
        catch (Exception ex) {
            log.error("Database error while registering user", ex);
            throw new UserServiceException("Unable to register user");
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            User user = userRepo.findEmail(request.getEmail())
                    .orElseThrow(()->{
                        log.error("Login failed: invalid email {}",request.getEmail());
                        return new InvalidCredentialsException("Invalid email or password");
                            });

            if(user.isDeleted()){
                log.error("Login failed: inactive user {}", request.getEmail());
                throw new UserInactiveException("User account is inactive");
            }

//            boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

//            if(!passwordMatches){
//                log.error("Login failed: invalid password for email {}", request.getEmail());
//                throw new InvalidCredentialsException("Invalid email or password");
//            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            LoginResponse response = new LoginResponse();
            BeanUtils.copyProperties(user, response);
            response.setToken(token);
            response.setMessage("Login Success");
            response.setUserId(user.getId());
            return response;
        }
        catch (InvalidCredentialsException | UserInactiveException ex){
            throw ex;
        }
        catch (Exception ex) {
            log.error("Database error while logging in user", ex);
            throw new UserServiceException("Unable to login user");
        }
    }

    @Override
    public List<UserResponse> getAllUsers() {
        try {
            List<User> users = userRepo.findAllActiveUsers();
            List<UserResponse> responses = new ArrayList<>();
            for(User user : users){
                UserResponse response = new UserResponse();
                BeanUtils.copyProperties(user, response);
                responses.add(response);
            }
            return responses;
        }
        catch (Exception ex){
            log.error("Failed to execute getAllUsers due to database issue", ex);
            throw new UserServiceException("Unable to fetch users from the database");
        }
    }
}
