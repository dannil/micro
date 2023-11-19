package com.github.dannil.micro.accountservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers(parallel = true)
public class AccountServiceAppTests {

  @Container
  private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      DockerImageName.parse("postgres:16")
  );

  @Container
  private static RabbitMQContainer rabbitMQ = new RabbitMQContainer(
      DockerImageName.parse("rabbitmq:3-management")
  );

  @DynamicPropertySource
  public static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
    registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
    registry.add("spring.rabbitmq.username", rabbitMQ::getAdminUsername);
    registry.add("spring.rabbitmq.password", rabbitMQ::getAdminPassword);
  }

  @Test
  public void contextLoads() {
  }

}
