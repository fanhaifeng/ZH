package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.Charge;
import com.lakecloud.foundation.domain.OrderForm;

public interface IChargeService {
	
	/**
	 * 赊销支付
	 * @param payType
	 * @param order_id
	 * @param chargeNum
	 * @return
	 */
	public void weixin_order_pay(String payType, String order_id,
			String chargeNum);
	
	
	/**
	 * 赊销还款
	 * @param order_id
	 * @param needpay
	 * @return
	 */
	public OrderForm seller_confirm_refund(String order_id,String needpay);
	
	/**
	 * 保存一个Charge，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Charge instance);
	
	/**
	 * 根据一个ID得到Charge
	 * 
	 * @param id
	 * @return
	 */
	Charge getObjById(Long id);
	
	/**
	 * 删除一个Charge
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Charge
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Charge
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Charge
	 * 
	 * @param id
	 *            需要更新的Charge的id
	 * @param dir
	 *            需要更新的Charge
	 */
	boolean update(Charge instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Charge> query(String query, Map params, int begin, int max);
}
