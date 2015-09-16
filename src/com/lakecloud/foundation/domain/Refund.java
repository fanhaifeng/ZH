package com.lakecloud.foundation.domain;

import java.math.BigDecimal;
import java.util.Date;

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
 * 赊销订单
 * @author Administrator
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "refund")
public class Refund extends IdEntity{

	@ManyToOne(fetch = FetchType.LAZY)
	private OrderForm of;
	
	@Column(precision = 12, scale = 2)
	private BigDecimal needPay;//还款金额

	public OrderForm getOf() {
		return of;
	}
	public void setOf(OrderForm of) {
		this.of = of;
	}
	public BigDecimal getNeedPay() {
		return needPay;
	}
	public void setNeedPay(BigDecimal needPay) {
		this.needPay = needPay;
	}	
}
