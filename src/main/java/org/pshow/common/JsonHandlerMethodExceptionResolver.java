package org.pshow.common;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHandlerMethodExceptionResolver extends ExceptionHandlerExceptionResolver {
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {
		
		if (handlerMethod == null) {
			return null;
		}
		
		Method method = handlerMethod.getMethod();

		if (method == null) {
			return null;
		}
		
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		try {
			this.objectMapper.writeValue(response.getOutputStream(), exception);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
}