package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.Crop;

public interface ICropService {
	/**
	 * 保存一个Crop，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Crop instance);
	
	/**
	 * 根据一个ID得到Crop
	 * 
	 * @param id
	 * @return
	 */
	Crop getObjById(Long id);
	
	/**
	 * 删除一个Crop
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Crop
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Crop
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Crop
	 * 
	 * @param id
	 *            需要更新的Crop的id
	 * @param dir
	 *            需要更新的Crop
	 */
	boolean update(Crop instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Crop> query(String query, Map params, int begin, int max);
}
