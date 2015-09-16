package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.Refund;

public interface IRefundService {
	/**
	 * 保存一个Refund，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Refund instance);
	
	/**
	 * 根据一个ID得到Refund
	 * 
	 * @param id
	 * @return
	 */
	Refund getObjById(Long id);
	
	/**
	 * 删除一个Refund
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Refund
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Refund
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Refund
	 * 
	 * @param id
	 *            需要更新的Refund的id
	 * @param dir
	 *            需要更新的Refund
	 */
	boolean update(Refund instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Refund> query(String query, Map params, int begin, int max);
}
