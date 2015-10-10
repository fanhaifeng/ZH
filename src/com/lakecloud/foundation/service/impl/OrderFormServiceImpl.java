package com.lakecloud.foundation.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import payment.tools.util.GUID;

import com.lakecloud.core.dao.IGenericDAO;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.GenericPageList;
import com.lakecloud.core.query.PageObject;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.SendMessageUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.Address;
import com.lakecloud.foundation.domain.Charge;
import com.lakecloud.foundation.domain.CouponInfo;
import com.lakecloud.foundation.domain.Evaluate;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.GoodsReturn;
import com.lakecloud.foundation.domain.GoodsReturnItem;
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.GroupGoods;
import com.lakecloud.foundation.domain.IntegralLog;
import com.lakecloud.foundation.domain.Integration;
import com.lakecloud.foundation.domain.Integration_Child;
import com.lakecloud.foundation.domain.Integration_Log;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.OrderFormLog;
import com.lakecloud.foundation.domain.Payment;
import com.lakecloud.foundation.domain.PredepositLog;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreCart;
import com.lakecloud.foundation.domain.StorePoint;
import com.lakecloud.foundation.domain.Template;
import com.lakecloud.foundation.domain.ThirdBinding;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.XConf;
import com.lakecloud.foundation.service.IAddressService;
import com.lakecloud.foundation.service.IChargeService;
import com.lakecloud.foundation.service.ICouponInfoService;
import com.lakecloud.foundation.service.IEvaluateService;
import com.lakecloud.foundation.service.IGoodsCartService;
import com.lakecloud.foundation.service.IGoodsReturnService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGoodsSpecPropertyService;
import com.lakecloud.foundation.service.IGroupGoodsService;
import com.lakecloud.foundation.service.IIntegralLogService;
import com.lakecloud.foundation.service.IIntegrationService;
import com.lakecloud.foundation.service.IIntegration_ChildService;
import com.lakecloud.foundation.service.IIntegration_LogService;
import com.lakecloud.foundation.service.IOrderFormLogService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IPaymentService;
import com.lakecloud.foundation.service.IPredepositLogService;
import com.lakecloud.foundation.service.IStoreCartService;
import com.lakecloud.foundation.service.IStorePointService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.ITemplateService;
import com.lakecloud.foundation.service.IThirdBindingService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.foundation.service.IXConfService;
import com.lakecloud.lucene.LuceneUtil;
import com.lakecloud.manage.admin.tools.MsgTools;
import com.lakecloud.manage.admin.tools.PaymentTools;
import com.lakecloud.pay.tools.PayTools;
import com.lakecloud.view.web.tools.GoodsViewTools;
import com.lakecloud.weixin.store.view.action.WeixinStorePayViewAction;
import com.lakecloud.weixin.utils.ConstantUtils;

@Service
@Transactional
public class OrderFormServiceImpl implements IOrderFormService {
	@Resource(name = "orderFormDAO")
	private IGenericDAO<OrderForm> orderFormDao;
	@Resource(name = "addressDAO")
	private IGenericDAO<Address> addressDao;
	@Resource(name = "storeDAO")
	private IGenericDAO<Store> storeDao;
	@Resource(name = "couponInfoDAO")
	private IGenericDAO<CouponInfo> couponInfoDao;
	@Resource(name = "chargeDAO")
	private IGenericDAO<Charge> chargeDao;
	@Resource(name = "goodsCartDAO")
	private IGenericDAO<GoodsCart> goodsCartDao;
	@Resource(name = "storeCartDAO")
	private IGenericDAO<StoreCart> storeCartDao;
	@Resource(name = "orderFormLogDAO")
	private IGenericDAO<OrderFormLog> orderFormLogDao;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IStoreCartService storeCartService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private PayTools payTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IChargeService chargeService;
	@Autowired
	private WeixinStorePayViewAction wsp;
	@Autowired
	private IThirdBindingService thirdBindingService;
	@Autowired
	private IXConfService xconfService;
	@Autowired
	private IGoodsReturnService goodsReturnService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IStorePointService storePointService;
	@Autowired
	private IIntegrationService integrationService;
	@Autowired
	private IIntegration_ChildService integrationChildService;
	@Autowired
	private IIntegration_LogService integrationLogService;

