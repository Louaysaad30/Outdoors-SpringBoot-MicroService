package tn.esprit.spring.userservice.Security;


import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
    @EnableWebSecurity
    @AllArgsConstructor
    @EnableGlobalMethodSecurity(securedEnabled = true)
    public class SecurityConfig {
        private final AuthenticationProvider authenticationProvider;
        private final JwtFilter jwtAuthFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
            return httpSecurity
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth-> auth.requestMatchers(
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/v2/api-docs",
                                    "/v3/api-docs",
                                    "/v3/api-docs/**",
                                    "/swagger-resources",
                                    "/swagger-resources/**",
                                    "/webjars/**",
                                    "/configuration/ui",
                                    "/configuration/security",
                                    "/auth/**",
                                    "/user/**",
                                    "/model-stt/**",
                                    "/applications/**",
                                    "/notifications/**",
                                    "/statistics/**",
                                    "/ws/**"
                            )
                            .permitAll()
                            .anyRequest()
                            .authenticated())
                    .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authenticationProvider(authenticationProvider)
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
        }

    }
