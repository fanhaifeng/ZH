package com.lakecloud.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 中金支付-绑卡
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net bm
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "third_binding")
public class ThirdBinding extends IdEntity {
	/**
	 * 机构号码
	 */
	private String institutionID;
	/**
	 * 绑定流水号
	 */
	private String txSNBinding;
	/**
	 * 银行ID
	 */
	private String bankID;
	/**
	 * 账户名称
	 */
	private String accountName;
	/**
	 * 账户号码
	 */
	private String accountNumber;
	/**
	 * 开户证件类型
	 */
	private String identificationType;
	/**
	 * 证件号码
	 */
	private String identificationNumber;
	/**
	 * 手机号
	 */
	private String phoneNumber;
	/**
	 * 卡类型:0身份证 1户口薄 2护照 3军官证 4士兵证 5港澳居民来往内地通行证 6台湾同胞来往内地通行证 7临时身份证 8外国人居留证 9警官证
	 * X其他证件
	 */
	private String cardType;
	/**
	 * 信用卡有效期
	 */
	private String validDate;
	/**
	 * 信用卡CVN号
	 */
	private String cvn2;
	/**
	 * TxCode
	 */
	private String txCode;
	/**
	 * 创建人
	 */
	@Column(length = 60)
	private String create_user;

	public String getInstitutionID() {
		return institutionID;
	}

	public void setInstitutionID(String institutionID) {
		this.institutionID = institutionID;
	}

	public String getTxSNBinding() {
		return txSNBinding;
	}

	public void setTxSNBinding(String txSNBinding) {
		this.txSNBinding = txSNBinding;
	}

	public String getBankID() {
		return bankID;
	}

	public void setBankID(String bankID) {
		this.bankID = bankID;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getIdentificationType() {
		return identificationType;
	}

	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}

	public String getIdentificationNumber() {
		return identificationNumber;
	}

	public void setIdentificationNumber(String identificationNumber) {
		this.identificationNumber = identificationNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getValidDate() {
		return validDate;
	}

	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}

	public String getCvn2() {
		return cvn2;
	}

	public void setCvn2(String cvn2) {
		this.cvn2 = cvn2;
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
}
