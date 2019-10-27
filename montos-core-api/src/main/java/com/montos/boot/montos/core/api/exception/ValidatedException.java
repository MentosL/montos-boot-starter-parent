package com.montos.boot.montos.core.api.exception;

public class ValidatedException extends BaseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidatedException(String message){
		super("415",message);
	}
	

	public ValidatedException(){
		super("415","缺少必填参数异常");
	}

	public ValidatedException(String message, Throwable ex) {
		super("415",message,ex);
	}
}