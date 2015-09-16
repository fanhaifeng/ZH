﻿package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.Store;

public interface IStoreService {
	/**
	 * 保存一个Store，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Store instance);

	/**
	 * 根据一个ID得到Store
	 * 
	 * @param id
	 * @return
	 */
	Store getObjById(Long id);

	/**
	 * 删除一个Store
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除Store
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到Store
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个Store
	 * 
	 * @param id
	 *            需要更新的Store的id
	 * @param dir
	 *            需要更新的Store
	 */
	boolean update(Store instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Store> query(String query, Map params, int begin, int max);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Store getObjByProperty(String propertyName, Object value);
	
	public JSONObject save_store_user(String mulitId,String flag,String option);
}
