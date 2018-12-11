package com.lvguangzeng.gateway;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lvguangzeng.gateway.GatewayConfig.UriConfiguration;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@EnableConfigurationProperties(UriConfiguration.class)
public class GatewayConfig {
	@Bean
	public DiscoveryClientRouteDefinitionLocator GateWayDiscoveryClientRouteDefinitionLocator(
			DiscoveryClient discoveryClient, DiscoveryLocatorProperties properties) {
		properties.setLowerCaseServiceId(true);
		List<String> services = discoveryClient.getServices();
		for (String service : services) {
			log.info("Find Service {}", service);
		}
		return new DiscoveryClientRouteDefinitionLocator(discoveryClient, properties);
	}

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {
		String httpUri = uriConfiguration.getHttpbin();
		return builder.routes()
				.route(p -> p.path("/get").filters(f -> f.addRequestHeader("Hello", "World")).uri(httpUri)).build();
	}

	@ConfigurationProperties
	class UriConfiguration {

		private String httpbin = "http://httpbin.org:80";

		public String getHttpbin() {
			return httpbin;
		}

		public void setHttpbin(String httpbin) {
			this.httpbin = httpbin;
		}
	}
	
}
