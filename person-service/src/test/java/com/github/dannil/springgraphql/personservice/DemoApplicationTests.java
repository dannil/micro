package com.github.dannil.springgraphql.personservice;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
public class DemoApplicationTests {

	public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
		DockerImageName.parse("postgres:16")
	);

	public static KafkaContainer kafka = new KafkaContainer(
		DockerImageName.parse("confluentinc/cp-kafka:7.5.1")
	);

	@DynamicPropertySource
	public static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
	}

	@BeforeAll
	public static void beforeAll() {
		postgres.start();
		kafka.start();
	}

	@AfterAll
	public static void afterAll() {
		postgres.stop();
		kafka.stop();
	}

	@Test
	public void contextLoads() {
	}

}
