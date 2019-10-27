package com.montos.boot.montos.core.helper;

import com.jimistore.boot.nemo.core.api.exception.ValidatedException;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.validation.beanvalidation.MethodValidationInterceptor;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class NemoMethodValidationInterceptor extends MethodValidationInterceptor {

	public NemoMethodValidationInterceptor() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NemoMethodValidationInterceptor(Validator validator) {
		super(validator);
		// TODO Auto-generated constructor stub
	}

	public NemoMethodValidationInterceptor(ValidatorFactory validatorFactory) {
		super(validatorFactory);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		try{
			Object obj = super.invoke(invocation);
			return obj;
		}catch(Exception e){
			if(e instanceof ConstraintViolationException){
				ConstraintViolationException mcve = (ConstraintViolationException) e;
				ConstraintViolation cv = mcve.getConstraintViolations().iterator().next();
				String[] fields = cv.getPropertyPath().toString().split("\\.");
				String field = fields[fields.length-1];
				throw new ValidatedException(cv.getMessage().replaceAll("\\$\\{field\\}", field),e);
			}
			throw e;
		}
	}

}
