package com.paymybuddy.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.paymybuddy.backend.model.request.LoginRequest;
import com.paymybuddy.backend.model.request.RegisterRequest;
import com.paymybuddy.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
            BindingResult bindingResult, Model model) {
        log.info("Tentative d'inscription pour l'utilisateur : {}", registerRequest.getUsername());

        if (bindingResult.hasErrors()) {

            return "auth/register"; // Retourne le template avec les erreurs
        }

        try {
            userService.saveUser(registerRequest);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }

        return "redirect:/auth/login"; // Redirige après succès
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
            BindingResult bindingResult, Model model) {
        log.info("Tentative de connexion pour l'utilisateur : {}", loginRequest.getUsername());
        try {
            userService.authenticateAndSetUser(loginRequest.getUsername(), loginRequest.getPassword());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                log.info("User authenticated: {}", authentication.getName());
                log.info("Authorities: {}", authentication.getAuthorities());
            } else {
                log.info("No user is authenticated.");
            }
            return "redirect:/profile"; // Redirige après succès
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }
}