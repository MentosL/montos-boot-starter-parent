package com.montos.boot.montos.dao.hibernate.validator;

import com.montos.boot.montos.dao.api.exception.QueryValidatorException;
import com.montos.boot.montos.dao.api.request.Filter;
import com.montos.boot.montos.dao.api.request.FilterEntry;
import com.montos.boot.montos.dao.api.request.IQuery;
import com.montos.boot.montos.dao.api.request.ITarget;
import com.montos.boot.montos.dao.hibernate.request.SqlTarget;

public class InjectSqlValidator implements IInjectSqlValidator {
	
	/**
	 * 非法字符串
	 */
	public static final String[] errStr={
			"'",
			" ",
			",",
			";",
			"*",
			"/",
			"\\",
			"+",
			"--",
			"=",
			"!",
			"<",
			">",
			"|",
			"?",
			"&",
			"$",
			"^",
			"@",
			"#"
			};
	
	public static void check(Object obj) throws QueryValidatorException {
		//是否字符串
		if(obj!=null && obj instanceof String){
			for(String str:errStr){
				if(obj.toString().indexOf(str)>=0){
					throw new QueryValidatorException();
				}
			}
		}
	}

	@Override
	public void check(IQuery<?> query) throws QueryValidatorException {
		if(query==null||query.getTarget()==null){
			return ;
		}
		this.check(query.getTarget());
		
	}
	
	private void check(ITarget target){
		this.check(target.getFilter());
		if(target instanceof SqlTarget){
			SqlTarget sqlTarget = (SqlTarget) target;
			if(sqlTarget.getJoinList()!=null){
				for(SqlTarget joinTarget:sqlTarget.getJoinList()){
					this.check(joinTarget);
				}
			}
			
		}
	}
	
	private void check(Filter filter){
		if(filter==null){
			return ;
		}
		do{
			for(FilterEntry filterEntry : filter.getFilterEntrys()){
				check(filterEntry.getValue());
			}
			
		}while((filter=filter.getNext())!=null);
	}

	@Override
	public void check(Object... objects) throws QueryValidatorException {
		for(Object obj:objects){
			InjectSqlValidator.check(obj);
		}
	}

}
