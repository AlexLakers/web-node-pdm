package com.alex.web.node.pdm.config.security;

import com.alex.web.node.pdm.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Optional;

@EnableJpaAuditing
@EnableEnversRepositories(basePackageClasses = ApplicationRunner.class)
@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return ()->Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(auth->(UserDetails)auth.getPrincipal())
                .map(UserDetails::getUsername);
    }
}
