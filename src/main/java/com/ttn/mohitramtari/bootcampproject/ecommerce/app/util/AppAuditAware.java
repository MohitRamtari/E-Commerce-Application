package com.ttn.mohitramtari.bootcampproject.ecommerce.app.util;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AppAuditAware implements AuditorAware<String> {

    @Autowired
    UserRepository userRepository;

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            return Optional.ofNullable(
                    SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        } catch (Exception e) {
            return Optional.of("SYSTEM");
        }
    }
}
