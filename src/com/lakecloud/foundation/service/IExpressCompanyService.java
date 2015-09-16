﻿package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.ExpressCompany;

public interface IExpressCompanyService {
	/**
	 * 保存一个ExpressCompany，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(ExpressCompany instance);
	
	/**
	 * 根据一个ID得到ExpressCompany
	 * 
	 * @param id
	 * @return
	 */
	ExpressCompany getObjById(Long id);
	
	/**
	 * 删除一个ExpressCompany
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除ExpressCompany
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到ExpressCompany
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个ExpressCompany
	 * 
	 * @param id
	 *            需要更新的ExpressCompany的id
	 * @param dir
	 *            需要更新的ExpressCompany
	 */
	boolean update(ExpressCompany instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<ExpressCompany> query(String query, Map params, int begin, int max);
}
