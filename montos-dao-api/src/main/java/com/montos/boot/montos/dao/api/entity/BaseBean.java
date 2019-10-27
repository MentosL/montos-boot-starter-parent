package com.montos.boot.montos.dao.api.entity;

import com.montos.boot.montos.core.api.annotation.JsonExclusion;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public class BaseBean<T> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//json的字段需要public声明
	@JsonExclusion
	@Transient
	public T[] ids;	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(nullable = false,updatable = false)
	public Date createTime;											//数据创建时间
	
	@Column(updatable = false)
	public String createAuthor;										//数据创建人员
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date updateTime;											//数据更新时间
	
	public String updateAuthor;										//数据更新人员
	@JsonExclusion
	public Boolean deleted=false;									//是否逻辑删除
	@Version @Column(nullable = false,columnDefinition="bigint default 0")	
	public Long version; 											//数据版本号
	@JsonExclusion @Transient
	public String[] fields;											//需要的查询字段
	
	public T getId(){
		return null;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateAuthor() {
		return updateAuthor;
	}

	public void setUpdateAuthor(String updateAuthor) {
		this.updateAuthor = updateAuthor;
	}

	public Boolean isDeleted() {
		return deleted;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public String getCreateAuthor() {
		return createAuthor;
	}

	public void setCreateAuthor(String createAuthor) {
		this.createAuthor = createAuthor;
	}

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public T[] getIds() {
		return ids;
	}

	public void setIds(T[] ids) {
		this.ids = ids;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}	
	
}