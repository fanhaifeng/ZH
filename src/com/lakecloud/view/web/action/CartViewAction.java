package com.lakecloud.view.web.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
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
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.GroupGoods;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.OrderFormLog;
import com.lakecloud.foundation.domain.Payment;
import com.lakecloud.foundation.domain.PredepositLog;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreCart;
import com.lakecloud.foundation.domain.Template;
import com.lakecloud.foundation.domain.ThirdBinding;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IAddressService;
import com.lakecloud.foundation.service.IAreaService;
import com.lakecloud.foundation.service.IChargeService;
import com.lakecloud.foundation.service.ICouponInfoService;
import com.lakecloud.foundation.service.IGoodsCartService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGoodsSpecPropertyService;
import com.lakecloud.foundation.service.IGroupGoodsService;
import com.lakecloud.foundation.service.IOrderFormLogService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IPaymentService;
import com.lakecloud.foundation.service.IPredepositLogService;
import com.lakecloud.foundation.service.IRefundService;
import com.lakecloud.foundation.service.IStoreCartService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.ITemplateService;
import com.lakecloud.foundation.service.IThirdBindingService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.manage.admin.tools.MsgTools;
import com.lakecloud.manage.admin.tools.PaymentTools;
import com.lakecloud.manage.seller.Tools.TransportTools;
import com.lakecloud.pay.tools.PayTools;
import com.lakecloud.view.web.tools.GoodsViewTools;
import com.lakecloud.weixin.store.view.action.WeixinStorePayViewAction;

