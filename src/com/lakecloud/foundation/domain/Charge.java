package com.lakecloud.foundation.domain;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;


@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "charge")
public class Charge extends IdEntity{

	 private int paymentDays;//赊销账期
	 
	 @Column(precision = 12, scale = 2)
	 private BigDecimal usedPayNum;//已经使用的赊销金额
	 @Column(precision = 12, scale = 2)
	 private BigDecimal paymentNum;//赊销金额
	 
	 @ManyToOne(optional=false)
	 @JoinColumn(name="user_id")
	 private User user;
	 
	 @ManyToOne(optional=false)
	 @JoinColumn(name="store_id")
	 private Store store;



	public int getPaymentDays() {
		return paymentDays;
	}

	public void setPaymentDays(int paymentDays) {
		this.paymentDays = paymentDays;
	}

	public BigDecimal getUsedPayNum() {
		return usedPayNum;
	}

	public void setUsedPayNum(BigDecimal usedPayNum) {
		this.usedPayNum = usedPayNum;
	}

	public BigDecimal getPaymentNum() {
		return paymentNum;
	}

	public void setPaymentNum(BigDecimal paymentNum) {
		this.paymentNum = paymentNum;
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
}
