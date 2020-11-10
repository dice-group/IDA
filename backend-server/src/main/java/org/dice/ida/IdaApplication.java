package org.dice.ida;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({
		"classpath:config/bean-config.xml"
})
public class IdaApplication extends SpringBootServletInitializer {
	/**
	 * Entry point for Spring Boot application
	 */

    public static void main(String[] args) {
        SpringApplication.run(IdaApplication.class, args);
    }
}
