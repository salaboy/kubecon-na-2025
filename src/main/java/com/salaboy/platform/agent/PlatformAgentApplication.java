package com.salaboy.platform.agent;

import io.dapr.spring.workflows.config.EnableDaprWorkflows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDaprWorkflows
public class PlatformAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlatformAgentApplication.class, args);
	}

}
