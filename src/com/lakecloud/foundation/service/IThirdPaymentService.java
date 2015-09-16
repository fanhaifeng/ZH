package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.ThirdPayment;

public interface IThirdPaymentService {
	/**
	 * 保存一个ThirdPayment，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(ThirdPayment instance);

	/**
	 * 根据一个ID得到ThirdPayment
	 * 
	 * @param id
	 * @return
	 */
	ThirdPayment getObjById(Long id);

	/**
	 * 删除一个ThirdPayment
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除ThirdPayment
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到ThirdPayment
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个ThirdPayment
	 * 
	 * @param id
	 *            需要更新的ThirdPayment的id
	 * @param dir
	 *            需要更新的ThirdPayment
	 */
	boolean update(ThirdPayment instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<ThirdPayment> query(String query, Map params, int begin, int max);

	List<ThirdPayment> queryByCreateUser();

	List<ThirdPayment> queryByOrderId(String orderId);
}
