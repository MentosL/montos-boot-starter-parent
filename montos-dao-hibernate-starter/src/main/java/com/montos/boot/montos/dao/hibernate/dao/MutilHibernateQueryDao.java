package com.montos.boot.montos.dao.hibernate.dao;

import com.montos.boot.montos.dao.hibernate.helper.AliasToEntityResultTransformer;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import java.util.List;
import java.util.Map;

public class MutilHibernateQueryDao extends MutilHibernateDao {

	public List<?> query(String hql, int pageNum, int pageSize) {
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult((pageNum - 1) * pageSize);
		query.setMaxResults(pageSize);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> queryBySql(String hql, int pageNum, int pageSize, Class<T> clazz) {
		SQLQuery query = this.getSession().createSQLQuery(hql);
		query.setFirstResult((pageNum - 1) * pageSize);
		query.setMaxResults(pageSize);
		if (clazz != null) {

			if (Map.class.isAssignableFrom(clazz)) {
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			} else {
				query.setResultTransformer(AliasToEntityResultTransformer.create(clazz));
			}

		}
		return query.list();
	}

	public int execute(String sql) {
		return this.getSession().createSQLQuery(sql).executeUpdate();
	}

}
