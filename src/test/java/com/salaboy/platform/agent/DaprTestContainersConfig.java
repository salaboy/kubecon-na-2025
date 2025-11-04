package com.salaboy.platform.agent;

import io.dapr.testcontainers.Component;
import io.dapr.testcontainers.DaprContainer;
import io.dapr.testcontainers.DaprLogLevel;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@TestConfiguration(proxyBeanMethods = false)
public class DaprTestContainersConfig {

  @Bean
  @ServiceConnection
  DaprContainer daprContainer() {
    return new DaprContainer("daprio/daprd:1.16.0")
        .withAppName("demo")
        .withAppPort(8080)
        .withComponent(new Component("kvstore", "state.in-memory", "v1",
            Map.of("actorStateStore", "true" )))
        .withAppChannelAddress("host.testcontainers.internal")
        //.withDaprLogLevel(DaprLogLevel.DEBUG)
        //.withLogConsumer(outputFrame -> System.out.println(outputFrame.getUtf8String()))
        .withAppHealthCheckPath("/actuator/health");
  }

}