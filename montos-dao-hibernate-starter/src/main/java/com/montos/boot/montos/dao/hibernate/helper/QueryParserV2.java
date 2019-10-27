package com.montos.boot.montos.dao.hibernate.helper;

import com.montos.boot.montos.core.util.AnnotationUtil;
import com.montos.boot.montos.dao.api.enums.AndOr;
import com.montos.boot.montos.dao.api.enums.Compare;
import com.montos.boot.montos.dao.api.request.*;
import com.montos.boot.montos.dao.hibernate.request.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.util.StringUtils;

import javax.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

public class QueryParserV2 implements IQueryParser {
	
	HibernateNamingStrategy hibernateNamingStrategy;

	public QueryParserV2 setHibernateNamingStrategy(HibernateNamingStrategy hibernateNamingStrategy) {
		this.hibernateNamingStrategy = hibernateNamingStrategy;
		return this;
	}

	public HibernateNamingStrategy getHibernateNamingStrategy() {
		return hibernateNamingStrategy;
	}



	@Override
	public Query parse(Session session, IQuery<?> query) {
		if(query instanceof SqlQuery){
			return this.parseSql(session, (SqlQuery<?>) query);
		}else{
			return this.parseHql(session, query);
		}
	}

	/**
	 * 解析sql
	 * @param session
	 * @param query
	 * @return
	 */
	public Query parseSql(Session session, SqlQuery<?> query) {
		List<SqlTarget> sqlTargetList = new ArrayList<SqlTarget>();
		if(query.getTarget() instanceof SqlTarget){
			this.fillTargetList((SqlTarget) query.getTarget(), sqlTargetList);
		}else if(query.getTarget() instanceof SqlJoinTarget){
			this.fillTargetList((SqlJoinTarget) query.getTarget(), sqlTargetList);
		}
		
		Map<SqlTarget,String> aliasMap = this.initAliasMap(sqlTargetList);
		
		//解析sql
		String selectSql = this.getSelectSql(aliasMap);
		String whereSql = this.getWhereSql(sqlTargetList, aliasMap);
		String targetSql = this.getFromSql(sqlTargetList, aliasMap);
		String groupSql = this.getGroupSql(aliasMap);
		String orderSql = this.getOrderSql(query, aliasMap);
		String sql = String.format("%s %s %s %s %s", selectSql, targetSql, whereSql, groupSql, orderSql);	
		
		Query sQuery = session.createSQLQuery(sql);
		if(query.getOutClass()!=null&&!query.getOutClass().equals(Map.class)){
			sQuery.setResultTransformer(AliasToEntityResultTransformer.create(query.getOutClass()));
		}else{
			sQuery.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);
		}
		//设置分页
		if(query!=null&&query.getPageNum()!=null&&query.getPageSize()!=null){
			sQuery.setFirstResult((query.getPageNum() - 1) * query.getPageSize());
			sQuery.setMaxResults(query.getPageSize());
		}
		return sQuery;
	}

	/**
	 * 解析hql
	 * @param session
	 * @param query
	 * @return
	 */
	private Query parseHql(Session session, IQuery<?> query) {
		
		String entity = query.getTarget().getEntityClass().getSimpleName();
		String alias = String.format("%s_0", entity.toLowerCase());
		String targetSql = String.format("from %s %s", entity, alias);
		String whereSql = this.getWhereSql(query.getTarget(), alias);
		String orderSql = this.getOrderSql(query);
		
		//解析hql
		String hql = String.format("%s %s %s", targetSql, whereSql, orderSql);
		
		Query hQuery = session.createQuery(hql);
		
		//设置分页
		if(query!=null&&query.getPageNum()!=null&&query.getPageSize()!=null){
			hQuery.setFirstResult((query.getPageNum() - 1) * query.getPageSize());
			hQuery.setMaxResults(query.getPageSize());
		}
		return hQuery;
	}
	
	private Map<SqlTarget,String> initAliasMap(List<SqlTarget> sqlTargetList){
		Map<SqlTarget,String> aliasMap = new HashMap<SqlTarget,String>();
		int i=0;
		for(SqlTarget sqlTarget:sqlTargetList){
			String alias = String.format("%s_%s", sqlTarget.getEntityClass().getSimpleName().toLowerCase(), i);
			aliasMap.put(sqlTarget, alias);
			i++;
		}
		return aliasMap;
	}
	
	private String getSelectSql(Map<SqlTarget,String> aliasMap){
		StringBuffer selectSql = new StringBuffer();
		Set<SqlTarget> sqlTargetSet = aliasMap.keySet();
		for(SqlTarget sqlTarget:sqlTargetSet){
			String alias = aliasMap.get(sqlTarget);
			if(sqlTarget.getOutFieldNames()!=null){
				for(Serializable field:sqlTarget.getOutFieldNames()){
					String fieldName = null;
					if(field instanceof SqlFunction){
						String column = ((SqlFunction)field).getContent();
						String as = ((SqlFunction)field).getAs();
						fieldName = String.format("%s as %s", column, as);
					}else{
						fieldName = field.toString();
					}
					if(selectSql.length()>0){
						selectSql.append(", ");
					}else{
						selectSql.append("select ");
					}
					if(fieldName.indexOf("as ")>=0){
						//判断是否是聚合函数
						if(fieldName.indexOf("(")>=0&&fieldName.indexOf(")")>=0){
							selectSql.append(fieldName);
						}else{
							String column = fieldName.substring(0,fieldName.indexOf("as ")-1);
							column = this.getColumnByFieldName(column);
							String as = fieldName.substring(fieldName.indexOf("as "));
							selectSql.append(String.format("%s.%s %s", alias, column, as));
						}
					}else{
						String column = this.getColumnByFieldName(fieldName);
						selectSql.append(String.format("%s.%s as %s", alias, column, fieldName));
					}
				}
			}
		}
		
		if(selectSql.length()==0){
			throw new RuntimeException("columns of target cannot be empty.");
		}
		return selectSql.toString();
	}
	
	/**
	 * 根据SqlTarget解析sql
	 * @param sqlTargetList
	 * @param aliasMap
	 * @return
	 */
	private String getFromSql(List<SqlTarget> sqlTargetList, Map<SqlTarget,String> aliasMap){
		StringBuffer fromSql = new StringBuffer();

		for(SqlTarget sqlTarget:sqlTargetList){
			String alias = aliasMap.get(sqlTarget);
			String entity = this.getTableNameByClass(sqlTarget.getEntityClass());
			if(entity==null){
				entity = sqlTarget.getEntityClass().getSimpleName();
			}
			if(fromSql.length()==0){
				fromSql.append("from");
			}else{
				fromSql.append(" ").append(sqlTarget.getJoinType().getCode());
			}

			fromSql.append(String.format(" %s as %s", entity, alias));
			
			if(sqlTarget.getPreKey()!=null){
				String preAlias = aliasMap.get(sqlTarget.getPreTarget());
				String preColumn = this.getColumnByFieldName(sqlTarget.getPreKey());
				String selfColumn = this.getColumnByFieldName(sqlTarget.getSelfKey());
				
				fromSql.append(String.format(" on %s.%s=%s.%s", preAlias, preColumn, alias, selfColumn));
			}
		}
		
		return fromSql.toString();
		
	}
	
	private String getGroupSql(Map<SqlTarget,String> aliasMap){
		StringBuffer groupSql = new StringBuffer();
		
		Set<SqlTarget> sqlTargetSet = aliasMap.keySet();
		for(SqlTarget sqlTarget:sqlTargetSet){
			String alias = aliasMap.get(sqlTarget);
			if(sqlTarget.getGroupFieldNames()!=null){
				for(Serializable field:sqlTarget.getGroupFieldNames()){
					String fieldName = null;
					if(field instanceof SqlFunction){
						fieldName = ((SqlFunction)field).getContent();
					}else{
						fieldName = field.toString();
					}
					if(groupSql.length()>0){
						groupSql.append(", ");
					}else{
						groupSql.append("group by ");
					}
					if(fieldName.indexOf("as ")>=0){
						//判断是否是聚合函数
						if(fieldName.indexOf("(")>=0&&fieldName.indexOf(")")>=0){
							groupSql.append(fieldName);
						}else{
							String column = fieldName.substring(0,fieldName.indexOf("as ")-1);
							column = this.getColumnByFieldName(column);
							groupSql.append(String.format("%s.%s", alias, column));
						}
					}else{
						String column = this.getColumnByFieldName(fieldName);
						groupSql.append(String.format("%s.%s", alias, column));
					}
				}
			}
		}
		
		return groupSql.toString();
	}

	/**
	 * 解析排序的sql
	 * @param query
	 * @return
	 */
	private String getOrderSql(IQuery<?> query, Map<SqlTarget,String> aliasMap){
		StringBuffer orderSql = new StringBuffer();
		
		//处理排序
		if(query.getOrders()!=null&&query.getOrders().length>0){
			
			for(Order order:query.getOrders()){
				if(orderSql.length()==0){
					orderSql.append(" order by ");
				}else{
					orderSql.append(",");
				}
				
				if(order instanceof SqlOrder){
					String column = null;
					if(order.getKey() instanceof SqlFunction){
						column = ((SqlFunction)order.getKey()).getContent();
					}else{
						column = this.getColumnByFieldName(order.getKey().toString());
					}
					String alias = aliasMap.get(((SqlOrder)order).getSqlTarget());
					orderSql.append(String.format("%s.%s %s", alias, column, order.getOrderType().getCode()));
				}else{
					orderSql.append(String.format("%s %s", order.getKey(), order.getOrderType().getCode()));
				}
			}
		}
		return orderSql.toString();
	}
	
	/**
	 * 
	 * @param target root
	 * @param sqlTargetList 目标集合
	 */
	private void fillTargetList(SqlTarget target, List<SqlTarget> sqlTargetList){
		sqlTargetList.add(target);
		
		if(target.getJoinList()!=null){
			for(SqlTarget sqlTarget:target.getJoinList()){
				sqlTarget.setPreTarget(target);
				this.fillTargetList(sqlTarget, sqlTargetList);
			}
		}
	}
	
	/**
	 * 
	 * @param target root
	 * @param sqlTargetList 目标集合
	 */
	private void fillTargetList(SqlJoinTarget target, List<SqlTarget> sqlTargetList){
		if(!sqlTargetList.contains(target.getParent())){
			sqlTargetList.add(target.getParent());
		}
		if(!sqlTargetList.contains(target.getChild())){
			sqlTargetList.add(target.getChild());
		}
		if(!target.getParent().getJoinList().contains(target.getChild())){
			target.getParent().getJoinList().add(target.getChild()
					.setJoinType(target.getJoinType())
					.setPreKey(target.getParentKey())
					.setSelfKey(target.getChildKey())
					);
			
			target.getChild().setPreTarget(target.getParent());
		}
		if(target.getNext()!=null){
			this.fillTargetList(target.getNext(), sqlTargetList);
		}
	}
	
	/**
	 * 解析过滤条件sql
	 * @param sqlTargetList
	 * @param aliasMap
	 * @return
	 */
	private String getWhereSql(List<SqlTarget> sqlTargetList, Map<SqlTarget,String> aliasMap){
		StringBuffer hql = new StringBuffer();
		//处理过滤条件
		for(SqlTarget target:sqlTargetList){
			String whereSql = this.getWhereSql(target, aliasMap.get(target));
			if(hql.length()>0){
				whereSql = whereSql.replaceAll(AndOr.where.getCode(), AndOr.and.getCode());
			}
			hql.append(" ").append(whereSql);
		}
		
		return hql.toString();
	}
	
	/**
	 * 解析过滤条件sql
	 * @param target
	 * @return
	 */
	private String getWhereSql(ITarget target, String alias){
		StringBuffer hql = new StringBuffer();
		//处理过滤条件
		Filter filter = target.getFilter();
		if(filter!=null){
			do{
				hql.append(String.format(" %s (", filter.getAndOr().getCode()));
				String joinStr = "";
				for(FilterEntry filterEntry:filter.getFilterEntrys()){
					hql.append(String.format("%s %s ", joinStr, this.getFilterEntrySql(target.getEntityClass(), filterEntry, alias, target instanceof SqlTarget)));
					joinStr = filter.getChildAndOr().getCode();
				}
				hql.append(")");
			}while((filter=filter.getNext())!=null);
		}
		return hql.toString();
	}
	
	private String getOrderSql(IQuery<?> query){
		return this.getOrderSql(query, null);
	}
	
	/**
	 * 获取过滤条件sql参数
	 * @param entityClass
	 * @param filterEntry
	 * @return
	 */
	private Object getFilterEntrySql(Class<?> entityClass, FilterEntry filterEntry,String alias, boolean isSql){
		Class<?> fieldType = String.class;
		String column = null;
		Serializable key = filterEntry.getKey();
		if(key instanceof SqlFunction){
			SqlFunction sqlFunction = (SqlFunction) key;
			column = sqlFunction.getContent();
			fieldType = sqlFunction.getValueType();
		}else{
			column = key.toString();
			Field field = this.getField(entityClass, column);
			if(isSql){
				column = this.getColumnByFieldName(column);
			}
			if(field==null){
//				throw new RuntimeException(String.format("field not mapping: %s", filterEntry.getKey()));
			}else{
				fieldType = field.getType();
				if(fieldType.equals(Object.class)){
					fieldType = filterEntry.getValue().getClass();
				}
			}
		}
		
		
		if(filterEntry.getCompare().equals(Compare.like)){
			return new StringBuffer(alias).append(".").append(column).append(" ").append(filterEntry.getCompare().getCode()).append(" '%").append(filterEntry.getValue()).append("%'").toString();
		}else if(filterEntry.getCompare().equals(Compare.lelike)){
			return new StringBuffer(alias).append(".").append(column).append(" ").append(filterEntry.getCompare().getCode()).append(" '%").append(filterEntry.getValue()).append("'").toString();
		}else if(filterEntry.getCompare().equals(Compare.rilike)){
			return new StringBuffer(alias).append(".").append(column).append(" ").append(filterEntry.getCompare().getCode()).append(" '").append(filterEntry.getValue()).append("%'").toString();
		}else if(filterEntry.getCompare().equals(Compare.nl)){
			return String.format("%s.%s is null", alias, column);
		}else if(filterEntry.getCompare().equals(Compare.nnl)){
			return String.format("%s.%s is not null", alias, column);
		}else if(filterEntry.getCompare().equals(Compare.in)||filterEntry.getCompare().equals(Compare.nin)||filterEntry.getValue().getClass().isArray() || fieldType.isArray()){
			StringBuffer sb =new StringBuffer(column).append(" ").append(filterEntry.getCompare().getCode()).append(" (");
			Object[] objs = (Object[])filterEntry.getValue();
			String joinStr="";
			for(Object obj:objs){
				sb.append(joinStr);
				joinStr=",";
				if(obj instanceof String){
					sb.append("'").append(obj).append("'");
				}else{
					sb.append(obj);
				}
			}
			sb.append(")");
			return sb.toString();
		}else if(fieldType.equals(String.class)){
			return String.format("%s.%s %s '%s'", alias, column, filterEntry.getCompare().getCode(), filterEntry.getValue());
		}else if(fieldType.equals(Boolean.class)||filterEntry.getValue() instanceof Boolean){
			boolean v = (Boolean)filterEntry.getValue();
			return String.format("%s.%s %s %s", alias, column, filterEntry.getCompare().getCode(), v?"1":"0");
		}else{
			return String.format("%s.%s %s %s", alias, column, filterEntry.getCompare().getCode(), filterEntry.getValue());
		}
	}
	
	/**
	 * 反射获取字段
	 * @param entityClass
	 * @param fieldName
	 * @return
	 */
	private Field getField(Class<?> entityClass, String fieldName){
		Field field = null;
		try {
			field = entityClass.getDeclaredField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
		}
		if(field==null&&entityClass.getSuperclass()!=null&&entityClass.getSuperclass()!=Object.class){
			return this.getField(entityClass.getSuperclass(), fieldName);
		}
		return field;
	}
	
	public Class<?> getTypeByField(Field field, Class<?> entityClass){
		return null;
	}
	
	private String getTableNameByClass(Class<?> clazz){
		Table table = AnnotationUtil.getAnnotation(clazz, Table.class);
		if(table!=null && !StringUtils.isEmpty(table.name())) {
			return table.name();
		}
		return getHibernateNamingStrategy().classToTableName(clazz.getSimpleName());
	}
	
	private String getColumnByFieldName(String filedName){
		return getHibernateNamingStrategy().propertyToColumnName(filedName);
	}
	

}
