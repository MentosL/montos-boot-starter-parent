package com.montos.boot.montos.core.util;

import com.montos.boot.montos.core.api.annotation.Json;
import com.montos.boot.montos.core.api.annotation.JsonExclusion;
import com.montos.boot.montos.core.api.annotation.JsonInclude;
import com.montos.boot.montos.core.api.annotation.JsonTitleAlias;
import com.montos.boot.montos.core.api.enums.JsonMode;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("rawtypes")
public class JsonString {
	private static final Logger log = Logger.getLogger(JsonString.class);
	
	public static boolean DEFAULT_NOT_SET=false;
	
	private Object obj;
	
	private String[] fieldNames;
	
	private Map exclusionMap;
	
	public static String toJson(Object obj){
		return new JsonString(obj).toString();
	}
	
	public static String toJson(Object obj,String[] fields){
		return new JsonString(obj,fields).toString();
	}
	
	public JsonString(Object obj) {
		super();
		this.obj = obj;
		exclusionMap=new HashMap();
	}
	
	public JsonString(Object obj,String[] fields) {
		super();
		this.obj = obj;
		this.fieldNames=fields;
		exclusionMap=new HashMap();
	}
	
	public JsonString(Object obj,String[] fields,Map exclusionMap) {
		super();
		this.obj = obj;
		this.fieldNames=fields;
		this.exclusionMap=exclusionMap;
	}
	
	@SuppressWarnings("unchecked")
	class JsonMap extends HashMap{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String[] fieldNames;
		private Map exclusionMap;
		public JsonMap(String[] fieldNames) {
			super();
			this.fieldNames = fieldNames;
			exclusionMap=new HashMap();
		}
		public JsonMap(String[] fieldNames,Map map) {
			super();
			this.fieldNames = fieldNames;
			this.putAll(map);
			this.exclusionMap=new HashMap();
		}
		public JsonMap(String[] fieldNames,Map map,Map exclusionMap) {
			super();
			this.fieldNames = fieldNames;
			this.exclusionMap=exclusionMap;
			this.putAll(map);
		}
		
