package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.Integration_Log;
import com.lakecloud.foundation.domain.OrderForm;

public interface IIntegration_LogService {
	/**
	 * 保存一个Integration_Log，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Integration_Log instance);

	/**
	 * 根据一个ID得到Integration_Log
	 * 
	 * @param id
	 * @return
	 */
	Integration_Log getObjById(Long id);

	/**
	 * 删除一个Integration_Log
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除Integration_Log
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到Integration_Log
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个Integration_Log
	 * 
	 * @param id
	 *            需要更新的Integration_Log的id
	 * @param dir
	 *            需要更新的Integration_Log
	 */
	boolean update(Integration_Log instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Integration_Log> query(String query, Map params, int begin, int max);

	/**
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Integration_Log getObjByProperty(String propertyName, Object value);

	boolean saveByIntegrationLogConstructor(Integration_Log integrationLog);

	/**
	 * 取消订单和退货时返还农豆值
	 * 
	 * @param OrderForm
	 */
	void return_Integration_Log_By_OrderForm(OrderForm OrderForm);
	// Integration_Log queryByOrderFormAndType(int type, OrderForm of);
}
