package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 系统广告位管理类，系统广告使用广告位管理，一个广告位可以同时发布多个广告信息，随机出现一条或者固定显示一条
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "adv_pos")
public class AdvertPosition extends IdEntity {
	private String ap_title;// 广告位标题
	/**
	 * @info 数据库移植兼容性修改
	 * @author chenyong
	 * @see
	 * 	修改@Column(columnDefinition = "LongText")
	 */
	@Lob
	@Column(length = 1048576)
	private String ap_content;// 广告位说明
	private String ap_type;// 广告类别,目前有幻灯、滚动、图片、文字四种
	private int ap_status;// 0为关闭，1为启用
	private int ap_use_status;// 广告位使用状态，0为空闲，1为正在被使用，用在固定广告,2可以继续使用
	private int ap_width;// 广告位宽度
	private int ap_height;// 广告位高度
	private int ap_price;// 广告位价格，金币/月
	private int ap_sale_type;// 0为销售类广告，会员可以用购买，1为商城投放的广告，不能购买
	private int ap_sys_type;// 是否系统广告位，0为系统广告位不可删除，1为自定义广告位
	private int ap_show_type;// 展示方式，0为只有一则广告，1为可以投放很多，随机出现
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory ap_acc;// 广告默认图片，图片广告
	private String ap_text;// 广告默认文字，文字广告
	private String ap_acc_url;// 广告默认链接
	/**
	 * @info 数据库移植兼容性修改
	 * @author chenyong
	 * @see
	 * 	修改@Column(columnDefinition = "LongText")
	 */
	@Lob
	@Column(length = 1048576)
	private String ap_code;// 广告默认代码，可以投放百度等广告
	@OneToMany(mappedBy = "ad_ap", cascade = CascadeType.REMOVE)
	@OrderBy(value = "ad_slide_sequence asc")
	private List<Advert> advs = new ArrayList<Advert>();// 广告位对应的广告
	@Transient
	private String adv_id;// 广告id，不存储数据库

	public List<Advert> getAdvs() {
		return advs;
	}

	public void setAdvs(List<Advert> advs) {
		this.advs = advs;
	}

	public int getAp_sys_type() {
		return ap_sys_type;
	}

	public void setAp_sys_type(int ap_sys_type) {
		this.ap_sys_type = ap_sys_type;
	}

	public String getAp_title() {
		return ap_title;
	}

	public void setAp_title(String ap_title) {
		this.ap_title = ap_title;
	}

	public String getAp_content() {
		return ap_content;
	}

	public void setAp_content(String ap_content) {
		this.ap_content = ap_content;
	}

	public String getAp_type() {
		return ap_type;
	}

	public void setAp_type(String ap_type) {
		this.ap_type = ap_type;
	}

	public int getAp_status() {
		return ap_status;
	}

	public void setAp_status(int ap_status) {
		this.ap_status = ap_status;
	}

	public int getAp_width() {
		return ap_width;
	}

	public void setAp_width(int ap_width) {
		this.ap_width = ap_width;
	}

	public int getAp_height() {
		return ap_height;
	}

	public void setAp_height(int ap_height) {
		this.ap_height = ap_height;
	}

	public int getAp_price() {
		return ap_price;
	}

	public void setAp_price(int ap_price) {
		this.ap_price = ap_price;
	}

	public int getAp_use_status() {
		return ap_use_status;
	}

	public void setAp_use_status(int ap_use_status) {
		this.ap_use_status = ap_use_status;
	}

	public Accessory getAp_acc() {
		return ap_acc;
	}

	public void setAp_acc(Accessory ap_acc) {
		this.ap_acc = ap_acc;
	}

	public int getAp_sale_type() {
		return ap_sale_type;
	}

	public void setAp_sale_type(int ap_sale_type) {
		this.ap_sale_type = ap_sale_type;
	}

	public String getAp_code() {
		return ap_code;
	}

	public void setAp_code(String ap_code) {
		this.ap_code = ap_code;
	}

	public int getAp_show_type() {
		return ap_show_type;
	}

	public void setAp_show_type(int ap_show_type) {
		this.ap_show_type = ap_show_type;
	}

	public String getAp_acc_url() {
		return ap_acc_url;
	}

	public void setAp_acc_url(String ap_acc_url) {
		this.ap_acc_url = ap_acc_url;
	}

	public String getAp_text() {
		return ap_text;
	}

	public void setAp_text(String ap_text) {
		this.ap_text = ap_text;
	}

	public String getAdv_id() {
		return adv_id;
	}

	public void setAdv_id(String adv_id) {
		this.adv_id = adv_id;
	}

}
