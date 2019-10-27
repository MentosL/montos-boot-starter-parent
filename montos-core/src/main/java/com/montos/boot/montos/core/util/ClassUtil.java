package com.montos.boot.montos.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@SuppressWarnings("rawtypes")
public class ClassUtil {

	/**
	 * 得到对象的泛型的类型
	 * 
	 * @param c
	 * @return
	 */
	public static Class getTemplateClass(Class c) {
		Type[] types = ClassUtil.getTemplateClasses(c);
		if (types != null && types.length > 0) {
			return (Class) types[0];
		}
		return null;
	}

	/**
	 * 得到对象的泛型的类型
	 * 
	 * @param c
	 * @return
	 */
	public static Type[] getTemplateClasses(Class c) {
		ParameterizedType pt = (ParameterizedType) c.getGenericSuperclass();
		return pt.getActualTypeArguments();
	}

	/**
	 * 复制对象的数据
	 * 
	 * @param srcObj
	 *            源对象
	 * @param destObj
	 *            目的对象
	 * @throws Exception
	 */
	public static void copy(Object srcObj, Object destObj, Class[] classes,
			boolean isNotNull) throws Exception {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : destObj.getClass().getDeclaredFields()) {
			fields.add(field);
		}
		for (Field field : destObj.getClass().getFields()) {
			fields.add(field);
		}
		for (Field field : fields) {
			if (!isInClasses(field.getType(), classes)) {
				String setMethodName = "set"
						+ field.getName().substring(0, 1).toUpperCase()
						+ field.getName().substring(1);
				String getMethodName = "get"
						+ field.getName().substring(0, 1).toUpperCase()
						+ field.getName().substring(1);
				if (field.getType().equals(boolean.class)) {
					getMethodName = "is"
							+ field.getName().substring(0, 1).toUpperCase()
							+ field.getName().substring(1);
				}
				try {
					Object obj = srcObj.getClass()
							.getMethod(getMethodName, new Class[] {})
							.invoke(srcObj, new Object[] {});
					if (!isNotNull || obj != null) {
						destObj.getClass()
								.getMethod(setMethodName,
										new Class[] { field.getType() })
								.invoke(destObj, new Object[] { obj });
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		}
	}

	public static boolean isBasicType(Object o) {
		if (o == null) {
			return false;
		}
		return false
				|| (o instanceof Integer)
				|| (o instanceof Short)
				|| (o instanceof Long)
				|| (o instanceof Character)
				|| (o instanceof Byte)
				|| (o instanceof Boolean)
				|| (o instanceof BigInteger)
				|| (o.getClass().getPackage() != null && o.getClass()
						.getPackage().getName().equals("java.math"));
	}
	
	public static boolean isDoubleType(Object o) {
		if (o == null) {
			return false;
		}
		return false
				|| (o instanceof Float)
				|| (o instanceof Double)
				|| (o instanceof BigDecimal);
	}

	public static boolean isInClasses(Class clssz, Class[] classes) {
		if (clssz != null && classes != null) {
			for (Class calssc : classes) {
				if (clssz.equals(calssc)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 根据属性获取属性的get方法
	 * 
	 * @param name
	 * @param classz
	 * @return
	 */
	public static String getMethodNameByFieldName(String name, Class classz) {
		Map<String, Class> fieldMap = new HashMap<String, Class>();
		for (Field field : classz.getDeclaredFields()) {
			fieldMap.put(field.getName(), field.getType());
		}
		for (Field field : classz.getFields()) {
			fieldMap.put(field.getName(), field.getType());
		}
		Class type = fieldMap.get(name);
		if (type != null && type.equals(Boolean.class)
				|| type.equals(boolean.class)) {
			return "is" + name.substring(0, 1).toUpperCase()
					+ name.substring(1);
		}
		return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	/**
	 * 根据属性获取属性的set方法
	 * 
	 * @param field
	 * @return
	 */
	public static String getSetMethodNameByField(Field field) {
		String name = field.getName();
		return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	/**
	 * 根据属性获取属性的get方法
	 * 
	 * @param field
	 * @return
	 */
	public static String getGetMethodNameByField(Field field) {
		String getMethodName = "get"
				+ field.getName().substring(0, 1).toUpperCase()
				+ field.getName().substring(1);
		// if(field.getType().equals(boolean.class)||field.getType().equals(Boolean.class))
		if (field.getType().equals(boolean.class)) {
			getMethodName = "is"
					+ field.getName().substring(0, 1).toUpperCase()
					+ field.getName().substring(1);
			if (field.getName().indexOf("is") == 0) {
				getMethodName = field.getName();
			}
		}
		return getMethodName;
	}
	
	/**
	 * 对象转map
	 * @param obj
	 * @return
	 */
	public static Map ObjectToMap(Object obj){
		return ClassUtil.ObjectToMap(obj, true);
	}
	
	/**
	 * 对象转map
	 * @param obj
	 * @param notNull
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map ObjectToMap(Object obj, boolean notNull){
		Map map = new HashMap();
		if(obj==null){
			return null;
		}
		Class classz = obj.getClass();
		List<Field> fields = ClassUtil.getFields(classz);
		for(Field field:fields){
			String key = field.getName();
			String getMethodName = ClassUtil.getMethodNameByFieldName(key, classz);
			Object value = null;
			try {
				value = obj.getClass()
						.getMethod(getMethodName)
						.invoke(obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			if(notNull&&value==null){
				break;
			}
			map.put(key, value);
		}
		return map;
	}

	/**
	 * Map转对象
	 * 
	 * @param map
	 * @param classz
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static Object mapToObject(Map map, Class classz)
			throws InstantiationException, IllegalAccessException {
		Object obj = classz.newInstance();
		List<Field> fields = new ArrayList<Field>();
		for (Field field : classz.getDeclaredFields()) {
			fields.add(field);
		}
		for (Field field : classz.getFields()) {
			fields.add(field);
		}
		for (Field field : fields) {
			String fieldName = field.getName();
			if (map.containsKey(fieldName)) {
				String setMethodName = "set"
						+ field.getName().substring(0, 1).toUpperCase()
						+ field.getName().substring(1);
				Object value = map.get(fieldName);
				try {
					obj.getClass()
							.getMethod(setMethodName,
									new Class[] { field.getType() })
							.invoke(obj, new Object[] { value });
				} catch (IllegalArgumentException e) {

					e.printStackTrace();
				} catch (SecurityException e) {

					e.printStackTrace();
				} catch (IllegalAccessException e) {

					e.printStackTrace();
				} catch (InvocationTargetException e) {

					e.printStackTrace();
				} catch (NoSuchMethodException e) {

					e.printStackTrace();
				}
			}
		}
		return obj;
	}

	/**
	 * 判断类是否包含某注解
	 * 
	 * @param classz
	 * @param annotation
	 * @return
	 */
	public static boolean containAnnotation(Class classz, Class annotation) {
		Annotation[] annotations = classz.getAnnotations();
		for (Annotation a : annotations) {
			if (a.annotationType().equals(annotation)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断类是否包含某注解
	 * 
	 * @param field
	 * @param annotation
	 * @return
	 */
	public static boolean containAnnotation(Field field, Class annotation) {
		Annotation[] annotations = field.getAnnotations();
		for (Annotation a : annotations) {
			if (a.annotationType().equals(annotation)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取类可以访问的属性
	 * 
	 * @param classz
	 * @return
	 */
	public static List<Field> getFields(Class classz) {
		return getFields(classz, null);
	}
	
	/**
	 * 
	 * @param classz
	 * @param annotation
	 * @return
	 */
	public static List<Field> getFieldByAnnotation(Class classz,Class annotation){
		List<Field> fields = new ArrayList<Field>();
		List<Field> fieldList = ClassUtil.getFields(classz);
		for(Field field:fieldList){
			if(ClassUtil.containAnnotation(field, annotation)){
				fields.add(field);
			}
		}
		return fields;
	}
	
	/**
	 * 递归获取类所有自己的属性和父类属性
	 * @param fieldList
	 * @param classz
	 */
	private static void fillFields(List<Field> fieldList, Class classz){
		Field[] fields = classz.getDeclaredFields();
		for(Field field:fields){
			fieldList.add(field);
		}
		Class superClass = classz.getSuperclass();
		if(superClass!=null&&!superClass.equals(Object.class)){
			ClassUtil.fillFields(fieldList, superClass);
		}
	}

	/**
	 * 获取类可以访问的属性
	 * 
	 * @param classz
	 * @param fieldNames
	 * @return
	 */
	public static List<Field> getFields(Class classz, String[] fieldNames) {
		List<Field> fieldList = new ArrayList<Field>();
		ClassUtil.fillFields(fieldList, classz);
		if (fieldNames != null) {
			for (int i = 0; i < fieldList.size(); i++) {
				Field field = fieldList.get(i);
				boolean flag = true;
				for (String fieldName : fieldNames) {
					if (field.getName().equals(
							fieldName.indexOf(".") > 0 ? fieldName.substring(0,
									fieldName.indexOf(".")) : fieldName)) {
						flag = false;
					}
				}
				if (flag) {
					fieldList.remove(i);
					i--;
				}
			}
		}
		return fieldList;
	}

	public static void clearSelf(Object srcObj, Object self) {
		if(srcObj==null){
			return ;
		}
		List<Field> fields = ClassUtil.getFields(srcObj.getClass());
		for (Field field : fields) {
			String setMethodName = "set"
					+ field.getName().substring(0, 1).toUpperCase()
					+ field.getName().substring(1);
			String getMethodName = "get"
					+ field.getName().substring(0, 1).toUpperCase()
					+ field.getName().substring(1);

			try {
				Object value = srcObj.getClass()
						.getMethod(getMethodName, new Class[] {})
						.invoke(srcObj, new Object[] {});
				if (value != null && value.equals(self)) {
					srcObj.getClass()
							.getMethod(setMethodName,
									new Class[] { field.getType() })
							.invoke(srcObj, new Object[] { null });
				}
			} catch (Exception e) {
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void clearPersistentProxy(Object srcObj) {
		if(srcObj instanceof Map){
			return ;
		}
		List<Field> fields = ClassUtil.getFields(srcObj.getClass());
		for (Field field : fields) {
			String setMethodName = "set"
					+ field.getName().substring(0, 1).toUpperCase()
					+ field.getName().substring(1);
			String getMethodName = "get"
					+ field.getName().substring(0, 1).toUpperCase()
					+ field.getName().substring(1);

			try {
				Object dest = srcObj.getClass()
						.getMethod(getMethodName, new Class[] {})
						.invoke(srcObj, new Object[] {});

				if (field.getType().isAssignableFrom(List.class)
						|| field.getType().isAssignableFrom(Set.class)) {

					Collection source = (Collection) dest;
					if (source != null && source.size() > 0) {
						if (field.getType().isAssignableFrom(List.class)) {
							dest = new ArrayList();
						} else if (field.getType().isAssignableFrom(Set.class)) {
							dest = new HashSet();
						}
						Collection destObj = (Collection) dest;
						Iterator it = source.iterator();
						while (it.hasNext()) {
							Object prop = it.next();
							ClassUtil.clearSelf(prop, srcObj);
							ClassUtil.clearPersistentProxy(prop);
							destObj.add(prop);
						}
					}
				} else {
					ClassUtil.clearSelf(dest, srcObj);
				}
				srcObj.getClass()
						.getMethod(setMethodName,
								new Class[] { field.getType() })
						.invoke(srcObj, new Object[] { dest });
			} catch (Exception e) {
			}
		}
	}
}
