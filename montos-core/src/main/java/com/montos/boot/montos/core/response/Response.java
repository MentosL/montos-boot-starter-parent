package com.montos.boot.montos.core.response;

import com.montos.boot.montos.core.api.annotation.Json;
import com.montos.boot.montos.core.api.exception.BaseException;

import java.io.Serializable;

@SuppressWarnings({"rawtypes", "unchecked"})
@Json(notNull=true)
public class Response<C extends Object> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String code;
	
	public String message;
	
	public String errMsg;
	
	public C data;

	public String getCode() {
		return code;
	}

	public Response<C> setCode(String code) {
		this.code = code;
		return this;
	}
	
	public C getData() {
		return data;
	}

	public Response<C> setData(C data) {
		this.data = data;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public Response<C> setMessage(String message) {
		this.message = message;
		return this;
	}
	
	
	public static Response error(Throwable e){
		e.printStackTrace();
		if(e instanceof BaseException){
			return new Response().setCode(((BaseException) e).getCode()).setMessage(e.getMessage()).setErrMsg(e.toString());
		}
		if(e.getMessage()==null||e.getMessage().length()==0){
			return new Response().setCode("500").setMessage("网络异常，请稍后重试！").setErrMsg(e.toString());
		}
		return new Response().setCode("500").setErrMsg(e.getMessage()).setMessage("网络异常，请稍后重试！");
	}
	
	public static Response success(){
		return new Response().setCode("200").setData("成功");
	}
	
	public static Response error(String errMsg){
		return new Response().setCode("500").setMessage("网络异常，请稍后重试！").setErrMsg(errMsg);
	}
	
	public static Response error(String msg,String errMsg){
		return new Response().setCode("500").setMessage(msg).setErrMsg(errMsg);
	}
	
	public static Response error(String code,String msg,String errMsg){
		return new Response().setCode(code).setMessage(msg).setErrMsg(errMsg);
	}
	
	public static Response success(Object data){
		if(data!=null&&data instanceof Response){
			return (Response)data;
		}
		return new Response().setCode("200").setData(data);
	}

	public String getErrMsg() {
		return errMsg;
	}

	public Response<C> setErrMsg(String errMsg) {
		this.errMsg = errMsg;
		return this;
	}

}
