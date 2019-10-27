package com.montos.boot.montos.dao.hibernate.validator;

import com.montos.boot.montos.core.util.AnnotationUtil;
import com.montos.boot.montos.core.util.ClassUtil;
import com.montos.boot.montos.core.util.JsonString;
import com.montos.boot.montos.dao.api.annotation.XssIgnoreField;
import com.montos.boot.montos.dao.api.exception.XssValidatorException;
import com.montos.boot.montos.dao.api.validator.IXSSValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class XSSValidator implements IXSSValidator {
	
	private static final Logger log = Logger.getLogger(XSSValidator.class);
	
	@Value("${xss.replace:}")
	String replace;
	
	/**
	 * 非法字符串
	 */
	public static final String[] errStr={
			"script",
			"link",
			"iframe",
			"img",
			"input",
			"frameset",
			"video",
			"sound",
			"object",
			"embed",
			"bgsound",
			"audio",
			"source",
			"<"
			};

	@Override
	public void check(Object entity) throws XssValidatorException {
		if(log.isDebugEnabled()){
			log.debug(String.format("xss checking, the entity is %s", JsonString.toJson(entity)));
		}
		if(entity==null){
			return ;
		}
		List<Field> fieldList = ClassUtil.getFields(entity.getClass());
		for(Field field:fieldList){
			String getMethod = ClassUtil.getGetMethodNameByField(field);
			try {
				Object value = entity.getClass().getMethod(getMethod, new Class[]{}).invoke(entity, new Object[]{});
				if(value instanceof String){
					String str = (String) value;
					String[] ignores = null;
					XssIgnoreField xssIgnoreField = AnnotationUtil.getAnnotation(field, XssIgnoreField.class);
					if(xssIgnoreField!=null){
						ignores = xssIgnoreField.value();
					}
					int result = checkValue(str, ignores);
					if(result>=0){
						//替换还是直接抛异常
						if(replace!=null&&replace.length()>0){
							str = str.replaceAll(errStr[result], String.format("%s%s%s", replace, errStr[result], replace));
							String setMethod = ClassUtil.getSetMethodNameByField(field);
							entity.getClass().getMethod(setMethod, new Class[]{String.class}).invoke(entity, new Object[]{str});
						}else{
							throw new XssValidatorException();
						}
						
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				if(log.isDebugEnabled()){
					log.debug(e);
				}
			}
		}
	}
	
	/**
	 * 检查命中异常规则第几项
	 * @param value
	 * @return
	 * @throws XssValidatorException
	 */
	public static int checkValue(String value, String[] ignores) throws XssValidatorException {
		for(int i=0;i<errStr.length;i++){
			String str = errStr[i];
			if(ignores!=null){
				//忽略全部
				if(ignores.length==0){
					continue ;
				}
				boolean flag = false;
				//忽略制定关键字
				for(String ignore:ignores){
					if(ignore!=null&&ignore.equals(str)){
						flag=true;
					}
				}
				if(flag){
					continue ;
				}
			}
			if(value.toString().indexOf(str)>=0){
				return i;
			}
		}
		return -1;
	}

}