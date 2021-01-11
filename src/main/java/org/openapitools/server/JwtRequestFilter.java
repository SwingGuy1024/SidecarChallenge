package org.openapitools.server;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openapitools.framework.ResponseUtility;
import org.openapitools.framework.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/9/21
 * <p>Time: 8:23 PM
 *
 * @author Miguel Mu\u00f1oz
 */
//@Component
public class JwtRequestFilter extends OncePerRequestFilter {
  private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

  private static final String AUTHORIZATION = "Authorization";
  private static final String BEARER_ = "Bearer ";
  private final JwtUserDetailsService jwtUserDetailsService;

  private final JwtTokenUtil jwtTokenUtil = JwtTokenUtil.getInstance();

//  @Autowired
  public JwtRequestFilter(final JwtUserDetailsService jwtUserDetailsService) {
    super();
    this.jwtUserDetailsService = jwtUserDetailsService;
    log.debug("Instantiating JwtRequestFilter");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    log.debug("JwtRequestFilter.doFilterInternal");
    ResponseUtility.logHeaders(request, "jwtRequestFilter");
//    Thread.dumpStack();

    final String requestTokenHeader = request.getHeader(AUTHORIZATION);

    // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
    if ((requestTokenHeader == null) || !requestTokenHeader.startsWith(BEARER_)) {
//      throw new IllegalArgumentException(String.format("JWT Token does not begin with Bearer String: %s", requestTokenHeader));
      chain.doFilter(request, response);
      return;
    }
    String jwtToken = requestTokenHeader.substring(BEARER_.length());
    String username = jwtTokenUtil.getUsernameFromToken(jwtToken);

    // Once we get the token validate it.
    if ((username != null) && (SecurityContextHolder.getContext().getAuthentication() == null)) {

      UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

      // if token is valid configure Spring Security to manually set authentication
      if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken 
            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // After setting the Authentication in the context, we specify that the current user is authenticated.
        // So it passes the Spring Security Configurations successfully.
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      }
    }
    chain.doFilter(request, response);
  }

}