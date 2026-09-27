package com.mymobile.it;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;

/**
 * A real MySQL (same version as docker-compose.yml) for the integration tests.
 * @ServiceConnection points the app's datasource at it; Flyway then runs every migration.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	MySQLContainer mysql() {
		return new MySQLContainer("mysql:8.4");
	}
}
