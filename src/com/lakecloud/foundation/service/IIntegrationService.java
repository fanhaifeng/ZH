package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.Integration;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.User;

public interface IIntegrationService {
	/**
	 * 保存一个Integration，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Integration instance);

	/**
	 * 根据一个ID得到Integration
	 * 
	 * @param id
	 * @return
	 */
	Integration getObjById(Long id);

	/**
	 * 删除一个Integration
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除Integration
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到Integration
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个Integration
	 * 
	 * @param id
	 *            需要更新的Integration的id
	 * @param dir
	 *            需要更新的Integration
	 */
	boolean update(Integration instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Integration> query(String query, Map params, int begin, int max);

	List<Integration> queryByUser(User user);

	boolean updateByOrderForm(OrderForm of, String money_overdue,
			Integration integration);

	/**
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Integration getObjByProperty(String propertyName, Object value);
}
