package com.dependency.ecommerce.security;

import com.dependency.ecommerce.model.User;
import com.dependency.ecommerce.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findEmail(email)
                .orElseThrow(()->{
                    return new UsernameNotFoundException("Username not found with email");
                });

        return new CustomUserPrincipal(user);
    }
}
