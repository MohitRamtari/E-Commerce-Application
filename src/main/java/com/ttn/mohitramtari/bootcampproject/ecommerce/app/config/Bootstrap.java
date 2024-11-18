package com.ttn.mohitramtari.bootcampproject.ecommerce.app.config;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.model.Admin;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model.User;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.repository.UserRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.role.model.Role;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.role.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements ApplicationRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (roleRepository.count() == 0) {
            addUserRoles();
        }
        if (userRepository.count() == 0) {
            addAdmin();
        }
    }

    private void addUserRoles() {
        roleRepository.save(new Role("ROLE_ADMIN"));
        roleRepository.save(new Role("ROLE_SELLER"));
        roleRepository.save(new Role("ROLE_CUSTOMER"));
    }

    private void addAdmin() {
        User user = new Admin();
        user.setUserId(1L);
        user.setUserFirstName("Mohit");
        user.setUserLastName("Ramtari");
        user.setUserEmail("mohit.ramtari.2001@gmail.com");
        user.setUserPassword(passwordEncoder.encode("Mohit123@"));
        user.setUserIsActive(true);
        user.setUserIsDeleted(false);
        user.setUserIsExpired(false);
        user.setUserIsLocked(false);

        Role role = roleRepository.findByRoleAuthority("ROLE_ADMIN");
        user.setRole(role);

        userRepository.save(user);
    }
}
