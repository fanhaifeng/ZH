package com.lakecloud.weixin.store.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.Address;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Charge;
import com.lakecloud.foundation.domain.CouponInfo;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Payment;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreCart;
import com.lakecloud.foundation.domain.ThirdBinding;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IAddressService;
import com.lakecloud.foundation.service.IAreaService;
import com.lakecloud.foundation.service.IChargeService;
import com.lakecloud.foundation.service.ICouponInfoService;
import com.lakecloud.foundation.service.IIntegration_ChildService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IStoreCartService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IThirdBindingService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.manage.admin.tools.PaymentTools;
import com.lakecloud.manage.seller.Tools.TransportTools;
import com.lakecloud.view.web.tools.GoodsViewTools;

/**
 * 
 * <p>
 * Title: WeixinStoreCartViewAction.java
 * </p>
 * 
 * <p>
 * Description:微信店铺购物控制器,包括购物车所有操作及订单相关操作
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net
 * </p>
 * 
 * @author hz
 * 
 * @date 2014-4-27
 * 
 * @version LakeCloud_C2C 1.3
 */
@Controller
public class WeixinStoreCartViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IStoreCartService storeCartService;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IChargeService chargeService;
	@Autowired
	private IThirdBindingService thirdBindingService;
	@Autowired
	private WeixinStorePayViewAction wsp;
	@Autowired
	private IIntegration_ChildService integrationChildService;
	@Autowired
	private IStoreService storeService;

	/**
	 * 根据商品规格加载商品的数量、价格
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param gsp
	 */
	@RequestMapping("/weixin/add_goods_cart.htm")
	public void weixin_add_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String count,
			String price, String gsp, String buy_type) {
		String ret = this.orderFormService.weixin_add_goods_cart(request,
				response, id, count, price, gsp, buy_type);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
	@RequestMapping("/weixin/remove_goods_cart.htm")
	public void weixin_remove_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String store_id) {
		String ret = this.orderFormService.weixin_remove_goods_cart(request,
				response, id, store_id);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 商品数量调整
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param store_id
	 */
	@RequestMapping("/weixin/goods_count_adjust.htm")
	public void weixin_goods_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String cart_id, String store_id,
			String count) {
		String ret = this.orderFormService.weixin_goods_count_adjust(request,
				response, cart_id, store_id, count);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			// System.out.println(Json.toJson(map, JsonFormat.compact()));
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 确认购物车商品
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "查看购物车", value = "/weixin/goods_cart1.htm*", rtype = "buyer", rname = "微信购物流程1", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/goods_cart1.htm")
	public ModelAndView weixin_goods_cart1(HttpServletRequest request,
			HttpServletResponse response) {
		return this.orderFormService.weixin_goods_cart1(request, response);
	}

	/**
	 * s 购物确认,填写用户地址，配送方式，支付方式等
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "确认购物车填写地址", value = "/weixin/goods_cart2.htm*", rtype = "buyer", rname = "微信购物流程2", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/goods_cart2.htm")
	public ModelAndView weixin_goods_cart2(HttpServletRequest request,
			HttpServletResponse response, String store_id) {
		ModelAndView mv = new JModelAndView("weixin/goods_cart2.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		List<StoreCart> cart = this.orderFormService.cart_calc(request);
		StoreCart sc = null;
		if (cart != null) {
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(CommUtil.null2Long(store_id))) {
					sc = sc1;
					break;
				}
			}
		}
		if (sc != null) {
			Map params = new HashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("status", true);
			List<Address> addrs = this.addressService
					.query(
							"select obj from Address obj where obj.user.id=:user_id and obj.deleteStatus!=:status order by obj.addTime desc",
							params, -1, -1);
			mv.addObject("addrs", addrs);
			if (store_id == null || store_id.equals("")) {
				store_id = sc.getStore().getId().toString();
			}
			String cart_session = CommUtil.randomString(32);
			request.getSession(false)
					.setAttribute("cart_session", cart_session);
			params.clear();
			params.put("coupon_order_amount", sc.getTotal_price());
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("coupon_begin_time", new Date());
			params.put("coupon_end_time", new Date());
			params.put("status", 0);
			List<CouponInfo> couponinfos = this.couponInfoService
					.query(
							"select obj from CouponInfo obj where obj.coupon.coupon_order_amount<=:coupon_order_amount and obj.status=:status and "
									+ "obj.user.id=:user_id and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
							params, -1, -1);
			mv.addObject("couponinfos", couponinfos);
			mv.addObject("sc", sc);
			mv.addObject("cart_session", cart_session);
			mv.addObject("store_id", store_id);
			mv.addObject("transportTools", transportTools);
			mv.addObject("goodsViewTools", goodsViewTools);
			// 查询当前购物车内是否有实体商品,配送选项。
			// 中化不考虑运费需要去掉配送方式
			/*
			 * boolean goods_delivery = false; List<GoodsCart> goodCarts =
			 * sc.getGcs(); for (GoodsCart gc : goodCarts) { if
			 * (gc.getGoods().getGoods_choice_type() == 0) { goods_delivery =
			 * true; break; } } mv.addObject("goods_delivery", goods_delivery);
			 */
		} else {
			mv = new JModelAndView("weixin/error.html", configService
					.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "购物车信息为空");
			String store_id2 = CommUtil.null2String(request.getSession(false)
					.getAttribute("store_id"));
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/index.htm?store_id=" + store_id2);
		}
		return mv;
	}

	@SecurityMapping(title = "完成订单提交进入支付", value = "/weixin/goods_cart3.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/goods_cart3.htm")
	public ModelAndView weixin_goods_cart3(HttpServletRequest request,
			HttpServletResponse response, String cart_session, String store_id,
			String addr_id, String coupon_id, String chargeNum)
			throws Exception {
		String type = "weixin";
		ModelAndView mv = this.orderFormService.goods_cart3(request, response,
				cart_session, store_id, addr_id, coupon_id, chargeNum, type);
		return mv;
	}

	@SecurityMapping(title = "订单支付详情", value = "/weixin/order_pay_view.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/order_pay_view.htm")
	public ModelAndView weixin_order_pay_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("weixin/order_pay.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		String store_id = CommUtil.null2String(request.getSession(false)
				.getAttribute("store_id"));
		Map payment_map = new HashMap();
		List<Payment> store_Payments = new ArrayList<Payment>();
		if (of.getOrder_status() == 10) {
			thirdBinding(mv);
			mv.addObject("of", of);
			mv.addObject("paymentTools", this.paymentTools);
			mv.addObject("url", CommUtil.getURL(request));
			Store store = of.getStore();
			User user = of.getUser();
			Long storeId = store.getId();
			Long userId = user.getId();
			String query = "select obj from Charge obj where obj.store.id=:store_id and obj.user.id=:user_id";
			Map<String, Long> chargeMap = new HashMap<String, Long>();
			chargeMap.put("store_id", storeId);
			chargeMap.put("user_id", userId);
			List<Charge> chargeList = this.chargeService.query(query,
					chargeMap, -1, -1);
			BigDecimal ret = BigDecimal.ZERO;
			if (chargeList != null && chargeList.size() == 1) {
				Charge charge = chargeList.get(0);
				BigDecimal b1 = charge.getPaymentNum();
				BigDecimal b2 = charge.getUsedPayNum();
				ret = b1.subtract(b2);
				mv.addObject("ret", ret.add(of.getCharge_Num()));
			} else {
				mv.addObject("ret", ret);
			}
			mv = orderFormService.setIntegrationPlatformAndIntegrationStore(of,
					mv);
		} else if (of.getOrder_status() < 10) {
			mv = new JModelAndView("weixin/error.html", configService
					.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "该订单已经取消！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/index.htm?store_id=" + store_id);
		} else {
			mv = new JModelAndView("weixin/error.html", configService
					.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "该订单已经付款！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/index.htm?store_id=" + store_id);
		}
		return mv;
	}

	/**
	 * 支付订单 赊销支付和第三方支付
	 * 
	 * @param request
	 * @param response
	 * @param payType
	 * @param order_id
	 *            订单id
	 * @param chargeNum
	 *            使用的赊销金额
	 * @return
	 */
	@SecurityMapping(title = "订单支付", value = "/weixin/order_pay.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/order_pay.htm")
	public ModelAndView weixin_order_pay(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String chargeNum, String integrationPlatform,
			String integrationStore) {
		// TODO
		return this.orderFormService.weixin_order_pay(request, response,
				payType, order_id, chargeNum, integrationPlatform,
				integrationStore);
	}

	@SecurityMapping(title = "订单线下支付", value = "/weixin/order_pay_outline.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/order_pay_outline.htm")
	public ModelAndView weixin_order_pay_outline(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception {
		return this.orderFormService.weixin_order_pay_outline(request,
				response, payType, order_id, pay_msg, pay_session);
	}

	@SecurityMapping(title = "订单预付款支付", value = "/weixin/order_pay_balance.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/order_pay_balance.htm")
	public ModelAndView weixin_order_pay_balance(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg) throws Exception {
		return this.orderFormService.weixin_order_pay_balance(request,
				response, payType, order_id, pay_msg);
	}

	@SecurityMapping(title = "订单货到付款", value = "/weixin/order_pay_payafter.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/order_pay_payafter.htm")
	public ModelAndView weixin_order_pay_payafter(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception {
		return this.orderFormService.weixin_order_pay_payafter(request,
				response, payType, order_id, pay_msg, pay_session);
	}

	@SecurityMapping(title = "订单支付结果", value = "/weixin/order_finish.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/order_finish.htm")
	public ModelAndView weixin_order_finish(HttpServletRequest request,
			HttpServletResponse response, String order_id) {
		ModelAndView mv = new JModelAndView("weixin/wap_order_finish.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		mv.addObject("obj", obj);
		return mv;
	}

	@SecurityMapping(title = "地址新增", value = "/weixin/cart_address.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/cart_address.htm")
	public ModelAndView weixin_cart_address(HttpServletRequest request,
			HttpServletResponse response, String id, String store_id) {
		ModelAndView mv = new JModelAndView("weixin/cart_address.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		mv.addObject("store_id", store_id);
		return mv;
	}

	@SecurityMapping(title = "购物车中收货地址保存", value = "/weixin/cart_address_save.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/cart_address_save.htm")
	public String cart_address_save(HttpServletRequest request,
			HttpServletResponse response, String id, String area_id,
			String store_id) {
		WebForm wf = new WebForm();
		Address address = null;
		if (id.equals("")) {
			address = wf.toPo(request, Address.class);
			address.setAddTime(new Date());
		} else {
			Address obj = this.addressService.getObjById(Long.parseLong(id));
			address = (Address) wf.toPo(request, obj);
		}
		address.setUser(SecurityUserHolder.getCurrentUser());
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		address.setArea(area);
		if (id.equals("")) {
			this.addressService.save(address);
		} else
			this.addressService.update(address);
		return "redirect:/weixin/goods_cart2.htm?store_id=" + store_id;
	}

	@SecurityMapping(title = "地址切换", value = "/weixin/order_address.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/order_address.htm")
	public void weixin_order_address(HttpServletRequest request,
			HttpServletResponse response, String addr_id, String store_id) {
		List<StoreCart> cart = (List<StoreCart>) request.getSession(false)
				.getAttribute("cart");
		StoreCart sc = null;
		if (cart != null) {
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(CommUtil.null2Long(store_id))) {
					sc = sc1;
					break;
				}
			}
		}
		Address addr = this.addressService.getObjById(CommUtil
				.null2Long(addr_id));
		List<SysMap> sms = this.transportTools.query_cart_trans(sc, CommUtil
				.null2String(addr.getArea().getId()));
		// System.out.println(Json.toJson(sms, JsonFormat.compact()));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(sms, JsonFormat.compact()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/weixin/check_goods.htm")
	public void check_goods(HttpServletRequest request,
			HttpServletResponse response, String sc_id)
			throws ClassNotFoundException {
		StoreCart sc = this.storeCartService.getObjById(Long.valueOf(sc_id));
		String val = "";
		if (sc != null) {
			if (null != sc.getGcs()) {
				OrderForm orderForm = new OrderForm();
				List<GoodsCart> goodsCartList = sc.getGcs();
				orderForm.setGcs(goodsCartList);
				if (wsp.check_goods_inventory(orderForm)) {
					for (GoodsCart goodsCart : goodsCartList) {
						Goods goods = goodsCart.getGoods();
						if (goods.getGoods_status() != 0) {
							val = goods.getGoods_name();
							break;
						}
					}
				} else {// 库存不够
					val = "check_goods_inventory";
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void thirdBinding(ModelAndView mv) {
		List<ThirdBinding> list = this.thirdBindingService.queryByCreateUser();
		if (null != list && list.size() > 0) {
			mv.addObject("thirdBinding", 1);
		} else {
			mv.addObject("thirdBinding", 0);
		}
	}
}
