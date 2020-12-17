package com.gesthelp.vote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gesthelp.vote.service.SecurityRoles;
import com.gesthelp.vote.web.service.UtilisateurLoginService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UtilisateurLoginService userDetailsService;

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		// @formatter:off
        http
        .csrf().disable()
        .authorizeRequests()
	        .antMatchers("/admin/**").hasRole(SecurityRoles.ADMIN)
	        .antMatchers("/js/**").permitAll()
	        .antMatchers("/login*", "/logout*").permitAll()
	        .antMatchers("/", "/index*").permitAll()
	        .antMatchers("/scrut/*").hasRole(SecurityRoles.SCRUTATEUR)
	        .anyRequest().hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.VOTANT, SecurityRoles.VOTANT_RECETTE)
	        .and()
        .formLogin()
	        .loginPage("/login")
	        .loginProcessingUrl("/perform_login")
	        .defaultSuccessUrl("/loginSuccess", true)
	        .failureUrl("/login?error=true")
	        //.failureHandler(authenticationFailureHandler())
	        .and()
        .logout()
        	.logoutUrl("/perform_logout")
        	.logoutSuccessUrl("/")
        	.deleteCookies("JSESSIONID")
        //.logoutSuccessHandler(logoutSuccessHandler());
        ;
        // @formatter:on
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
