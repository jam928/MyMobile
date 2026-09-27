package com.mymobile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Replaces web.xml: bootstraps the DispatcherServlet (mapped to "/") and
 * component scanning of com.mymobile.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class MyMobileApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyMobileApplication.class, args);
	}
}
