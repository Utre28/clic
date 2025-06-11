package org.example.clic.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime expiryDate;

    public VerificationToken() {}

    public VerificationToken(String token, User user, LocalDateTime expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    public enum Type {
        VERIFICATION,
        RESET_PASSWORD
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type = Type.VERIFICATION;

    // getters y setters
    public Long getId() { return id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public boolean isExpired(int minutes) {
        return expiryDate.isBefore(java.time.LocalDateTime.now().minusMinutes(minutes));
    }
}
