package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.PromotionLevel;

public interface IPromotionLevelService {
	/**
	 * 保存一个PromotionLevel，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(PromotionLevel instance);

	/**
	 * 根据一个ID得到PromotionLevel
	 * 
	 * @param id
	 * @return
	 */
	PromotionLevel getObjById(Long id);

	/**
	 * 删除一个PromotionLevel
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除PromotionLevel
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到PromotionLevel
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个PromotionLevel
	 * 
	 * @param id
	 *            需要更新的PromotionLevel的id
	 * @param dir
	 *            需要更新的PromotionLevel
	 */
	boolean update(PromotionLevel instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<PromotionLevel> query(String query, Map params, int begin, int max);
}
