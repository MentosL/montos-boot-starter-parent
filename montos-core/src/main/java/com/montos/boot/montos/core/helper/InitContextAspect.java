package com.jimistore.boot.nemo.core.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Order(11)
public class InitContextAspect {

	@SuppressWarnings("unused")
	private final Logger log = Logger.getLogger(getClass());
	
	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void initController(){
	}
	
	private String loginUserKey;

	@Value("${login.user.key:userId}")
	public void setLoginUserKey(String loginUserKey) {
		this.loginUserKey = loginUserKey;
	}
	
	@Before("initController()")
	public void beforeController(JoinPoint joinPoint){
		try{
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	        HttpServletRequest request = attributes.getRequest();
	        Object user = this.parse(request, loginUserKey);
	        if(Context.get(Context.CONTEXT_REQUEST_USER)==null){
				Context.put(Context.CONTEXT_REQUEST_USER, user);
	        }
		}catch(Exception e){
			
		}
	}
	
	public Object parse(HttpServletRequest request, String key){
		Object value = request.getParameter(key);
        if(value==null){
        	value = request.getHeader(key);
        }
        if(value==null){
	        HttpSession session = request.getSession();
        	value = session.getAttribute(key);
        }
        return value;
	}
}
