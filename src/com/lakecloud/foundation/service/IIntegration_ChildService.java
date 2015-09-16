package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.Integration_Child;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.XConf;

public interface IIntegration_ChildService {
	/**
	 * 保存一个Integration_Child，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Integration_Child instance);

	/**
	 * 根据一个ID得到Integration_Child
	 * 
	 * @param id
	 * @return
	 */
	Integration_Child getObjById(Long id);

	/**
	 * 删除一个Integration_Child
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除Integration_Child
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到Integration_Child
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个Integration_Child
	 * 
	 * @param id
	 *            需要更新的Integration_Child的id
	 * @param dir
	 *            需要更新的Integration_Child
	 */
	boolean update(Integration_Child instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Integration_Child> query(String query, Map params, int begin, int max);

	List<Integration_Child> queryByTypeStoreOrUser(int type, User user,
			Store store);

	boolean subtractByOrderForm(OrderForm of);

	boolean saveOrUpdateByXConfAndOrderForm(XConf xconf, OrderForm of);

	Integer count_integration_by_real_price(XConf xconf, OrderForm of);
}
