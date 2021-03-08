package com.neptunedreams;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/**
 * <p>I need to put the actual application class, OpenAPI2SpringBoot, in the Common module, because other modules need access to it
 * for their unit tests. But maven packaging needs demand that I put it in the Server module, which depends on all the other modules,
 * so is the last module to get built, presumably for packaging reasons. So this class exists solely to wrap the actual application
 * in its {@code main()} method. If anyone has a better suggestion, please let me know.</p>
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 3/2/21
 * <p>Time: 11:43 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@ComponentScan(basePackages = {
    "com.neptunedreams.api",
    "org.openapitools",
    "org.openapitools.configuration"
})
@EnableCaching
@SpringBootApplication
public class ServerMaster extends OpenAPI2SpringBoot implements CommandLineRunner {
  public static void main(String[] args) {
    new SpringApplication(ServerMaster.class).run(args);
  }
}
