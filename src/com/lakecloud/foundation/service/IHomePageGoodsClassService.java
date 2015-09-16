﻿package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.HomePageGoodsClass;

public interface IHomePageGoodsClassService {
	/**
	 * 保存一个HomePageGoodsClass，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(HomePageGoodsClass instance);
	
	/**
	 * 根据一个ID得到HomePageGoodsClass
	 * 
	 * @param id
	 * @return
	 */
	HomePageGoodsClass getObjById(Long id);
	
	/**
	 * 删除一个HomePageGoodsClass
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除HomePageGoodsClass
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到HomePageGoodsClass
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个HomePageGoodsClass
	 * 
	 * @param id
	 *            需要更新的HomePageGoodsClass的id
	 * @param dir
	 *            需要更新的HomePageGoodsClass
	 */
	boolean update(HomePageGoodsClass instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<HomePageGoodsClass> query(String query, Map params, int begin, int max);
}
