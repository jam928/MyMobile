package com.mymobile.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

/**
 * Java equivalent of the MVC parts of mymobile-servlet.xml and web.xml.
 * (Views are Thymeleaf templates in classpath:/templates and static files are
 * served from classpath:/static, both configured by Spring Boot.)
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final AdminInterceptor adminInterceptor;

	// <welcome-file>index.jsp</welcome-file>, which redirected to main/list
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "/main/list");
		registry.addRedirectViewController("/admin", "/admin/phones");
	}

	// only admins may use /admin/** and /api/admin/**
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(adminInterceptor).addPathPatterns("/admin", "/admin/**", "/api/admin/**");
	}
}
