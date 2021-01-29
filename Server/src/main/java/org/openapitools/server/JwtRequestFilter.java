package org.openapitools.server;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Supplier;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.openapitools.framework.ResponseUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
  private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

  static final String AUTHORIZATION = "Authorization";
  static final String BEARER_ = "Bearer ";

  private final JwtTokenUtil jwtTokenUtil = JwtTokenUtil.getInstance();
  private Supplier<SecurityContext> contextSupplier = SecurityContextHolder::getContext;

  public JwtRequestFilter() {
    super();
    log.trace("Instantiating JwtRequestFilter");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain
  ) throws ServletException, IOException {
    log.debug("JwtRequestFilter.doFilterInternal");
//    ResponseUtility.logHeaders(request, "jwtRequestFilter");
    log.trace("Tail: {}", ResponseUtility.getUriTail(request));

    final String requestTokenHeader = request.getHeader(AUTHORIZATION);

    // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
    if ((requestTokenHeader == null) || !requestTokenHeader.startsWith(BEARER_)) {
      log.debug("No Bearer token");
      chain.doFilter(request, response);
      return;
    }
    String jwtToken = requestTokenHeader.substring(BEARER_.length());
    log.trace("Found token: {}", jwtToken);

    processFilter(request, jwtToken);
    log.trace("filter processed");
    chain.doFilter(request, response);
    log.trace("Filter chained");
  }

  private void processFilter(final HttpServletRequest request, final String jwtToken) {
    String username;
    try {
      username = jwtTokenUtil.getUsernameFromToken(jwtToken);
    } catch (ExpiredJwtException e) {
      log.debug("Token expired");
      final UsernamePasswordAuthenticationToken expired 
          = new UsernamePasswordAuthenticationToken("Unknown User", "Expired Token");
      contextSupplier.get().setAuthentication(expired);
      return;
    }

    log.trace("Time remains on token");

    // Once we get the token validate it.
    final SecurityContext context = contextSupplier.get();
    if ((username != null) && (context.getAuthentication() == null)) {

      String role = JwtTokenUtil.instance.getRoleFromToken(jwtToken);
      final Collection<? extends GrantedAuthority> authorities = UserAndRoleDetails.getAuthoritiesFromRole(role);
      log.trace("Authenticating user {}", username);
      if (log.isTraceEnabled()) {
        log.trace("user Found with {}", authorities.iterator().next());
      }

      // if token is valid configure Spring Security to manually set authentication
      if (jwtTokenUtil.validateToken(jwtToken)) {

        log.trace("User Authenticated with first authorityCount = {}", authorities.size());
        if (log.isDebugEnabled() && !authorities.isEmpty()) {
          log.trace("First authority: {}", authorities.iterator().next());
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken 
            = new UsernamePasswordAuthenticationToken(username, null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // After setting the Authentication in the context, we specify that the current user is authenticated.
        // So it passes the Spring Security Configurations successfully.
        context.setAuthentication(usernamePasswordAuthenticationToken);
      } else {
        log.trace("Authentication failed");
      }
    }
  }

  // Package level methods for testing only.
  void setContextSupplierTestOnly(Supplier<SecurityContext> supplier) {
    contextSupplier = supplier;
  }
}