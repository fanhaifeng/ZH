package com.lakecloud.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;


@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "referprice")
public class ReferPrice extends IdEntity {

	@Column(precision = 12, scale = 2)
	private BigDecimal referprice;// 推荐价格
	@Column(length = 1048576)
	private int goods_id;//商品id
	@Column(length = 1048576)
	private int branch_id;//分公司id
	
	
	public BigDecimal getReferprice() {
		return referprice;
	}
	public int getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(int goodsId) {
		goods_id = goodsId;
	}
	public int getBranch_id() {
		return branch_id;
	}
	public void setBranch_id(int branchId) {
		branch_id = branchId;
	}
	public void setReferprice(BigDecimal referprice) {
		this.referprice = referprice;
	}



}
