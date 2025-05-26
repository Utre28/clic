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
        // Convertir DTO a entidad User
        User user = userMapper.toEntity(userDTO);
        userService.save(user);
        return "redirect:/login?registered";
    }
}
