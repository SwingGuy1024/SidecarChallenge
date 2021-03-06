package com.neptunedreams;

import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/**
 * Caching Configuration Reference: https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#why-spring-redis
 */

@EnableCaching
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.neptunedreams",
    "com.neptunedreams.framework.util",
})
public class OpenAPI2SpringBoot implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(OpenAPI2SpringBoot.class);

    @Override
    public void run(String... arg0) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> log.error("Thread {}: {}", t.getName(), e.getLocalizedMessage(), e));
        if ((arg0.length > 0) && "exitcode".equals(arg0[0])) { //NON-NLS
            throw new ExitException();
        }
    }

    static class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }

    }
//
//    // This is not working, and I don't know why, especially since it used to work. The purpose is to 
//    // show stack traces for exceptions that get caught by Spring. I don't know why I don't see these exceptions, since my server code
//    // catches everything and logs it, with stack traces. I've found many things that worked fine under Spring-Boot version 1 that don't
//    // work under version 2.
//    @Bean
//    HandlerExceptionResolver customExceptionResolver() {
//        log.trace("Installing DefaultHandlerExceptionResolver ****");
//        return new DefaultHandlerExceptionResolver() {
//            @Override
//            public ModelAndView resolveException(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) {
//                log.debug("resolveException: ", ex);
//                final ModelAndView modelAndView = super.resolveException(request, response, handler, ex);
//                log.error("Exception: ", ex);
//                return modelAndView;
//            }
//
//            @Override
//            protected String buildLogMessage(final Exception ex, final HttpServletRequest request) {
//                log.debug("build log message", ex);
//                final String message = super.buildLogMessage(ex, request);
//                log.error("{}: {}", ex.getClass().getSimpleName(), ex.getLocalizedMessage(), ex);
//                return message;
//            }
//
//            @Override
//            protected ModelAndView doResolveException(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) {
//                log.debug("doResolveException", ex);
//                ModelAndView result = super.doResolveException(request, response, handler, ex);
//                log.error("{}: {}", ex.getClass().getSimpleName(), ex.getLocalizedMessage(), ex);
//                return result;
//            }
//        };
//    }  
}
