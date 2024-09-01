package com.example.ag_gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@EnableWebFluxSecurity
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable);

        http
                .formLogin(AbstractHttpConfigurer::disable);

        http
                .httpBasic(AbstractHttpConfigurer::disable);



        http
                .authorizeHttpRequests((requests) -> requests
                                .requestMatchers("/signin", "/signup").permitAll()
                                .requestMatchers("/").hasRole("USER")
                                .anyRequest().authenticated())
                .logout((logoutConfig)->
                        logoutConfig
                                .logoutSuccessUrl("/"))
//                .exceptionHandling((exceptionHandling) ->
//                        exceptionHandling
//                                .authenticationEntryPoint(unauthorizedEntryPoint)
//                                .accessDeniedPage(accessDeniedHandler))
//                .userDetailsService(myUserDetailsService());
        ;

        http
                .sessionManagement((session)->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
