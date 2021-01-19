package org.openapitools.framework.util;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/14/21
 * <p>Time: 10:51 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Component
public class WebUIConfig implements WebMvcConfigurer {
  @SuppressWarnings({"HardCodedStringLiteral", "HardcodedFileSeparator"})
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");  }
 
}
