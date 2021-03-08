package com.neptunedreams.auth;

import com.neptunedreams.engine.Role;
import org.jetbrains.annotations.NonNls;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
  private static final @NonNls Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);
  
  private final JwtUserDetailsService jwtUserDetailsService;

  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  private final JwtRequestFilter jwtRequestFilter;

  private final PasswordEncoder encoder = new BCryptPasswordEncoder();

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder authBuilder) {
    authBuilder.eraseCredentials(false);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return encoder;
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
    jwtRequestFilter = new JwtRequestFilter();
  }

  @Override
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
    log.trace("configure(AuthenticationBuilder)");
    auth
        .userDetailsService(jwtUserDetailsService)
        .passwordEncoder(passwordEncoder());
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    log.trace("Configuring WebSecurityConfig");

    // The idea here is that the menu is accessible under /menuItem, which does not require authentication, so 
    // potential customers may always look at a menu. Customer operations like placing an order require a 
    // authentication with the CUSTOMER role. Administrative work, such as changing the menu, requires
    // authentication with the ADMIN role.
    //noinspection HardcodedFileSeparator
    http
        .csrf()
          .disable()
        .cors()
          .disable()
        .formLogin()
          .disable()
        .authorizeRequests()
          .antMatchers("/admin/**")
            .hasRole(Role.ADMIN.toString()) 
          .antMatchers("/order/**")
            .hasRole(Role.CUSTOMER.toString())
          .antMatchers(
              "/login/**",
              "/home",
              "/menuItem/**",
              "/swagger-ui.html",
              "/api-docs",
              "/configuration/**",
              "/swagger*/**",
              "/webjars/**",
              "/swagger-resources/**",
              "/v2/api-docs",
              "/"
          ).permitAll()
          .anyRequest().authenticated()
        .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)//          .httpBasic()
        .and()
          .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
          ;
  }

  @Bean
  @Override
  protected UserDetailsService userDetailsService() {
    return jwtUserDetailsService;
  }
}
