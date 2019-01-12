package com.demo.config;

import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import com.demo.model.User;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
//	@Autowired
//	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private DataSource dataSource;
	
	@Autowired
    private UserDetailsService userDetailService;
	
	@Value("${spring.queries.users-query}")
    private String usersQuery;

    @Value("${spring.queries.roles-query}")
    private String rolesQuery;
    
    
    
    @Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		    .authorizeRequests()
		    .antMatchers("/", "/login", "/signup", "/signup2", "/confirm", "/succeeded", "/signup-succeeded", "/error").permitAll()  //  the "/" and "/login" paths are configured to not require any authentication.
		    .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
		    .antMatchers("/show").hasAuthority("ROLE_ADMIN")
		    .antMatchers("/edit").hasAuthority("ROLE_USER")
		    .antMatchers("/todo","/createTodo").hasAnyRole("ADMIN", "USER")
		    .anyRequest().authenticated()
//		    .and().csrf().disable()
		    .and()
		.formLogin()
		    .loginPage("/login").failureUrl("/login?error=true")
		    .defaultSuccessUrl("/") 
		    .permitAll()
		    .usernameParameter("username") // user 'username' columm as username (email can also be a choice)
            .passwordParameter("password")
		    .and()
		.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/").and().exceptionHandling()
			.accessDeniedPage("/access-denied");
	}
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
    	auth.authenticationProvider(authenticationProvider());
//        auth.
//                jdbcAuthentication()
//                .usersByUsernameQuery(usersQuery)
//                .authoritiesByUsernameQuery(rolesQuery)
//                .dataSource(dataSource)
//                .passwordEncoder(bCryptPasswordEncoder);
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userDetailService);
        return authenticationProvider;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
           .ignoring()
           .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "/vendor/**");
    }
    
    
    
    /**
     *  it sets up an in-memory user store with a single user.
     */
//	@Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//        UserDetails user =
//        		User.withDefaultPasswordEncoder()
//                .username("hannah")
//                .password("1234")
//                .roles("USER")
//                .build();
//        
//        return new InMemoryUserDetailsManager(user);
//	}

}
