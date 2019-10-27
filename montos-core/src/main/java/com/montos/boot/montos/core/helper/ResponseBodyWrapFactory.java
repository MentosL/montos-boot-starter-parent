package com.jimistore.boot.nemo.core.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import com.jimistore.boot.nemo.core.response.Response;
import com.jimistore.boot.nemo.core.util.JsonString;

public class ResponseBodyWrapFactory implements InitializingBean {
	
	private String filterPrefix="/api";
	
	@Autowired  
    private RequestMappingHandlerAdapter adapter;
	
	public ResponseBodyWrapFactory setFilterPrefix(String filterPrefix) {
		this.filterPrefix = filterPrefix;
		return this;
	}

	private class JsonStringHandle implements HandlerMethodReturnValueHandler{
		
		private HandlerMethodReturnValueHandler delegate;
	    private JsonStringHandle(HandlerMethodReturnValueHandler delegate){  
	    	this.delegate=delegate;  
	    } 

		@SuppressWarnings("rawtypes")
		@Override
		public void handleReturnValue(Object arg0, MethodParameter arg1,
				ModelAndViewContainer arg2, NativeWebRequest arg3)
				throws Exception {
			HttpServletRequest request = (HttpServletRequest) arg3.getNativeRequest();
			if(request.getRequestURI().indexOf(filterPrefix)>=0){
				Response response = null;
				String[] fields = null;
				if(arg1.getMethod().getReturnType().toString().equals("void")){
					response = Response.success();
				}else{
					response = Response.success(arg0);
				}
				arg0 = JsonString.toJson(response, fields);
			}
			delegate.handleReturnValue(arg0, arg1, arg2, arg3);
		}

		@Override
		public boolean supportsReturnType(MethodParameter returnType) {
			return delegate.supportsReturnType(returnType);
		}
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		List<HandlerMethodReturnValueHandler> handlers = adapter.getReturnValueHandlers();  
        List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<HandlerMethodReturnValueHandler>();  
        for (HandlerMethodReturnValueHandler handler : handlers) {  
            if (handler instanceof RequestResponseBodyMethodProcessor) {  
                //用自己的ResponseBody包装类替换掉框架的，达到返回Result的效果  
            	handler = new JsonStringHandle(handler);
            }
            returnValueHandlers.add(handler);
        }  
        adapter.setReturnValueHandlers(returnValueHandlers); 
	}

}
