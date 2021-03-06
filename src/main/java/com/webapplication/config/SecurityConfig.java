package com.webapplication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/", "/join", "/login", "/h2-console/**").permitAll()
                .anyRequest().authenticated();
        http.formLogin()
                .loginPage("/login")
                .usernameParameter("name")
                .permitAll();
        http.logout()
                .logoutSuccessUrl("/");
        http.csrf()
                .ignoringAntMatchers("/h2-console/**")
                .and();
        http.headers()
                .frameOptions()
                .disable();
    }
}
