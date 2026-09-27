package com.mymobile.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcherEntry;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

/**
 * Login, logout and access rules.
 *
 * CSRF protection is on for every POST/PUT/DELETE. Thymeleaf adds the token to every form rendered
 * with th:action; JavaScript clients such as Swagger UI read it from the XSRF-TOKEN cookie and send
 * it in the X-XSRF-TOKEN header.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// after login, return to the page that asked for it (without adding "?continue" to its URL)
		HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
		requestCache.setMatchingRequestParameterName(null);

		// signed-out visitors: API calls get 401, everything else is sent to the login page
		AuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(
				new LoginUrlAuthenticationEntryPoint("/account/login"),
				new RequestMatcherEntry<>(PathPatternRequestMatcher.withDefaults().matcher("/api/**"),
						new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

		http
			.requestCache(cache -> cache.requestCache(requestCache))
			.authorizeHttpRequests(auth -> auth
				// public pages
				.requestMatchers("/", "/main/**", "/photos/**", "/css/**", "/error").permitAll()
				.requestMatchers("/account/login", "/account/register", "/account/addCustomer",
						"/account/forgotPW", "/account/reset", "/account/updateCredentials").permitAll()
				// public API and its documentation
				.requestMatchers("/api/phones", "/api/phones/**").permitAll()
				.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
				// admin pages and API (AdminInterceptor also re-checks the role in the database)
				.requestMatchers("/admin", "/admin/**", "/api/admin/**").hasRole("ADMIN")
				// everything else under /account needs a logged in customer
				.anyRequest().authenticated())
			.formLogin(form -> form
				.loginPage("/account/login")
				.loginProcessingUrl("/account/loggedIn")
				.usernameParameter("email")
				.passwordParameter("password")
				// back to the page that asked for a login, or the account page
				.defaultSuccessUrl("/account/myAccount")
				.failureUrl("/account/login?error"))
			.logout(logout -> logout
				.logoutUrl("/account/logout")
				.logoutSuccessUrl("/account/login?logout"))
			// token in a cookie readable by JavaScript (for Swagger UI) as well as in forms
			.csrf(csrf -> csrf.spa())
			.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(entryPoint));

		return http.build();
	}

	// BCrypt, stored with a "{bcrypt}" prefix so the algorithm can be changed later
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
