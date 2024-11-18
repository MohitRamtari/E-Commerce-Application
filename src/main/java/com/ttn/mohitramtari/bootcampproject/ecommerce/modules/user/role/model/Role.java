package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.role.model;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @NotBlank(message = "{authority.blank}")
    @Column(name = "role_authority")
    private String roleAuthority;

    @OneToMany(mappedBy = "role")
    private Set<User> user = new HashSet<>();

    public Role(String authority) {
        this.roleAuthority = authority;
    }

    @Override
    public String getAuthority() {
        return roleAuthority;
    }
}
