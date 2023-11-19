package com.Sinorbis.demo.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // Authentication configuration (in-memory users for demo purpose)
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
            .withUser("user").password("{noop}password").roles("USER");
    }

    // Authorization configuration (secure endpoints)
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // For simplicity, CSRF protection disabled (not recommended in production)
            .authorizeRequests()
                .antMatchers("/chatBot/messages").permitAll() // Allow access without authentication
                .antMatchers("/chatBot/messages").authenticated() // Secure endpoint, requires authentication
                .and()
            .httpBasic(); // Use HTTP Basic Authentication
    }
}
