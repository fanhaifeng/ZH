package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.Consult;
import com.lakecloud.foundation.domain.GoodsDCImport;

public interface IGoodsDCImportService {
	/**
	 * 保存一个GoodsDCImport，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsDCImport instance);
	
	/**
	 * 根据一个ID得到GoodsDCImport
	 * 
	 * @param id
	 * @return
	 */
	GoodsDCImport getObjById(Long id);
	
	/**
	 * 删除一个GoodsDCImport
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoodsDCImport
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoodsDCImport
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GoodsDCImport
	 * 
	 * @param id
	 *            需要更新的GoodsDCImport的id
	 * @param dir
	 *            需要更新的GoodsDCImport
	 */
	boolean update(GoodsDCImport instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsDCImport> query(String query, Map params, int begin, int max);
}
