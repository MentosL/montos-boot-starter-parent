package com.jimistore.boot.nemo.dao.api.validator;

import com.jimistore.boot.nemo.dao.api.exception.QueryValidatorException;
import com.jimistore.boot.nemo.dao.api.request.IQuery;

public interface IQueryValidator {
	
	public void check(IQuery<?> query) throws QueryValidatorException;

}
