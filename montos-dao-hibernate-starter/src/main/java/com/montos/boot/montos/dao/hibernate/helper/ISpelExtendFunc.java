package com.montos.boot.montos.dao.hibernate.helper;

/**
 * 自定义spel函数适配器
 * @author chenqi
 * @Date 2018年8月22日
 *
 */
public interface ISpelExtendFunc {
	
	/**
	 * spel变量关键字
	 * @return
	 */
	public String getKey();
	
	/**
	 * 格式化函数
	 * @param format
	 * @param value
	 * @return
	 */
	public Object format(String format, Object... value);

}
