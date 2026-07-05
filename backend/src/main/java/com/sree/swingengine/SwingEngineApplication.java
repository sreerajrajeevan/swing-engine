package com.sree.swingengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class SwingEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwingEngineApplication.class, args);
	}

}
