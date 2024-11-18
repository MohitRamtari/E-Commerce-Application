package com.ttn.mohitramtari.bootcampproject.ecommerce.app.config.security.service;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model.User;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.repository.UserRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.role.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("Invalid Credential :: " + username);
        }

        return new org.springframework.security.core.userdetails.User(user.getUserEmail(),
                user.getUserPassword(), getAuthorities(user.getRole()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Role roles) {
        return List.of(new SimpleGrantedAuthority(roles.getAuthority()));
    }
}