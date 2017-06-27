package de.codeboje.springbootbook.consumer;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.codeboje.springbootbook.commons.CommentstoreObjectMapper;

@SpringBootApplication
@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableDiscoveryClient
public class ConsumerApplication {

	@Bean
	@Primary
	ObjectMapper objectMapper() {
		return new CommentstoreObjectMapper();
	}

	@Bean
	@LoadBalanced
	RestTemplate restTemplate(@Value("${commentstore.auth.user}") String username,
			@Value("${commentstore.auth.password}") String password, RestTemplateBuilder restTemplateBuilder) {
		final RestTemplate restTemplate = restTemplateBuilder.build();
		restTemplate.getInterceptors().add(0, new BasicAuthorizationInterceptor(username, password));
		return restTemplate;
	}

	@Bean
	public Filter logFilter() {
	    CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
	    filter.setIncludeQueryString(true);
	    filter.setIncludeHeaders(true);
	    filter.setMaxPayloadLength(5120);
	    return filter;
	}
	
	@Bean
	CommentService commentService() {
		return new CommentService();
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ConsumerApplication.class, args);
	}

}
