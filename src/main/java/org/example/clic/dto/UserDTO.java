package org.example.clic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.clic.model.User.Role;
// DTO para transferir datos de usuario
public class UserDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Email(message =  "El correo debe ser válido")
    @NotBlank(message ="El correo es obligatorio")
    private String email;

    @NotNull(message = "El rol es obligatorio")
    private Role role;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
