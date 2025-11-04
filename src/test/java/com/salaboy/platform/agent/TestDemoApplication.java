package com.salaboy.platform.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestDemoApplication {

  public static void main(String[] args) {

    SpringApplication.from(PlatformAgentApplication::main)
        .with(DaprTestContainersConfig.class)
        .run(args);
    org.testcontainers.Testcontainers.exposeHostPorts(8080);
  }

}
