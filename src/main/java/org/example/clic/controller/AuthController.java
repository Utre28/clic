package org.example.clic.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.clic.dto.UserDTO;
import org.example.clic.model.User;
import org.example.clic.model.VerificationToken;
import org.example.clic.service.EmailService;
import org.example.clic.service.UserService;
import org.example.clic.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Optional;

@Controller
public class AuthController {
    private final UserService userService;
    private final VerificationTokenService tokenService;
    private final EmailService emailService;

    @Value("${app.url:http://localhost:8080}")
    private String appUrl;

    public AuthController(UserService userService, VerificationTokenService tokenService, EmailService emailService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailService = emailService;
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
    public String showRegisterForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult br, Model model, HttpSession session) {
        if (br.hasErrors()) {
            return "register";
        }
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "register";
        }
        if (userService.existsByEmail(userDTO.getEmail())) {
            model.addAttribute("error", "El correo ya está registrado.");
            return "register";
        }
        // Solo crea el objeto User, NO lo guardes aún en la base de datos
        User user = userService.registerUser(userDTO);
        session.setAttribute("pendingUser", user);

        // Genera código y envía email
        String code = String.format("%06d", new java.util.Random().nextInt(999999));
        session.setAttribute("pendingCode", code);
        session.setAttribute("pendingEmail", user.getEmail());
        emailService.sendEmail(user.getEmail(), "Código de verificación", "Tu código de verificación es: <b>" + code + "</b>");

        // Redirige a la página de verificación (no renderiza, sino redirect)
        return "redirect:/verify-code?email=" + user.getEmail();
    }

    @GetMapping("/verify-code")
    public String showVerifyCodeForm(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "verify-code";
    }

    @PostMapping("/verify-code")
    public String verifyCode(@RequestParam("email") String email,
                             @RequestParam("code") String code,
                             HttpSession session,
                             Model model) {
        String pendingCode = (String) session.getAttribute("pendingCode");
        String pendingEmail = (String) session.getAttribute("pendingEmail");
        User user = (User) session.getAttribute("pendingUser");

        if (user == null || pendingCode == null || pendingEmail == null) {
            model.addAttribute("error", "Sesión expirada. Por favor, regístrate de nuevo.");
            model.addAttribute("userDTO", new UserDTO());
            return "register";
        }

        if (!code.equals(pendingCode) || !email.equals(pendingEmail)) {
            model.addAttribute("error", "Código incorrecto o expirado.");
            model.addAttribute("email", email);
            return "verify-code";
        }
        // Guardar usuario solo si el código es correcto
        user.setEmailVerified(true);
        userService.save(user);

        // Limpia la sesión
        session.removeAttribute("pendingUser");
        session.removeAttribute("pendingCode");
        session.removeAttribute("pendingEmail");

        model.addAttribute("message", "¡Registro y verificación completados! Ya puedes iniciar sesión.");
        return "verify-code";
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        Optional<VerificationToken> tokenOpt = tokenService.findByToken(token);
        if (tokenOpt.isPresent()) {
            User user = tokenOpt.get().getUser();
            user.setEmailVerified(true);
            userService.save(user);
            tokenService.deleteByToken(token);
            model.addAttribute("message", "Correo verificado correctamente. Ya puedes iniciar sesión.");
            return "login";
        } else {
            model.addAttribute("error", "Token inválido o expirado.");
            return "login";
        }
    }
}
