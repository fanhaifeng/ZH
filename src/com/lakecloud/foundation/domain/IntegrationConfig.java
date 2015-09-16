package com.lakecloud.foundation.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.OneToOne;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * 农豆配置表
 * @author chendandan
 *
 */

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "integrationConfig")
public class IntegrationConfig extends IdEntity{

	 @Column
	 private int regInt;//注册获得农豆数
	 
	 @Column
	 private int inviteInt;//邀请获得农豆数
 
	 @Column(precision = 12, scale = 2)
	 private BigDecimal order_to_int;//订单金额可折合的农豆数
	 
	 @Column(precision = 12, scale = 2)
	 private BigDecimal int_to_mon;//农豆兑换钱的比例
	 
	 @Column(precision = 12, scale = 2)
	 private BigDecimal order_need_int;//订单可使用的农豆比例
	 
	@Column(length = 50)
	 private String field1;//预留字段
	@Column(length = 50)
	 private String field2;
	@Column(length = 50)
	 private String field3;

	

	public int getRegInt() {
		return regInt;
	}

	public void setRegInt(int regInt) {
		this.regInt = regInt;
	}

	public int getInviteInt() {
		return inviteInt;
	}

	public void setInviteInt(int inviteInt) {
		this.inviteInt = inviteInt;
	}

	public BigDecimal getOrder_to_int() {
		return order_to_int;
	}

	public void setOrder_to_int(BigDecimal orderToInt) {
		order_to_int = orderToInt;
	}

	public BigDecimal getInt_to_mon() {
		return int_to_mon;
	}

	public void setInt_to_mon(BigDecimal intToMon) {
		int_to_mon = intToMon;
	}

	public BigDecimal getOrder_need_int() {
		return order_need_int;
	}

	public void setOrder_need_int(BigDecimal orderNeedInt) {
		order_need_int = orderNeedInt;
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
