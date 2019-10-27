package com.montos.boot.montos.dao.api.entity;

import com.montos.boot.montos.dao.api.validator.ValidateGroup;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class UUIDBaseBean extends BaseBean<String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(generator = "uuid") @Column(length=32)
    @GenericGenerator(name = "uuid", strategy = "uuid")
	@NotBlank(groups={ValidateGroup.Get.class,ValidateGroup.Delete.class})
	public String id;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}