/**
 * @info 购物控制器,包括购物车所有操作及订单相关操作
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Controller
public class CartViewAction {
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
	private IAreaService areaService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IOrderFormService orderFormService;
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
	private TransportTools transportTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IChargeService chargeService;
	@Autowired
	private IRefundService refundService;
	@Autowired
	private WeixinStorePayViewAction wsp;
	@Autowired
	private IThirdBindingService thirdBindingService;

	/**
	 * 动态加载商城前端页面顶部的购物车信息 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/cart_menu_detail.htm")
	public ModelAndView cart_menu_detail(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("cart_menu_detail.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<StoreCart> cart = this.orderFormService.cart_calc(request);
		List<GoodsCart> list = new ArrayList<GoodsCart>();
		if (cart != null) {
			for (StoreCart sc : cart) {
				if (sc != null)
					list.addAll(sc.getGcs());
			}
		}
		float total_price = 0;
		for (GoodsCart gc : list) {
			Goods goods = this.goodsService.getObjById(gc.getGoods().getId());
			if (CommUtil.null2String(gc.getCart_type()).equals("combin")) {
				total_price = CommUtil.null2Float(goods.getCombin_price());
			} else {
				total_price = CommUtil.null2Float(CommUtil.mul(gc.getCount(),
						goods.getGoods_current_price())) + total_price;
			}
		}
		mv.addObject("total_price", total_price);
		mv.addObject("cart", list);
		return mv;
	}

	/**
	 * 根据商品规格加载商品的数量、价格
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param gsp
	 */
	@RequestMapping("/add_goods_cart.htm")
	public void add_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String count,
			String price, String gsp, String buy_type) {
		String ret=this.goodsCartService.add_goods_cart(request, response, id, count, price, gsp, buy_type);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	@RequestMapping("/remove_goods_cart.htm")
	public void remove_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String store_id) {
		String ret=this.goodsCartService.remove_goods_cart(request, response, id, store_id);
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
	@RequestMapping("/goods_count_adjust.htm")
	public void goods_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String cart_id, String store_id,
			String count) {
		String ret = this.storeCartService.goods_count_adjust(request,
				response, cart_id, store_id, count);
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
	 * 确认购物车商品
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_cart1.htm")
	public ModelAndView goods_cart1(HttpServletRequest request,
			HttpServletResponse response) {
		return this.orderFormService.goods_cart1(request, response);
	}

	private List<Goods> randomZtcGoods(List<Goods> goods) {
		Random random = new Random();
		int random_num = 0;
		int num = 0;
		if (goods.size() - 8 > 0) {
			num = goods.size() - 8;
			random_num = random.nextInt(num);
		}
		Map ztc_map = new HashMap();
		ztc_map.put("ztc_status", 3);
		ztc_map.put("now_date", new Date());
		ztc_map.put("ztc_gold", 0);
		List<Goods> ztc_goods = this.goodsService
				.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
						+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc",
						ztc_map, random_num, 8);
		Collections.shuffle(ztc_goods);
		return ztc_goods;
	}

	/**
	 * 购物确认,填写用户地址，配送方式，支付方式等
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "确认购物车填写地址", value = "/goods_cart2.htm*", rtype = "buyer", rname = "购物流程2", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/goods_cart2.htm")
	public ModelAndView goods_cart2(HttpServletRequest request,
			HttpServletResponse response, String store_id) {
		ModelAndView mv = new JModelAndView("goods_cart2.html",
				configService.getSysConfig(),this.
				userConfigService.getUserConfig(), 1, request, response);
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
					.query("select obj from Address obj where obj.user.id=:user_id and obj.deleteStatus!=:status order by obj.addTime desc",
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
					.query("select obj from CouponInfo obj where obj.coupon.coupon_order_amount<=:coupon_order_amount and obj.status=:status and obj.user.id=:user_id and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
							params, -1, -1);
			mv.addObject("couponinfos", couponinfos);
			mv.addObject("sc", sc);
			mv.addObject("cart_session", cart_session);
			mv.addObject("store_id", store_id);
			mv.addObject("transportTools", transportTools);
			mv.addObject("goodsViewTools", goodsViewTools);
			// 查询当前购物车内是否有实体商品,配送选项。
			boolean goods_delivery = false;
			List<GoodsCart> goodCarts = sc.getGcs();
			for (GoodsCart gc : goodCarts) {
				if (gc.getGoods().getGoods_choice_type() == 0) {
					goods_delivery = true;
					break;
				}
			}
			mv.addObject("goods_delivery", goods_delivery);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "购物车信息为空");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "完成订单提交进入支付", value = "/goods_cart3.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/goods_cart3.htm")
	public ModelAndView goods_cart3(HttpServletRequest request,
			HttpServletResponse response, String cart_session, String store_id,
			String addr_id, String coupon_id, String transport,String chargeNum)
			throws Exception {
		return this.orderFormService.pc_goods_cart3(request, response,
				cart_session, store_id, addr_id, coupon_id, chargeNum);
	}

	private void thirdBinding(ModelAndView mv) {
		List<ThirdBinding> list = this.thirdBindingService.queryByCreateUser();
		if (null != list && list.size() > 0) {
			mv.addObject("thirdBinding", 1);
		} else {
			mv.addObject("thirdBinding", 0);
		}
	}
	
	@SecurityMapping(title = "订单支付详情", value = "/order_pay_view.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_pay_view.htm")
	public ModelAndView order_pay_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("order_pay.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
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
			int flag_payThird = 0;
			List<Payment> paymentList = this.paymentService.queryByStore(of
					.getStore());
			if (null != paymentList && paymentList.size() > 0) {
				flag_payThird = 1;
			}
			mv.addObject("flag_payThird", flag_payThird);
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
	
	
	@SecurityMapping(title = "订单支付", value = "/order_pay.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_pay.htm")
	public ModelAndView order_pay(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String chargeNum, String integrationPlatform,
			String integrationStore) {
		return this.orderFormService.order_pay(request, response,
				payType, order_id, chargeNum,  integrationPlatform,
				 integrationStore);
	}

	@SecurityMapping(title = "订单线下支付", value = "/order_pay_outline.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_pay_outline.htm")
	public ModelAndView order_pay_outline(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception {
		return this.orderFormService.order_pay_outline(request, response, payType, order_id, pay_msg, pay_session);
	}

	@SecurityMapping(title = "订单货到付款", value = "/order_pay_payafter.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_pay_payafter.htm")
	public ModelAndView order_pay_payafter(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception {
		return this.orderFormService.order_pay_payafter(request, response, payType, order_id, pay_msg, pay_session);
	}

	@SecurityMapping(title = "订单预付款支付", value = "/order_pay_balance.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_pay_balance.htm")
	public ModelAndView order_pay_balance(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg) throws Exception {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
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
					.query("select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
							params, -1, -1);
			if (payments.size() > 0) {
				of.setPayment(payments.get(0));
				of.setPayTime(new Date());
			}
			boolean ret = this.orderFormService.update(of);
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.send_email(request, of,
						of.getStore().getUser().getEmail(),
						"email_toseller_balance_pay_ok_notify");
				this.send_email(request, of,
						of.getStore().getUser().getEmail(),
						"email_tobuyer_balance_pay_ok_notify");
			}
			if (this.configService.getSysConfig().isSmsEnbale()) {
				this.send_sms(request, of, of.getStore().getUser().getMobile(),
						"sms_toseller_balance_pay_ok_notify");
				this.send_sms(request, of, of.getUser().getMobile(),
						"sms_tobuyer_balance_pay_ok_notify");
			}
			if (ret) {
				user.setAvailableBalance(BigDecimal.valueOf(CommUtil.subtract(
						user.getAvailableBalance(), of.getTotalPrice())));
				user.setFreezeBlance(BigDecimal.valueOf(CommUtil.add(
						user.getFreezeBlance(), of.getTotalPrice())));
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
							if (gg.getGroup().getId()
									.equals(goods.getGroup().getId())) {
								gg.setGg_count(gg.getGg_count() - gc.getCount());
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
								temp.put("count",
										CommUtil.null2Int(temp.get("count"))
												- gc.getCount());
							}
						}
						goods.setGoods_inventory_detail(Json.toJson(list,
								JsonFormat.compact()));
					}
					for (GroupGoods gg : goods.getGroup_goods_list()) {
						if (gg.getGroup().getId()
								.equals(goods.getGroup().getId())
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
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "可用余额不足，支付失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单支付结果", value = "/order_finish.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_finish.htm")
	public ModelAndView order_finish(HttpServletRequest request,
			HttpServletResponse response, String order_id) {
		ModelAndView mv = new JModelAndView("order_finish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		mv.addObject("obj", obj);
		return mv;
	}

	@SecurityMapping(title = "地址新增", value = "/cart_address.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/cart_address.htm")
	public ModelAndView cart_address(HttpServletRequest request,
			HttpServletResponse response, String id, String store_id) {
		ModelAndView mv = new JModelAndView("cart_address.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		mv.addObject("store_id", store_id);
		return mv;
	}

	@SecurityMapping(title = "购物车中收货地址保存", value = "/cart_address_save.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/cart_address_save.htm")
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
		return "redirect:goods_cart2.htm?store_id=" + store_id;
	}

	@SecurityMapping(title = "地址切换", value = "/order_address.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_address.htm")
	public void order_address(HttpServletRequest request,
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
		List<SysMap> sms = this.transportTools.query_cart_trans(sc,
				CommUtil.null2String(addr.getArea().getId()));
		// System.out.println(Json.toJson(sms, JsonFormat.compact()));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(sms, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void send_email(HttpServletRequest request, OrderForm order,
			String email, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			String subject = template.getTitle();
			String path = request.getSession().getServletContext()
					.getRealPath("")
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
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
					request.getRealPath("/") + "vm" + File.separator);
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

	private void send_sms(HttpServletRequest request, OrderForm order,
			String mobile, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			String path = request.getSession().getServletContext()
					.getRealPath("")
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
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
					request.getRealPath("/") + "vm" + File.separator);
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
			this.msgTools.sendSMS(mobile, content);
		}
	}
}
