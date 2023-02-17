package com.example.popconback.configurations;

import com.example.popconback.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig {

   private final UserService userService;

   @Value("${jwt.secret}")
   private String secretKey;
   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
      return httpSecurity
              .httpBasic().disable()
              .csrf().disable()
              .cors().and()
              .authorizeRequests()
              .antMatchers("/api/v1/user/login","/api/v1/user/refresh").permitAll()
              .antMatchers("/swagger-ui/**","/swagger-resources/**","/v3/api-docs").permitAll()
              .antMatchers("/api/v1/token").permitAll()
              .anyRequest().authenticated()
//              .antMatchers(HttpMethod.POST).authenticated()
//              .antMatchers(HttpMethod.GET).authenticated()
              .and()
              .sessionManagement()
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
              .and()
              .addFilterBefore(new JwtFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)
              .build();
   }

}
