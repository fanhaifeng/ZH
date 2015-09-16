package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.XConf;

public interface IXConfService {
	/**
	 * 保存一个XConf，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(XConf instance);

	/**
	 * 根据一个ID得到XConf
	 * 
	 * @param id
	 * @return
	 */
	XConf getObjById(Long id);

	/**
	 * 删除一个XConf
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除XConf
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到XConf
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个XConf
	 * 
	 * @param id
	 *            需要更新的XConf的id
	 * @param dir
	 *            需要更新的XConf
	 */
	boolean update(XConf instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<XConf> query(String query, Map params, int begin, int max);

	XConf queryByXconfkey(String xconfkey);
}
