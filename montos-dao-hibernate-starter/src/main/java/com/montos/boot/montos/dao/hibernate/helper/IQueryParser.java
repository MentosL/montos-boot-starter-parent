package com.montos.boot.montos.dao.hibernate.helper;

import com.montos.boot.montos.dao.api.request.IQuery;
import org.hibernate.Query;
import org.hibernate.Session;

public interface IQueryParser {
	
	/**
	 * 根据过滤条件解析hibernate的query
	 * @param session
	 * @param query
	 * @return
	 */
	public Query parse(Session session, IQuery<?> query);


}
