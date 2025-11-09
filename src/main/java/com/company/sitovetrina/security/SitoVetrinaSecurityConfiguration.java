package com.company.sitovetrina.security;

import io.jmix.core.JmixSecurityFilterChainOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;

/**
 * This configuration complements standard security configurations that come from Jmix modules (security-flowui, oidc,
 * authserver).
 * <p>
 * You can configure custom API endpoints security by defining {@link SecurityFilterChain} beans in this class.
 * In most cases, custom SecurityFilterChain must be applied first, so the proper
 * {@link org.springframework.core.annotation.Order} should be defined for the bean. The order value from the
 * {@link io.jmix.core.JmixSecurityFilterChainOrder#CUSTOM} is guaranteed to be smaller than any other filter chain
 * order from Jmix.
 * <p>
 * Example:
 *
 * <pre>
 * &#064;Bean
 * &#064;Order(JmixSecurityFilterChainOrder.CUSTOM)
 * SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
 *     http.securityMatcher("/public/**")
 *             .authorizeHttpRequests(authorize ->
 *                     authorize.anyRequest().permitAll()
 *             );
 *     return http.build();
 * }
 * </pre>
 *
 * @see io.jmix.securityflowui.security.FlowuiVaadinWebSecurity
 */
@Configuration
public class SitoVetrinaSecurityConfiguration {

    // Implementazione UserDetails per utente anonimo
    public static class AnonymousUserDetails implements UserDetails {
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(() -> "anonymous-role");
        }

        @Override
        public String getPassword() {
            return "";
        }

        @Override
        public String getUsername() {
            return "anonymous";
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    @Bean
    @Order(JmixSecurityFilterChainOrder.CUSTOM)
    public SecurityFilterChain anonymousAccessFilterChain(HttpSecurity http) throws Exception {
        http
                // URL accessibili ad anonimi (es. home, login, risorse pubbliche)
                .securityMatcher( "/home", "/login", "/public/**")
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .anonymous(anonymous -> anonymous
                        .principal(new AnonymousUserDetails()) // assegna principal anonimo UserDetails
                        .authorities("anonymous-role")       // assegna ruolo anonimo
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}