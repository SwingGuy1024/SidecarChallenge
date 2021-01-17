package org.openapitools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.ComponentScan;

@Cacheable(cacheNames = "menuItems")
@SpringBootApplication
@ComponentScan(basePackages = {
    "org.openapitools",
    "org.openapitools.api",
    "org.openapitools.server",
    "org.openapitools.repositories",
    "org.openapitools.framework.util",
    "org.openapitools.configuration"
})
public class OpenAPI2SpringBoot implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(OpenAPI2SpringBoot.class);
    public static final String MENU_ITEM_CACHE = "menuItems";

    @Override
    public void run(String... arg0) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> log.error("Thread {}: {}", t.getName(), e.getLocalizedMessage(), e));
        if ((arg0.length > 0) && "exitcode".equals(arg0[0])) {
            throw new ExitException();
        }
    }

    public static void main(String[] args) {
        new SpringApplication(OpenAPI2SpringBoot.class).run(args);
    }

    static class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }

    }

//    @Bean
//    public WebMvcConfigurer webConfigurer() {
//        return new WebMvcConfigurer() {
//            
//            /*@Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("*")
//                        .allowedMethods("*")
//                        .allowedHeaders("Content-Type");
//            }*/
//        };
//    }

//    @Bean
//    public Module jsonNullableModule() {
//        return new JsonNullableModule();
//    }

}
