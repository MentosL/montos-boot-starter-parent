package com.jimistore.boot.nemo.dao.api.exception;

import com.jimistore.boot.nemo.core.api.exception.BaseException;

public class DaoException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String code="500";
	
	private static final String msg = "数据访问异常";
	
	public DaoException(String message,Throwable cause) {
		super(message,cause);
	}

	public DaoException(String message) {
		super(message);
	}

	public DaoException() {
		super(msg);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public Throwable fillInStackTrace() {
//		return super.fillInStackTrace();
		//性能优化300倍
		return this;
	}


	@Override
	public String toString() {
		return new StringBuilder()
		.append(this.getClass().getName())
		.append("==>")
		.append(this.getMessage())
		.toString();
	}
	

}
