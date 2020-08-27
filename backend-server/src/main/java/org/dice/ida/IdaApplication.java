package org.dice.ida;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class IdaApplication extends SpringBootServletInitializer {
	/**
	 * Entry point for Spring Boot application
	 */

    public static void main(String[] args) {
        SpringApplication.run(IdaApplication.class, args);
    }
}
