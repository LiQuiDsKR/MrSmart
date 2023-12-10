package com.care4u.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.care4u.constant.Role;
import com.care4u.hr.membership.MembershipService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	MembershipService membershipService;
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            	.antMatchers("/membership/login","/membership/login/error").permitAll()
                .antMatchers("/**").hasAnyRole(Role.USER.name(),Role.ADMIN.name(),Role.MANAGER.name())
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/membership/login")
                .defaultSuccessUrl("/")
                .usernameParameter("code")
                .passwordParameter("password")
                .failureUrl("/membership/login/error")
            .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/membership/logout"))
                .logoutSuccessUrl("/")
            ;
    }
    
    @Override
    public void configure(WebSecurity web) throws Exception{
    	web.ignoring().antMatchers("/css/**","/js/**","/img/**","/plugins/**","/fonts/**","/bootstrap/**","/images/**","/sass/**");
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.userDetailsService(membershipService).passwordEncoder(passwordEncoder());
    }
}
