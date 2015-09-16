package com.lakecloud.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * 农豆主表
 * 
 * @author chendandan
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "integration")
public class Integration extends IdEntity {
	private Integer integrals;// 农豆
	private Integer overdue_integrals;// 即将过期的农豆
	@OneToOne
	private User user;
	@Column(length = 50)
	private String field1;// 预留字段
	@Column(length = 50)
	private String field2;
	@Column(length = 50)
	private String field3;

	public Integer getIntegrals() {
		return integrals;
	}

	public void setIntegrals(Integer integrals) {
		this.integrals = integrals;
	}

	public Integer getOverdue_integrals() {
		return overdue_integrals;
	}

	public void setOverdue_integrals(Integer overdueIntegrals) {
		overdue_integrals = overdueIntegrals;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	public String getField3() {
		return field3;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}
}
