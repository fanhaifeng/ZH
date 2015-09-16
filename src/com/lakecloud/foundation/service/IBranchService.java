package com.lakecloud.foundation.service;

import java.util.List;
import java.util.Map;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.Branch;

public interface IBranchService {
	/**
	 * 
	 * @param audit
	 * @return
	 */
	boolean save(Branch branch);

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
	boolean update(Branch branch);

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
	Branch  getObjById(Long id);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Branch getObjByProperty(String propertyName, String value);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Branch> query(String query, Map params, int begin, int max);
	
	List getBranch(String query, Map params, int begin, int max);
}
