package com.ttn.mohitramtari.bootcampproject.ecommerce.app.config.security;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model.User;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationManager implements AuthenticationManager {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationManager.class);


    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        logger.info("CustomAuthenticationManager::authenticate execution started.");
        logger.debug(
                "CustomAuthenticationManager::authenticate authenticating credentials, generating token");

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        User user = userRepository.findByUserEmail(username);

        if (user == null) {
            logger.error("Exception occurred while authenticating");
            throw new UsernameNotFoundException("Invalid credentials");
        }

        if (user.getUserIsLocked()) {
            logger.error("Exception occurred while authenticating");
            throw new BadCredentialsException("Account is locked");
        }

        if (!user.getUserIsActive()) {
            logger.error("Exception occurred while authenticating");
            throw new BadCredentialsException("Account is not active yet");
        }

        if (user.getUserIsDeleted()) {
            logger.error("Exception occurred while authenticating");
            throw new BadCredentialsException("Your account has been deleted");
        }

        if (!passwordEncoder.matches(password, user.getUserPassword())) {
            logger.debug("CustomAuthenticationManager::authenticate invalid attempt");
            Integer counter = user.getUserInvalidAttemptCount();
            if (counter < 2) {
                user.setUserInvalidAttemptCount(++counter);
                userRepository.save(user);
            } else {
                user.setUserIsLocked(true);
                user.setUserInvalidAttemptCount(0);
                userRepository.save(user);
                logger.debug("CustomAuthenticationManager::authenticate  account locked");
            }
            logger.error("Exception occurred while authenticating");
            throw new BadCredentialsException("Invalid Credentials");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getAuthority()));

        user.setUserInvalidAttemptCount(0);
        userRepository.save(user);
        logger.debug("CustomAuthenticationManager::authenticate credentials authenticated ");
        logger.info("CustomAuthenticationManager::authenticate execution ended.");
        return new UsernamePasswordAuthenticationToken(username, password, authorities);
    }
}