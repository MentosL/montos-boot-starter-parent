package com.montos.boot.montos.core.helper;

import org.aopalliance.aop.Advice;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validator;

public class MontosMethodValidationPostProcessor extends MethodValidationPostProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected Advice createMethodValidationAdvice(Validator validator) {
		return (validator != null ? new MontosMethodValidationInterceptor(validator) : new MontosMethodValidationInterceptor());
	}

}
