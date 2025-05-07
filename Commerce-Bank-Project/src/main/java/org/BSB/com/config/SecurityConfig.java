package org.BSB.com.config;

import org.BSB.com.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          // Allow frames for H2 console
          .headers(h -> h.frameOptions().disable())

          // Disable CSRF only on the H2 console
          .csrf(csrf -> csrf
              .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
          )

          // Authorize requests
          .authorizeHttpRequests(auth -> auth
              // permit all to login, register, static, and H2 console
              .requestMatchers(
                new AntPathRequestMatcher("/login"),
                new AntPathRequestMatcher("/login"),   // both GET and POST
                new AntPathRequestMatcher("/register"),
                new AntPathRequestMatcher("/css/**"),
                new AntPathRequestMatcher("/js/**"),
                new AntPathRequestMatcher("/images/**"),
                new AntPathRequestMatcher("/h2-console/**")
              ).permitAll()
              // everything else requires authentication
              .anyRequest().authenticated()
          )

          // your custom login page
          .formLogin(form -> form
              .loginPage("/login")
              .loginProcessingUrl("/login")
              .usernameParameter("email")
              .passwordParameter("password")
              .defaultSuccessUrl("/dashboard", true)
              .permitAll()
          )

          // logout
          .logout(logout -> logout
              .logoutUrl("/logout")
              .logoutSuccessUrl("/login?logout")
              .permitAll()
          );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return email -> repo.findByEmail(email)
        .map(user -> org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .roles("USER")
            .build()
        )
        .orElseThrow(() -> new UsernameNotFoundException("No user for " + email));
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
        PasswordEncoder encoder,
        UserDetailsService uds) throws Exception {

        return http
        .getSharedObject(AuthenticationManagerBuilder.class)
        .userDetailsService(uds)
        .passwordEncoder(encoder)
        .and()
        .build();
    }
}
