package com.example.demo.config;

import com.example.demo.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, proxyTargetClass = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ClientService clientService;

    // for hide password
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(clientService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling().accessDeniedPage("/403"); // if access denied go to 403

        http.authorizeRequests().antMatchers("/css/**", "/js/**").permitAll(); // for access static resources

        http.formLogin().loginPage("/login").permitAll() // reference to login page
                .usernameParameter("user_nickname") // form name = user_nickname
                .passwordParameter("user_password") // form name = user_password
                .loginProcessingUrl("/auth").permitAll() // form action = auth and used method post
                .failureUrl("/login?error") // if login is failure go to login again
                .defaultSuccessUrl("/"); // if login is success go to home page

        http.logout().logoutUrl("/logout").permitAll() // form action = logout method and used post
                .logoutSuccessUrl("/login");

    }
}
