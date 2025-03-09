package com.alex.web.node.pdm.config.security;

import com.alex.web.node.pdm.model.enums.RoleName;
import com.alex.web.node.pdm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/login", "/users/registration").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users").hasAuthority(RoleName.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/users/{id}").hasAnyAuthority(RoleName.USER.name(), RoleName.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/users/delete").hasAnyAuthority(RoleName.USER.name(), RoleName.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/users/{id}/update").hasAnyAuthority(RoleName.USER.name(), RoleName.ADMIN.name())

                        .requestMatchers(HttpMethod.GET, "/error/**").permitAll()
                        .requestMatchers("/specifications/**").permitAll()
                        .anyRequest().permitAll())
                //.requestMatchers("http://localhost:8085/login/oauth2/code/google").permitAll())
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/index")
                        .permitAll())//Change to /specification


                /* .requestMatchers("/users/{[0-9]+}/delete").hasAuthority(RoleName.ADMIN.name())*/
                /* .requestMatchers()*/
                // .requestMatchers("/admin/users/**").hasAnyAuthority(RoleName.ADMIN.name(), RoleName.USER.name())


                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID"));
        return http.build();
    }
    }