package com.jimistore.boot.nemo.core.api.exception;

public class BaseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String code;

	public BaseException(String code,String message,Throwable cause) {
		super(message,cause);
		this.code=code;
	}


	public BaseException(String code,String message) {
		super(message);
		this.code=code;
	}
	
	public BaseException(String message,Throwable cause) {
		super(message,cause);
		this.code="500";
	}

	public BaseException(String message) {
		super(message);
		this.code="500";
	}

	public BaseException() {
		super("业务访问异常");
		this.code="500";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public Throwable fillInStackTrace() {
		//性能优化300倍
		return this;
	}


	@Override
	public String toString() {
		return new StringBuilder()
		.append(this.getClass().getSimpleName())
		.append("==>")
//		.append(this.getStackTrace()[0].getClassName())
//		.append(".")
//		.append(this.getStackTrace()[0].getMethodName())
//		.append("[")
//		.append(this.getStackTrace()[0].getLineNumber())
//		.append("]:")
		.append(this.getMessage())
		.toString();
	}

}
