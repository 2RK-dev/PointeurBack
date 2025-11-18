package io.github.two_rk_dev.pointeurback.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
        return obj instanceof User other && id.equals(other.id)
               && username.equals(other.username)
               && password.equals(other.password)
               && role.equals(other.role);
    }

    public boolean isSuperAdmin() {
        return role.equals("SUPERADMIN");
    }
}
