﻿package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.GoodsFloorGoods;

public interface IGoodsFloorGoodsService {
	/**
	 * 保存一个GoodsFloorGoods，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsFloorGoods instance);

	/**
	 * 根据一个ID得到GoodsFloorGoods
	 * 
	 * @param id
	 * @return
	 */
	GoodsFloorGoods getObjById(Long id);

	/**
	 * 删除一个GoodsFloorGoods
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除GoodsFloorGoods
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到GoodsFloorGoods
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个GoodsFloorGoods
	 * 
	 * @param id
	 *            需要更新的GoodsFloorGoods的id
	 * @param dir
	 *            需要更新的GoodsFloorGoods
	 */
	boolean update(GoodsFloorGoods instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsFloorGoods> query(String query, Map params, int begin, int max);
}
