﻿package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.HomePage;

public interface IHomePageService {
	/**
	 * 保存一个HomePage，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(HomePage instance);
	
	/**
	 * 根据一个ID得到HomePage
	 * 
	 * @param id
	 * @return
	 */
	HomePage getObjById(Long id);
	
	/**
	 * 删除一个HomePage
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除HomePage
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到HomePage
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个HomePage
	 * 
	 * @param id
	 *            需要更新的HomePage的id
	 * @param dir
	 *            需要更新的HomePage
	 */
	boolean update(HomePage instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<HomePage> query(String query, Map params, int begin, int max);
}
