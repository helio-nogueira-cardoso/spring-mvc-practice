package br.com.helio.springmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(
                        authorizationManagerRequestMatcherRegistry ->
                                authorizationManagerRequestMatcherRegistry.anyRequest().authenticated()
                )
                .oauth2ResourceServer(
                        oauth2ResourceServerConfigurer ->
                                oauth2ResourceServerConfigurer.jwt(Customizer.withDefaults())
                );
        return httpSecurity.build();
    }
}
