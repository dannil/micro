package com.github.dannil.micro.accountservice.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(Customizer.withDefaults())
      .authorizeHttpRequests(authorize -> authorize
        .requestMatchers("/graphql").access((auth, context) -> {
          String url = context.getRequest().getRequestURL().toString();
          System.out.println(url);
          if (url.contains("graphiql")) {
            return new AuthorizationDecision(true);
          }
          return new AuthorizationDecision(auth.get().isAuthenticated());
        })
        .requestMatchers("/graphiql/**").permitAll()
        .anyRequest().authenticated())
      .oauth2ResourceServer(oauth2 -> oauth2
        .jwt(Customizer.withDefaults())
    );
    return http.build();
  }

}
