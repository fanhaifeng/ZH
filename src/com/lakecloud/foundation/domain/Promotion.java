package com.lakecloud.foundation.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 促销
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "promotion")
public class Promotion extends IdEntity {
	/**
	 * 促销类型
	 */
	@Column(length = 10)
	private String pr_type;
	/**
	 * 是否可堆叠
	 */
	@Column(length = 10)
	private String ispile;
	/**
	 * 立减金额
	 */
	@Column(precision = 12, scale = 2)
	private BigDecimal discount;
	/**
	 * 促销优先级
	 */
	private long sort;
	/**
	 * 创建时间
	 */
	private Timestamp createtime;
	/**
	 * 更新时间
	 */
	private Timestamp updatetime;
	@Column(length = 200)
	private String field1;
	@Column(length = 200)
	private String field2;
	@Column(length = 200)
	private String field3;
	/**
	 * 开始时间
	 */
	private Timestamp starttime;
	/**
	 * 结束时间
	 */
	private Timestamp endtime;
	/**
	 * 状态：0未激活，1已激活，2已停用，4删除
	 */
	@Column(length = 40)
	private String status;
	/**
	 * 促销名称
	 */
	@Column(length = 200)
	private String name;
	/**
	 * 创建人
	 */
	@Column(length = 60)
	private String create_user;
	@OneToMany(mappedBy = "promotion")
	private List<PromotionLevel> promotionLevels = new ArrayList<PromotionLevel>();
	/**
	 * 前缀
	 */
	@Column(length = 10)
	private String prefix;
	/**
	 * 系统生成的唯一代码长度
	 */
	@Column(length = 10)
	private String codeLen;
	/**
	 * 后缀
	 */
	@Column(length = 10)
	private String suffix;
	/**
	 * 要生成的唯一代码数
	 */
	@Column(length = 10)
	private String codeNum;

	public String getPr_type() {
		return pr_type;
	}

	public void setPr_type(String prType) {
		pr_type = prType;
	}

	public String getIspile() {
		return ispile;
	}

	public void setIspile(String ispile) {
		this.ispile = ispile;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public long getSort() {
		return sort;
	}

	public void setSort(long sort) {
		this.sort = sort;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Timestamp getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Timestamp createtime) {
		this.createtime = createtime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Timestamp getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
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

	@Temporal(TemporalType.TIMESTAMP)
	public Timestamp getStarttime() {
		return starttime;
	}

	public void setStarttime(Timestamp starttime) {
		this.starttime = starttime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Timestamp getEndtime() {
		return endtime;
	}

	public void setEndtime(Timestamp endtime) {
		this.endtime = endtime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreate_user() {
		return create_user;
	}

	public void setCreate_user(String createUser) {
		create_user = createUser;
	}

	public List<PromotionLevel> getPromotionLevels() {
		return promotionLevels;
	}

	public void setPromotionLevels(List<PromotionLevel> promotionLevels) {
		this.promotionLevels = promotionLevels;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getCodeLen() {
		return codeLen;
	}

	public void setCodeLen(String codeLen) {
		this.codeLen = codeLen;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getCodeNum() {
		return codeNum;
	}

	public void setCodeNum(String codeNum) {
		this.codeNum = codeNum;
	}
}
