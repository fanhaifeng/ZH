package com.lakecloud.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_floor_goods")
public class GoodsFloorGoods extends IdEntity {
	@OneToOne(fetch = FetchType.LAZY)
	private Goods goods;
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsFloor goodsFloor;
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsFloor goodsFloor2;
	@ManyToOne(fetch = FetchType.LAZY)
	private Store goods_store;// 所属店铺

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public GoodsFloor getGoodsFloor() {
		return goodsFloor;
	}

	public void setGoodsFloor(GoodsFloor goodsFloor) {
		this.goodsFloor = goodsFloor;
	}

	public GoodsFloor getGoodsFloor2() {
		return goodsFloor2;
	}

	public void setGoodsFloor2(GoodsFloor goodsFloor2) {
		this.goodsFloor2 = goodsFloor2;
	}

	public Store getGoods_store() {
		return goods_store;
	}

	public void setGoods_store(Store goodsStore) {
		goods_store = goodsStore;
	}
}
