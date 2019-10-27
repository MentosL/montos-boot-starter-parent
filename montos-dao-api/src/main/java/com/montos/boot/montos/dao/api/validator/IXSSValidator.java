package com.jimistore.boot.nemo.dao.api.validator;

import com.jimistore.boot.nemo.dao.api.exception.XssValidatorException;

public interface IXSSValidator {
	
	/**
	 * 校验新入库的数据是否有XSS攻击风险
	 * @param entity
	 * @throws XssValidatorException
	 */
	public void check(Object entity) throws XssValidatorException;

}
