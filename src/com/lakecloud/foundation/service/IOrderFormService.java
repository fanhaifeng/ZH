package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.Charge;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.StoreCart;

public interface IOrderFormService {
	/**
	 * 保存一个OrderForm，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(OrderForm instance);

	/**
	 * 根据一个ID得到OrderForm
	 * 
	 * @param id
	 * @return
	 */
	OrderForm getObjById(Long id);

	/**
	 * 删除一个OrderForm
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除OrderForm
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到OrderForm
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个OrderForm
	 * 
	 * @param id
	 *            需要更新的OrderForm的id
	 * @param dir
	 *            需要更新的OrderForm
	 */
	boolean update(OrderForm instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<OrderForm> query(String query, Map params, int begin, int max);

	OrderForm queryByOrderId(String orderId);

	public void add_order(OrderForm of, String addr_id, String store_id,
			String coupon_id, String chargeNum, List<StoreCart> cart);

	/**
	 * 完成订单提交进入支付
	 */
	ModelAndView goods_cart3(HttpServletRequest request,
			HttpServletResponse response, String cart_session, String store_id,
			String addr_id, String coupon_id, String chargeNum, String type);

	/**
	 * 订单支付
	 */
	ModelAndView weixin_order_pay(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String chargeNum, String integrationPlatform,
			String integrationStore);

	/**
	 * 订单线下支付
	 */
	ModelAndView weixin_order_pay_outline(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception;

	/**
	 * pc端订单线下支付
	 */
	ModelAndView order_pay_outline(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception;

	/**
	 * 根据商品规格加载商品的数量、价格
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param gsp
	 */
	String weixin_add_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String count,
			String price, String gsp, String buy_type);

	/**
	 * 计算并合并购车信息
	 * 
	 * @param request
	 * @return
	 */
	List<StoreCart> cart_calc(HttpServletRequest request);

	/**
	 * 从购物车移除商品
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param count
	 * @param price
	 * @param spec_info
	 */
	String weixin_remove_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String store_id);

	/**
	 * 商品数量调整
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param store_id
	 */
	String weixin_goods_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String cart_id, String store_id,
			String count);

	/**
	 * 确认购物车商品
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	ModelAndView weixin_goods_cart1(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * PC确认购物车商品
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	ModelAndView goods_cart1(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * 订单预付款支付
	 * 
	 * @param request
	 * @param response
	 * @param payType
	 * @param order_id
	 * @param pay_msg
	 * @return
	 * @throws Exception
	 */
	ModelAndView weixin_order_pay_balance(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg) throws Exception;

	/**
	 * 微信订单货到付款
	 * 
	 * @param request
	 * @param response
	 * @param payType
	 * @param order_id
	 * @param pay_msg
	 * @param pay_session
	 * @return
	 * @throws Exception
	 */
	ModelAndView weixin_order_pay_payafter(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception;

	/**
	 * PC订单货到付款
	 * 
	 * @param request
	 * @param response
	 * @param payType
	 * @param order_id
	 * @param pay_msg
	 * @param pay_session
	 * @return
	 * @throws Exception
	 */
	ModelAndView order_pay_payafter(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception;

	/**
	 * 卖家调整订单费用保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param goods_amount
	 * @param totalPrice
	 * @return
	 * @throws Exception
	 */
	Object order_fee_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String goods_amount, String totalPrice) throws Exception;

	/**
	 * 买家中心发送短信
	 * 
	 * @param request
	 * @param obj
	 * @param string
	 * @throws Exception
	 */
	void send_email(HttpServletRequest request, OrderForm obj, String string)
			throws Exception;

	/**
	 * 线下付款确认保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param state_info
	 * @return
	 * @throws Exception
	 */
	String seller_order_outline_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info) throws Exception;

	/**
	 * 卖家取消订单保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param state_info
	 * @param other_state_info
	 * @return
	 * @throws Exception
	 */
	Object order_cancel_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info, String oper)
			throws Exception;

	/**
	 * 确认买家退货
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	ModelAndView seller_order_return_confirm(HttpServletRequest request,
			HttpServletResponse response, String id);

	/**
	 * 卖家确认发货保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param state_info
	 * @return
	 * @throws Exception
	 */
	Object order_shipping_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info) throws Exception;

	/**
	 * 卖家评价保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param evaluate_info
	 * @param evaluate_seller_val
	 * @return
	 */
	ModelAndView order_evaluate_save(HttpServletRequest request,
			HttpServletResponse response, String id, String evaluate_info,
			String evaluate_seller_val);

	/**
	 * 买家订单取消
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param state_info
	 * @param other_state_info
	 * @return
	 * @throws Exception
	 */
	Object weixin_order_cancel_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info) throws Exception;

	/**
	 * 买家确认收货，确认收货后，订单状态值改变为40，如果是预存款支付，买家冻结预存款中同等订单账户金额自动转入商家预存款，如果开启预存款分润，
	 * 则按照分润比例，买家预存款分别进入商家及平台商的账户
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 * @throws Exception
	 */
	String weixin_order_cofirm_save(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	/**
	 * 买家评价保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @throws Exception
	 */
	ModelAndView weixin_order_evaluate_save(HttpServletRequest request,
			HttpServletResponse response, String id) throws Exception;

	ModelAndView order_pay(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String chargeNum, String integrationPlatform,
			String integrationStore);

	/**
	 * pc端完成订单进入支付页面
	 * 
	 * @param request
	 * @param response
	 * @param cart_session
	 * @param store_id
	 * @param addr_id
	 * @param coupon_id
	 * @param chargeNum
	 * @return
	 */
	ModelAndView pc_goods_cart3(HttpServletRequest request,
			HttpServletResponse response, String cart_session, String store_id,
			String addr_id, String coupon_id, String chargeNum);

	/**
	 * 回滚库存赊销信息
	 * 
	 * @param order
	 */
	void release_goods_inventory(OrderForm order);

	void thirdBinding(ModelAndView mv);

	ModelAndView setIntegrationPlatformAndIntegrationStore(OrderForm of,
			ModelAndView mv);

	String order_cofirm_save(HttpServletRequest request,
			HttpServletResponse response, String id,String currentPage) throws Exception;
}
