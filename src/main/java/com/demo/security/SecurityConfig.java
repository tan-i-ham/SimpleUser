package com.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  
    @Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		    .authorizeRequests()
		    .antMatchers("/", "login").permitAll()  //  the "/" and "/login" paths are configured to not require any authentication.
		    .anyRequest().authenticated()
		    .and()
		.formLogin()
		    .loginPage("/login")
		    .defaultSuccessUrl("/show") // let the user can see all data
		    .permitAll()
		    .and()
		.logout()
		    .permitAll();
	}
	
    
    /**
     *  it sets up an in-memory user store with a single user.
     */
	@Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
        		User.withDefaultPasswordEncoder()
                .username("hannah")
                .password("1234")
                .roles("USER")
                .build();
        
        return new InMemoryUserDetailsManager(user);
	}

}
