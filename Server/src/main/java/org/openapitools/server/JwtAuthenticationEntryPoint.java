package org.openapitools.server;


import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openapitools.framework.ResponseUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * TODO:  Figure this class out. Is this why I don't see the authorization exceptions that are getting thrown? Is this where my exceptions
 * todo   are getting cleaned up, and where I lose the cause of the authorization failure?
 * (As of now, this class isn't used. It seems to be invoking a call to /error, but that's not clear. But every time I use it,
 * it logs the current request URI as /error.) 
 * 
 * It doesn't do anything for me, since it doesn't catch the exceptions that I throw. And 
 * calling response.sendError() returns a response with no body.
 * 
 * This class gets enabled when it gets installed in WebSecurityConfig.configure(HttpSecurity) after a call to exceptionHandling().
 * 
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/9/21
 * <p>Time: 8:41 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
  private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

  private static final long serialVersionUID = -7858869558953243875L;

  public JwtAuthenticationEntryPoint() {
    log.debug("Instantiating JwtAuthenticationEntryPoint");
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
                       final AuthenticationException authException) throws IOException {
    if (log.isTraceEnabled()) {
      log.trace("Error processing {}", ResponseUtility.getUriTail(request));
    }
    Throwable prior = null;
    Throwable ex = authException;
    //noinspection ObjectEquality
    while (ex != prior) {
      if (log.isDebugEnabled()) {
        log.debug("Error message from {}: {}", ex.getClass().getSimpleName(), authException.getLocalizedMessage());
      }
      prior = ex;
      ex = ex.getCause();
    }
//    if (authException instanceof CredentialsExpiredException) {
//      response.sendError(HttpServletResponse.SC_GONE, authException.getLocalizedMessage());
//    }
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getLocalizedMessage());
  }
}
