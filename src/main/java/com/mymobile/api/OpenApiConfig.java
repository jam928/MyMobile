package com.mymobile.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * OpenAPI description of the /api endpoints, shown by Swagger UI at /MyMobile/swagger-ui.html.
 */
@Configuration
public class OpenApiConfig {

	static final String SESSION_COOKIE = "sessionCookie";

	@Bean
	public OpenAPI myMobileOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("MyMobile API")
						.version("1.0")
						.description("""
								The phone catalog is public. The admin endpoints use the same sign-in as the website: \
								sign in as an admin at /MyMobile/account/login in this browser, then use "Try it out" here.

								Requests that change data (PUT) need the CSRF token; Swagger UI sends it automatically \
								from the XSRF-TOKEN cookie."""))
				.components(new Components().addSecuritySchemes(SESSION_COOKIE, new SecurityScheme()
						.type(SecurityScheme.Type.APIKEY)
						.in(SecurityScheme.In.COOKIE)
						.name("JSESSIONID")
						.description("The session cookie set when you sign in on the website")));
	}
}
