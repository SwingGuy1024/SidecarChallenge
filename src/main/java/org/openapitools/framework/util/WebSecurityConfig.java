package org.openapitools.framework.util;

import org.openapitools.model.UserDto;
import org.openapitools.server.JwtAuthenticationEntryPoint;
import org.openapitools.server.JwtRequestFilter;
import org.openapitools.server.JwtUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Adapted from https://www.tutorialspoint.com/spring_security/spring_security_with_jwt.htm
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 12/30/20
 * <p>Time: 1:03 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);
  
  private final JwtUserDetailsService jwtUserDetailsService;

  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  private final JwtRequestFilter jwtRequestFilter;
  
  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder authBuilder) {
    authBuilder.eraseCredentials(false);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return jwtUserDetailsService.getEncoder();
  }

  @Autowired
  public WebSecurityConfig(
      final JwtUserDetailsService jwtUserDetailsService,
      final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint
//      final JwtRequestFilter jwtRequestFilter
  ) {
    super();
    this.jwtUserDetailsService = jwtUserDetailsService;
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
//    this.jwtRequestFilter = jwtRequestFilter;
    jwtRequestFilter = new JwtRequestFilter(jwtUserDetailsService);
  }

  @Override
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
  }

  @SuppressWarnings({"HardCodedStringLiteral", "HardcodedFileSeparator"})
  @Override
  public void configure(WebSecurity web) {
    web.ignoring().mvcMatchers(HttpMethod.OPTIONS, "/**");
    // ignore swagger 
    web
        .ignoring()
        .mvcMatchers("/swagger-ui.html/**", "/configuration/**", "/swagger-resources/**", "/v2/api-docs", "/webjars/**")
        ;
  }


  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    log.error("Configuring WebSecurityConfig");
    
    // The numerous matchers for permitAll() were a vain attempt to get the API Docs to work. The partly fixed the problem,
    // but the docs are still unavailable. However, this now assumes that all admin APIs will start with /admin, and all
    // customer APIs requiring authentication will start with orders. I don't authenticate any other prefixes because I
    // don't plan to have any, beyond menuItem, which does not require logging in. The menu is always available.
    //noinspection HardcodedFileSeparator
    http
        .csrf()
          .disable()
        .cors()
          .disable()
        .formLogin()
          .disable()
        .authorizeRequests()
//          .anyRequest().permitAll()
        .antMatchers("/admin/**").hasRole(UserDto.RoleEnum.ADMIN.toString())
        .antMatchers("/order/**").hasRole(UserDto.RoleEnum.CUSTOMER.toString())
        .antMatchers("/login", "/menuItem", "/**", "/home", "/swagger-ui.html", "/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**", "/swagger-resources/**").permitAll()
        .and()
          .exceptionHandling()
          .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)//          .httpBasic()
        .and()
          .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  @Override
  protected UserDetailsService userDetailsService() {
    return jwtUserDetailsService;
  }
}


