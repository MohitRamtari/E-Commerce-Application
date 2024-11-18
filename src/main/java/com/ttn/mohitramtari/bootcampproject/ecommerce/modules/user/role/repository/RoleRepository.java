package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.role.repository;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.role.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleAuthority(String roleAuthority);
}
