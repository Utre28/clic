package org.example.clic.controller;

import jakarta.validation.Valid;
import org.example.clic.dto.UserDTO;
import org.example.clic.mapper.UserMapper;
import org.example.clic.model.User;
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
    private final UserMapper userMapper;

    public AuthController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
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
            User user = userMapper.toEntity(userDTO);
            user.setRole(User.Role.CLIENT);
            userService.save(user);
        } catch (Exception ex) {
            // Si el error es por clave duplicada, muestra el error
            if (ex.getCause() != null && ex.getCause().getMessage().contains("Duplicate entry")) {
                bindingResult.rejectValue("email", "error.userDTO", "Ya existe una cuenta con este correo.");
                return "registro";
            }
            // Si es otro error, puedes mostrar un mensaje general o relanzarlo
            throw ex;
        }
        return "redirect:/login?registered";
    }
}
