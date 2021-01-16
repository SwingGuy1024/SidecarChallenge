package org.openapitools.server;


import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openapitools.framework.ResponseUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
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
                       AuthenticationException authException) throws IOException {
    log.debug("JwtAuthenticationEntryPoint.commence");
    log.debug("  Req: path <{}> -- {}", request.getContextPath(), request.getServletPath());
    log.debug("  Req: AuthType {} -- {}", request.getContextPath(), request.getAuthType());
    log.debug("  req: URI:     {}", request.getRequestURI());
    log.debug("  req: Method:  {}", request.getMethod());
    ResponseUtility.logHeaders(request, "EntryPoint");
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getLocalizedMessage());
  }
}
