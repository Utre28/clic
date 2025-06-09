package org.example.clic.controller;

import jakarta.validation.Valid;
import org.example.clic.dto.UserDTO;
import org.example.clic.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/logout-success")
    public String logoutPage() {
        return "logout";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "registro";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                                      BindingResult bindingResult,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            return "registro";
        }
        if (userService.existsByEmail(userDTO.getEmail())) {
            bindingResult.rejectValue("email", "error.userDTO", "Ya existe una cuenta con este correo.");
            return "registro";
        }
        try {
            userService.registerUser(userDTO);
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("confirmPassword", "error.userDTO", ex.getMessage());
            return "registro";
        } catch (Exception ex) {
            if (ex.getCause() != null && ex.getCause().getMessage().contains("Duplicate entry")) {
                bindingResult.rejectValue("email", "error.userDTO", "Ya existe una cuenta con este correo.");
                return "registro";
            }
            throw ex;
        }
        return "redirect:/login?registered";
    }
}
