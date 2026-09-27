package com.mymobile.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.flywaydb.core.api.migration.Context;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.mymobile.dto.response.BrandCount;

import db.migration.V8__Hash_existing_passwords;

/**
 * The Flyway migrations produce the schema and data the application expects
 * (the context only starts if Hibernate's schema validation passes).
 */
class MigrationIT extends IntegrationTest {

	@Autowired
	DataSource dataSource;

	@Test
	void allMigrationsRanSuccessfully() {
		List<String> versions = jdbc.queryForList(
				"SELECT version FROM flyway_schema_history WHERE success = 1 ORDER BY installed_rank", String.class);

		assertThat(versions).containsExactly("1", "2", "3", "4", "5", "6", "7", "8");
	}

	@Test
	void seedsThePhoneCatalogAndPlans() {
		assertThat(query("SELECT COUNT(*) FROM phones", Integer.class)).isEqualTo(24);
		assertThat(query("SELECT COUNT(*) FROM phoneplans", Integer.class)).isEqualTo(3);
		assertThat(query("SELECT COUNT(*) FROM phones WHERE brand IS NULL OR description IS NULL", Integer.class)).isZero();

		Map<String, Long> brands = phoneService.countByBrand(null).stream()
				.collect(Collectors.toMap(BrandCount::brand, BrandCount::count));
		assertThat(brands).containsExactlyInAnyOrderEntriesOf(Map.of(
				"Apple", 7L, "Samsung", 6L, "Google", 4L, "Motorola", 3L, "OnePlus", 2L, "Nothing", 1L, "Sony", 1L));
	}

	@Test
	void passwordMigrationHashesPlainTextPasswordsOnly() throws Exception {
		update("INSERT INTO customer (name, email, password, birthday) VALUES ('Plain', 'plain@example.com', 'plain-pass-1', '1990-01-01')");
		update("INSERT INTO customer (name, email, password, birthday) VALUES ('Hashed', 'hashed@example.com', '{bcrypt}already', '1990-01-01')");

		// run the Java migration on the test's connection
		Connection connection = DataSourceUtils.getConnection(dataSource);
		Context context = mock(Context.class);
		when(context.getConnection()).thenReturn(connection);
		new V8__Hash_existing_passwords().migrate(context);

		String migrated = query("SELECT password FROM customer WHERE email = 'plain@example.com'", String.class);
		assertThat(migrated).startsWith("{bcrypt}");
		assertThat(passwordEncoder.matches("plain-pass-1", migrated)).isTrue();
		assertThat(query("SELECT password FROM customer WHERE email = 'hashed@example.com'", String.class))
				.isEqualTo("{bcrypt}already");
	}
}
