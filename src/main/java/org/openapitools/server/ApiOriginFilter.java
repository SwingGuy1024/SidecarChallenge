package org.openapitools.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.RequestFacade;
import org.openapitools.framework.util.ReplaceChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-20T04:00:38.477Z")

@Component
public class ApiOriginFilter implements javax.servlet.Filter {
  private static final Logger log = LoggerFactory.getLogger(ApiOriginFilter.class);

  public ApiOriginFilter() {
    log.debug("Instantiating ApiOriginFilter");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain chain) throws IOException, ServletException {
    HttpServletResponse httpResponse = (HttpServletResponse) response;
//    Enumeration<String> aNames = request.getAttributeNames();
    log.debug("  req: class:     {}", request.getClass());
//    log.debug("  req: Method:  {}", request.getMethod());
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      log.debug("req URI {}", httpServletRequest.getRequestURI());
      log.debug("CtxPath {}", httpServletRequest.getContextPath());
      log.debug("PthInfo {}", httpServletRequest.getPathInfo());
      log.debug("LclName {}", httpServletRequest.getLocalName());
      log.debug("LclAddr {}", httpServletRequest.getLocalAddr());
    }

    if (log.isDebugEnabled()) {
      if (request instanceof RequestFacade) {
        RequestFacade facade = (RequestFacade) request;
        final String requestURI = facade.getRequestURI();
        // filter out calls to documentation
        if (!requestURI.contains("springfox")) {
          log.debug("Request URI: {}", charFilter(requestURI));
        }
      } else {
        log.debug("Request of {}", request.getClass());
      }
    }

    httpResponse.addHeader("Access-Control-Allow-Origin", "*");
    httpResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
    httpResponse.addHeader("Access-Control-Allow-Headers", "Content-Type");
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  private static final Pattern OPEN_BRACE_PATTERN = Pattern.compile("%7B");
  private static final Pattern CLOSE_BRACE_PATTERN = Pattern.compile("%7D");

  /**
   *  Replace %7B and %7D with curly braces in url paths
   * @param request The request string
   * @return the corrected String
   */
  private static final String charFilter(final String request) {
    return ReplaceChain.build(request)
        .replaceAll(OPEN_BRACE_PATTERN, "{")
        .replaceAll(CLOSE_BRACE_PATTERN, "}")
        .toString();
  }
  
  private static String findAndReplace(String s, Pattern match, String replacement) {
    return match.matcher(s).replaceAll(replacement);
  }
}
