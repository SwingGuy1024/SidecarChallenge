package org.openapitools.server;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Supplier;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.openapitools.framework.ResponseUtility;
import org.openapitools.model.UserAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/9/21
 * <p>Time: 8:23 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public class JwtRequestFilter extends OncePerRequestFilter {
  private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

  // These have package access to be accessible to the unit test.
  static final String AUTHORIZATION = "Authorization";
  static final String BEARER_ = "Bearer ";
  static final String UNKNOWN_USER = "Unknown User";

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
    log.trace("JwtRequestFilter.doFilterInternal");
//    ResponseUtility.logHeaders(request, "jwtRequestFilter");
    if (log.isTraceEnabled()) {
      log.trace("Tail: {}", ResponseUtility.getUriTail(request));
    }

    final String requestTokenHeader = request.getHeader(AUTHORIZATION);

    // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
    if ((requestTokenHeader == null) || !requestTokenHeader.startsWith(BEARER_)) {
      log.debug("No Bearer token");
      chain.doFilter(request, response);
      return;
    }
    String jwtToken = requestTokenHeader.substring(BEARER_.length());
    log.trace("Found token: {}", jwtToken);

    processFilter(request, response, jwtToken);
    log.trace("filter processed");
    chain.doFilter(request, response);
    log.trace("Filter chained");
  }

  private void processFilter(final HttpServletRequest request, final HttpServletResponse response, final String jwtToken) {
    String username;
    try {
      // This validates the token as well
      username = jwtTokenUtil.getUsernameFromToken(jwtToken); // will throw ExpiredJwtException if expired
    } catch (ExpiredJwtException e) {
      // The JWT library call does not test for an expired token or throw this exception until after it has validated the signature.
      // So we know the token is valid, and nobody is trying to hack in. We can safely inform the caller that they need to log
      // back in. (As of this writing, this doesn't work yet.) 
      log.debug("Token expired.");
      // Spring Security expects me to set an "expired token" message into the context, but this returns a 403: Forbidden, which is the
      // same thing it returns if the token has been counterfeited. So there's no way for the client to know that it just needs to log in
      // again for a new token. Throwing a different exception doesn't help. I don't know why the server doesn't return the message
      // specified in the UsernamePasswordAuthenticationToken, which would be more helpful. Instead, we notify the caller by setting
      // a custom header in the Response.

      final UsernamePasswordAuthenticationToken expired = new UsernamePasswordAuthenticationToken(UNKNOWN_USER, "Expired Token");
      contextSupplier.get().setAuthentication(expired);

      // This is the only way to tell the caller that the token has timed out. Spring Security cleans all information out from any
      // exception I throw, so that doesn't work. This is safe because I only get the ExpiredJwtException when it has a valid signature.
      response.setHeader("JwtExpiredToken", "EXPIRED");
      return;

//      return;
      // Above: I tried throwing a CredentialsExpiredException, which extends AuthorizationException, in the hope that it would get
      // caught by the JwtAuthenticationEntryPoint class. It didn't work. That class never saw this one. I'm not sure what good the
      // JwtAuthenticationEntryPoint class does me. Early in the development process, it was catching things, but not anymore.
    } catch (SignatureException se) {
      log.warn("Invalid Token: {}", jwtToken);
      return;
    }
    
    // At this point, we know the token is valid, because it did not throw an exception when getting the username.

    log.trace("Time remains on token");
    
    assert username != null;

    final SecurityContext context = contextSupplier.get();
    if ((username != null) && (context.getAuthentication() == null)) {

      String role = JwtTokenUtil.instance.getRoleFromToken(jwtToken);
      final Collection<? extends GrantedAuthority> authorities = UserAuthority.getAuthoritiesFromRole(role);
      log.trace("Authenticating user {}", username);
      if (log.isTraceEnabled()) {
        log.trace("user Found with {}", authorities.iterator().next());
      }

      // if token is valid configure Spring Security to manually set authentication
      if (jwtTokenUtil.validateToken(jwtToken)) {

        if (log.isTraceEnabled() && !authorities.isEmpty()) {
          log.trace("User Authenticated with first authorityCount = {}", authorities.size());
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