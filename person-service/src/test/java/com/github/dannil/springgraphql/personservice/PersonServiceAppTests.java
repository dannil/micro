package com.github.dannil.springgraphql.personservice;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
public class PersonServiceAppTests {

	public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
		DockerImageName.parse("postgres:16")
	);

    public static RabbitMQContainer rabbitMQ = new RabbitMQContainer(
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

	@BeforeAll
	public static void beforeAll() {
		postgres.start();
		rabbitMQ.start();
	}

	@AfterAll
	public static void afterAll() {
		postgres.stop();
		rabbitMQ.stop();
	}

	@Test
	public void contextLoads() {
	}

}
