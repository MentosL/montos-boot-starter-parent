package com.montos.boot.montos.dao.api.exception;

public class XssValidatorException extends DaoException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private String code="41502";

	private static final String msg = "疑似XSS脚本攻击，本次请求已记录日志，供公司法务部门取证！";
	
	public XssValidatorException(String message,Throwable cause) {
		super(message,cause);
	}

	public XssValidatorException(String message) {
		super(message);
	}

	public XssValidatorException() {
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
