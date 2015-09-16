﻿package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.StorePartner;

public interface IStorePartnerService {
	/**
	 * 保存一个StorePartner，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(StorePartner instance);
	
	/**
	 * 根据一个ID得到StorePartner
	 * 
	 * @param id
	 * @return
	 */
	StorePartner getObjById(Long id);
	
	/**
	 * 删除一个StorePartner
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除StorePartner
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到StorePartner
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个StorePartner
	 * 
	 * @param id
	 *            需要更新的StorePartner的id
	 * @param dir
	 *            需要更新的StorePartner
	 */
	boolean update(StorePartner instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<StorePartner> query(String query, Map params, int begin, int max);
}
