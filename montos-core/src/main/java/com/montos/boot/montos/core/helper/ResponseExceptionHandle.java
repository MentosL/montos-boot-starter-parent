package com.jimistore.boot.nemo.core.helper;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.jimistore.boot.nemo.core.api.exception.ValidatedException;
import com.jimistore.boot.nemo.core.response.Response;
import com.jimistore.boot.nemo.core.util.JsonString;

public class ResponseExceptionHandle implements HandlerExceptionResolver  {
	
	private static Logger log = Logger.getLogger(ResponseExceptionHandle.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		log.error(ex.getMessage(), ex);
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		
		if(ex instanceof UndeclaredThrowableException){
			ex = (Exception) ((UndeclaredThrowableException)ex).getUndeclaredThrowable();
		}
		
		if(ex instanceof ConstraintViolationException){
			ConstraintViolationException mcve = (ConstraintViolationException) ex;
			@SuppressWarnings("rawtypes")
			ConstraintViolation cv = mcve.getConstraintViolations().iterator().next();
			String[] fields = cv.getPropertyPath().toString().split("\\.");
			String field = fields[fields.length-1];
			ex = new ValidatedException(new StringBuilder(cv.getMessage()).append(field).toString(),ex);
		}
		
		try {
			Response<?> resp = Response.error(ex);
			response.getWriter().print(JsonString.toJson(resp));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ModelAndView();
	}

}
