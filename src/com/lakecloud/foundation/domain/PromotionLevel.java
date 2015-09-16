package com.lakecloud.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 促销等级
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "promotionlevel")
public class PromotionLevel extends IdEntity {
	// private long promotion_id;
	private String user_username;
	private long goods_id;
	private String product;
	private String field1;
	private String field2;
	private String field3;
	private String isdirectory;
	@ManyToOne(fetch = FetchType.LAZY)
	private Promotion promotion;

	// public long getPromotion_id() {
	// return promotion_id;
	// }
	//
	// public void setPromotion_id(long promotionId) {
	// promotion_id = promotionId;
	// }
	public String getUser_username() {
		return user_username;
	}

	public void setUser_username(String userUsername) {
		user_username = userUsername;
	}

	public long getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(long goodsId) {
		goods_id = goodsId;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
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

	public String getIsdirectory() {
		return isdirectory;
	}

	public void setIsdirectory(String isdirectory) {
		this.isdirectory = isdirectory;
	}

	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}
}
