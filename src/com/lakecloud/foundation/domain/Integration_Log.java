package com.lakecloud.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * 农豆记录信息
 * 
 * @author chendandan
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "integration_log")
public class Integration_Log extends IdEntity {
	@Column
	private Integer integrals;// 平台农豆
	@Column
	private Integer integrals_store;// 店铺农豆
	@Column
	private Integer be_integrals_store;// 消费前的店铺农豆
	@Column
	private Integer be_integrals;// 消费前的平台农豆
	@Column
	private Integer af_integrals_store;// 消费后的店铺农豆
	@Column
	private Integer af_integrals;// 消费后的平台农豆
	@Column
	private int type;// 加减类型：1:增加农豆 -1：减农豆
	@Column
	private int gettype;// 消费路径：0:订单 1:注册 2：推荐用户 -1：农技服务
	@Column
	private Integer overdue_integrals;// 即将过期的平台农豆
	@Column
	private Integer overdue_integrals_order;// 即将过期的订单农豆
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 对应用户
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;// 对应店铺
	@ManyToOne(fetch = FetchType.LAZY)
	private Integration integ;// 对应的主表
	@ManyToOne(fetch = FetchType.LAZY)
	private OrderForm orderForm;// 对应的订单号
	@Column(length = 50)
	private String field1;// 预留字段
	@Column(length = 50)
	private String field2;
	@Column(length = 50)
	private String field3;

	public Integration_Log() {
	}

	public Integration_Log(Integer integrals, Integer integralsStore, int type,
			int gettype, Integration integ, OrderForm orderForm) {
		this.integrals = integrals;
		integrals_store = integralsStore;
		this.type = type;
		this.gettype = gettype;
		this.integ = integ;
		this.orderForm = orderForm;
	}

	public Integer getIntegrals() {
		return integrals;
	}

	public void setIntegrals(Integer integrals) {
		this.integrals = integrals;
	}

	public Integer getIntegrals_store() {
		return integrals_store;
	}

	public void setIntegrals_store(Integer integralsStore) {
		integrals_store = integralsStore;
	}

	public Integer getBe_integrals_store() {
		return be_integrals_store;
	}

	public void setBe_integrals_store(Integer beIntegralsStore) {
		be_integrals_store = beIntegralsStore;
	}

	public Integer getBe_integrals() {
		return be_integrals;
	}

	public void setBe_integrals(Integer beIntegrals) {
		be_integrals = beIntegrals;
	}

	public Integer getAf_integrals_store() {
		return af_integrals_store;
	}

	public void setAf_integrals_store(Integer afIntegralsStore) {
		af_integrals_store = afIntegralsStore;
	}

	public Integer getAf_integrals() {
		return af_integrals;
	}

	public void setAf_integrals(Integer afIntegrals) {
		af_integrals = afIntegrals;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getGettype() {
		return gettype;
	}

	public void setGettype(int gettype) {
		this.gettype = gettype;
	}

	public Integer getOverdue_integrals() {
		return overdue_integrals;
	}

	public void setOverdue_integrals(Integer overdueIntegrals) {
		overdue_integrals = overdueIntegrals;
	}

	public Integer getOverdue_integrals_order() {
		return overdue_integrals_order;
	}

	public void setOverdue_integrals_order(Integer overdueIntegralsOrder) {
		overdue_integrals_order = overdueIntegralsOrder;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Integration getInteg() {
		return integ;
	}

	public void setInteg(Integration integ) {
		this.integ = integ;
	}

	public OrderForm getOrderForm() {
		return orderForm;
	}

	public void setOrderForm(OrderForm orderForm) {
		this.orderForm = orderForm;
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

	public String getField3() {
		return field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}
}
