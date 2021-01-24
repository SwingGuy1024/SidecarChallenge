package org.openapitools.framework.util;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/8/21
 * <p>Time: 3:43 PM
 *
 * @author Miguel Mu\u00f1oz
 */
//@Component
//@Order(1)
public class AuthFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);
  
  @Override
  public void init(final FilterConfig filterConfig) throws ServletException {
    log.error("init AuthFilter");
  }

  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
    log.error("LocalName: {}", request.getLocalName());
    log.info("LocalAddr:    {}", request.getLocalAddr());
    log.info("Scheme:       {}", request.getScheme());
    log.info("vServerName:  {}", request.getServletContext().getVirtualServerName());
    log.info("ContextPath:  {}", request.getServletContext().getContextPath());
  }

  @Override
  public void destroy() {
  }
}
