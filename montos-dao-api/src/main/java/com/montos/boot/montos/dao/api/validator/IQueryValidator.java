package com.montos.boot.montos.dao.api.validator;

import com.montos.boot.montos.dao.api.exception.QueryValidatorException;
import com.montos.boot.montos.dao.api.request.IQuery;

public interface IQueryValidator {
	
	public void check(IQuery<?> query) throws QueryValidatorException;

}
