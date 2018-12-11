package com.lvguangzeng.gateway;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ResponseStatusException;

@Configuration
@EnableConfigurationProperties({ ServerProperties.class, ResourceProperties.class })
public class ErrorHandlerConfiguration {

	private final ServerProperties serverProperties;

	private final ApplicationContext applicationContext;

	private final ResourceProperties resourceProperties;

	private final List<ViewResolver> viewResolvers;

	private final ServerCodecConfigurer serverCodecConfigurer;

	public ErrorHandlerConfiguration(ServerProperties serverProperties, ResourceProperties resourceProperties,
			ObjectProvider<List<ViewResolver>> viewResolversProvider, ServerCodecConfigurer serverCodecConfigurer,
			ApplicationContext applicationContext) {
		this.serverProperties = serverProperties;
		this.applicationContext = applicationContext;
		this.resourceProperties = resourceProperties;
		this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
		this.serverCodecConfigurer = serverCodecConfigurer;
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
		JSONErrorWebExceptionHandler exceptionHandler = new JSONErrorWebExceptionHandler(errorAttributes,
				this.resourceProperties, this.serverProperties.getError(), this.applicationContext);
		exceptionHandler.setViewResolvers(this.viewResolvers);
		exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
		exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
		return exceptionHandler;
	}

	class JSONErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

		public JSONErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
				ErrorProperties errorProperties, ApplicationContext applicationContext) {
			super(errorAttributes, resourceProperties, errorProperties, applicationContext);
		}

		@Override
		protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
			return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
		}

		/**
		 * 根据status获取对应的HttpStatus
		 * 
		 * @param errorAttributes
		 */
		@Override
		protected HttpStatus getHttpStatus(Map<String, Object> errorAttributes) {
			HttpStatus status = (HttpStatus) errorAttributes.get("status");
			return HttpStatus.valueOf(status.value());
		}

		/**
		 * 自定义错误信息
		 */
		@Override
		protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
			Throwable ex = super.getError(request);
			HttpStatus status = HttpStatus.BAD_GATEWAY;
			if (ex instanceof NotFoundException) {
				status = HttpStatus.NOT_FOUND;
			} else if (ex instanceof ResponseStatusException) {
				ResponseStatusException responseStatusException = (ResponseStatusException) ex;
				status = responseStatusException.getStatus();
			}
			return responseBbody(status, buildMessage(request, ex));
		}

		/**
		 * 构建异常信息
		 * 
		 * @param request
		 * @param ex
		 * @return
		 */
		private String buildMessage(ServerRequest request, Throwable ex) {
			StringBuilder message = new StringBuilder("Failed to handle request [");
			message.append(request.methodName());
			message.append(" ");
			message.append(request.uri());
			message.append("]");
			if (ex != null) {
				message.append(": ");
				message.append(ex.getMessage());
			}
			return message.toString();
		}

		private Map<String, Object> responseBbody(HttpStatus status, String errorMessage) {
			Map<String, Object> map = new HashMap<>();
			map.put("status", status);
			map.put("message", errorMessage);
			map.put("data", null);
			return map;
		}

	}

}
