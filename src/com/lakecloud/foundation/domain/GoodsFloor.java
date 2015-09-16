package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 首页楼层管理类,首页楼层商城管理员可以使用拖拽式管理完成楼层配置，商城首页按照配置的楼层信息显示对应的商品、品牌、广告
 * @since V1.2
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_floor")
public class GoodsFloor extends IdEntity {
	private String gf_name;// 分类名称
	private String gf_css;// css名称，目前系统提供4种样式
	private int gf_sequence;// 分类序号，升序排列
	private int gf_goods_count;// 显示商品个数
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	@OrderBy(value = "gf_sequence asc")
	private List<GoodsFloor> childs = new ArrayList<GoodsFloor>();// 子分类
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsFloor parent;// 父级分类
	private int gf_level;// 层级
	private boolean gf_display;// 是否显示
	/**
	 * @info 数据库移植兼容性修改
	 * @author chenyong
	 * @see
	 * 	修改@Column(columnDefinition = "LongText")
	 */
	@Lob
	@Column(length = 1048576)
	private String gf_gc_list;// 楼层显示分类商品列表,使用json管理[{"pid":1,"gc_id":1,"gc_id":2},{"pid":1,"gc_id":1,"gc_id":2}]
	/**
	 * @info 数据库移植兼容性修改
	 * @author chenyong
	 * @see
	 * 	修改@Column(columnDefinition = "LongText")
	 */
	@Lob
	@Column(length = 1048576)
	private String gf_gc_goods;// 楼层显示分类商品,使用JSON管理{"goods_id1":1,"goods_id2":32768}
	/**
	 * @info 数据库移植兼容性修改
	 * @author chenyong
	 * @see
	 * 	修改@Column(columnDefinition = "LongText")
	 */
	@Lob
	@Column(length = 1048576)
	private String gf_list_goods;// 楼层右侧你别显示商品，使用JSON管理{"goods_id":1,"goods_id":32768}
	/**
	 * @info 数据库移植兼容性修改
	 * @author chenyong
	 * @see
	 * 	修改@Column(columnDefinition = "LongText")
	 */
	@Lob
	@Column(length = 1048576)
	private String gf_left_adv;// 楼层左侧广告，json管理{"acc_id":1,"acc_url":"www.chinacloud.net","adv_id":1}
	/**
	 * @info 数据库移植兼容性修改
	 * @author chenyong
	 * @see
	 * 	修改@Column(columnDefinition = "LongText")
	 */
	@Lob
	@Column(length = 1048576)
	private String gf_right_adv;// 楼层右侧广告，json管理{"acc_id":1,"acc_url":"www.chinacloud.net","adv_id":1}
	/**
	 * @info 数据库移植兼容性修改
	 * @author chenyong
	 * @see
	 * 	修改@Column(columnDefinition = "LongText")
	 */
	@Lob
	@Column(length = 1048576)
	private String gf_brand_list;// 楼层品牌信息，json管理{"brand_id1":1,"brand_id2":2}
	@Transient
	private String gf_list_goods_json;//用于pc首页，过滤城市

	public String getGf_brand_list() {
		return gf_brand_list;
	}

	public void setGf_brand_list(String gf_brand_list) {
		this.gf_brand_list = gf_brand_list;
	}

	public String getGf_name() {
		return gf_name;
	}

	public void setGf_name(String gf_name) {
		this.gf_name = gf_name;
	}

	public String getGf_css() {
		return gf_css;
	}

	public void setGf_css(String gf_css) {
		this.gf_css = gf_css;
	}

	public int getGf_sequence() {
		return gf_sequence;
	}

	public void setGf_sequence(int gf_sequence) {
		this.gf_sequence = gf_sequence;
	}

	public int getGf_goods_count() {
		return gf_goods_count;
	}

	public void setGf_goods_count(int gf_goods_count) {
		this.gf_goods_count = gf_goods_count;
	}

	public List<GoodsFloor> getChilds() {
		return childs;
	}

	public void setChilds(List<GoodsFloor> childs) {
		this.childs = childs;
	}

	public GoodsFloor getParent() {
		return parent;
	}

	public void setParent(GoodsFloor parent) {
		this.parent = parent;
	}

	public int getGf_level() {
		return gf_level;
	}

	public void setGf_level(int gf_level) {
		this.gf_level = gf_level;
	}

	public boolean isGf_display() {
		return gf_display;
	}

	public void setGf_display(boolean gf_display) {
		this.gf_display = gf_display;
	}

	public String getGf_gc_list() {
		return gf_gc_list;
	}

	public void setGf_gc_list(String gf_gc_list) {
		this.gf_gc_list = gf_gc_list;
	}

	public String getGf_gc_goods() {
		return gf_gc_goods;
	}

	public void setGf_gc_goods(String gf_gc_goods) {
		this.gf_gc_goods = gf_gc_goods;
	}

	public String getGf_list_goods() {
		return gf_list_goods;
	}

	public void setGf_list_goods(String gf_list_goods) {
		this.gf_list_goods = gf_list_goods;
	}

	public String getGf_left_adv() {
		return gf_left_adv;
	}

	public void setGf_left_adv(String gf_left_adv) {
		this.gf_left_adv = gf_left_adv;
	}

	public String getGf_right_adv() {
		return gf_right_adv;
	}

	public void setGf_right_adv(String gf_right_adv) {
		this.gf_right_adv = gf_right_adv;
	}

	public String getGf_list_goods_json() {
		return gf_list_goods_json;
	}

	public void setGf_list_goods_json(String gfListGoodsJson) {
		gf_list_goods_json = gfListGoodsJson;
	}

}
