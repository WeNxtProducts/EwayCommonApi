package com.maan.eway.auth.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1000)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Lazy
	@Autowired
    private UserDetailsService userDetailsService;
    
	@Lazy
    @Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
              .passwordEncoder(bCryptPasswordEncoder);
    }

   	@Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationFilter();
    }
   	
    @Override
    public void configure(WebSecurity web) throws Exception {
       //  Filters will not get executed for the resources
        web.ignoring().antMatchers("/", "/resources/**", "/styles/**", "/static/**", "/jasper/**", "/public/**", "/webui/**", "/h2-console/**"
        	    , "/configuration/**", "/swagger-ui/**", "/swagger-resources/**", "/api-docs", "/api-docs/**","/fonts/**", "/v2/api-docs/**"
                , "/*.html", "/**/*.html" ,"/*.jpg","/**/*.css","/**/*.js","/**/*.png","/**/*.jpg", "/**/*.gif", "/**/*.svg", "/**/*.ico", "/**/*.ttf","/**/*.woff","/**/*.otf");
        }
           
    


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().
                authorizeRequests()
                .antMatchers("/post/integration/**","/login/CheckChangePassword","/login/LoginChangePassword","/login/getForgotPassword","/JasperFiles/*","/JasperFiles/**","/login/getBranchDetail","/login/verify/policy","/authentication/doauth").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/admin/region/list").loginPage("/authentication/login").permitAll()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                /*.sessionManagement().maximumSessions(1).sessionRegistry(sessionRegistry());*/
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CORSFilter(), ChannelProcessingFilter.class);
        
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
    	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }

}
