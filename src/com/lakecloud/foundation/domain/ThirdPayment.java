package com.lakecloud.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 中金支付-支付
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net bm
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "third_payment")
public class ThirdPayment extends IdEntity {
	/**
	 * 机构号码
	 */
	private String institutionID;
	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 支付流水号
	 */
	private String paymentNo;
	/**
	 * 订单金额
	 */
	private long amount;
	/**
	 * 绑定流水号
	 */
	private String txSNBinding;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * TxCode
	 */
	private String txCode;
	/**
	 * 创建人
	 */
	@Column(length = 60)
	private String create_user;
	/**
	 * BBC订单号
	 */
	private String order_id;

	public String getInstitutionID() {
		return institutionID;
	}

	public void setInstitutionID(String institutionID) {
		this.institutionID = institutionID;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getTxSNBinding() {
		return txSNBinding;
	}

	public void setTxSNBinding(String txSNBinding) {
		this.txSNBinding = txSNBinding;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTxCode() {
		return txCode;
	}

	public void setTxCode(String txCode) {
		this.txCode = txCode;
	}

	public String getCreate_user() {
		return create_user;
	}

	public void setCreate_user(String createUser) {
		create_user = createUser;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String orderId) {
		order_id = orderId;
	}
}
