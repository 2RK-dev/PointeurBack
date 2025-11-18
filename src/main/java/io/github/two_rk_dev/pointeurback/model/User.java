package io.github.two_rk_dev.pointeurback.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username;

    private String password;

    private String role;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User other && Objects.equals(id, other.id)
               && Objects.equals(username, other.username)
               && Objects.equals(password, other.password)
               && Objects.equals(role, other.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, role);
    }

    public boolean isSuperAdmin() {
        return role.equals("SUPERADMIN");
    }
}
