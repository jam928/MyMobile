package com.mymobile.it;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import jakarta.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import com.mymobile.entity.Customer;
import com.mymobile.entity.Role;
import com.mymobile.security.CustomerUserDetails;
import com.mymobile.service.PhoneService;
import com.mymobile.service.PhotoService;

/**
 * Base class for integration tests: the whole application (security, MVC, Thymeleaf, JPA, Flyway)
 * against MySQL in Testcontainers. Every test runs in a transaction that is rolled back afterwards.
 * MinIO is replaced by a mock.
 */
@SpringBootTest(properties = "spring.jpa.show-sql=false")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
abstract class IntegrationTest {

	protected static final String PASSWORD = "password123";

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected PhoneService phoneService;

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	protected JdbcTemplate jdbc;

	@MockitoBean
	protected PhotoService photoService;

	@Autowired
	private EntityManager entityManager;

	/**
	 * Reads one value with SQL. Pending JPA changes are written first: the test's transaction never
	 * commits, so without the flush SQL wouldn't see what the requests changed.
	 */
	protected <T> T query(String sql, Class<T> type, Object... args) {
		entityManager.flush();
		return jdbc.queryForObject(sql, type, args);
	}

	/**
	 * Changes data with SQL, then clears JPA's cache so the next request reads the new values
	 * (like a real request, which starts with an empty cache).
	 */
	protected void update(String sql, Object... args) {
		entityManager.flush();
		jdbc.update(sql, args);
		entityManager.clear();
	}

	protected Customer createCustomer(String email, Role role) {
		Customer customer = new Customer();
		customer.setName(email.substring(0, email.indexOf('@')));
		customer.setEmail(email);
		customer.setPassword(passwordEncoder.encode(PASSWORD));
		customer.setBirthday("1990-01-01");
		customer.setRole(role);
		phoneService.addCustomer(customer);
		return customer;
	}

	// signs the request in as the customer, with the role they have right now
	protected static RequestPostProcessor signedInAs(Customer customer) {
		return user(new CustomerUserDetails(customer));
	}

	protected int phoneId(String name) {
		return jdbc.queryForObject("SELECT pid FROM phones WHERE name = ? LIMIT 1", Integer.class, name);
	}
}
