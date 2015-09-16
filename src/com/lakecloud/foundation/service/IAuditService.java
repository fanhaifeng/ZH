package com.lakecloud.foundation.service;

import java.util.List;
import java.util.Map;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.Audit;

public interface IAuditService {
	/**
	 * 
	 * @param audit
	 * @return
	 */
	boolean save(Audit audit);

	/**
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 
	 * @param Audit
	 * @return
	 */
	boolean update(Audit audit);

	/**
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 
	 * @param id
	 * @return
	 */
	Audit  getObjById(Long id);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Audit getObjByProperty(String propertyName, String value);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Audit> query(String query, Map params, int begin, int max);
}
