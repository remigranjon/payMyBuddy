package com.paymybuddy.backend.service;

import com.paymybuddy.backend.model.entity.User;
import com.paymybuddy.backend.model.request.RegisterRequest;
import com.paymybuddy.backend.repository.interfaces.UserRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }
    
    public User saveUser(RegisterRequest user) throws Exception {
        if (user == null) {
            throw new Exception("User request cannot be null");
        }
        try {
            validateUser(user);
        } catch (IllegalArgumentException e) {
            throw new Exception("Invalid user data: " + e.getMessage());
        }
        User newUser = User.builder().username(user.getUsername()).email(user.getEmail()).password(passwordEncoder.encode(
                user.getPassword())).build();
        try {
            return userRepository.save(newUser);
        } catch (DataAccessException e) {
            throw new Exception("Error saving user");
        }
    }

    public void authenticateAndSetUser(String email, String password) throws Exception {
        try {
            log.info("Authenticating user with email: {}", email);
            // Créer un token d'authentification
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, password);

            // Authentifier l'utilisateur
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // Définir l'utilisateur comme authentifié dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("User authenticated successfully: {}", email);
            log.debug("Authentication : {}", authentication.isAuthenticated());
        } catch (Exception e) {
            throw new Exception("Authentication failed: " + e.getMessage());
        }
    }

    public boolean authenticateUser(String email, String rawPassword) throws Exception {
        User user;
        try {
            user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));
        } catch (DataAccessException e) {
            throw new Exception("Error retrieving user for authentication");
        }
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public User findByUsername(String username) throws Exception {
        try {
            return userRepository.findByUsername(username).orElseThrow(() -> new Exception("User not found"));
        } catch (DataAccessException e) {
            throw new Exception("Error retrieving user by username");
        }
    }

    public User findByEmail(String email) throws Exception {
        try {
            return userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));
        } catch (DataAccessException e) {
            throw new Exception("Error retrieving user by email");
        }
    }

    private void validateUser(RegisterRequest user) throws IllegalArgumentException {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        /* String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (!user.getPassword().matches(regex)) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character");
        } */
    }
}
