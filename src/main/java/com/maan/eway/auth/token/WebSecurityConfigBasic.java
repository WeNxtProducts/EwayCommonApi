package com.maan.eway.auth.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
 

@Configuration
@EnableWebSecurity
@Order(999)
public class WebSecurityConfigBasic extends WebSecurityConfigurerAdapter {

	@Autowired
	private BasicAuthenticationPoint basicAuthenticationPoint;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();
	//	http.antMatcher("/post/notification/**").authorizeRequests().anyRequest().hasRole("USER").and().httpBasic();

http
                .requestMatchers().antMatchers("/basicauth/**")
                .and()
                .authorizeRequests().anyRequest().hasRole("USER")
                .and()
                .httpBasic();
	
		http.cors();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.httpBasic().authenticationEntryPoint(basicAuthenticationPoint);
	}
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		//PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		auth.inMemoryAuthentication().withUser("ewayapi").password(bCryptPasswordEncoder.encode("ewayapi123#")).roles("USER");
	}
	  
/*
	@Bean
	public static NoOpPasswordEncoder bpasswordEncoder() {
		return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	}*/
	/*@Bean
    public BCryptPasswordEncoder basicpasswordEncoder(){
    	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }*/
}
