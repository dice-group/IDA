package org.dice.ida;

import org.dice.ida.constant.IDAConst;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

import java.util.Properties;

@SpringBootApplication
@ImportResource({
		"classpath:config/bean-config.xml"
})
public class IdaApplication extends SpringBootServletInitializer {
	/**
	 * Entry point for Spring Boot application
	 */

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(IdaApplication.class);

		Properties props = new Properties();
		// Async thread timeout
		props.put("spring.mvc.async.request-timeout", IDAConst.TIMEOUT_LIMIT);
		app.setDefaultProperties(props);

		app.run(args);
	}
}
