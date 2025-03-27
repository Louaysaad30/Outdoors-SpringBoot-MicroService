package tn.esprit.spring.userservice.Security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
    @EnableWebSecurity
    @RequiredArgsConstructor
    @EnableGlobalMethodSecurity(securedEnabled = true)
    public class SecurityConfig {
        private final AuthenticationProvider authenticationProvider;
        private JwtFilter JwtAuthFilter;


        @Bean
        public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
            return httpSecurity
                    .cors(Customizer.withDefaults())
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth-> auth.requestMatchers(
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/v2/api-docs/**",
                                    "/v3/api-docs/**",
                                    "/swagger-resources/**",
                                    "/webjars/**",
                                    "/auth/**",
                                    "/user/**",
                                    "/model-stt/**",
                                    "/applications/**",
                                    "/notifications/**")
                            .permitAll()
                            .anyRequest()
                            .authenticated())

                    .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .oauth2Login(oauth2 -> oauth2
                            .loginPage("/auth/login")
                            .defaultSuccessUrl("/User")
                            .failureUrl("/auth/login?error=true")
                            .permitAll()
                    )
                    .oauth2ResourceServer(oauth2 -> oauth2
                            .jwt(Customizer.withDefaults())
                    ).authenticationProvider(authenticationProvider)
                    .addFilterBefore(JwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
        }

    }
