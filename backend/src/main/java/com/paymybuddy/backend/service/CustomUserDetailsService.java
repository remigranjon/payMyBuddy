package com.paymybuddy.backend.service;

import com.paymybuddy.backend.model.entity.CustomUserDetails;
import com.paymybuddy.backend.model.entity.User;
import com.paymybuddy.backend.repository.interfaces.UserRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        log.info("User found: {}", user.getEmail());
        return new CustomUserDetails(user);
    }
}