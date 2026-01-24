package com.paymybuddy.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.backend.model.entity.User;
import com.paymybuddy.backend.model.request.ConnectionRequest;
import com.paymybuddy.backend.model.request.CreditRequest;
import com.paymybuddy.backend.model.request.PasswordRequest;
import com.paymybuddy.backend.model.request.TransactionRequest;
import com.paymybuddy.backend.service.ConnectionService;
import com.paymybuddy.backend.service.TransactionService;
import com.paymybuddy.backend.service.UserService;

@Controller
public class DashboardController {

    @Autowired 
    private UserService userService;

    @Autowired
    private ConnectionService connectionService;

    @Autowired 
    private TransactionService transactionService;

    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            User user = userService.findByUsername(username);
            model.addAttribute("user", user);
            model.addAttribute("connections", connectionService.getConnectionsForUser(user.getId().intValue()));
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "dashboard/profile";
    }

    @GetMapping("/updatePassword")
    public String showUpdatePasswordForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            User user = userService.findByUsername(username);
            model.addAttribute("user", user);
            model.addAttribute("passwordRequest", new PasswordRequest());
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "dashboard/updatePassword";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@ModelAttribute("passwordRequest") PasswordRequest passwordRequest,
            Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            userService.updatePassword(username, passwordRequest.getCurrentPassword(), passwordRequest.getNewPassword());
            redirectAttributes.addFlashAttribute("successMessage", "Mot de passe mis à jour avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }

    @GetMapping("/connection")
    public String showConnectionForm(Model model) {
        model.addAttribute("connectionRequest", new ConnectionRequest());
        return "dashboard/connection";
    }

    @PostMapping("/connection")
    public String addConnection(@ModelAttribute("connectionRequest") ConnectionRequest connectionRequest,
            Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            User user = userService.findByUsername(username);
            connectionService.addConnection(user.getId().intValue(), connectionRequest.getConnectionEmail());
            redirectAttributes.addFlashAttribute("successMessage", "Connexion ajoutée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/connection";
    }

    @GetMapping("/addCredit")
    public String showAddCreditForm(Model model) {
        model.addAttribute("creditRequest", new CreditRequest());
        return "dashboard/addCredit";
    }

    @PostMapping("/addCredit")
    public String addCredit(@ModelAttribute("amount") CreditRequest creditRequest,
            Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            userService.addCredit(username, creditRequest.getAmount());
            redirectAttributes.addFlashAttribute("successMessage", "Crédit ajouté avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/addCredit";
    }

    @GetMapping("/transfer")
    public String showTransferForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            User user = userService.findByUsername(username);
            model.addAttribute("connections", connectionService.getConnectionsForUser(user.getId().intValue()));
            model.addAttribute("transactionRequest", new TransactionRequest());
            model.addAttribute("transactions", transactionService.getTransactionsForUser(user.getId().intValue()));
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "dashboard/transfer";
    }

    @PostMapping("/transfer")
    public String makeTransfer(@ModelAttribute("transactionRequest") TransactionRequest transactionRequest,
            Model model, RedirectAttributes redirectAttributes) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
            User user = userService.findByUsername(username);
            transactionService.saveTransaction(transactionRequest, user.getId().intValue());
            redirectAttributes.addFlashAttribute("successMessage", "Transfert effectué avec succès.");
        // } catch (Exception e) {
        //     redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        //     throw e;
        // }
        return "redirect:/transfer";
    }
}