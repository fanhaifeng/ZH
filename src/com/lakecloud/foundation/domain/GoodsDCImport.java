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

/**
 * 
 * <p>
 * Title: GoodsDCImport.java
 * </p>
 * 
 * <p>
 * Description: 商品导入临时存放类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-5-7
 * 
 * @version LakeCloud_C2C 1.4
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "GOODS_DC_IMPORT")
public class GoodsDCImport extends IdEntity {
	private String seo_keywords;// seo关键字
	private String seo_description;// seo描述
	private String goods_name;// 商品名称
	private String goods_serial;// 商品货号
	private String goods_weight;// 商品重量
	private String gcfirst;// 商品对应的大分类一级目录
	private String gcsecond;// 商品对应的大分类二级目录
	private String gcthird;// 商品对应的大分类三级目录
	private String goods_brand;// 商品品牌
	private String goods_clour;// 商品颜色
	private String goods_bag_weight;// 商品袋重
	private String goods_formula;// 商品配比
	public String getSeo_keywords() {
		return seo_keywords;
	}
	public void setSeo_keywords(String seo_keywords) {
		this.seo_keywords = seo_keywords;
	}
	public String getSeo_description() {
		return seo_description;
	}
	public void setSeo_description(String seo_description) {
		this.seo_description = seo_description;
	}
	public String getGoods_name() {
		return goods_name;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	public String getGoods_serial() {
		return goods_serial;
	}
	public void setGoods_serial(String goods_serial) {
		this.goods_serial = goods_serial;
	}
	public String getGoods_weight() {
		return goods_weight;
	}
	public void setGoods_weight(String goods_weight) {
		this.goods_weight = goods_weight;
	}
	public String getGcfirst() {
		return gcfirst;
	}
	public void setGcfirst(String gcfirst) {
		this.gcfirst = gcfirst;
	}
	public String getGcsecond() {
		return gcsecond;
	}
	public void setGcsecond(String gcsecond) {
		this.gcsecond = gcsecond;
	}
	public String getGcthird() {
		return gcthird;
	}
	public void setGcthird(String gcthird) {
		this.gcthird = gcthird;
	}
	public String getGoods_brand() {
		return goods_brand;
	}
	public void setGoods_brand(String goods_brand) {
		this.goods_brand = goods_brand;
	}
	public String getGoods_clour() {
		return goods_clour;
	}
	public void setGoods_clour(String goods_clour) {
		this.goods_clour = goods_clour;
	}
	public String getGoods_bag_weight() {
		return goods_bag_weight;
	}
	public void setGoods_bag_weight(String goods_bag_weight) {
		this.goods_bag_weight = goods_bag_weight;
	}
	public String getGoods_formula() {
		return goods_formula;
	}
	public void setGoods_formula(String goods_formula) {
		this.goods_formula = goods_formula;
	}
	

}