		@Override
		public int hashCode() {
			
			int h = 0;
			Iterator i = entrySet().iterator();
			while (i.hasNext()){
			    h += i.next().hashCode();
			}
			return h;
		}
		@Override
		public String toString() {
			
			StringBuffer jsonStr=new StringBuffer();
			jsonStr.append("{");
			if(fieldNames==null){
				Iterator it=this.keySet().iterator();
				while(it.hasNext()){
					if(jsonStr.length()>1){
						jsonStr.append(",");
					}
					Object key=it.next();
					Object o=this.get(key);
					jsonStr.append("\""+key+"\"");
					jsonStr.append(":");

					if(ClassUtil.isBasicType(o) || o instanceof String || o instanceof java.util.Date){
						jsonStr.append("\"");
					}
					jsonStr.append(new JsonString(o,null,exclusionMap));

					if(ClassUtil.isBasicType(o) || o instanceof String || o instanceof java.util.Date){
						jsonStr.append("\"");
					}
				}
			}else{
				for(String key:fieldNames){
					if(key.indexOf(".")>0){
						key=key.substring(0,key.indexOf("."));
					}
					
					if(jsonStr.length()>1){
						jsonStr.append(",");
					}
					Object o=this.get(key);
					jsonStr.append("\""+key+"\"");
					jsonStr.append(":");
					if(ClassUtil.isBasicType(o) || o instanceof String || o instanceof java.util.Date){
						jsonStr.append("\"");
					}
					jsonStr.append(new JsonString(o,getSonFields(fieldNames,key),exclusionMap));
					if(ClassUtil.isBasicType(o) || o instanceof String || o instanceof java.util.Date){
						jsonStr.append("\"");
					}
				}
			}
			jsonStr.append("}");
			return jsonStr.toString().replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "");
		}
	}
	@SuppressWarnings("unchecked")
	class JsonList extends ArrayList{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String[] fieldNames;
		private Map exclusionMap;
		public JsonList(String[] fieldNames) {
			super();
			this.fieldNames = fieldNames;
			exclusionMap=new HashMap();
		}
		public JsonList(String[] fieldNames,Collection list) {
			super();
			this.fieldNames = fieldNames;
			this.addAll(list);
			exclusionMap=new HashMap();
		}
		public JsonList(String[] fieldNames,Collection list,Map exclusionMap) {
			super();
			this.fieldNames = fieldNames;
			this.exclusionMap=exclusionMap;
			this.addAll(list);
		}
		@Override
		public String toString() {
			
			StringBuilder sb=new StringBuilder();
			sb.append("[");
			for(Object o:this){
				if(sb.length()>1){
					sb.append(",");
				}
				if(ClassUtil.isBasicType(o) || o instanceof String || o instanceof java.util.Date){
					sb.append("\"");
				}
				sb.append(new JsonString(o,fieldNames,exclusionMap));
				if(ClassUtil.isBasicType(o) || o instanceof String || o instanceof java.util.Date){
					sb.append("\"");
				}
			}
			sb.append("]");
			return sb.toString();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		
		if(obj==null){
			return "\"null\"";
		}
		if(obj instanceof Set){
			return "{}";
		}
		if(exclusionMap.containsKey(obj))
		{
			return exclusionMap.get(obj).toString();
		}
		exclusionMap.put(obj, "{}");
		StringBuffer jsonStr=new StringBuffer();
		if(ClassUtil.isDoubleType(obj)){
			jsonStr.append(String.format("%.4f", Double.parseDouble(String.valueOf(obj))));
		}else if(ClassUtil.isBasicType(obj)){
			jsonStr.append(String.valueOf(obj));
		}else if(obj instanceof String){
			jsonStr.append(obj.toString().replaceAll("\"", "\\\\\""));
		}else if(obj instanceof java.sql.Time){
			jsonStr.append(DateUtil.getTimeStr((java.sql.Time)obj));
		}else if(obj instanceof java.sql.Date){
			jsonStr.append(DateUtil.getDateStr((java.sql.Date)obj));
		}else if(obj instanceof java.util.Date||obj instanceof java.sql.Timestamp){
			jsonStr.append(DateUtil.getDateTimeStr((java.util.Date)obj));
		}else if(obj instanceof Map){
			jsonStr.append(new JsonMap(fieldNames,(Map)obj,exclusionMap));
		}else if(obj instanceof Collection){
			jsonStr.append(new JsonList(fieldNames,(Collection)obj,exclusionMap));
		}else if(obj instanceof Throwable){
			Throwable e = (Throwable)obj;
			jsonStr.append(e.getMessage());
		}else{
			Json json=(Json) AnnotationUtil.getAnnotation(obj.getClass(), Json.class);
			jsonStr.append("{");
//			List<Field> fields=getFields();
			List<Field> fields = ClassUtil.getFields(obj.getClass(), fieldNames);
			for(Field field:fields)
			{
				boolean flag=true;
				String key=field.getName();
				if(json!=null){
					if(json.mode().equals(JsonMode.Include)){
						flag=AnnotationUtil.getAnnotation(field, JsonInclude.class)!=null;
					}else if(json.mode().equals(JsonMode.Exclusion)){
						flag=AnnotationUtil.getAnnotation(field, JsonExclusion.class)==null;
					}
					JsonTitleAlias jsonTitleAlias=(JsonTitleAlias)AnnotationUtil.getAnnotation(field, JsonTitleAlias.class);
					if(jsonTitleAlias!=null){
						key=jsonTitleAlias.alias();
					}
				}else{
					flag=AnnotationUtil.getAnnotation(field, JsonExclusion.class)==null;
					JsonTitleAlias jsonTitleAlias=(JsonTitleAlias)AnnotationUtil.getAnnotation(field, JsonTitleAlias.class);
					if(jsonTitleAlias!=null){
						key=jsonTitleAlias.alias();
					}
				}
				if(flag){
					String getMethodName=getMethodNameByField(field);
					StringBuilder result = null;
					try {
						Object value = obj.getClass().getMethod(getMethodName, new Class[]{}).invoke(obj, new Object[]{});
						if(value!=null){
							if(field.getType().isArray()||value.getClass().isArray()){
								Object[] array=(Object[]) value;
								List list=new ArrayList();
								if(array!=null){
									for(Object arrayItem:array){
										list.add(arrayItem);
									}
								}
								result=new StringBuilder(new JsonList(getSonFields(fieldNames,key),list,exclusionMap).toString());
								result.append(",");
							}else{
								result = new StringBuilder();
								if(ClassUtil.isBasicType(value) || value instanceof String || value instanceof java.util.Date){
									result.append("\"");
								}
								result.append(new JsonString(value,getSonFields(fieldNames,key),exclusionMap).toString());

								if(ClassUtil.isBasicType(value) || value instanceof String || value instanceof java.util.Date){
									result.append("\"");
								}
								result.append(",");
							}
						}
					} catch (Exception e) {
						result=null;
						if(log.isDebugEnabled()){
							log.debug(String.format("输出json，反射获取异常，msg is %s", e.getMessage()));
						}
					}
					if(result!=null){
						jsonStr.append("\"");
						jsonStr.append(key);
						jsonStr.append("\"");
						jsonStr.append(":");
						jsonStr.append(result.toString());
					}else if(json!=null&&!json.notNull()){
						if(field.getType().isArray()||field.getType().isAssignableFrom(Collection.class)){
							jsonStr.append("\"");
							jsonStr.append(key);
							jsonStr.append("\"");
							jsonStr.append(":");
							jsonStr.append("[],");
						}else{
							jsonStr.append("\"");
							jsonStr.append(key);
							jsonStr.append("\"");
							jsonStr.append(":");
							jsonStr.append("\"null\",");
						}
					}
				}
			}
			if(jsonStr.indexOf(",")>0)
			{
				jsonStr.delete(jsonStr.lastIndexOf(","), jsonStr.lastIndexOf(",")+1);
			}
			jsonStr.append("}");
		}
		exclusionMap.put(obj, jsonStr.toString());
		return jsonStr.toString().replace("\r", "").replace("\n", "").replace("\t", "");
	}
	
	/**
	 * 获取子字段
	 * @param fields
	 * @param key
	 * @return
	 */
	public String[] getSonFields(String[] fields,String key){
		List<String> sonFieldNameList=new ArrayList<String>();
		String[] sonFieldNames=null;
		if(fields!=null){
			for(String fieldName:fields){
				if(fieldName.indexOf(".")>0&&key.equals(fieldName.substring(0,fieldName.indexOf(".")))){
					sonFieldNameList.add(fieldName.substring(fieldName.indexOf(".")+1));
				}
			}
			sonFieldNames=new String[sonFieldNameList.size()];
			sonFieldNameList.toArray(sonFieldNames);
		}
		return sonFieldNames;
	}
	
	/**
	 * 根据属性获取属性的get方法
	 * @param field
	 * @return
	 */
	private String getMethodNameByField(Field field){
		String getMethodName="get"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
//		if(field.getType().equals(boolean.class)||field.getType().equals(Boolean.class))
		if(field.getType().equals(boolean.class))
		{
			getMethodName="is"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
			if(field.getName().indexOf("is")==0){
				getMethodName=field.getName();
			}
		}
		return getMethodName;
	}
	
	@SuppressWarnings("unused")
	private List<Field> getFields(){
		List<Field> fieldList=new ArrayList<Field>();
		Set<Field> fields=new HashSet<Field>();
		for(Field field:obj.getClass().getDeclaredFields()){
			fields.add(field);
		}
		for(Field field:obj.getClass().getFields()){
			fields.add(field);
		}
		fieldList.addAll(fields);
		if(fieldNames!=null){
			for(int i=0;i<fieldList.size();i++){
				Field field=fieldList.get(i);
				boolean flag=true;
				for(String fieldName:fieldNames){
					if(field.getName().equals(fieldName.indexOf(".")>0?fieldName.substring(0,fieldName.indexOf(".")):fieldName)){
						flag=false;
					}
				}
				if(flag){
					fieldList.remove(i);
					i--;
				}
			}
		}
		return fieldList;
	}
	
}