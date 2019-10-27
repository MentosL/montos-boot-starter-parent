package com.jimistore.boot.nemo.dao.core.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

import com.jimistore.boot.nemo.core.helper.Context;
import com.jimistore.boot.nemo.dao.api.entity.BaseBean;

@Aspect
@Order(11)
public class InitContextAspect {

	private static final Logger log = Logger.getLogger(InitContextAspect.class);
	
	@Pointcut("execution(* com.jimistore.boot.nemo.dao.api.dao.IDao.*(..))")
	public void beforeDao(){}
	
	@Before("beforeDao()")
	public void beforeDao(JoinPoint joinPoint){
		try{
			String user = (String)Context.get(Context.CONTEXT_REQUEST_USER);
			Date now = Calendar.getInstance().getTime();
			Object[] args = joinPoint.getArgs();
			if(log.isDebugEnabled()){
				log.debug(String.format("before dao, the user is :%s", user));
			}
			for(Object arg:args){
				if(arg instanceof BaseBean){
					initBaseBean((BaseBean<?>)arg,now,user,null);
				}
			}
		}catch(Exception e){
			log.warn("init basebean error" , e);
		}
	}

	
	/**
	 * 初始化创建时间、更新时间、创建人、更新人
	 * @param baseBean
	 * @param now
	 * @param user
	 * @param set
	 */
	private void initBaseBean(BaseBean<?> baseBean,Date now,String user,Set<BaseBean<?>> set) {
		if(set==null){
			set=new HashSet<BaseBean<?>>();
		}
		set.add(baseBean);
		if(baseBean.getCreateTime()==null) {
			baseBean.setCreateTime(now);
		}
		baseBean.setUpdateTime(now);
		baseBean.setCreateAuthor(user);
		baseBean.setUpdateAuthor(user);
		
		List<Field> fields = new ArrayList<Field>();
		for (Field field : baseBean.getClass().getDeclaredFields()) {
			fields.add(field);
		}
		for (Field field : baseBean.getClass().getFields()) {
			fields.add(field);
		}
		for (Field field : fields) {
			
			String prefix = "get";
			if (field.getType().equals(boolean.class)) {
				prefix = "is";
			}
			String getMethodName = String.format("%s%s%s", prefix, field.getName().substring(0, 1).toUpperCase(), field.getName().substring(1));
			try {
				Object obj = baseBean.getClass()
						.getMethod(getMethodName, new Class[] {})
						.invoke(baseBean, new Object[] {});
				if (obj != null) {
					if(obj instanceof BaseBean&&!set.contains(obj)){
						initBaseBean((BaseBean<?>)obj, now, user, set);
					}else if(obj instanceof Collection){
						Collection<?> co = (Collection<?>)obj;
						Object[] objs = co.toArray();
						if(co.size()>0&&objs[0] instanceof BaseBean){
							for(Object ele:objs){
								if(set.contains(ele)){
									break;
								}
								initBaseBean((BaseBean<?>)ele, now, user, set);
							}
						}
					}
				}
			} catch (Exception e) {
			}
		}
	}
}
