package com.montos.boot.montos.dao.hibernate.validator;

import com.montos.boot.montos.dao.api.exception.QueryValidatorException;
import com.montos.boot.montos.dao.api.validator.IQueryValidator;

public interface IInjectSqlValidator extends IQueryValidator {
	
	public void check(Object... objects) throws QueryValidatorException;

}
