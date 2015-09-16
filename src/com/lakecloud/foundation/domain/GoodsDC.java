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
 * Title: Goods.java
 * </p>
 * 
 * <p>
 * Description: 商品实体类,用来描述系统商品信息，系统核心实体类
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "GOODS_DC")
public class GoodsDC extends IdEntity {
	private String seo_keywords;// 关键字
	/**
	 * @info 数据库移植兼容性修改
	 * @author chenyong
	 * @see 修改@Column(columnDefinition = "LongText")
	 */
	@Lob
	@Column(length = 1048576)
	private String seo_description;// 描述
	private String goods_name;// 商品名称
	private int goods_salenum;// 商品售出数量
	private String goods_serial;// 商品货号
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_weight;// 商品重量
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_volume;// 商品体积，自从V1.3开始
	private String goods_fee;// 运费
	/**
	 * @info 数据库移植兼容性修改
	 * @author chenyong
	 * @see 修改@Column(columnDefinition = "LongText")
	 */
	@Lob
	@Column(length = 1048576)
	private String goods_details;// 详细说明
	private boolean goods_recommend;// 是否店铺推荐，推荐后在店铺首页推荐位显示
	@ManyToOne(fetch = FetchType.LAZY)
	private Store goods_store;// 所属店铺
	private int goods_status;// 商品状态，0为上架，1为在仓库中，-1为手动下架状态，-2为违规下架状态,-3被举报禁售
	private int goods_transfee;// 商品运费承担方式，0为买家承担，1为卖家承担
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsClass gc;// 商品对应的大分类
	@ManyToOne(cascade = CascadeType.REMOVE)
	private Accessory goods_main_photo;// 商品主图片
	@ManyToMany
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "goodsDC_photo", joinColumns = @JoinColumn(name = "goods_id"), inverseJoinColumns = @JoinColumn(name = "photo_id"))
	private List<Accessory> goodsDC_photos = new ArrayList<Accessory>();// 商品其他图片，目前只允许4张,图片可以重复使用

	@ManyToMany
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "goodsDC_spec", joinColumns = @JoinColumn(name = "goods_id"), inverseJoinColumns = @JoinColumn(name = "spec_id"))
	@OrderBy(value = "sequence asc")
	private List<GoodsSpecProperty> goodsDC_specs = new ArrayList<GoodsSpecProperty>();// 商品对应的规格值
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsBrand goods_brand;// 商品品牌

	@Lob
	@Column(length = 1048576)
	private String goods_property;

	private int goods_choice_type;// 0实体商品，1为虚拟商品
	@Column
	private int goodsdc_status;// 0可售，1不可销售

	@Column(precision = 12, scale = 2)
	private BigDecimal mail_trans_fee;// 平邮费用
	@Column(precision = 12, scale = 2)
	private BigDecimal express_trans_fee;// 快递费用
	@Column(precision = 12, scale = 2)
	private BigDecimal ems_trans_fee;// EMS费用
	@ManyToOne(fetch = FetchType.LAZY)
	private Transport transport;// 调用的运费模板信息

	public BigDecimal getMail_trans_fee() {
		return mail_trans_fee;
	}

	public void setMail_trans_fee(BigDecimal mailTransFee) {
		mail_trans_fee = mailTransFee;
	}

	public BigDecimal getExpress_trans_fee() {
		return express_trans_fee;
	}

	public void setExpress_trans_fee(BigDecimal expressTransFee) {
		express_trans_fee = expressTransFee;
	}

	public BigDecimal getEms_trans_fee() {
		return ems_trans_fee;
	}

	public void setEms_trans_fee(BigDecimal emsTransFee) {
		ems_trans_fee = emsTransFee;
	}

	public Transport getTransport() {
		return transport;
	}

	public void setTransport(Transport transport) {
		this.transport = transport;
	}

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

	public int getGoods_salenum() {
		return goods_salenum;
	}

	public void setGoods_salenum(int goods_salenum) {
		this.goods_salenum = goods_salenum;
	}

	public String getGoods_serial() {
		return goods_serial;
	}

	public void setGoods_serial(String goods_serial) {
		this.goods_serial = goods_serial;
	}

	public BigDecimal getGoods_weight() {
		return goods_weight;
	}

	public void setGoods_weight(BigDecimal goods_weight) {
		this.goods_weight = goods_weight;
	}

	public String getGoods_fee() {
		return goods_fee;
	}

	public void setGoods_fee(String goods_fee) {
		this.goods_fee = goods_fee;
	}

	public String getGoods_details() {
		return goods_details;
	}

	public void setGoods_details(String goods_details) {
		this.goods_details = goods_details;
	}

	public boolean isGoods_recommend() {
		return goods_recommend;
	}

	public void setGoods_recommend(boolean goods_recommend) {
		this.goods_recommend = goods_recommend;
	}

	public Store getGoods_store() {
		return goods_store;
	}

	public void setGoods_store(Store goods_store) {
		this.goods_store = goods_store;
	}

	public int getGoods_status() {
		return goods_status;
	}

	public void setGoods_status(int goods_status) {
		this.goods_status = goods_status;
	}

	public int getGoods_transfee() {
		return goods_transfee;
	}

	public void setGoods_transfee(int goods_transfee) {
		this.goods_transfee = goods_transfee;
	}

	public GoodsClass getGc() {
		return gc;
	}

	public void setGc(GoodsClass gc) {
		this.gc = gc;
	}

	public Accessory getGoods_main_photo() {
		return goods_main_photo;
	}

	public void setGoods_main_photo(Accessory goods_main_photo) {
		this.goods_main_photo = goods_main_photo;
	}

	// public List<UserGoodsClass> getGoods_ugcs() {
	// return goods_ugcs;
	// }
	//
	// public void setGoods_ugcs(List<UserGoodsClass> goods_ugcs) {
	// this.goods_ugcs = goods_ugcs;
	// }

	public GoodsBrand getGoods_brand() {
		return goods_brand;
	}

	public void setGoods_brand(GoodsBrand goods_brand) {
		this.goods_brand = goods_brand;
	}

	public String getGoods_property() {
		return goods_property;
	}

	public void setGoods_property(String goods_property) {
		this.goods_property = goods_property;
	}

	public int getGoods_choice_type() {
		return goods_choice_type;
	}

	public void setGoods_choice_type(int goods_choice_type) {
		this.goods_choice_type = goods_choice_type;
	}

	public BigDecimal getGoods_volume() {
		return goods_volume;
	}

	public void setGoods_volume(BigDecimal goods_volume) {
		this.goods_volume = goods_volume;
	}

	public void setGoodsDC_photos(List<Accessory> goodsDC_photos) {
		this.goodsDC_photos = goodsDC_photos;
	}

	public List<Accessory> getGoodsDC_photos() {
		return goodsDC_photos;
	}

	public void setGoodsDC_specs(List<GoodsSpecProperty> goodsDC_specs) {
		this.goodsDC_specs = goodsDC_specs;
	}

	public List<GoodsSpecProperty> getGoodsDC_specs() {
		return goodsDC_specs;
	}

	public void setGoodsdc_status(int goodsdc_status) {
		this.goodsdc_status = goodsdc_status;
	}

	public int getGoodsdc_status() {
		return goodsdc_status;
	}

}
