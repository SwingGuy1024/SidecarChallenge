package org.openapitools;

import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.hateoas.mediatype.collectionjson.CollectionJsonLinkDiscoverer;
import org.springframework.plugin.core.SimplePluginRegistry;

/**
 * Caching Configuration Reference: https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#why-spring-redis
 */

@EnableCaching()
@SpringBootApplication
@ComponentScan(basePackages = {
    "org.openapitools",
    "org.openapitools.api",
    "org.openapitools.server",
    "org.openapitools.repositories",
    "org.openapitools.framework.util",
    "org.openapitools.configuration"
})
public class OpenAPI2SpringBoot implements CommandLineRunner {//}, CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(OpenAPI2SpringBoot.class);
    public static final String MENU_ITEM_CACHE = "menuItems";

    @Override
    public void run(String... arg0) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> log.error("Thread {}: {}", t.getName(), e.getLocalizedMessage(), e));
        if ((arg0.length > 0) && "exitcode".equals(arg0[0])) { //NON-NLS
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

    // I got a strange error message saying "Parameter 0 of method linkDiscoverers 
    // in org.springframework.hateoas.config.HateoasConfiguration req" (I'm not 
    // using HATEOAS.) This link showed me how to fix it. 
    // https://www.codeleading.com/article/27224372792/
    @Bean
    public LinkDiscoverers discoverers() {
        List<LinkDiscoverer> plugins = new LinkedList<>();
        plugins.add(new CollectionJsonLinkDiscoverer());
        return new LinkDiscoverers(SimplePluginRegistry.create(plugins));
    }

//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("server", 6379));
//    }

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


//    @Override
//    public CacheManager cacheManager() {
//        RedisOperations<String, Object> ops = new 
//        return new RedisCacheManager();
//    }
//
//    @Override
//    public CacheResolver cacheResolver() {
//        // TODO: Write OpenAPI2SpringBoot.cacheResolver()
//        throw new AssertionError("OpenAPI2SpringBoot.cacheResolver() is not yet implemented");
//    }
//
//    @Override
//    public KeyGenerator keyGenerator() {
//        // TODO: Write OpenAPI2SpringBoot.keyGenerator()
//        throw new AssertionError("OpenAPI2SpringBoot.keyGenerator() is not yet implemented");
//    }
//
//    @Override
//    public CacheErrorHandler errorHandler() {
//        // TODO: Write OpenAPI2SpringBoot.errorHandler()
//        throw new AssertionError("OpenAPI2SpringBoot.errorHandler() is not yet implemented");
//    }
}