	public boolean save(OrderForm orderForm) {
		/**
		 * init other field here
		 */
		try {
			this.orderFormDao.save(orderForm);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public OrderForm getObjById(Long id) {
		OrderForm orderForm = this.orderFormDao.get(id);
		if (orderForm != null) {
			return orderForm;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.orderFormDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> orderFormIds) {
		for (Serializable id : orderFormIds) {
			delete((Long) id);
		}
		return true;
	}

	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(OrderForm.class, query,
				params, this.orderFormDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
						.getCurrentPage(), pageObj.getPageSize() == null ? 0
						: pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}

	public boolean update(OrderForm orderForm) {
		try {
			this.orderFormDao.update(orderForm);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<OrderForm> query(String query, Map params, int begin, int max) {
		return this.orderFormDao.query(query, params, begin, max);
	}

	public OrderForm queryByOrderId(String orderId) {
		Map params = new HashMap();
		params.put("order_id", orderId);
		List<OrderForm> list = query(
				"select obj from OrderForm obj where obj.order_id=:order_id",
				params, -1, -1);
		OrderForm orderForm;
		if (null != list && list.size() > 0) {
			orderForm = list.get(0);
		} else {
			orderForm = null;
			System.out.println(ConstantUtils
					._getLogEnter(" OrderFormServiceImpl"));
			System.out.println("not found OrderForm queryByOrderId");
			System.out.println(ConstantUtils
					._getLogExit(" OrderFormServiceImpl"));
		}
		return orderForm;
	}

	public void add_order(OrderForm of, String addr_id, String store_id,
			String coupon_id, String chargeNum, List<StoreCart> cart) {
		of.setAddTime(new Date());
		of.setOrder_id(SecurityUserHolder.getCurrentUser().getId()
				+ CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
		Address addr = this.addressDao.get(CommUtil.null2Long(addr_id));
		of.setAddr(addr);
		of.setAlreadyPay(BigDecimal.ZERO);
		of.setCharge_Num(BigDecimal.ZERO);
		of.setOrder_status(10);
		of.setUser(SecurityUserHolder.getCurrentUser());
		of.setStore(this.storeDao.get(CommUtil.null2Long(store_id)));
		of.setPayTime(new Date());
		Payment payment = this.paymentService
				.getObjByProperty("mark", "charge");
		of.setPayment(payment);
		// 处理订单总金额
		Map param = new HashMap();
		param.put("userId", of.getUser().getId());
		param.put("storeId", CommUtil.null2Long(store_id));
		param.put("status", 0);
		List<StoreCart> sclist = this.storeDao
				.query(
						"select obj from StoreCart obj where obj.user.id=:userId and obj.sc_status=:status and obj.store.id=:storeId",
						param, -1, -1);
		StoreCart storeCart = sclist.get(0) == null ? new StoreCart() : sclist
				.get(0);
		of.setGoods_amount(storeCart.getTotal_price());
		of.setTotalPrice(BigDecimal.valueOf(
				CommUtil.add(of.getGoods_amount(), of.getShip_price()))
				.setScale(2, BigDecimal.ROUND_HALF_UP));
		// 添加商品详情
		// List<GoodsCart> goods_cart = new ArrayList<GoodsCart>(
		// storeCart.getGcs());
		// of.setGcs(goods_cart);
		if (!CommUtil.null2String(coupon_id).equals("")) {
			CouponInfo ci = this.couponInfoDao.get(CommUtil
					.null2Long(coupon_id));
			ci.setStatus(1);
			this.couponInfoDao.update(ci);
			of.setCi(ci);
			of.setTotalPrice(BigDecimal.valueOf(CommUtil.subtract(of
					.getTotalPrice(), ci.getCoupon().getCoupon_amount())));
		}
		of.setOrder_type("weixin");// 设置为手机网页订单
		of.setIntegrationPlatform(0);
		of.setIntegrationStore(0);
		of.setIntegration_price(BigDecimal.ZERO);
		this.orderFormDao.save(of);
		for (StoreCart sc : cart) {
			if (sc.getStore().getId().toString().equals(store_id)) {
				for (GoodsCart gc : sc.getGcs()) {
					gc.setOf(of);
					this.goodsCartDao.update(gc);
				}
				// 设置订单对应购物车的映射(不加会导致库存无法扣减)
				of.setGcs(new ArrayList<GoodsCart>(sc.getGcs()));
				sc.setCart_session_id(null);
				sc.setUser(SecurityUserHolder.getCurrentUser());
				sc.setSc_status(1);
				this.storeCartDao.update(sc);// 更新该店铺购物车的状态为1，表示该店铺购物车已经完成订单提交，不需要再显示在前台用户购物车信息中
				break;
			}
		}
		OrderFormLog ofl = new OrderFormLog();
		ofl.setAddTime(new Date());
		ofl.setOf(of);
		ofl.setLog_info("提交订单");
		ofl.setLog_user(SecurityUserHolder.getCurrentUser());
		this.orderFormLogDao.save(ofl);
	}

	private void update_charge_orderForm(OrderForm of, Charge charge,
			BigDecimal chargeNumDec, int order_status) {
		charge.setUsedPayNum(charge.getUsedPayNum().add(chargeNumDec));
		of.setCharge_Num(chargeNumDec);
		of.setCharge_Day(charge.getPaymentDays());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(of.getAddTime());
		calendar.add(Calendar.DAY_OF_MONTH, charge.getPaymentDays());
		of.setExpireTime(calendar.getTime());
		if (ConstantUtils._ORDERFORM_ORDER_STATUS[3] == order_status) {
			of.setOrder_status(ConstantUtils._ORDERFORM_ORDER_STATUS[3]);
		}
		this.chargeDao.update(charge);
		this.orderFormDao.update(of);
	}

	@Override
	public ModelAndView goods_cart3(HttpServletRequest request,
			HttpServletResponse response, String cart_session, String store_id,
			String addr_id, String coupon_id, String chargeNum, String type) {
		String prefix = "";
		if (type.equals("weixin"))
			prefix = "weixin/";
		ModelAndView mv = new JModelAndView(prefix + "goods_cart3.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		Map payment_map = new HashMap();
		List<Payment> store_Payments = new ArrayList<Payment>();
		if (this.configService.getSysConfig().getConfig_payment_type() == 1) {
			payment_map.put("install", true);
			payment_map.put("type", "admin");
			store_Payments = this.paymentService
					.query(
							"select obj from Payment obj where obj.install=:install and obj.type=:type",
							payment_map, -1, -1);
		} else {
			payment_map.put("store_id", CommUtil.null2Long(store_id));
			payment_map.put("install", true);
			store_Payments = this.paymentService
					.query(
							"select obj from Payment obj where obj.store.id=:store_id and obj.install=:install",
							payment_map, -1, -1);
		}
		if (store_Payments.size() > 0) {
			String cart_session1 = (String) request.getSession(false)
					.getAttribute("cart_session");
			List<StoreCart> cart = this.cart_calc(request);
			if (cart != null) {
				if (CommUtil.null2String(cart_session1).equals(cart_session)) {// 禁止重复提交订单信息
					thirdBinding(mv);
					request.getSession(false).removeAttribute("cart_session");// 删除订单提交唯一标示，用户不能进行第二次订单提交
					WebForm wf = new WebForm();
					OrderForm of = wf.toPo(request, OrderForm.class);
					Long storeId = Long.parseLong(store_id);
					Long userId = SecurityUserHolder.getCurrentUser().getId();
					String query = "select obj from Charge obj where store.id=:store_id and user.id=:user_id";
					Map<String, Long> chargeMap = new HashMap<String, Long>();
					chargeMap.put("store_id", storeId);
					chargeMap.put("user_id", userId);
					List<Charge> chargeList = this.chargeService.query(query,
							chargeMap, -1, -1);
					double ret = 0.0;
					if (chargeList != null && chargeList.size() == 1) {
						Charge charge = chargeList.get(0);
						BigDecimal b1 = charge.getPaymentNum();
						BigDecimal b2 = charge.getUsedPayNum();
						ret = b1.subtract(b2).doubleValue();
					}
					mv.addObject("ret", String.valueOf(ret));
					Cookie[] cookies = request.getCookies();
					if (cookies != null) {
						for (Cookie cookie : cookies) {
							if (cookie.getName().equals("cart_session_id")) {
								cookie.setDomain(CommUtil
										.generic_domain(request));
								cookie.setValue("");
								cookie.setMaxAge(0);
								response.addCookie(cookie);
							}
						}
					}
					add_order(of, addr_id, store_id, coupon_id, chargeNum, cart);
					if (wsp.check_goods_inventory(of)) {
						wsp.update_goods_inventory(of);
					} else {
						// 库存不够
						mv = new JModelAndView(prefix + "error.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "库存不足无法提交表单!");
						String store_id2 = CommUtil.null2String(request
								.getSession(false).getAttribute("store_id"));
						mv.addObject("url", CommUtil.getURL(request) + prefix
								+ "/goods_cart1.htm");
						return mv;
					}
					mv.addObject("of", of);
					mv.addObject("paymentTools", paymentTools);
					mv = setIntegrationPlatformAndIntegrationStore(of, mv);
				} else {
					mv = new JModelAndView(prefix + "error.html", configService
							.getSysConfig(), this.userConfigService
							.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "订单已经失效");
					String store_id2 = CommUtil.null2String(request.getSession(
							false).getAttribute("store_id"));
					mv.addObject("url", CommUtil.getURL(request) + prefix
							+ "/buyer/order.htm");
				}
			} else {
				mv = new JModelAndView(prefix + "error.html", configService
						.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单信息错误");
				String store_id2 = CommUtil.null2String(request.getSession(
						false).getAttribute("store_id"));
				mv.addObject("url", CommUtil.getURL(request) + prefix
						+ "/buyer/order.htm");
			}
		} else {
			mv = new JModelAndView(prefix + "error.html", configService
					.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "没有开通支付方式，不能付款");
			String store_id2 = CommUtil.null2String(request.getSession(false)
					.getAttribute("store_id"));
			mv.addObject("url", CommUtil.getURL(request) + prefix
					+ "/buyer/order.htm");
		}
		return mv;
	}

	/*
	 * @Override public ModelAndView goods_cart3(HttpServletRequest request,
	 * HttpServletResponse response, String cart_session, String store_id,
	 * String addr_id, String coupon_id, String chargeNum) { ModelAndView mv =
	 * new JModelAndView("goods_cart3.html", configService.getSysConfig(),
	 * this.userConfigService.getUserConfig(), 1, request, response); Map
	 * payment_map = new HashMap(); List<Payment> store_Payments = new
	 * ArrayList<Payment>(); if
	 * (this.configService.getSysConfig().getConfig_payment_type() == 1) {
	 * payment_map.put("install", true); payment_map.put("type", "admin");
	 * store_Payments = this.paymentService.query(
	 * "select obj from Payment obj where obj.install=:install and obj.type=:type"
	 * , payment_map, -1, -1); } else { payment_map.put("store_id",
	 * CommUtil.null2Long(store_id)); payment_map.put("install", true);
	 * store_Payments = this.paymentService.query(
	 * "select obj from Payment obj where obj.store.id=:store_id and obj.install=:install"
	 * , payment_map, -1, -1); } if (store_Payments.size() > 0) { String
	 * cart_session1 = (String) request.getSession(false)
	 * .getAttribute("cart_session"); List<StoreCart> cart =
	 * this.cart_calc(request); if (cart != null) { if
	 * (CommUtil.null2String(cart_session1).equals(cart_session)) {// 禁止重复提交订单信息
	 * thirdBinding(mv);
	 * request.getSession(false).removeAttribute("cart_session");//
	 * 删除订单提交唯一标示，用户不能进行第二次订单提交 WebForm wf = new WebForm(); OrderForm of =
	 * wf.toPo(request, OrderForm.class); Long storeId =
	 * Long.parseLong(store_id); Long userId =
	 * SecurityUserHolder.getCurrentUser().getId(); String query =
	 * "select obj from Charge obj where store.id=:store_id and user.id=:user_id"
	 * ; Map<String, Long> chargeMap = new HashMap<String, Long>();
	 * chargeMap.put("store_id", storeId); chargeMap.put("user_id", userId);
	 * List<Charge> chargeList = this.chargeService.query(query, chargeMap, -1,
	 * -1); double ret = 0.0; if (chargeList != null && chargeList.size() == 1)
	 * { Charge charge = chargeList.get(0); BigDecimal b1 =
	 * charge.getPaymentNum(); BigDecimal b2 = charge.getUsedPayNum(); ret =
	 * b1.subtract(b2).doubleValue(); } mv.addObject("ret",String.valueOf(ret));
	 * Cookie[] cookies = request.getCookies(); if (cookies != null) { for
	 * (Cookie cookie : cookies) { if
	 * (cookie.getName().equals("cart_session_id")) { cookie.setDomain(CommUtil
	 * .generic_domain(request)); cookie.setValue(""); cookie.setMaxAge(0);
	 * response.addCookie(cookie); } } } add_order(of, addr_id, store_id,
	 * coupon_id, chargeNum, cart); if (wsp.check_goods_inventory(of)) {
	 * wsp.update_goods_inventory(of); } else { // 库存不够 mv = new
	 * JModelAndView("error.html", configService.getSysConfig(),
	 * this.userConfigService.getUserConfig(), 1, request, response);
	 * mv.addObject("op_title", "库存不足无法提交表单!"); String store_id2 =
	 * CommUtil.null2String(request
	 * .getSession(false).getAttribute("store_id")); mv.addObject("url",
	 * CommUtil.getURL(request) + "/goods_cart1.htm"); return mv; }
	 * mv.addObject("of", of); mv.addObject("paymentTools", paymentTools); }
	 * else { mv = new JModelAndView("error.html", configService.getSysConfig(),
	 * this.userConfigService.getUserConfig(), 1, request, response);
	 * mv.addObject("op_title", "订单已经失效"); String store_id2 =
	 * CommUtil.null2String(request.getSession(
	 * false).getAttribute("store_id")); mv.addObject("url",
	 * CommUtil.getURL(request) + "/buyer/order.htm"); } } else { mv = new
	 * JModelAndView("error.html", configService.getSysConfig(),
	 * this.userConfigService.getUserConfig(), 1, request, response);
	 * mv.addObject("op_title", "订单信息错误"); String store_id2 =
	 * CommUtil.null2String(request.getSession(
	 * false).getAttribute("store_id")); mv.addObject("url",
	 * CommUtil.getURL(request) + "/buyer/order.htm"); } } else { mv = new
	 * JModelAndView("error.html", configService.getSysConfig(),
	 * this.userConfigService.getUserConfig(), 1, request, response);
	 * mv.addObject("op_title", "没有开通支付方式，不能付款"); String store_id2 =
	 * CommUtil.null2String(request.getSession(false)
	 * .getAttribute("store_id")); mv.addObject("url", CommUtil.getURL(request)
	 * + "/buyer/order.htm"); } return mv; }
	 */
	/**
	 * 计算并合并购车信息
	 * 
	 * @param request
	 * @return
	 */
	@Override
	public List<StoreCart> cart_calc(HttpServletRequest request) {
		List<StoreCart> cart = new ArrayList<StoreCart>();// 整体店铺购物车
		List<StoreCart> user_cart = new ArrayList<StoreCart>();// 当前用户未提交订单的店铺购物车
		List<StoreCart> cookie_cart = new ArrayList<StoreCart>();// 当前cookie指向的店铺购物车
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
		}
		String cart_session_id = "";
		Map params = new HashMap();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(cookie.getValue());
				}
			}
		}
		if (user != null) {
			if (!cart_session_id.equals("")) {
				// 如果用户拥有自己的店铺，删除购物车中自己店铺中的商品信息
				if (user.getStore() != null) {
					params.clear();
					params.put("cart_session_id", cart_session_id);
					params.put("user_id", user.getId());
					params.put("sc_status", 0);
					params.put("store_id", user.getStore().getId());
					List<StoreCart> store_cookie_cart = this.storeCartService
							.query(
									"select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
									params, -1, -1);
					for (StoreCart sc : store_cookie_cart) {
						for (GoodsCart gc : sc.getGcs()) {
							gc.getGsps().clear();
							this.goodsCartService.delete(gc.getId());
						}
						this.storeCartService.delete(sc.getId());
					}
				}
				// 查询出cookie中的商品信息
				params.clear();
				params.put("cart_session_id", cart_session_id);
				params.put("sc_status", 0);
				cookie_cart = this.storeCartService
						.query(
								"select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
								params, -1, -1);
				// 查询用户未提交订单的购物车信息
				params.clear();
				params.put("user_id", user.getId());
				params.put("sc_status", 0);
				user_cart = this.storeCartService
						.query(
								"select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
								params, -1, -1);
			} else {
				// 查询用户未提交订单的购物车信息
				params.clear();
				params.put("user_id", user.getId());
				params.put("sc_status", 0);
				user_cart = this.storeCartService
						.query(
								"select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
								params, -1, -1);
			}
		} else {
			// 查询cookie中保存的购物车信息
			if (!cart_session_id.equals("")) {
				params.clear();
				params.put("cart_session_id", cart_session_id);
				params.put("sc_status", 0);
				cookie_cart = this.storeCartService
						.query(
								"select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
								params, -1, -1);
			}
		}
		// 合并当前用户未提交订单的店铺购物车和当前cookie指向的店铺购物车
		for (StoreCart sc : user_cart) {
			List<GoodsCart> goodsCartList = this.goodsCartService
					.queryByStoreCart(sc);
			if (goodsCartList != null && goodsCartList.size() > 0) {
				boolean sc_add = true;
				for (StoreCart sc1 : cart) {
					if (sc1.getStore().getId().equals(sc.getStore().getId())) {
						sc_add = false;
					}
				}
				if (sc_add) {
					cart.add(sc);
				}
			}
		}
		for (StoreCart sc : cookie_cart) {
			boolean sc_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc.getStore().getId())) {
					sc_add = false;
				}
			}
			if (sc_add) {
				cart.add(sc);
			}
		}
		return cart;
	}

	public void thirdBinding(ModelAndView mv) {
		List<ThirdBinding> list = this.thirdBindingService.queryByCreateUser();
		if (null != list && list.size() > 0) {
			mv.addObject("thirdBinding", 1);
		} else {
			mv.addObject("thirdBinding", 0);
		}
	}

	@Override
	public ModelAndView weixin_order_pay(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String chargeNum, String integrationPlatform,
			String integrationStore) {
		ModelAndView mv = null;
		BigDecimal chargeNumDec;
		try {
			chargeNumDec = new BigDecimal(chargeNum);
		} catch (Exception e) {
			chargeNumDec = BigDecimal.ZERO;
		}
		OrderForm of = getObjById(CommUtil.null2Long(order_id));
		if (of != null && of.getOrder_status() == 10) {
			if (of.getUser().getId().equals(
					SecurityUserHolder.getCurrentUser().getId())) {
				setIntegration_for_OrderForm(of, integrationPlatform,
						integrationStore);
				// 获取该用户在该经销商的赊销额度
				String query = "select obj from Charge obj where store.id=:store_id and user.id=:user_id";
				Map<String, Long> chargeMap = new HashMap<String, Long>();
				chargeMap.put("store_id", of.getStore().getId());
				chargeMap.put("user_id", of.getUser().getId());
				List<Charge> chargeList = this.chargeService.query(query,
						chargeMap, -1, -1);
				if (chargeList != null && chargeList.size() == 1) {
					BigDecimal ret = BigDecimal.ZERO;// 可用赊销额度
					Charge charge = chargeList.get(0);
					// 有赊销金额
					if ((new BigDecimal(CommUtil.null2Long(of.getCharge_Num())))
							.compareTo(BigDecimal.ZERO) > 0) {
						// 将原先的赊销金额退还原处
						charge.setUsedPayNum(charge.getUsedPayNum().subtract(
								new BigDecimal(CommUtil.null2Long(of
										.getCharge_Num()))));
						of.setCharge_Num(BigDecimal.ZERO);
					}
					// 可用赊销额度
					BigDecimal b1 = charge.getPaymentNum();
					BigDecimal b2 = charge.getUsedPayNum();
					ret = b1.subtract(b2);
					if (chargeNumDec.compareTo(ret) == -1
							|| chargeNumDec.compareTo(ret) == 0) {
						BigDecimal total_from_web = chargeNumDec;
						if (CommUtil.isNotNull(integrationPlatform)) {
							total_from_web = total_from_web.add(new BigDecimal(
									integrationPlatform));
						}
						if (CommUtil.isNotNull(integrationStore)) {
							total_from_web = total_from_web.add(new BigDecimal(
									integrationStore));
						}
						if (total_from_web.compareTo(of.getTotalPrice()) == -1) {
							// 扣除此次用的赊销额度 并跳转到第三方支付界面
							update_charge_orderForm(of, charge, chargeNumDec,
									-1);
							
							mv = thirdPay(request, response, payType, order_id);
						} else {
							update_charge_orderForm(of, charge, chargeNumDec,
									ConstantUtils._ORDERFORM_ORDER_STATUS[3]);
							subtract_operations_for_integration(of, request);
							if (this.configService.getSysConfig().isSmsEnbale()) {
								try {
									String phone = of.getStore().getUser()
											.getTelephone();
									this.send_sms(request, of, phone,
											"sms_toseller_pay_notify_ok");
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							/**
							 * 库存扣减现在买家提交订单时触发 wsp.update_goods_inventory(of);
							 **/
							mv = new JModelAndView("weixin/success.html",
									configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1,
									request, response);
							mv.addObject("op_title", "您已经完成了支付！");
							mv.addObject("url", CommUtil.getURL(request)
									+ "/weixin/buyer/order.htm");
						}
					} else {
						mv = new JModelAndView("weixin/error.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "可用赊销额度不足以支付该订单！");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/weixin/buyer/order.htm");
					}
				} else {
					// 没有赊销金额直接跳第三方支付界面
					mv = thirdPay(request, response, payType, order_id);
				}
			} else {// fix bugs for session
				mv = new JModelAndView("weixin/success.html", configService
						.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "正在为您跳转");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/buyer/account.htm");
			}
		} else {
			mv = new JModelAndView("weixin/error.html", configService
					.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "该订单不存在！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	@Override
	public ModelAndView order_pay(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String chargeNum, String integrationPlatform,
			String integrationStore) {
		ModelAndView mv = null;
		BigDecimal chargeNumDec;
		try {
			chargeNumDec = new BigDecimal(chargeNum);
		} catch (Exception e) {
			chargeNumDec = BigDecimal.ZERO;
		}
		OrderForm of = getObjById(CommUtil.null2Long(order_id));
		if (of != null && of.getOrder_status() == 10) {
			if (of.getUser().getId().equals(
					SecurityUserHolder.getCurrentUser().getId())) {
				setIntegration_for_OrderForm(of, integrationPlatform,
						integrationStore);
				// 获取该用户在该经销商的赊销额度
				String query = "select obj from Charge obj where store.id=:store_id and user.id=:user_id";
				Map<String, Long> chargeMap = new HashMap<String, Long>();
				chargeMap.put("store_id", of.getStore().getId());
				chargeMap.put("user_id", of.getUser().getId());
				List<Charge> chargeList = this.chargeService.query(query,
						chargeMap, -1, -1);
				if (chargeList != null && chargeList.size() == 1) {
					BigDecimal ret = BigDecimal.ZERO;// 可用赊销额度
					Charge charge = chargeList.get(0);
					// 有赊销金额
					if ((new BigDecimal(CommUtil.null2Long(of.getCharge_Num())))
							.compareTo(BigDecimal.ZERO) > 0) {
						// 将原先的赊销金额退还原处
						charge.setUsedPayNum(charge.getUsedPayNum().subtract(
								new BigDecimal(CommUtil.null2Long(of
										.getCharge_Num()))));
						of.setCharge_Num(BigDecimal.ZERO);
					}
					// 可用赊销额度
					BigDecimal b1 = charge.getPaymentNum();
					BigDecimal b2 = charge.getUsedPayNum();
					ret = b1.subtract(b2);
					if (chargeNumDec.compareTo(ret) == -1
							|| chargeNumDec.compareTo(ret) == 0) {
						BigDecimal total_from_web = chargeNumDec;
						if (CommUtil.isNotNull(integrationPlatform)) {
							total_from_web = total_from_web.add(new BigDecimal(
									integrationPlatform));
						}
						if (CommUtil.isNotNull(integrationStore)) {
							total_from_web = total_from_web.add(new BigDecimal(
									integrationStore));
						}
						if (total_from_web.compareTo(of.getTotalPrice()) == -1) {
							// 扣除此次用的赊销额度 并跳转到第三方支付界面
							update_charge_orderForm(of, charge, chargeNumDec,
									-1);		
							mv = pc_thirdPay(request, response, payType, order_id);
						} else {
							update_charge_orderForm(of, charge, chargeNumDec,
									ConstantUtils._ORDERFORM_ORDER_STATUS[3]);
							subtract_operations_for_integration(of, request);
							if (this.configService.getSysConfig().isSmsEnbale()) {
								try {
									String phone = of.getStore().getUser()
											.getTelephone();
									this.send_sms(request, of, phone,
											"sms_toseller_pay_notify_ok");
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							/**
							 * 库存扣减现在买家提交订单时触发 wsp.update_goods_inventory(of);
							 **/
							mv = new JModelAndView("success.html",
									configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1,
									request, response);
							mv.addObject("op_title", "您已经完成了支付！");
							mv.addObject("url", CommUtil.getURL(request)
									+ "/buyer/order.htm");
						}
					} else {
						mv = new JModelAndView("error.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "可用赊销额度不足以支付该订单！");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/order.htm");
					}
				} else {
					// 没有赊销金额直接跳第三方支付界面
					
					mv = pc_thirdPay(request, response, payType, order_id);
				}
			} else {// fix bugs for session
				mv = new JModelAndView("success.html", configService
						.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "正在为您跳转");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/account.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService
					.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "该订单不存在！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order.htm");
		}
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param payType
	 * @param order_id
	 * @return
	 */
	private ModelAndView pc_thirdPay(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id) {
		ModelAndView mv = null;
		OrderForm of = getObjById(CommUtil.null2Long(order_id));
		if (CommUtil.null2String(payType).equals("")) {
			// 没有第三方支付方式 报错
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "支付方式错误！");
			String store_id = CommUtil.null2String(request.getSession(false)
					.getAttribute("store_id"));
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		} else {
			List<Payment> payments = new ArrayList<Payment>();
			Map<String, Object> params = new HashMap<String, Object>();
			if (this.configService.getSysConfig().getConfig_payment_type() == 1) {// 系统开启平台支付
				params.put("mark", payType);
				params.put("type", "admin");
				payments = this.paymentService
						.query(
								"select obj from Payment obj where obj.mark=:mark and obj.type=:type",
								params, -1, -1);
			} else {
				params.put("store_id", of.getStore().getId());
				params.put("mark", payType);
				payments = this.paymentService
						.query(
								"select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
								params, -1, -1);
			}
			of.setPayment(payments.get(0));
			update(of);
			if (payType.equals(ConstantUtils._PAYMENT_MARK[1])) {
				subtract_operations_for_integration(of, request);
				mv = new JModelAndView("third_payment.html", configService
						.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				XConf xconf = this.xconfService
						.queryByXconfkey(ConstantUtils._PAY_THIRD_INSTITUTIONID);
				String paymentNo = GUID.getTxNo();
				mv.addObject("InstitutionID", xconf.getXconfvalue());// 机构号：001639
				mv.addObject("PaymentNo", paymentNo);
				mv.addObject("order_id", of.getOrder_id());
				mv.addObject("totalPrice", of.getTotalPrice().subtract(
						new BigDecimal(CommUtil.null2Long(of.getCharge_Num())))
						.toString());
				ThirdBinding thirdBinding = null;
				List<ThirdBinding> list = this.thirdBindingService
						.queryByCreateUser();
				if (null != list && list.size() > 0) {
					thirdBinding = list.get(0);
				}
				mv.addObject("thirdBinding", thirdBinding);
				mv.addObject("bankBeanList", ConstantUtils._getBankIDList());
				mv.addObject("identificationTypeList", ConstantUtils
						._getIdentificationTypeList());
				String orderNo = "" + System.currentTimeMillis();
				mv.addObject("OrderNo", orderNo);
			} else if (payType.equals("outline")) {
				mv = new JModelAndView("outline_pay.html", configService
						.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				String pay_session = CommUtil.randomString(32);
				request.getSession(false).setAttribute("pay_session",
						pay_session);
				mv.addObject("paymentTools", this.paymentTools);
				mv.addObject("store_id", getObjById(
						CommUtil.null2Long(order_id)).getStore().getId());
				mv.addObject("pay_session", pay_session);
				mv.addObject("order_id", order_id);
			} else if (payType.equals("payafter")) {
				mv = new JModelAndView("payafter_pay.html", configService
						.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				String pay_session = CommUtil.randomString(32);
				request.getSession(false).setAttribute("pay_session",
						pay_session);
				mv.addObject("paymentTools", this.paymentTools);
				mv.addObject("store_id", getObjById(
						CommUtil.null2Long(order_id)).getStore().getId());
				mv.addObject("pay_session", pay_session);
				mv.addObject("order_id", order_id);
			} else {
				mv = new JModelAndView("line_pay.html", configService
						.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("payType", payType);
				mv.addObject("url", CommUtil.getURL(request));
				mv.addObject("payTools", payTools);
				mv.addObject("type", "goods");
				mv.addObject("payment_id", of.getPayment().getId());
				/* mv.addObject("spbill_ip", CommUtil.getIpAddr(request)); */
				mv.addObject("order_id", order_id);
			}
		}
		return mv;
	}

	/**
	 * 现款自提，中金支付
	 */
	private ModelAndView thirdPay(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id) {
		ModelAndView mv = null;
		OrderForm of = getObjById(CommUtil.null2Long(order_id));
		if (CommUtil.null2String(payType).equals("")) {
			// 没有第三方支付方式 报错
			mv = new JModelAndView("weixin/error.html", configService
					.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "支付方式错误！");
			String store_id = CommUtil.null2String(request.getSession(false)
					.getAttribute("store_id"));
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/index.htm?store_id=" + store_id);
		} else {
			List<Payment> payments = new ArrayList<Payment>();
			Map<String, Object> params = new HashMap<String, Object>();
			if (this.configService.getSysConfig().getConfig_payment_type() == 1) {// 系统开启平台支付
				params.put("mark", payType);
				params.put("type", "admin");
				payments = this.paymentService
						.query(
								"select obj from Payment obj where obj.mark=:mark and obj.type=:type",
								params, -1, -1);
			} else {
				params.put("store_id", of.getStore().getId());
				params.put("mark", payType);
				payments = this.paymentService
						.query(
								"select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
								params, -1, -1);
			}
			of.setPayment(payments.get(0));
			update(of);
			if (payType.equals(ConstantUtils._PAYMENT_MARK[1])) {// 中金支付
				subtract_operations_for_integration(of, request);
				mv = new JModelAndView("weixin/third_payment.html",
						configService.getSysConfig(), this.userConfigService
								.getUserConfig(), 1, request, response);
				XConf xconf = this.xconfService
						.queryByXconfkey(ConstantUtils._PAY_THIRD_INSTITUTIONID);
				String paymentNo = GUID.getTxNo();
				mv.addObject("InstitutionID", xconf.getXconfvalue());// 机构号：001639
				mv.addObject("PaymentNo", paymentNo);
				mv.addObject("order_id", of.getOrder_id());
				mv.addObject("totalPrice", ConstantUtils
						._getRealPriceByOrderForm(of));
				ThirdBinding thirdBinding = null;
				List<ThirdBinding> list = this.thirdBindingService
						.queryByCreateUser();
				if (null != list && list.size() > 0) {
					thirdBinding = list.get(0);
				}
				mv.addObject("thirdBinding", thirdBinding);
				mv.addObject("bankBeanList", ConstantUtils._getBankIDList());
				mv.addObject("identificationTypeList", ConstantUtils
						._getIdentificationTypeList());
				String orderNo = "" + System.currentTimeMillis();
				mv.addObject("OrderNo", orderNo);
			} else if (payType.equals(ConstantUtils._PAYMENT_MARK[2])) {// 现款自提
				mv = new JModelAndView("weixin/payafter_pay.html",
						configService.getSysConfig(), this.userConfigService
								.getUserConfig(), 1, request, response);
				String pay_session = CommUtil.randomString(32);
				request.getSession(false).setAttribute("pay_session",
						pay_session);
				mv.addObject("paymentTools", this.paymentTools);
				mv.addObject("store_id", getObjById(
						CommUtil.null2Long(order_id)).getStore().getId());
				mv.addObject("pay_session", pay_session);
				mv.addObject("order_id", order_id);
			} else if (payType.equals("outline")) {
				mv = new JModelAndView("weixin/outline_pay.html", configService
						.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				String pay_session = CommUtil.randomString(32);
				request.getSession(false).setAttribute("pay_session",
						pay_session);
				mv.addObject("paymentTools", this.paymentTools);
				mv.addObject("store_id", getObjById(
						CommUtil.null2Long(order_id)).getStore().getId());
				mv.addObject("pay_session", pay_session);
				mv.addObject("order_id", order_id);
			} else {
				mv = new JModelAndView("weixin/line_pay.html", configService
						.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("payType", payType);
				mv.addObject("url", CommUtil.getURL(request));
				mv.addObject("payTools", payTools);
				mv.addObject("type", "goods");
				mv.addObject("payment_id", of.getPayment().getId());
				// mv.addObject("spbill_ip", CommUtil.getIpAddr(request));
				mv.addObject("order_id", order_id);
			}
		}
		return mv;
	}

	@Override
	public ModelAndView weixin_order_pay_outline(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception {
		ModelAndView mv = new JModelAndView("weixin/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		String pay_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("pay_session"));
		if (pay_session1.equals(pay_session)) {
			OrderForm of = getObjById(CommUtil.null2Long(order_id));
			of.setPay_msg(pay_msg);
			Map params = new HashMap();
			params.put("mark", "outline");
			params.put("store_id", of.getStore().getId());
			List<Payment> payments = this.paymentService
					.query(
							"select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
							params, -1, -1);
			if (payments.size() > 0) {
				of.setPayment(payments.get(0));
				of.setPayTime(new Date());
			}
			of.setOrder_status(15);
			update(of);
			if (this.configService.getSysConfig().isSmsEnbale()) {
				String phone = of.getStore().getUser().getTelephone();
				if (phone == null)
					phone = of.getStore().getUser().getMobile();
				this.send_sms(request, of, phone,
						"sms_toseller_outline_pay_ok_notify");
			}
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.send_email(request, of,
						of.getStore().getUser().getEmail(),
						"email_toseller_outline_pay_ok_notify");
			}
			// 记录支付日志
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("提交线下支付申请");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(of);
			this.orderFormLogService.save(ofl);
			request.getSession(false).removeAttribute("pay_session");
			mv.addObject("op_title", "线下支付提交成功，等待卖家审核！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		} else {
			mv = new JModelAndView("weixin/error.html", configService
					.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "订单已经支付，禁止重复支付！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	private void send_sms(HttpServletRequest request, OrderForm order,
			String mobile, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			SendMessageUtil sendmessage = new SendMessageUtil();
			String path = request.getSession().getServletContext().getRealPath(
					"/")
					+ File.separator + "vm" + File.separator;
			if (!CommUtil.fileExist(path)) {
				CommUtil.createFolder(path);
			}
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + "msg.vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path);
			p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
			Velocity.init(p);
			org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm",
					"UTF-8");
			VelocityContext context = new VelocityContext();
			context.put("buyer", order.getUser());
			context.put("seller", order.getStore().getUser());
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			context.put("webPath", CommUtil.getURL(request));
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			// System.out.println(writer.toString());
			String content = writer.toString();
			try {
				sendmessage.sendHttpPost(mobile, content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void send_email(HttpServletRequest request, OrderForm order,
			String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			String email = order.getUser().getEmail();
			String subject = template.getTitle();
			String path = request.getSession().getServletContext().getRealPath(
					"")
					+ File.separator + "vm" + File.separator;
			if (!CommUtil.fileExist(path)) {
				CommUtil.createFolder(path);
			}
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + "msg.vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, request
					.getRealPath("")
					+ File.separator + "vm" + File.separator);
			p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
			Velocity.init(p);
			org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm",
					"UTF-8");
			VelocityContext context = new VelocityContext();
			context.put("buyer", order.getUser());
			context.put("seller", order.getStore().getUser());
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			context.put("webPath", CommUtil.getURL(request));
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			// System.out.println(writer.toString());
			String content = writer.toString();
			this.msgTools.sendEmail(email, subject, content);
		}
	}

	private void send_email(HttpServletRequest request, OrderForm order,
			String email, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			String subject = template.getTitle();
			String path = request.getSession().getServletContext().getRealPath(
					"")
					+ File.separator + "vm" + File.separator;
			if (!CommUtil.fileExist(path)) {
				CommUtil.createFolder(path);
			}
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + "msg.vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, request
					.getRealPath("/")
					+ "vm" + File.separator);
			p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
			Velocity.init(p);
			org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm",
					"UTF-8");
			VelocityContext context = new VelocityContext();
			context.put("buyer", order.getUser());
			context.put("seller", order.getStore().getUser());
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			context.put("webPath", CommUtil.getURL(request));
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			// System.out.println(writer.toString());
			String content = writer.toString();
			this.msgTools.sendEmail(email, subject, content);
		}
	}

	@Override
	public String weixin_add_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String count,
			String price, String gsp, String buy_type) {
		String cart_session_id = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(cookie.getValue());
				}
			}
		}
		if (cart_session_id.equals("")) {
			cart_session_id = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("cart_session_id", cart_session_id);
			cookie.setDomain(CommUtil.generic_domain(request));
			response.addCookie(cookie);
		}
		List<StoreCart> cart = new ArrayList<StoreCart>();// 整体店铺购物车
		List<StoreCart> user_cart = new ArrayList<StoreCart>();// 当前用户未提交订单的店铺购物车
		List<StoreCart> cookie_cart = new ArrayList<StoreCart>();// 当前cookie指向的店铺购物车
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
		}
		Map params = new HashMap();
		if (user != null) {
			if (!cart_session_id.equals("")) {
				// 如果用户拥有自己的店铺，删除购物车中自己店铺中的商品信息
				if (user.getStore() != null) {
					params.clear();
					params.put("cart_session_id", cart_session_id);
					params.put("user_id", user.getId());
					params.put("sc_status", 0);
					params.put("store_id", user.getStore().getId());
					List<StoreCart> store_cookie_cart = this.storeCartService
							.query(
									"select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
									params, -1, -1);
					for (StoreCart sc : store_cookie_cart) {
						for (GoodsCart gc : sc.getGcs()) {
							gc.getGsps().clear();
							this.goodsCartService.delete(gc.getId());
						}
						this.storeCartService.delete(sc.getId());
					}
				}
				// 查询出cookie中的商品信息
				params.clear();
				params.put("cart_session_id", cart_session_id);
				params.put("sc_status", 0);
				cookie_cart = this.storeCartService
						.query(
								"select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
								params, -1, -1);
				// 查询用户未提交订单的购物车信息
				params.clear();
				params.put("user_id", user.getId());
				params.put("sc_status", 0);
				user_cart = this.storeCartService
						.query(
								"select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
								params, -1, -1);
			} else {
				// 查询用户未提交订单的购物车信息
				params.clear();
				params.put("user_id", user.getId());
				params.put("sc_status", 0);
				user_cart = this.storeCartService
						.query(
								"select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
								params, -1, -1);
			}
		} else {
			// 查询cookie中保存的购物车信息
			if (!cart_session_id.equals("")) {
				params.clear();
				params.put("cart_session_id", cart_session_id);
				params.put("sc_status", 0);
				cookie_cart = this.storeCartService
						.query(
								"select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
								params, -1, -1);
			}
		}
		// 合并当前用户未提交订单的店铺购物车和当前cookie指向的店铺购物车
		for (StoreCart sc : user_cart) {
			boolean sc_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc.getStore().getId())) {
					sc_add = false;
				}
			}
			if (sc_add) {
				cart.add(sc);
			}
		}
		for (StoreCart sc : cookie_cart) {
			boolean sc_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc.getStore().getId())) {
					sc_add = false;
				}
			}
			if (sc_add) {
				cart.add(sc);
			}
		}
		// 购物车查询合并完毕
		String[] gsp_ids = gsp.split(",");
		Arrays.sort(gsp_ids);
		boolean add = true;
		double total_price = 0;
		int total_count = 0;
		for (StoreCart sc : cart) {
			for (GoodsCart gc : sc.getGcs()) {
				if (gsp_ids != null && gsp_ids.length > 0
						&& gc.getGsps() != null && gc.getGsps().size() > 0) {
					String[] gsp_ids1 = new String[gc.getGsps().size()];
					for (int i = 0; i < gc.getGsps().size(); i++) {
						gsp_ids1[i] = gc.getGsps().get(i) != null ? gc
								.getGsps().get(i).getId().toString() : "";
					}
					Arrays.sort(gsp_ids1);
					if (gc.getGoods().getId().toString().equals(id)
							&& Arrays.equals(gsp_ids, gsp_ids1)) {
						add = false;
					}
				} else {
					if (gc.getGoods().getId().toString().equals(id)) {
						add = false;
					}
				}
			}
		}
		if (add) {// 排除购物车中没有重复商品后添加该商品到购物车
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			String type = "save";
			StoreCart sc = new StoreCart();
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(
						goods.getGoods_store().getId())) {
					sc = sc1;
					type = "update";
					break;
				}
			}
			sc.setStore(goods.getGoods_store());
			if (type.equals("save")) {
				sc.setAddTime(new Date());
				this.storeCartService.save(sc);// 保存该StoreCart
			} else {
				this.storeCartService.update(sc);// 如果是已经存在的则更新storeCart
			}
			GoodsCart obj = new GoodsCart();
			obj.setAddTime(new Date());
			if (CommUtil.null2String(buy_type).equals("")) {
				obj.setCount(CommUtil.null2Int(count));
				obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(goods
						.getGoods_current_price())));// fix bug:price
			}
			if (CommUtil.null2String(buy_type).equals("combin")) {// 组合销售只添加一件商品
				obj.setCount(1);// 设置组合销售套数
				obj.setCart_type("combin");// 设置组合销售标识
				obj.setPrice(goods.getCombin_price());// 设置为组合销售价格
			}
			obj.setGoods(goods);
			String spec_info = "";
			for (String gsp_id : gsp_ids) {
				GoodsSpecProperty spec_property = this.goodsSpecPropertyService
						.getObjById(CommUtil.null2Long(gsp_id));
				obj.getGsps().add(spec_property);
				if (spec_property != null) {
					spec_info = spec_property.getSpec().getName() + ":"
							+ spec_property.getValue() + " " + spec_info;
				}
			}
			obj.setSc(sc);
			obj.setSpec_info(spec_info);
			this.goodsCartService.save(obj);
			sc.getGcs().add(obj);
			double cart_total_price = 0;
			for (GoodsCart gc1 : sc.getGcs()) {
				if (CommUtil.null2String(gc1.getCart_type()).equals("")) {
					if (!CommUtil.null2String(gsp).equals("")
							&& !CommUtil.null2String(gsp).equals(",")) { // 处理属性促销价问题
						cart_total_price = cart_total_price
								+ CommUtil.null2Double(gc1.getPrice())
								* gc1.getCount();
					} else {
						cart_total_price = cart_total_price
								+ CommUtil.null2Double(gc1.getGoods()
										.getGoods_current_price())
								* gc1.getCount();
					}
				}
				if (CommUtil.null2String(gc1.getCart_type()).equals("combin")) { // 如果是组合销售购买，则设置组合价格
					cart_total_price = cart_total_price
							+ CommUtil.null2Double(gc1.getGoods()
									.getCombin_price()) * gc1.getCount();
				}
			}
			sc.setTotal_price(BigDecimal.valueOf(CommUtil
					.formatMoney(cart_total_price)));
			if (user == null) {
				sc.setCart_session_id(cart_session_id);
			} else {
				sc.setUser(user);
			}
			if (type.equals("save")) {
				sc.setAddTime(new Date());
				this.storeCartService.save(sc);// 保存该StoreCart
			} else {
				this.storeCartService.update(sc);
			}
			boolean cart_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc.getStore().getId())) {
					cart_add = false;
				}
			}
			if (cart_add) {
				cart.add(sc);// 将新增的StoreCart添加到整体购物车中，计算总体价格和数量
			}
		}
		for (StoreCart sc1 : cart) {
			// System.out.println(sc1.getGcs().size());
			total_count = total_count + sc1.getGcs().size();
			for (GoodsCart gc1 : sc1.getGcs()) {
				total_price = total_price
						+ CommUtil.mul(gc1.getPrice(), gc1.getCount());
			}
		}
		Map map = new HashMap();
		map.put("count", total_count);
		map.put("total_price", total_price);
		String ret = Json.toJson(map, JsonFormat.compact());
		return ret;
	}

	@Override
	public String weixin_remove_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String store_id) {
		GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(id));
		StoreCart the_sc = gc.getSc();
		gc.getGsps().clear();
		// the_sc.getGcs().remove(gc);
		this.goodsCartService.delete(CommUtil.null2Long(id));
		if (the_sc.getGcs().size() == 0) {
			this.storeCartService.delete(the_sc.getId());
		}
		List<StoreCart> cart = cart_calc(request);
		double total_price = 0;
		double sc_total_price = 0;
		double count = 0;
		for (StoreCart sc2 : cart) {
			List<GoodsCart> goodsCartList = new ArrayList<GoodsCart>();
			for (GoodsCart gc1 : sc2.getGcs()) {
				GoodsCart temp_gc = this.goodsCartService.getObjById(gc1
						.getId());
				if (temp_gc != null) {
					goodsCartList.add(temp_gc);
					total_price = CommUtil.null2Double(gc1.getPrice())
							* gc1.getCount() + total_price;
					count++;
					if (store_id != null
							&& !store_id.equals("")
							&& sc2.getStore().getId().toString().equals(
									store_id)) {
						sc_total_price = sc_total_price
								+ CommUtil.null2Double(gc1.getPrice())
								* gc1.getCount();
						sc2.setTotal_price(BigDecimal.valueOf(sc_total_price));
					}
				}
			}
			sc2.setGcs(goodsCartList);
			this.storeCartService.update(sc2);
		}
		request.getSession(false).setAttribute("cart", cart);
		Map map = new HashMap();
		map.put("count", count);
		map.put("total_price", total_price);
		map.put("sc_total_price", sc_total_price);
		String ret = Json.toJson(map, JsonFormat.compact());
		return ret;
	}

	@Override
	public String weixin_goods_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String cart_id, String store_id,
			String count) {
		List<StoreCart> cart = cart_calc(request);
		//
		double goods_total_price = 0;
		String error = "100";// 100表示修改成功，200表示库存不足,300表示团购库存不足
		Goods goods = null;
		String cart_type = "";// 判断是否为组合销售
		for (StoreCart sc : cart) {
			for (GoodsCart gc : sc.getGcs()) {
				if (gc.getId().toString().equals(cart_id)) {
					goods = gc.getGoods();
					cart_type = CommUtil.null2String(gc.getCart_type());
				}
			}
		}
		if (cart_type.equals("")) {// 普通商品的处理
			if (goods.getGroup_buy() == 2) {
				GroupGoods gg = new GroupGoods();
				for (GroupGoods gg1 : goods.getGroup_goods_list()) {
					if (gg1.getGg_goods().equals(goods.getId())) {
						gg = gg1;
					}
				}
				if (gg.getGg_count() >= CommUtil.null2Int(count)) {
					for (StoreCart sc : cart) {
						for (int i = 0; i < sc.getGcs().size(); i++) {
							GoodsCart gc = sc.getGcs().get(i);
							GoodsCart gc1 = gc;
							if (gc.getId().toString().equals(cart_id)) {
								sc.setTotal_price(BigDecimal.valueOf(CommUtil
										.add(sc.getTotal_price(), (CommUtil
												.null2Int(count) - gc
												.getCount())
												* CommUtil.null2Double(gc
														.getPrice()))));
								gc.setCount(CommUtil.null2Int(count));
								gc1 = gc;
								sc.getGcs().remove(gc);
								sc.getGcs().add(gc1);
								goods_total_price = CommUtil.null2Double(gc1
										.getPrice())
										* gc1.getCount();
								this.storeCartService.update(sc);
							}
						}
					}
				} else {
					error = "300";
				}
			} else {
				if (goods.getGoods_inventory() >= CommUtil.null2Int(count)) {
					for (StoreCart sc : cart) {
						for (int i = 0; i < sc.getGcs().size(); i++) {
							GoodsCart gc = sc.getGcs().get(i);
							GoodsCart gc1 = gc;
							if (gc.getId().toString().equals(cart_id)) {
								sc
										.setTotal_price(BigDecimal
												.valueOf(CommUtil
														.add(
																sc
																		.getTotal_price(),
																(CommUtil
																		.null2Int(count) - gc
																		.getCount())
																		* Double
																				.parseDouble(gc
																						.getPrice()
																						.toString()))));
								gc.setCount(CommUtil.null2Int(count));
								gc1 = gc;
								sc.getGcs().remove(gc);
								sc.getGcs().add(gc1);
								goods_total_price = Double.parseDouble(gc1
										.getPrice().toString())
										* gc1.getCount();
								this.storeCartService.update(sc);
							}
						}
					}
				} else {
					count = CommUtil.null2String(goods.getGoods_inventory());
					error = "200";
				}
			}
		}
		if (cart_type.equals("combin")) {// 组合销售的处理
			if (goods.getGoods_inventory() >= CommUtil.null2Int(count)) {
				for (StoreCart sc : cart) {
					for (int i = 0; i < sc.getGcs().size(); i++) {
						GoodsCart gc = sc.getGcs().get(i);
						GoodsCart gc1 = gc;
						if (gc.getId().toString().equals(cart_id)) {
							sc.setTotal_price(BigDecimal.valueOf(CommUtil.add(
									sc.getTotal_price(), (CommUtil
											.null2Int(count) - gc.getCount())
											* CommUtil.null2Float(gc.getGoods()
													.getCombin_price()))));
							gc.setCount(CommUtil.null2Int(count));
							gc1 = gc;
							sc.getGcs().remove(gc);
							sc.getGcs().add(gc1);
							goods_total_price = Double.parseDouble(gc1
									.getPrice().toString())
									* gc1.getCount();
							this.storeCartService.update(sc);
						}
					}
				}
			} else {
				error = "200";
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		Map map = new HashMap();
		map.put("count", count);
		for (StoreCart sc : cart) {
			System.out.println("=============:" + sc.getTotal_price());
			if (sc.getStore().getId().equals(CommUtil.null2Long(store_id))) {
				map.put("sc_total_price", df.format(CommUtil.null2Float(sc
						.getTotal_price())));
			}
		}
		map.put("goods_total_price", df.format(goods_total_price));
		map.put("error", error);
		return Json.toJson(map, JsonFormat.compact());
	}

	@Override
	public ModelAndView weixin_goods_cart1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/goods_cart1.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		List<StoreCart> cart = this.cart_calc(request);
		if (cart != null) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			Store store = user.getStore() != null ? user.getStore() : null;
			this.storeCartService.weixin_goods_cart1(store, cart);
			request.getSession(false).setAttribute("cart", cart);
			mv.addObject("cart", cart);
			mv.addObject("goodsViewTools", goodsViewTools);
		} else {
			mv = new JModelAndView("weixin/error.html", configService
					.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "购物车信息为空");
			String store_id = CommUtil.null2String(request.getSession(false)
					.getAttribute("store_id"));
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/platform/index.htm");
		}
		return mv;
	}

	@Override
	public ModelAndView goods_cart1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("goods_cart1.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		List<StoreCart> cart = this.cart_calc(request);
		if (cart != null) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			Store store = user.getStore() != null ? user.getStore() : null;
			this.storeCartService.weixin_goods_cart1(store, cart);
			request.getSession(false).setAttribute("cart", cart);
			mv.addObject("cart", cart);
			mv.addObject("goodsViewTools", goodsViewTools);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "购物车信息为空");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@Override
	public ModelAndView weixin_order_pay_balance(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg) throws Exception {
		ModelAndView mv = new JModelAndView("weixin/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		OrderForm of = getObjById(CommUtil.null2Long(order_id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (CommUtil.null2Double(user.getAvailableBalance()) > CommUtil
				.null2Double(of.getTotalPrice())) {
			of.setPay_msg(pay_msg);
			of.setOrder_status(20);
			Map params = new HashMap();
			params.put("mark", "balance");
			params.put("store_id", of.getStore().getId());
			List<Payment> payments = this.paymentService
					.query(
							"select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
							params, -1, -1);
			if (payments.size() > 0) {
				of.setPayment(payments.get(0));
				of.setPayTime(new Date());
			}
			boolean ret = update(of);
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.send_email(request, of,
						of.getStore().getUser().getEmail(),
						"email_toseller_balance_pay_ok_notify");
				this.send_email(request, of,
						of.getStore().getUser().getEmail(),
						"email_tobuyer_balance_pay_ok_notify");
			}
			if (this.configService.getSysConfig().isSmsEnbale()) {
				String phone = of.getStore().getUser().getTelephone();
				if (phone == null)
					phone = of.getStore().getUser().getMobile();
				this.send_sms(request, of, phone,
						"sms_toseller_balance_pay_ok_notify");
				String phone2 = of.getUser().getTelephone();
				if (phone2 == null)
					phone2 = of.getUser().getMobile();
				this.send_sms(request, of, phone2,
						"sms_tobuyer_balance_pay_ok_notify");
			}
			if (ret) {
				user.setAvailableBalance(BigDecimal.valueOf(CommUtil.subtract(
						user.getAvailableBalance(), of.getTotalPrice())));
				user.setFreezeBlance(BigDecimal.valueOf(CommUtil.add(user
						.getFreezeBlance(), of.getTotalPrice())));
				this.userService.update(user);
				PredepositLog log = new PredepositLog();
				log.setAddTime(new Date());
				log.setPd_log_user(user);
				log.setPd_op_type("消费");
				log.setPd_log_amount(BigDecimal.valueOf(-CommUtil
						.null2Double(of.getTotalPrice())));
				log.setPd_log_info("订单" + of.getOrder_id() + "购物减少可用预存款");
				log.setPd_type("可用预存款");
				this.predepositLogService.save(log);
				// 执行库存减少,如果是团购商品，团购库存同步减少
				for (GoodsCart gc : of.getGcs()) {
					Goods goods = gc.getGoods();
					if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
						for (GroupGoods gg : goods.getGroup_goods_list()) {
							if (gg.getGroup().getId().equals(
									goods.getGroup().getId())) {
								gg
										.setGg_count(gg.getGg_count()
												- gc.getCount());
								gg.setGg_def_count(gg.getGg_def_count()
										+ gc.getCount());
								this.groupGoodsService.update(gg);
							}
						}
					}
					List<String> gsps = new ArrayList<String>();
					for (GoodsSpecProperty gsp : gc.getGsps()) {
						gsps.add(gsp.getId().toString());
					}
					String[] gsp_list = new String[gsps.size()];
					gsps.toArray(gsp_list);
					goods.setGoods_salenum(goods.getGoods_salenum()
							+ gc.getCount());
					if (goods.getInventory_type().equals("all")) {
						goods.setGoods_inventory(goods.getGoods_inventory()
								- gc.getCount());
					} else {
						List<HashMap> list = Json.fromJson(ArrayList.class,
								goods.getGoods_inventory_detail());
						for (Map temp : list) {
							String[] temp_ids = CommUtil.null2String(
									temp.get("id")).split("_");
							Arrays.sort(temp_ids);
							Arrays.sort(gsp_list);
							if (Arrays.equals(temp_ids, gsp_list)) {
								temp.put("count", CommUtil.null2Int(temp
										.get("count"))
										- gc.getCount());
							}
						}
						goods.setGoods_inventory_detail(Json.toJson(list,
								JsonFormat.compact()));
					}
					for (GroupGoods gg : goods.getGroup_goods_list()) {
						if (gg.getGroup().getId().equals(
								goods.getGroup().getId())
								&& gg.getGg_count() == 0) {
							goods.setGroup_buy(3);// 标识商品的状态为团购数量已经结束
						}
					}
					this.goodsService.update(goods);
				}
			}
			// 记录支付日志
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("预付款支付");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(of);
			this.orderFormLogService.save(ofl);
			mv.addObject("op_title", "预付款支付成功！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		} else {
			mv = new JModelAndView("weixin/error.html", configService
					.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "可用余额不足，支付失败！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	@Override
	public ModelAndView weixin_order_pay_payafter(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception {
		ModelAndView mv = new JModelAndView("weixin/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		String pay_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("pay_session"));
		if (pay_session1.equals(pay_session)) {
			OrderForm of = getObjById(CommUtil.null2Long(order_id));
			of.setPay_msg(pay_msg);
			Map params = new HashMap();
			params.put("mark", "payafter");
			params.put("store_id", of.getStore().getId());
			List<Payment> payments = this.paymentService
					.query(
							"select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
							params, -1, -1);
			if (payments.size() > 0) {
				of.setPayment(payments.get(0));
				of.setPayTime(new Date());
			}
			of.setOrder_status(16);
			update(of);
			subtract_operations_for_integration(of, request);
			if (this.configService.getSysConfig().isSmsEnbale()) {
				try {
					String phone = of.getStore().getUser().getTelephone();
					this.send_sms(request, of, phone,
							"sms_toseller_pay_notify_ok");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 记录支付日志
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("提交货到付款申请");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(of);
			this.orderFormLogService.save(ofl);
			request.getSession(false).removeAttribute("pay_session");
			mv.addObject("op_title", "货到付款提交成功，等待卖家发货");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		} else {
			mv = new JModelAndView("weixin/error.html", configService
					.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "订单已经支付，禁止重复支付！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	@Override
	public Object order_fee_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String goods_amount, String totalPrice) throws Exception {
		OrderForm obj = getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId().equals(
				SecurityUserHolder.getCurrentUser().getStore().getId())) {
			obj.setGoods_amount(BigDecimal.valueOf(CommUtil
					.null2Double(goods_amount)));
			/*
			 * obj.setShip_price(BigDecimal.valueOf(CommUtil
			 * .null2Double(ship_price)));
			 */
			obj.setOrigin_price(obj.getTotalPrice());// 记录订单调整费用之前的值
			obj.setTotalPrice(BigDecimal.valueOf(CommUtil
					.null2Double(totalPrice)));
			update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("调整订单费用");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
			// 捕捉短信发送异常
			try {
				if (this.configService.getSysConfig().isEmailEnable()) {
					this.send_email(request, obj,
							"email_tobuyer_order_update_fee_notify");
				}
				if (this.configService.getSysConfig().isSmsEnbale()) {
					String phone = obj.getUser().getTelephone();
					if (phone == null)
						phone = obj.getUser().getMobile();
					this.send_sms(request, obj, phone,
							"sms_tobuyer_order_fee_notify");
				}
			} catch (Exception e) {
				ModelAndView mv = new JModelAndView("error.html", configService
						.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "费用调整成功!");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/order.htm?currentPage=" + currentPage);
				return mv;
			}
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@Override
	public String seller_order_outline_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info) throws Exception {
		OrderForm obj = getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId().equals(
				SecurityUserHolder.getCurrentUser().getStore().getId())) {
			obj.setOrder_status(20);
			update(obj);
			// 付款成功，更新商品库存，如果是团购商品，则同步更新团购库存
			/*
			 * for (GoodsCart gc : obj.getGcs()) { Goods goods = gc.getGoods();
			 * if (goods.getGroup() != null && goods.getGroup_buy() == 2) { for
			 * (GroupGoods gg : goods.getGroup_goods_list()) { if
			 * (gg.getGroup().equals(goods.getGroup().getId())) {
			 * gg.setGg_count(gg.getGg_count() - gc.getCount());
			 * gg.setGg_def_count(gg.getGg_def_count() + gc.getCount());
			 * this.groupGoodsService.update(gg); } } } List<String> gsps = new
			 * ArrayList<String>(); for (GoodsSpecProperty gsp : gc.getGsps()) {
			 * gsps.add(gsp.getId().toString()); } String[] gsp_list = new
			 * String[gsps.size()]; gsps.toArray(gsp_list);
			 * goods.setGoods_salenum(goods.getGoods_salenum() + gc.getCount());
			 * String inventory_type = goods.getInventory_type() == null ? "all"
			 * : goods.getInventory_type(); if (inventory_type.equals("all")) {
			 * goods.setGoods_inventory(goods.getGoods_inventory() -
			 * gc.getCount()); } else { List<HashMap> list =
			 * Json.fromJson(ArrayList.class,
			 * goods.getGoods_inventory_detail()); for (Map temp : list) {
			 * String[] temp_ids = CommUtil
			 * .null2String(temp.get("id")).split("_"); Arrays.sort(temp_ids);
			 * Arrays.sort(gsp_list); if (Arrays.equals(temp_ids, gsp_list)) {
			 * temp.put( "count", CommUtil.null2Int(temp.get("count")) -
			 * gc.getCount()); } }
			 * goods.setGoods_inventory_detail(Json.toJson(list,
			 * JsonFormat.compact())); } for (GroupGoods gg :
			 * goods.getGroup_goods_list()) { if
			 * (gg.getGroup().getId().equals(goods.getGroup().getId()) &&
			 * gg.getGg_count() == 0) { goods.setGroup_buy(3);//
			 * 标识商品的状态为团购数量已经结束 } } this.goodsService.update(goods); }
			 */
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("确认线下付款");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			ofl.setState_info(state_info);
			this.orderFormLogService.save(ofl);
			if (this.configService.getSysConfig().isSmsEnbale()) {
				String phone = obj.getUser().getTelephone();
				if (phone == null)
					phone = obj.getUser().getMobile();
				this.send_sms(request, obj, phone,
						"sms_tobuyer_order_outline_pay_ok_notify");
			}
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@Override
	public Object order_cancel_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info, String oper)
			throws Exception {
		OrderForm obj = getObjById(CommUtil.null2Long(id));
		if (obj.getOrder_status() < 30 && obj.getOrder_status() > 0) {
			if (oper != null && oper.equals("buyer")) {
				if (obj.getUser().getId().equals(
						SecurityUserHolder.getCurrentUser().getId())) {
					obj.setOrder_status(0);
					update(obj);
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("取消订单");
					ofl.setLog_user(SecurityUserHolder.getCurrentUser());
					ofl.setOf(obj);
					if (state_info.equals("other")) {
						ofl.setState_info(other_state_info);
					} else {
						ofl.setState_info(state_info);
					}
					this.orderFormLogService.save(ofl);
					// 释放库存
					release_goods_inventory(obj);
					this.integrationLogService
							.return_Integration_Log_By_OrderForm(obj);// 返还农豆
					if (this.configService.getSysConfig().isSmsEnbale()) {
						String phone = obj.getStore().getUser().getTelephone();
						if (phone == null)
							phone = obj.getStore().getUser().getMobile();
						this.send_sms(request, obj, phone,
								"sms_toseller_order_cancel_notify");
					}
				}
			} else {
				if (obj.getStore().getId().equals(
						SecurityUserHolder.getCurrentUser().getStore().getId())) {
					obj.setOrder_status(0);
					update(obj);
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("取消订单");
					ofl.setLog_user(SecurityUserHolder.getCurrentUser());
					ofl.setOf(obj);
					if (state_info.equals("other")) {
						ofl.setState_info(other_state_info);
					} else {
						ofl.setState_info(state_info);
					}
					this.orderFormLogService.save(ofl);
					// 释放库存
					release_goods_inventory(obj);
					this.integrationLogService
							.return_Integration_Log_By_OrderForm(obj);// 返还农豆
					if (this.configService.getSysConfig().isEmailEnable()) {
						send_email(request, obj,
								"email_tobuyer_order_cancel_notify");
					}
					if (this.configService.getSysConfig().isSmsEnbale()) {
						String phone = obj.getUser().getTelephone();
						if (phone == null)
							phone = obj.getUser().getMobile();
						this.send_sms(request, obj, phone,
								"sms_tobuyer_order_cancel_notify");
					}
				}
			}
			return "redirect:order.htm?currentPage=" + currentPage;
		} else {
			ModelAndView mv = new JModelAndView("error.html", configService
					.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "该订单无法取消！");
			if (oper != null && oper.equals("buyer")) {
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order.htm");
			}
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
			return mv;
		}
	}

	// 取消订单后，释放库存
	@Override
	@SuppressWarnings("unchecked")
	public void release_goods_inventory(OrderForm order) {
		// 取消订单后，释放商品库存，如果是团购商品，则释放团购库存
		for (GoodsCart gc : order.getGcs()) {
			Goods goods = gc.getGoods();
			// if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
			// for (GroupGoods gg : goods.getGroup_goods_list()) {
			// if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
			// gg.setGg_def_count(gg.getGg_def_count() - gc.getCount());
			// gg.setGg_count(gg.getGg_count() + gc.getCount());
			// this.groupGoodsService.update(gg);
			// }
			// }
			// }
			// List<String> gsps = new ArrayList<String>();
			// for (GoodsSpecProperty gsp : gc.getGsps()) {
			// gsps.add(gsp.getId().toString());
			// }
			// String[] gsp_list = new String[gsps.size()];
			// gsps.toArray(gsp_list);
			goods.setGoods_salenum(goods.getGoods_salenum() - gc.getCount());
			String inventory_type = goods.getInventory_type() == null ? "all"
					: goods.getInventory_type();
			if (inventory_type.equals("all")) {
				goods.setGoods_inventory(goods.getGoods_inventory()
						+ gc.getCount());
			}
			// else {
			// List<HashMap> list = Json
			// .fromJson(ArrayList.class, CommUtil.null2String(goods
			// .getGoods_inventory_detail()));
			// for (Map temp : list) {
			// String[] temp_ids = CommUtil.null2String(temp.get("id"))
			// .split("_");
			// Arrays.sort(temp_ids);
			// Arrays.sort(gsp_list);
			// if (Arrays.equals(temp_ids, gsp_list)) {
			// temp.put("count", CommUtil.null2Int(temp.get("count"))
			// + gc.getCount());
			// }
			// }
			// goods.setGoods_inventory_detail(Json.toJson(list,
			// JsonFormat.compact()));
			// }
			// for (GroupGoods gg : goods.getGroup_goods_list()) {
			// if (gg.getGroup().getId().equals(goods.getGroup().getId())
			// && gg.getGg_count() == 0) {
			// goods.setGroup_buy(3);// 标识商品的状态为团购数量已经结束
			// }
			// }
			this.goodsService.update(goods);
			// 更新lucene索引
			LuceneUtil.instance().ruLuceneIndex(goods, "u");
		}
		// 返回赊销
		String query = "from Charge where user.id=:uid and store.id=:sid";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", order.getUser().getId());
		params.put("sid", order.getStore().getId());
		List<Charge> list = this.chargeDao.query(query, params, -1, -1);
		try {
			if (list.size() == 0) {
			} else {
				Charge charge = list.get(0);
				if ((new BigDecimal(CommUtil.null2Long(order.getCharge_Num())))
						.compareTo(charge.getUsedPayNum()) == 1) {
					System.out.println("买家订单赊销金额大于赊销账单金额！");
				} else {
					System.out.println("扣减前赊销金额" + charge.getUsedPayNum());
					System.out.println("扣减赊销金额" + order.getCharge_Num());
					BigDecimal usedPayNum = charge.getUsedPayNum().subtract(
							order.getCharge_Num());
					charge.setUsedPayNum(usedPayNum);
					System.out.println("扣减后赊销金额" + charge.getUsedPayNum());
					this.chargeDao.update(charge);
				}
			}
		} catch (Exception e) {
			System.out.println("买家赊销信息为空或异常！");
		} finally {
			order.setCharge_Num(new BigDecimal(0));
			this.orderFormDao.update(order);
		}
	}

	@Override
	public ModelAndView seller_order_return_confirm(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("error.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		OrderForm obj = getObjById(CommUtil.null2Long(id));
		if (obj.getOrder_status() == 46) {
			if (obj.getStore().getId().equals(
					SecurityUserHolder.getCurrentUser().getStore().getId())) {
				obj.setOrder_status(47);
				// 添加保存退货表单
				GoodsReturn gr = new GoodsReturn();
				gr.setReturn_id("R" + obj.getId());
				gr.setOf(obj);
				gr.setUser(SecurityUserHolder.getCurrentUser());
				gr.setReturn_info(obj.getReturn_content());
				gr.setAddTime(new Date());
				List<GoodsCart> gcs = obj.getGcs();
				List<GoodsReturnItem> grts = new ArrayList<GoodsReturnItem>();
				for (GoodsCart goodsCart : gcs) {
					GoodsReturnItem gri = new GoodsReturnItem();
					gri.setAddTime(new Date());
					gri.setGoods(goodsCart.getGoods());
					gri.setGr(gr);
					List<GoodsSpecProperty> gslist = new ArrayList<GoodsSpecProperty>(
							goodsCart.getGsps());
					gri.setGsps(gslist);
					gri.setSpec_info(goodsCart.getSpec_info());
					gri.setCount(goodsCart.getCount());
					grts.add(gri);
				}
				gr.setItems(grts);
				update(obj);
				// 释放库存
				release_goods_inventory(obj);
				this.integrationLogService
						.return_Integration_Log_By_OrderForm(obj);
				this.goodsReturnService.save(gr);
				mv = new JModelAndView("success.html", configService
						.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您已成功确认退货");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/order.htm");
			} else {
				mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/order.htm");
			}
		} else {
			mv.addObject("op_title", "该订单无法退货！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@Override
	public Object order_shipping_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info) throws Exception {
		OrderForm obj = getObjById(CommUtil.null2Long(id));
		/**
		 * if(obj.getOrder_status() == 16) {//货到付款 if(
		 * wsp.check_goods_inventory(obj) ){ wsp.update_goods_inventory(obj);
		 * }else{ //库存不够 ModelAndView mv = new JModelAndView("error.html",
		 * configService.getSysConfig(), this.userConfigService.getUserConfig(),
		 * 1, request, response); mv.addObject("op_title", "库存不足无法发货!");
		 * mv.addObject("url", CommUtil.getURL(request) +
		 * "/seller/order.htm?currentPage=" + currentPage); return mv; } }
		 **/
		if (obj.getStore().getId().equals(
				SecurityUserHolder.getCurrentUser().getStore().getId())) {
			obj.setOrder_status(30);
			obj.setShipTime(new Date());
			update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("确认发货");
			ofl.setState_info(state_info);
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
			try {
				if (this.configService.getSysConfig().isSmsEnbale()) {
					String phone = obj.getUser().getTelephone();
					if (phone == null)
						phone = obj.getUser().getMobile();
					this.send_sms(request, obj, phone,
							"sms_tobuyer_order_ship_notify");
				}
			} catch (Exception e) {
				ModelAndView mv = new JModelAndView("success.html",
						configService.getSysConfig(), this.userConfigService
								.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "确认发货成功!");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/order.htm?currentPage=" + currentPage);
				return mv;
			}
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@Override
	public ModelAndView order_evaluate_save(HttpServletRequest request,
			HttpServletResponse response, String id, String evaluate_info,
			String evaluate_seller_val) {
		OrderForm obj = getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId().equals(
				SecurityUserHolder.getCurrentUser().getStore().getId())) {
			if (obj.getOrder_status() == 50) {
				obj.setOrder_status(60);
				obj.setFinishTime(new Date());
				update(obj);
				java.util.Enumeration enum1 = request.getParameterNames();
				List<Map> maps = new ArrayList<Map>();
				while (enum1.hasMoreElements()) {
					String paramName = (String) enum1.nextElement();
					if (paramName.indexOf("evaluate_seller_val") >= 0) {
						String value = request.getParameter(paramName);
						Evaluate eva = this.evaluateService.getObjById(CommUtil
								.null2Long(paramName.substring(19)));
						eva.setEvaluate_seller_val(CommUtil.null2Int(request
								.getParameter(paramName)));
						eva.setEvaluate_seller_user(SecurityUserHolder
								.getCurrentUser());
						eva.setEvaluate_seller_info(request
								.getParameter("evaluate_info"
										+ eva.getId().toString()));
						eva.setEvaluate_seller_time(new Date());
						this.evaluateService.update(eva);
						User user = obj.getUser();
						user.setUser_credit(user.getUser_credit()
								+ eva.getEvaluate_seller_val());
						// 到此为止订单完成，增加买家积分
						if (this.configService.getSysConfig().isIntegral()) {
							int integral = 0;
							if (this.configService.getSysConfig()
									.getConsumptionRatio() > 0) {
								integral = CommUtil.null2Int(CommUtil.div(obj
										.getTotalPrice(), this.configService
										.getSysConfig().getConsumptionRatio()));
							}
							integral = integral > this.configService
									.getSysConfig().getEveryIndentLimit() ? this.configService
									.getSysConfig().getEveryIndentLimit()
									: integral;
							user.setIntegral(user.getIntegral() + integral);
							this.userService.update(user);
							IntegralLog log = new IntegralLog();
							log.setAddTime(new Date());
							log.setContent("订单" + obj.getOrder_id() + "完成增加"
									+ integral + "分");
							log.setIntegral(integral);
							log.setIntegral_user(user);
							log.setType("login");
							this.integralLogService.save(log);
						}
					}
				}
			}
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("评价订单");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
		}
		ModelAndView mv = new JModelAndView("success.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		mv.addObject("op_title", "订单评价成功！");
		mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		return mv;
	}

	@Override
	public Object weixin_order_cancel_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info) throws Exception {
		OrderForm obj = getObjById(CommUtil.null2Long(id));
		if (obj.getOrder_status() == 10 || obj.getOrder_status() == 15) {
			if (obj.getUser().getId().equals(
					SecurityUserHolder.getCurrentUser().getId())) {
				obj.setOrder_status(0);
				update(obj);
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("取消订单");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(obj);
				if (state_info.equals("other")) {
					ofl.setState_info(other_state_info);
				} else {
					ofl.setState_info(state_info);
				}
				this.orderFormLogService.save(ofl);
				// 取消订单，释放库存
				release_goods_inventory(obj);
				if (this.configService.getSysConfig().isEmailEnable()) {
					this.send_email(request, obj,
							"email_toseller_order_cancel_notify");
				}
				if (this.configService.getSysConfig().isSmsEnbale()) {
					String phone = obj.getStore().getUser().getTelephone();
					if (phone == null)
						phone = obj.getStore().getUser().getMobile();
					this.send_sms(request, obj, phone,
							"sms_toseller_order_cancel_notify");
				}
			}
		} else {
			ModelAndView mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(), this.userConfigService
							.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "该订单无法取消！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
			return mv;
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@Override
	public String weixin_order_cofirm_save(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (request.getSession(false) == null)
			return "redirect:/weixin/login.htm";
		Object id = request.getSession(false).getAttribute("ofid");
		request.getSession(false).removeAttribute("ofid");
		OrderForm obj = null;
		if (id != null)
			obj = getObjById(CommUtil.null2Long(id));
		if (obj.getUser().getId().equals(
				SecurityUserHolder.getCurrentUser().getId())) {
			obj.setOrder_status(40);
			boolean ret = update(obj);
			if (ret) {// 订单状态更新成功，更新相关信息
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("确认收货");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(obj);
				this.orderFormLogService.save(ofl);
			}
		}
		add_operations_for_integration(obj, request);
		String url = "redirect:/weixin/buyer/order.htm";
		return url;
	}

	@Override
	public String order_cofirm_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage)
			throws Exception {
		OrderForm obj = null;
		if (id != null)
			obj = getObjById(CommUtil.null2Long(id));
		if (obj.getUser().getId().equals(
				SecurityUserHolder.getCurrentUser().getId())) {
			obj.setOrder_status(40);
			boolean ret = update(obj);
			if (ret) {// 订单状态更新成功，更新相关信息
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("确认收货");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(obj);
				this.orderFormLogService.save(ofl);
			}
		}
		add_operations_for_integration(obj, request);
		String url = "redirect:/buyer/order.htm?currentPage=" + currentPage;
		return url;
	}

	@Override
	public ModelAndView weixin_order_evaluate_save(HttpServletRequest request,
			HttpServletResponse response, String id) throws Exception {
		OrderForm obj = getObjById(CommUtil.null2Long(id));
		if (obj.getUser().getId().equals(
				SecurityUserHolder.getCurrentUser().getId())) {
			if (obj.getOrder_status() == 40) {
				obj.setOrder_status(50);
				update(obj);
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("评价订单");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(obj);
				this.orderFormLogService.save(ofl);
				for (GoodsCart gc : obj.getGcs()) {
					Evaluate eva = new Evaluate();
					eva.setAddTime(new Date());
					eva.setEvaluate_goods(gc.getGoods());
					eva.setEvaluate_info(request.getParameter("evaluate_info_"
							+ gc.getGoods().getId()));
					eva.setEvaluate_buyer_val(CommUtil.null2Int(request
							.getParameter("evaluate_buyer_val" + gc.getId())));
					eva.setDescription_evaluate(BigDecimal.valueOf(CommUtil
							.null2Double(request
									.getParameter("description_evaluate"
											+ gc.getId()))));
					eva.setService_evaluate(BigDecimal.valueOf(CommUtil
							.null2Double(request
									.getParameter("service_evaluate"
											+ gc.getId()))));
					eva.setShip_evaluate(BigDecimal.valueOf(CommUtil
							.null2Double(request.getParameter("ship_evaluate"
									+ gc.getId()))));
					eva.setEvaluate_type("goods");
					eva.setEvaluate_user(SecurityUserHolder.getCurrentUser());
					eva.setOf(obj);
					eva.setGoods_spec(gc.getSpec_info());
					this.evaluateService.save(eva);
					Map params = new HashMap();
					params.put("store_id", obj.getStore().getId());
					List<Evaluate> evas = this.evaluateService
							.query(
									"select obj from Evaluate obj where obj.of.store.id=:store_id",
									params, -1, -1);
					double store_evaluate1 = 0;
					double store_evaluate1_total = 0;
					double description_evaluate = 0;
					double description_evaluate_total = 0;
					double service_evaluate = 0;
					double service_evaluate_total = 0;
					double ship_evaluate = 0;
					double ship_evaluate_total = 0;
					DecimalFormat df = new DecimalFormat("0.0");
					for (Evaluate eva1 : evas) {
						store_evaluate1_total = store_evaluate1_total
								+ eva1.getEvaluate_buyer_val();
						description_evaluate_total = description_evaluate_total
								+ CommUtil.null2Double(eva1
										.getDescription_evaluate());
						service_evaluate_total = service_evaluate_total
								+ CommUtil.null2Double(eva1
										.getService_evaluate());
						ship_evaluate_total = ship_evaluate_total
								+ CommUtil.null2Double(eva1.getShip_evaluate());
					}
					store_evaluate1 = CommUtil.null2Double(df
							.format(store_evaluate1_total / evas.size()));
					description_evaluate = CommUtil.null2Double(df
							.format(description_evaluate_total / evas.size()));
					service_evaluate = CommUtil.null2Double(df
							.format(service_evaluate_total / evas.size()));
					ship_evaluate = CommUtil.null2Double(df
							.format(ship_evaluate_total / evas.size()));
					Store store = obj.getStore();
					store.setStore_credit(store.getStore_credit()
							+ eva.getEvaluate_buyer_val());
					this.storeService.update(store);
					params.clear();
					params.put("store_id", store.getId());
					List<StorePoint> sps = this.storePointService
							.query(
									"select obj from StorePoint obj where obj.store.id=:store_id",
									params, -1, -1);
					StorePoint point = null;
					if (sps.size() > 0) {
						point = sps.get(0);
					} else {
						point = new StorePoint();
					}
					point.setAddTime(new Date());
					point.setStore(store);
					point.setDescription_evaluate(BigDecimal
							.valueOf(description_evaluate));
					point.setService_evaluate(BigDecimal
							.valueOf(service_evaluate));
					point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate));
					point.setStore_evaluate1(BigDecimal
							.valueOf(store_evaluate1));
					if (sps.size() > 0) {
						this.storePointService.update(point);
					} else {
						this.storePointService.save(point);
					}
					// 增加用户积分
					// User user = obj.getUser();
					// user.setIntegral(user.getIntegral()
					// + this.configService.getSysConfig()
					// .getIndentComment());
					// this.userService.update(user);
				}
			}
			// if (this.configService.getSysConfig().isEmailEnable()) {
			// this.send_email(request, obj,
			// "email_toseller_evaluate_ok_notify");
			// }
		}
		ModelAndView mv = new JModelAndView("weixin/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "订单评价成功！");
		mv.addObject("url", CommUtil.getURL(request)
				+ "/weixin/buyer/order.htm");
		return mv;
	}

	/**
	 * 下订单增加农豆，用户订单完成之后，获取农豆
	 */
	private void add_operations_for_integration(OrderForm of,
			HttpServletRequest request) {
		XConf xconf = this.xconfService
				.queryByXconfkey(ConstantUtils._INTEGRATION_FROM_ORDER);
		User user = of.getUser();
		List<Integration> integrationList = integrationService
				.queryByUser(user);
		Integration integration = integrationList.get(0);
		integrationChildService.saveOrUpdateByXConfAndOrderForm(xconf, of);
		Integer change_integrationStore = integrationChildService
				.count_integration_by_real_price(xconf, of);
		integration.setIntegrals(integration.getIntegrals()
				+ change_integrationStore);
		integrationService.update(integration);
		log_operations_for_integration(of, change_integrationStore,
				integration, null);
		if (this.configService.getSysConfig().isSmsEnbale()) {
			SendMessageUtil sendmessage = new SendMessageUtil();
			try {
				String phone = of.getUser().getTelephone();
				sendmessage.sendHttpPost(phone, "尊敬的"
						+ of.getUser().getUserName()
						+ "您好，您有一笔订单（订单编号:"
						+ of.getOrder_id()
						+ "），已获取店铺农豆"
						+ integrationChildService
								.count_integration_by_real_price(xconf, of)
						+ "。");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 生成订单，农豆抵现，扣减农豆
	 */
	private void subtract_operations_for_integration(OrderForm of,
			HttpServletRequest request) {
		if (CommUtil.isNotNull(of.getStore().getWeixin_token())) {// 判断店铺是否开通农豆折合成金额
			if (of.getIntegrationPlatform() > 0 || of.getIntegrationStore() > 0) {
				integrationChildService.subtractByOrderForm(of);
				User user = of.getUser();
				List<Integration> integrationList = integrationService
						.queryByUser(user);
				Integration integration = integrationList.get(0);
				Integer overdue_integrals_order = 0;// 即将过期的店铺农豆
				Integer integration_new = integration.getIntegrals();
				if (null != of.getIntegrationStore()) {
					if (integration_new >= 0) {
						integration_new = integration_new
								- of.getIntegrationStore();
						if (CommUtil.isNotNull(integration
								.getOverdue_integrals())) {// 是否含过期的店铺农豆
							Integer overdue_integrals_new = integration
									.getOverdue_integrals()
									- of.getIntegrationStore();
							if (overdue_integrals_new >= 0) {// 判断店铺农豆是否够
								overdue_integrals_order = of
										.getIntegrationStore();
								integration
										.setOverdue_integrals(overdue_integrals_new);
							} else {
								overdue_integrals_order = integration
										.getOverdue_integrals();
								integration.setOverdue_integrals(0);
							}
						}
					} else {
						System.out.println(ConstantUtils
								._getIntegrationServiceImplFunctions(0, 2)
								+ "店铺农豆<0");
					}
				}
				integrationService.updateByOrderForm(of, integration,integration_new);
				log_operations_for_integration(of, null, integration,
						overdue_integrals_order);
				if (this.configService.getSysConfig().isSmsEnbale()) {
					try {
						String phone = of.getUser().getTelephone();
						this.send_sms(request, of, phone,
								"sms_toseller_subtract_integration");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			System.out.println(ConstantUtils
					._getIntegrationServiceImplFunctions(0, 2)
					+ "店铺未设置农豆折合成金额的比例");
		}
	}

	/**
	 * 农豆日志操作
	 */
	private void log_operations_for_integration(OrderForm of,
			Integer change_integrationStore, Integration integration,
			Integer overdue_integrals_order) {
		Integration_Log integrationLog = new Integration_Log();
		if (null != change_integrationStore) {
			integrationLog.setIntegrals(0);
			try {
				integrationLog.setIntegrals_store(change_integrationStore);
			} catch (Exception e) {
				e.printStackTrace();
			}
			integrationLog.setType(ConstantUtils._INTEGRATION_LOG_TYPE[0]);
		} else {
			integrationLog.setIntegrals(of.getIntegrationPlatform());
			integrationLog.setIntegrals_store(of.getIntegrationStore());
			integrationLog.setType(ConstantUtils._INTEGRATION_LOG_TYPE[1]);
		}
		if (null != overdue_integrals_order) {
			integrationLog.setOverdue_integrals_order(overdue_integrals_order);// 设置店铺过期农豆
		}
		integrationLog.setGettype(ConstantUtils._INTEGRATION_LOG_GETTYPE[0]);
		integrationLog.setInteg(integration);
		integrationLog.setOrderForm(of);
		integrationLogService.saveByIntegrationLogConstructor(integrationLog);
	}

	/**
	 * 设置前台页面店铺农豆和平台农豆
	 */
	public ModelAndView setIntegrationPlatformAndIntegrationStore(OrderForm of,
			ModelAndView mv) {
		User user = of.getUser();
		int integrationPlatform = 0;
		List<Integration_Child> integrationPlatformList = integrationChildService
				.queryByTypeStoreOrUser(ConstantUtils._INTEGRATION_TYPE[0],
						user, null);
		if (null != integrationPlatformList
				&& integrationPlatformList.size() > 0) {
			integrationPlatform = integrationPlatformList.get(0).getIntegrals() != null ? integrationPlatformList
					.get(0).getIntegrals()
					: 0;
		}
		int integrationStore = 0;
		String integration_to_money_store = "0";// 店铺农豆兑换成金额的比例
		if (CommUtil.isNotNull(of.getStore().getWeixin_token())) {// 判断店铺是否开通农豆折合成金额
			integration_to_money_store = of.getStore().getWeixin_token();
			try {
				double rate_store = Double.valueOf(of.getStore()
						.getWeixin_token());
				if (rate_store > 0) {
					List<Integration_Child> integrationStoreList = integrationChildService
							.queryByTypeStoreOrUser(
									ConstantUtils._INTEGRATION_TYPE[1], user,
									of.getStore());
					if (null != integrationStoreList
							&& integrationStoreList.size() > 0) {
						integrationStore = integrationStoreList.get(0)
								.getIntegrals() != null ? integrationStoreList
								.get(0).getIntegrals() : 0;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println(ConstantUtils
					._getIntegrationServiceImplFunctions(0, 1)
					+ "店铺未设置农豆折合成金额的比例");
		}
		mv.addObject("integrationPlatform", integrationPlatform);// 总数
		mv.addObject("integrationStore", integrationStore);// 总数
		mv.addObject("integrationTotal", total_price_to_integration(of
				.getTotalPrice()));// 总农豆可以抵用的订单总金额
		XConf xconf_integration_rate_for_money = xconfService
				.queryByXconfkey(ConstantUtils._INTEGRATION_RATE_FOR_MONEY);
		mv.addObject("integration_to_money", xconf_integration_rate_for_money
				.getXconfvalue());// 农豆转金额的比例
		mv.addObject("integration_to_money_store", integration_to_money_store);// 农豆转金额的比例
		return mv;
	}

	private OrderForm setIntegration_for_OrderForm(OrderForm of,
			String integrationPlatform, String integrationStore) {
		try {
			if (CommUtil.isNotNull(integrationPlatform)) {
				of.setIntegrationPlatform(Integer.valueOf(integrationPlatform));
			} else {
				of.setIntegrationPlatform(0);
			}
			if (CommUtil.isNotNull(integrationStore)) {
				of.setIntegrationStore(Integer.valueOf(integrationStore));
			} else {
				of.setIntegrationStore(0);
			}
			of.setIntegration_price(count_integration_price(of));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return of;
	}

	/**
	 * 通过订单，计算农豆折合成金额
	 */
	private BigDecimal count_integration_price(OrderForm of) {
		XConf xconf_integration_rate_for_money = xconfService
				.queryByXconfkey(ConstantUtils._INTEGRATION_RATE_FOR_MONEY);
		BigDecimal integration_price = BigDecimal.ZERO;
		try {
			if (null != of.getIntegrationStore()) {
				// integration_price = integration_price.add(new BigDecimal(of
				// .getIntegrationStore()
				// * Double.valueOf(xconf_integration_rate_for_money
				// .getXconfvalue())));
				if (CommUtil.isNotNull(of.getStore().getWeixin_token())) {// 判断店铺是否开通农豆折合成金额
					integration_price = integration_price.add(new BigDecimal(of
							.getIntegrationStore()).multiply(new BigDecimal(of
							.getStore().getWeixin_token())));
				}
			}
			if (null != of.getIntegrationPlatform()) {
				integration_price = integration_price.add(new BigDecimal(of
						.getIntegrationPlatform()
						* Double.valueOf(xconf_integration_rate_for_money
								.getXconfvalue())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return integration_price;
	}

	/**
	 * 总金额转换成农豆
	 */
	private BigDecimal total_price_to_integration(BigDecimal total_price) {
		XConf xconf_integration_rate_for_order = xconfService
				.queryByXconfkey(ConstantUtils._INTEGRATION_RATE_FOR_ORDER);
		BigDecimal price = BigDecimal.ZERO;
		try {
			price = total_price.multiply(new BigDecimal(
					xconf_integration_rate_for_order.getXconfvalue()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return price;
	}

	/**
	 * 金额转换成农豆
	 */
	private Integer price_to_integration(BigDecimal price) {
		XConf xconf_integration_rate_for_money = xconfService
				.queryByXconfkey(ConstantUtils._INTEGRATION_RATE_FOR_MONEY);
		Integer result = 0;
		try {
			result = price.divide(
					new BigDecimal(xconf_integration_rate_for_money
							.getXconfvalue())).setScale(0,
					BigDecimal.ROUND_DOWN).intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public ModelAndView pc_goods_cart3(HttpServletRequest request,
			HttpServletResponse response, String cart_session, String store_id,
			String addr_id, String coupon_id, String chargeNum) {
		ModelAndView mv = new JModelAndView("goods_cart3.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		Map payment_map = new HashMap();
		List<Payment> store_Payments = new ArrayList<Payment>();
		if (this.configService.getSysConfig().getConfig_payment_type() == 1) {
			payment_map.put("install", true);
			payment_map.put("type", "admin");
			store_Payments = this.paymentService
					.query(
							"select obj from Payment obj where obj.install=:install and obj.type=:type",
							payment_map, -1, -1);
		} else {
			payment_map.put("store_id", CommUtil.null2Long(store_id));
			payment_map.put("install", true);
			store_Payments = this.paymentService
					.query(
							"select obj from Payment obj where obj.store.id=:store_id and obj.install=:install",
							payment_map, -1, -1);
		}
		if (store_Payments.size() > 0) {
			String cart_session1 = (String) request.getSession(false)
					.getAttribute("cart_session");
			List<StoreCart> cart = this.cart_calc(request);
			if (cart != null) {
				if (CommUtil.null2String(cart_session1).equals(cart_session)) {// 禁止重复提交订单信息
					thirdBinding(mv);
					request.getSession(false).removeAttribute("cart_session");// 删除订单提交唯一标示，用户不能进行第二次订单提交
					WebForm wf = new WebForm();
					OrderForm of = wf.toPo(request, OrderForm.class);
					Long storeId = Long.parseLong(store_id);
					Long userId = SecurityUserHolder.getCurrentUser().getId();
					String query = "select obj from Charge obj where store.id=:store_id and user.id=:user_id";
					Map<String, Long> chargeMap = new HashMap<String, Long>();
					chargeMap.put("store_id", storeId);
					chargeMap.put("user_id", userId);
					List<Charge> chargeList = this.chargeService.query(query,
							chargeMap, -1, -1);
					double ret = 0.0;
					if (chargeList != null && chargeList.size() == 1) {
						Charge charge = chargeList.get(0);
						BigDecimal b1 = charge.getPaymentNum();
						BigDecimal b2 = charge.getUsedPayNum();
						ret = b1.subtract(b2).doubleValue();
					}
					mv.addObject("ret", String.valueOf(ret));
					Cookie[] cookies = request.getCookies();
					if (cookies != null) {
						for (Cookie cookie : cookies) {
							if (cookie.getName().equals("cart_session_id")) {
								cookie.setDomain(CommUtil
										.generic_domain(request));
								cookie.setValue("");
								cookie.setMaxAge(0);
								response.addCookie(cookie);
							}
						}
					}
					add_order(of, addr_id, store_id, coupon_id, chargeNum, cart);
					if (wsp.check_goods_inventory(of)) {
						wsp.update_goods_inventory(of);
					} else {
						// 库存不够
						mv = new JModelAndView("error.html", configService
								.getSysConfig(), this.userConfigService
								.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "库存不足无法提交表单!");
						String store_id2 = CommUtil.null2String(request
								.getSession(false).getAttribute("store_id"));
						mv.addObject("url", CommUtil.getURL(request)
								+ "/goods_cart1.htm");
						return mv;
					}
					mv.addObject("of", of);
					mv.addObject("paymentTools", paymentTools);
					mv = setIntegrationPlatformAndIntegrationStore(of, mv);
				} else {
					mv = new JModelAndView("error.html", configService
							.getSysConfig(), this.userConfigService
							.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "订单已经失效");
					String store_id2 = CommUtil.null2String(request.getSession(
							false).getAttribute("store_id"));
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/order.htm");
				}
			} else {
				mv = new JModelAndView("error.html", configService
						.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单信息错误");
				String store_id2 = CommUtil.null2String(request.getSession(
						false).getAttribute("store_id"));
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "没有开通支付方式，不能付款");
			String store_id2 = CommUtil.null2String(request.getSession(false)
					.getAttribute("store_id"));
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@Override
	public ModelAndView order_pay_outline(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception {
		ModelAndView mv = new JModelAndView("success.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		String pay_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("pay_session"));
		if (pay_session1.equals(pay_session)) {
			OrderForm of = getObjById(CommUtil.null2Long(order_id));
			of.setPay_msg(pay_msg);
			Map params = new HashMap();
			params.put("mark", "outline");
			params.put("store_id", of.getStore().getId());
			List<Payment> payments = this.paymentService
					.query(
							"select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
							params, -1, -1);
			if (payments.size() > 0) {
				of.setPayment(payments.get(0));
				of.setPayTime(new Date());
			}
			of.setOrder_status(15);
			update(of);
			if (this.configService.getSysConfig().isSmsEnbale()) {
				this.send_sms(request, of, of.getStore().getUser().getMobile(),
						"sms_toseller_outline_pay_ok_notify");
			}
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.send_email(request, of,
						of.getStore().getUser().getEmail(),
						"email_toseller_outline_pay_ok_notify");
			}
			// 记录支付日志
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("提交线下支付申请");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(of);
			this.orderFormLogService.save(ofl);
			request.getSession(false).removeAttribute("pay_session");
			mv.addObject("op_title", "线下支付提交成功，等待卖家审核！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单已经支付，禁止重复支付！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@Override
	public ModelAndView order_pay_payafter(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception {
		ModelAndView mv = new JModelAndView("success.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		String pay_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("pay_session"));
		if (pay_session1.equals(pay_session)) {
			OrderForm of = getObjById(CommUtil.null2Long(order_id));
			of.setPay_msg(pay_msg);
			Map params = new HashMap();
			params.put("mark", "payafter");
			params.put("store_id", of.getStore().getId());
			List<Payment> payments = this.paymentService
					.query(
							"select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
							params, -1, -1);
			if (payments.size() > 0) {
				of.setPayment(payments.get(0));
				of.setPayTime(new Date());
			}
			of.setOrder_status(16);
			update(of);
			subtract_operations_for_integration(of, request);
			if (this.configService.getSysConfig().isSmsEnbale()) {
				try {
					String phone = of.getStore().getUser().getTelephone();
					this.send_sms(request, of, phone,
							"sms_toseller_pay_notify_ok");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			// 记录支付日志
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("提交货到付款申请");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(of);
			this.orderFormLogService.save(ofl);
			request.getSession(false).removeAttribute("pay_session");
			mv.addObject("op_title", "货到付款提交成功，等待卖家发货");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单已经支付，禁止重复支付");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}
}
