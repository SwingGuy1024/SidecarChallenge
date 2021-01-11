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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
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

  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    // configure AuthenticationManager so that it knows from where to load
    // user for matching credentials
    // Use BCryptPasswordEncoder
    log.debug("Calling WebSecurityConfig.configureGlobal() with {}", auth);
    auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

//  @Autowired
//  public WebSecurityConfig(final JwtUserDetailsService jwtUserDetailsService) {
//    super();
//    this.jwtUserDetailsService = jwtUserDetailsService;
//  }


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

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    log.error("Configuring WebSecurityConfig");
    Thread.dumpStack();
    //noinspection HardcodedFileSeparator
    http
        .csrf()
          .disable()
        .cors()
          .disable()
        .authorizeRequests()
          .antMatchers("/authenticate").permitAll()
//          .antMatchers("/login", "/menuItem").permitAll()
//          .antMatchers("/admin/*").hasRole(UserDto.RoleEnum.ADMIN.toString())
//          .antMatchers("/**").hasRole(UserDto.RoleEnum.CUSTOMER.toString())
//          .antMatchers("/", "/home").permitAll()
        .anyRequest().authenticated()
        .and()
          .exceptionHandling()
          .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)//          .httpBasic()
        .and()
          .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//        .and()
//          .anyRequest().authenticated()
//          .and()
//        .httpBasic()
//          .and()
//        .formLogin()
//          .loginPage("/login")
//          .permitAll()
//          .and()
//        .logout()
//          .permitAll()
          ;
//    httpSecurity.csrf().disable()
//        // dont authenticate this particular request
//        .authorizeRequests().antMatchers("/authenticate").permitAll().
//        // all other requests need to be authenticated
//            anyRequest().authenticated().and().
//        // make sure we use stateless session; session won't be used to
//        // store user's state.
//            exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
//        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//    // Add a filter to validate the tokens with every request
//    httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
  }

  
  @Bean
  @Override
  protected UserDetailsService userDetailsService() {
    return jwtUserDetailsService;
  }
}


