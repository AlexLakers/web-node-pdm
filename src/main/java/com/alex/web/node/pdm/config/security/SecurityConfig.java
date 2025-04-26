package com.alex.web.node.pdm.config.security;

import com.alex.web.node.pdm.model.enums.RoleName;
import com.alex.web.node.pdm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * This clas is a configuration which contains two beans with security settings.
 * The first 'securityFilterChain' bean used for handling request from  controllers.
 * The second 'securityFilterChain' bean used for handling request from  rest-controllers.
 * The order annotation sets a necessary order.
 */

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserService userService;

    //For controllers
    @Bean
    @Order(20)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) //logout used GET method then I need to disable csrf
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/login/**", "/logout/**", "/registration/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/error/**").permitAll()
                        .requestMatchers("http://localhost:8085/login/oauth2/code/google").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users").hasAuthority(RoleName.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/users/{id}").hasAnyAuthority(RoleName.USER.name(), RoleName.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/users/delete").hasAnyAuthority(RoleName.USER.name(), RoleName.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/users/{id}/update").hasAnyAuthority(RoleName.USER.name(), RoleName.ADMIN.name())
                        .requestMatchers("/specifications/**").hasAnyAuthority(RoleName.USER.name(), RoleName.ADMIN.name())
                        .requestMatchers("/details/**").hasAnyAuthority(RoleName.USER.name(), RoleName.ADMIN.name())
                        .anyRequest().denyAll())
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/specifications"))
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .defaultSuccessUrl("/specifications")
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .oidcUserService(userService))
                )
                /* .requestMatchers("/users/{[0-9]+}/delete").hasAuthority(RoleName.ADMIN.name())*/
                /* .requestMatchers()*/
                // .requestMatchers("/admin/users/**").hasAnyAuthority(RoleName.ADMIN.name(), RoleName.USER.name())


                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID"));
        return http.build();

    }

    //For rest controllers
    @Bean
    @Order(10)
    public SecurityFilterChain securityFilterChainRest(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/api/v1/**", "/v3/api-docs/**", "/swagger-ui/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("api/v1/users/{id}/specifications").hasAnyAuthority(RoleName.USER.name(), RoleName.ADMIN.name())
                        .requestMatchers("/api/v1/users/**").hasAnyAuthority(RoleName.USER.name(), RoleName.ADMIN.name())
                        .requestMatchers("/api/v1/specifications/{id}/details").hasAnyAuthority(RoleName.USER.name(), RoleName.ADMIN.name())
                        .requestMatchers("/api/v1/specifications/**").hasAnyAuthority(RoleName.USER.name(), RoleName.ADMIN.name())
                        .requestMatchers("/api/v1/details/**").hasAnyAuthority(RoleName.USER.name(), RoleName.ADMIN.name())
                        .requestMatchers("/api/v1/**").authenticated().anyRequest().denyAll())
                .httpBasic(Customizer.withDefaults());
        return http.build();

    }
}