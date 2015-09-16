package com.lakecloud.weixin.store.view.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.Md5Encrypt;
import com.lakecloud.foundation.domain.GoldLog;
import com.lakecloud.foundation.domain.GoldRecord;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.GroupGoods;
import com.lakecloud.foundation.domain.IntegralGoods;
import com.lakecloud.foundation.domain.IntegralGoodsCart;
import com.lakecloud.foundation.domain.IntegralGoodsOrder;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.OrderFormLog;
import com.lakecloud.foundation.domain.Payment;
import com.lakecloud.foundation.domain.Predeposit;
import com.lakecloud.foundation.domain.PredepositLog;
import com.lakecloud.foundation.domain.StoreDepositLog;
import com.lakecloud.foundation.domain.Template;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IGoldLogService;
import com.lakecloud.foundation.service.IGoldRecordService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGroupGoodsService;
import com.lakecloud.foundation.service.IIntegralGoodsOrderService;
import com.lakecloud.foundation.service.IIntegralGoodsService;
import com.lakecloud.foundation.service.IOrderFormLogService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IPaymentService;
import com.lakecloud.foundation.service.IPredepositLogService;
import com.lakecloud.foundation.service.IPredepositService;
import com.lakecloud.foundation.service.IStoreDepositLogService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.ITemplateService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.lucene.LuceneUtil;
import com.lakecloud.manage.admin.tools.MsgTools;
import com.lakecloud.pay.alipay.config.AlipayConfig;
import com.lakecloud.pay.alipay.util.AlipayNotify;
import com.lakecloud.pay.bill.util.MD5Util;
import com.lakecloud.pay.tenpay.RequestHandler;
import com.lakecloud.pay.tenpay.ResponseHandler;
import com.lakecloud.pay.tenpay.util.TenpayUtil;

/**
 * 
 * <p>
 * Title: WeixinStorePayViewAction.java
 * </p>
 * 
 * <p>
 * Description: 微信店铺在线支付回调控制器
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
 * @author hezeng
 * 
 * @date 2014-4-27
 * 
 * @version sinofert_cc 1.3
 */
@Controller
public class WeixinStorePayViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IPredepositService predepositService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IGoldRecordService goldRecordService;
	@Autowired
	private IGoldLogService goldLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private IIntegralGoodsService integralGoodsService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IStoreDepositLogService storeDepositLogService;
	@Autowired
	private MsgTools msgTools;

	@RequestMapping("/weixin/alipay_return.htm")
	public ModelAndView wap_alipay_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("weixin/wap_order_finish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String trade_no = request.getParameter("trade_no"); // 支付宝交易号
		String[] order_nos = request.getParameter("out_trade_no").split("-");
		; // 获取订单号
		String total_fee = request.getParameter("price"); // 获取总金额
		String subject = request.getParameter("subject");// 交易参数
		String result = request.getParameter("result");// 交易状态
		// System.out.println("支付返回结果是：" + result);
		// String(request.getParameter("subject").getBytes("ISO-8859-1"),
		// "UTF-8");//
		// 商品名称、订单名称
		String order_no = order_nos[2];
		String type = CommUtil.null2String(order_nos[3]);
		if (type.equals("")) {
			type = "goods";
		}
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		StoreDepositLog deposit = null;
		if (type.equals("goods")) {
			order = this.orderFormService.getObjById(CommUtil
					.null2Long(order_no));
		}
		if (type.equals("cash")) {
			obj = this.predepositService.getObjById(CommUtil
					.null2Long(order_no));
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil
					.null2Long(order_no));
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(order_no));
		}
		if (type.equals("store_deposit")) {
			deposit = this.storeDepositLogService.getObjById(CommUtil
					.null2Long(order_no));
		}
		// 获取支付宝GET过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			// 如果没有配置Tomcat的get编码为UTF-8，需要下面一行代码转码，否则会出现乱码，导致回调失败
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		AlipayConfig config = new AlipayConfig();
		if (type.equals("goods")) {
			config.setKey(order.getPayment().getSafeKey());
			config.setPartner(order.getPayment().getPartner());
			config.setSeller_email(order.getPayment().getSeller_email());
		}
		if (type.equals("cash") || type.equals("gold")
				|| type.equals("integral") || type.equals("store_deposit")) {
			Map q_params = new HashMap();
			q_params.put("install", true);
			if (type.equals("cash")) {
				q_params.put("mark", obj.getPd_payment());
			}
			if (type.equals("gold")) {
				q_params.put("mark", gold.getGold_payment());
			}
			if (type.equals("integral")) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			if (type.equals("store_deposit")) {
				q_params.put("mark", deposit.getDp_payment_mark());
			}
			q_params.put("type", "admin");
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type",
							q_params, -1, -1);
			config.setKey(payments.get(0).getSafeKey());
			config.setPartner(payments.get(0).getPartner());
			config.setSeller_email(payments.get(0).getSeller_email());
		}
		boolean verify_result = AlipayNotify.verify(config, params);
		if (verify_result && result.equals("success")) {// 验证成功
			if (type.equals("goods")) {
				if (order.getOrder_status() < 20) {
					order.setOrder_status(20);
					order.setOut_order_id(trade_no);
					order.setPayTime(new Date());
					this.orderFormService.update(order);
					// 付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
					this.update_goods_inventory(order);
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("支付宝在线支付");
					ofl.setLog_user(SecurityUserHolder.getCurrentUser());
					ofl.setOf(order);
					this.orderFormLogService.save(ofl);
					// 付款成功，发送邮件提示
					if (this.configService.getSysConfig().isEmailEnable()) {
						this.send_order_email(request, order, order.getUser()
								.getEmail(),
								"email_tobuyer_online_pay_ok_notify");
						this.send_order_email(request, order, order.getStore()
								.getUser().getEmail(),
								"email_toseller_online_pay_ok_notify");
					}
					// 付款成功，发送短信提示
					if (this.configService.getSysConfig().isSmsEnbale()) {
						this.send_order_sms(request, order, order.getUser()
								.getMobile(),
								"sms_tobuyer_online_pay_ok_notify");
						this.send_order_sms(request, order, order.getStore()
								.getUser().getMobile(),
								"sms_toseller_online_pay_ok_notify");
					}
				}
				mv.addObject("obj", order);
			}
			if (type.equals("cash")) {
				if (obj.getPd_pay_status() != 2) {
					obj.setPd_status(1);
					obj.setPd_pay_status(2);
					this.predepositService.update(obj);
					User user = this.userService.getObjById(obj.getPd_user()
							.getId());
					user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
							user.getAvailableBalance(), obj.getPd_amount())));
					this.userService.update(user);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_amount(obj.getPd_amount());
					log.setPd_log_user(obj.getPd_user());
					log.setPd_op_type("充值");
					log.setPd_type("可用预存款");
					log.setPd_log_info("支付宝在线支付");
					this.predepositLogService.save(log);
				}
				mv = new JModelAndView("success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/predeposit_list.htm");
			}
			if (type.equals("gold")) {
				if (gold.getGold_pay_status() != 2) {
					gold.setGold_status(1);
					gold.setGold_pay_status(2);
					this.goldRecordService.update(gold);
					User user = this.userService.getObjById(gold.getGold_user()
							.getId());
					user.setGold(user.getGold() + gold.getGold_count());
					this.userService.update(user);
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_payment(gold.getGold_payment());
					log.setGl_content("支付宝在线支付");
					log.setGl_money(gold.getGold_money());
					log.setGl_count(gold.getGold_count());
					log.setGl_type(0);
					log.setGl_user(gold.getGold_user());
					log.setGr(gold);
					this.goldLogService.save(log);
				}
				mv = new JModelAndView("success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "兑换" + gold.getGold_count() + "金币成功");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/gold_record_list.htm");
			}
			if (type.equals("integral")) {
				if (ig_order.getIgo_status() != 20) {
					ig_order.setIgo_status(20);
					ig_order.setIgo_pay_time(new Date());
					ig_order.setIgo_payment("alipay");
					this.integralGoodsOrderService.update(ig_order);
					for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
						IntegralGoods goods = igc.getGoods();
						goods.setIg_goods_count(goods.getIg_goods_count()
								- igc.getCount());
						goods.setIg_exchange_count(goods.getIg_exchange_count()
								+ igc.getCount());
						this.integralGoodsService.update(goods);
					}
				}
				mv = new JModelAndView("integral_order_finish.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("obj", ig_order);
			}
			if (type.equals("store_deposit")) {
				if (deposit.getDp_status() != 10) {
					deposit.setDp_status(10);
					this.storeDepositLogService.update(deposit);
					User store_user = this.userService.getObjById(deposit
							.getDp_user_id());
					store_user.setStore_deposit_status(10);
					store_user.setStore_deposit(deposit.getDp_amount());
					this.userService.update(store_user);
				}
				mv = new JModelAndView("success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "店铺保证金缴纳成功");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/store_deposit.htm");
			}
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "支付回调失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/weixin/index.htm");
		}
		return mv;
	}

	@RequestMapping("/weixin/alipay_notify.htm")
	public void wap_alipay_notify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			// 如果没有配置Tomcat的get编码为UTF-8，需要下面一行代码转码，否则会出现乱码，导致回调失败
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//

		// 解密（如果是RSA签名需要解密，如果是MD5签名则下面一行清注释掉）
		// Map<String,String> decrypt_params = AlipayNotify.decrypt(params);
		// XML解析notify_data数据
		// System.out.println(params);
		Document doc_notify_data = DocumentHelper.parseText(params
				.get("notify_data"));
		// 商户订单号
		String[] order_nos = doc_notify_data
				.selectSingleNode("//notify/out_trade_no").getText().split("-");
		// 支付宝交易号
		String trade_no = doc_notify_data.selectSingleNode("//notify/trade_no")
				.getText();
		// 交易状态
		String trade_status = doc_notify_data.selectSingleNode(
				"//notify/trade_status").getText();
		// 商品名称、订单名称
		String order_no = order_nos[2];
		String type = CommUtil.null2String(order_nos[3]);
		OrderForm order = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		StoreDepositLog deposit = null;
		if (type.equals("goods") || type.equals("")) {
			order = this.orderFormService.getObjById(CommUtil
					.null2Long(order_no));
		}
		if (type.equals("cash")) {
			pd = this.predepositService
					.getObjById(CommUtil.null2Long(order_no));
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil
					.null2Long(order_no));
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(order_no));
		}
		if (type.equals("store_deposit")) {
			deposit = this.storeDepositLogService.getObjById(CommUtil
					.null2Long(order_no));
		}
		AlipayConfig config = new AlipayConfig();
		if (type.equals("goods")) {
			config.setKey(order.getPayment().getSafeKey());
			config.setPartner(order.getPayment().getPartner());
			config.setSeller_email(order.getPayment().getSeller_email());
		}
		if (type.equals("cash") || type.equals("gold")
				|| type.equals("integral") || type.equals("store_deposit")) {
			Map q_params = new HashMap();
			q_params.put("install", true);
			if (type.equals("cash")) {
				q_params.put("mark", pd.getPd_payment());
			}
			if (type.equals("gold")) {
				q_params.put("mark", gold.getGold_payment());
			}
			if (type.equals("integral")) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			q_params.put("type", "admin");
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type",
							q_params, -1, -1);
			config.setKey(payments.get(0).getSafeKey());
			config.setPartner(payments.get(0).getPartner());
			config.setSeller_email(payments.get(0).getSeller_email());
		}
		boolean verify_result = AlipayNotify.verifyWapNotify(config, params);
		if (verify_result) {// 验证成功
			if (type.equals("goods")) {
				if (trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {
					if (order.getOrder_status() < 20) {
						order.setOrder_status(20);
						order.setOut_order_id(trade_no);
						order.setPayTime(new Date());
						this.orderFormService.update(order);
						// 付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
						this.update_goods_inventory(order);
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("支付宝在线支付");
						ofl.setLog_user(order.getUser());
						ofl.setOf(order);
						this.orderFormLogService.save(ofl);
						// 付款成功，发送邮件提示
						if (this.configService.getSysConfig().isEmailEnable()) {
							this.send_order_email(request, order, order
									.getUser().getEmail(),
									"email_tobuyer_online_pay_ok_notify");
							this.send_order_email(request, order, order
									.getStore().getUser().getEmail(),
									"email_toseller_online_pay_ok_notify");
						}
						// 付款成功，发送短信提示
						if (this.configService.getSysConfig().isSmsEnbale()) {
							this.send_order_sms(request, order, order.getUser()
									.getMobile(),
									"sms_tobuyer_online_pay_ok_notify");
							this.send_order_sms(request, order, order
									.getStore().getUser().getMobile(),
									"sms_toseller_online_pay_ok_notify");
						}
					}
				}
			}
			if (type.equals("cash")) {
				if (trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {
					if (pd.getPd_pay_status() != 2) {
						pd.setPd_status(1);
						pd.setPd_pay_status(2);
						this.predepositService.update(pd);
						User user = this.userService.getObjById(pd.getPd_user()
								.getId());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(user.getAvailableBalance(),
										pd.getPd_amount())));
						this.userService.update(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(pd.getPd_amount());
						log.setPd_log_user(pd.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("支付宝在线支付");
						this.predepositLogService.save(log);
					}
				}
			}
			if (type.equals("gold")) {
				if (trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {
					if (gold.getGold_pay_status() != 2) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold
								.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("支付宝在线支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
					}
				}
			}
			if (type.equals("integral")) {
				if (trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {
					if (ig_order.getIgo_status() < 20) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("alipay");
						this.integralGoodsOrderService.update(ig_order);
						for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
							IntegralGoods goods = igc.getGoods();
							goods.setIg_goods_count(goods.getIg_goods_count()
									- igc.getCount());
							goods.setIg_exchange_count(goods
									.getIg_exchange_count() + igc.getCount());
							this.integralGoodsService.update(goods);
						}
					}
				}
			}
			if (type.equals("store_deposit")) {
				if (trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {
					if (deposit.getDp_status() != 10) {
						deposit.setDp_status(10);
						this.storeDepositLogService.update(deposit);
						User store_user = this.userService.getObjById(deposit
								.getDp_user_id());
						store_user.setStore_deposit_status(10);
						store_user.setStore_deposit(deposit.getDp_amount());
						this.userService.update(store_user);
					}
				}
			}
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.print("success");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.print("fail");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 快钱在线支付回调处理控制
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/weixin/bill_return.htm")
	public ModelAndView wap_bill_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("weixin/wap_order_finish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// 获取扩展字段1
		String ext1 = (String) request.getParameter("ext1").trim();
		String ext2 = CommUtil.null2String(request.getParameter("ext2").trim());
		OrderForm order = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		StoreDepositLog deposit = null;
		if (ext2.equals("goods")) {
			order = this.orderFormService.getObjById(CommUtil.null2Long(ext1));
		}
		if (ext2.equals("cash")) {
			pd = this.predepositService.getObjById(CommUtil.null2Long(ext1));
		}
		if (ext2.equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(ext1));
		}
		if (ext2.equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(ext1));
		}
		if (ext2.equals("store_deposit")) {
			deposit = this.storeDepositLogService.getObjById(CommUtil
					.null2Long(ext1));
		}
		// 获取人民币网关账户号
		String merchantAcctId = (String) request.getParameter("merchantAcctId")
				.trim();
		// 设置人民币网关密钥
		// /区分大小写
		String key = "";
		if (ext2.equals("goods")) {
			key = order.getPayment().getRmbKey();
		}
		if (ext2.equals("cash") || ext2.equals("gold")
				|| ext2.equals("integral") || ext2.equals("store_deposit")) {
			Map q_params = new HashMap();
			q_params.put("install", true);
			if (ext2.equals("cash")) {
				q_params.put("mark", pd.getPd_payment());
			}
			if (ext2.equals("gold")) {
				q_params.put("mark", gold.getGold_payment());
			}
			if (ext2.equals("integral")) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			if (ext2.equals("store_deposit")) {
				q_params.put("mark", deposit.getDp_payment_mark());
			}
			q_params.put("type", "admin");
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type",
							q_params, -1, -1);
			key = payments.get(0).getRmbKey();
		}
		// /快钱会根据版本号来调用对应的接口处理程序。
		// /本代码版本号固定为v2.0
		String version = (String) request.getParameter("version").trim();
		// 获取语言种类.固定选择值。
		// /只能选择1、2、3
		// /1代表中文；2代表英文
		// /默认值为1
		String language = (String) request.getParameter("language").trim();
		// 签名类型.固定值
		// /1代表MD5签名
		// /当前版本固定为1
		String signType = (String) request.getParameter("signType").trim();

		// 获取支付方式
		// /值为：10、11、12、13、14
		// /00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）.14：B2B支付（网关支付页面只显示B2B支付，但需要向快钱申请开通才能使用）
		String payType = (String) request.getParameter("payType").trim();

		// 获取银行代码
		// /参见银行代码列表
		String bankId = (String) request.getParameter("bankId").trim();
		// 获取商户订单号
		String orderId = (String) request.getParameter("orderId").trim();
		// 获取订单提交时间
		// /获取商户提交订单时的时间.14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		// /如：20080101010101
		String orderTime = (String) request.getParameter("orderTime").trim();
		// 获取原始订单金额
		// /订单提交到快钱时的金额，单位为分。
		// /比方2 ，代表0.02元
		String orderAmount = (String) request.getParameter("orderAmount")
				.trim();
		// 获取快钱交易号
		// /获取该交易在快钱的交易号
		String dealId = (String) request.getParameter("dealId").trim();
		// 获取银行交易号
		// /如果使用银行卡支付时，在银行的交易号。如不是通过银行支付，则为空
		String bankDealId = (String) request.getParameter("bankDealId").trim();
		// 获取在快钱交易时间
		// /14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		// /如；20080101010101
		String dealTime = (String) request.getParameter("dealTime").trim();
		// 获取实际支付金额
		// /单位为分
		// /比方 2 ，代表0.02元
		String payAmount = (String) request.getParameter("payAmount").trim();
		// 获取交易手续费
		// /单位为分
		// /比方 2 ，代表0.02元
		String fee = (String) request.getParameter("fee").trim();
		// 获取扩展字段2
		// /10代表 成功11代表 失败
		String payResult = (String) request.getParameter("payResult").trim();
		// 获取错误代码
		String errCode = (String) request.getParameter("errCode").trim();
		// 获取加密签名串
		String signMsg = (String) request.getParameter("signMsg").trim();
		// 生成加密串。必须保持如下顺序。
		String merchantSignMsgVal = "";
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "merchantAcctId",
				merchantAcctId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "version", version);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "language",
				language);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "signType",
				signType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payType", payType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankId", bankId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderId", orderId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderTime",
				orderTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderAmount",
				orderAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealId", dealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankDealId",
				bankDealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealTime",
				dealTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payAmount",
				payAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "fee", fee);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext1", ext1);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext2", ext2);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payResult",
				payResult);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "errCode", errCode);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "key", key);

		String merchantSignMsg = MD5Util.md5Hex(
				merchantSignMsgVal.getBytes("utf-8")).toUpperCase();
		// 商家进行数据处理，并跳转会商家显示支付结果的页面
		// /首先进行签名字符串验证
		if (signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())) {
			// /接着进行支付结果判断
			switch (Integer.parseInt(payResult)) {
			case 10:
				// 特别注意：只有signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())，且payResult=10，才表示支付成功！同时将订单金额与提交订单前的订单金额进行对比校验。
				if (ext2.equals("goods")) {
					order.setOrder_status(20);
					order.setPayTime(new Date());
					this.orderFormService.update(order);
					this.update_goods_inventory(order);// 更新商品库存
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("快钱在线支付");
					ofl.setLog_user(SecurityUserHolder.getCurrentUser());
					ofl.setOf(order);
					this.orderFormLogService.save(ofl);
					mv.addObject("obj", order);
					// 付款成功，发送邮件提示
					if (this.configService.getSysConfig().isEmailEnable()) {
						this.send_order_email(request, order, order.getUser()
								.getEmail(),
								"email_tobuyer_online_pay_ok_notify");
						this.send_order_email(request, order, order.getStore()
								.getUser().getEmail(),
								"email_toseller_online_pay_ok_notify");
					}
					// 付款成功，发送短信提示
					if (this.configService.getSysConfig().isSmsEnbale()) {
						this.send_order_sms(request, order, order.getUser()
								.getMobile(),
								"sms_tobuyer_online_pay_ok_notify");
						this.send_order_sms(request, order, order.getStore()
								.getUser().getMobile(),
								"sms_toseller_online_pay_ok_notify");
					}
				}
				if (ext2.equals("cash")) {
					pd.setPd_status(1);
					pd.setPd_pay_status(2);
					this.predepositService.update(pd);
					User user = this.userService.getObjById(pd.getPd_user()
							.getId());
					user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
							user.getAvailableBalance(), pd.getPd_amount())));
					this.userService.update(user);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_amount(pd.getPd_amount());
					log.setPd_log_user(pd.getPd_user());
					log.setPd_op_type("充值");
					log.setPd_type("可用预存款");
					log.setPd_log_info("快钱在线支付");
					this.predepositLogService.save(log);
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "充值" + pd.getPd_amount() + "成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/predeposit_list.htm");
				}
				if (ext2.equals("gold")) {
					gold.setGold_status(1);
					gold.setGold_pay_status(2);
					this.goldRecordService.update(gold);
					User user = this.userService.getObjById(gold.getGold_user()
							.getId());
					user.setGold(user.getGold() + gold.getGold_count());
					this.userService.update(user);
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_payment(gold.getGold_payment());
					log.setGl_content("快钱在线支付");
					log.setGl_money(gold.getGold_money());
					log.setGl_count(gold.getGold_count());
					log.setGl_type(0);
					log.setGl_user(gold.getGold_user());
					log.setGr(gold);
					this.goldLogService.save(log);
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "兑换" + gold.getGold_count()
							+ "金币成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/gold_record_list.htm");
				}
				if (ext2.equals("integral")) {
					ig_order.setIgo_status(20);
					ig_order.setIgo_pay_time(new Date());
					ig_order.setIgo_payment("bill");
					this.integralGoodsOrderService.update(ig_order);
					for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
						IntegralGoods goods = igc.getGoods();
						goods.setIg_goods_count(goods.getIg_goods_count()
								- igc.getCount());
						goods.setIg_exchange_count(goods.getIg_exchange_count()
								+ igc.getCount());
						this.integralGoodsService.update(goods);
					}
					mv = new JModelAndView("integral_order_finish.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("obj", ig_order);
				}
				if (ext2.equals("store_deposit")) {
					if (deposit.getDp_status() != 10) {
						deposit.setDp_status(10);
						this.storeDepositLogService.update(deposit);
						User store_user = this.userService.getObjById(deposit
								.getDp_user_id());
						store_user.setStore_deposit_status(10);
						store_user.setStore_deposit(deposit.getDp_amount());
						this.userService.update(store_user);
					}
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "店铺保证金缴纳成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/store_deposit.htm");
				}
				break;
			default:
				mv = new JModelAndView("weixin/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "快钱支付失败！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/index.htm");
				break;

			}

		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "快钱支付失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/weixin/index.htm");
		}
		return mv;
	}

	public String appendParam(String returnStr, String paramId,
			String paramValue) {
		if (!returnStr.equals("")) {
			if (!paramValue.equals("")) {
				returnStr = returnStr + "&" + paramId + "=" + paramValue;
			}
		} else {
			if (!paramValue.equals("")) {
				returnStr = paramId + "=" + paramValue;
			}
		}
		return returnStr;
	}

	/**
	 * 网银在线回调函数
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/weixin/chinabank_return.htm")
	public ModelAndView wap_chinabank_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("weixin/wap_order_finish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String remark1 = request.getParameter("remark1"); // 备注1
		String remark2 = CommUtil.null2String(request.getParameter("remark2"));
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if (remark2.equals("goods")) {
			order = this.orderFormService.getObjById(CommUtil.null2Long(remark1
					.trim()));
		}
		if (remark2.equals("cash")) {
			obj = this.predepositService
					.getObjById(CommUtil.null2Long(remark1));
		}
		if (remark2.equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil
					.null2Long(remark1));
		}
		if (remark2.equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(remark1));
		}
		String key = "";
		if (remark2.equals("goods")) {
			key = order.getPayment().getChinabank_key();
		}
		if (remark2.equals("cash") || remark2.equals("gold")
				|| remark2.equals("integral")) {
			Map q_params = new HashMap();
			q_params.put("install", true);
			if (remark2.equals("cash")) {
				q_params.put("mark", obj.getPd_payment());
			}
			if (remark2.equals("gold")) {
				q_params.put("mark", gold.getGold_payment());
			}
			if (remark2.equals("integral")) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			q_params.put("type", "admin");
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type",
							q_params, -1, -1);
			key = payments.get(0).getChinabank_key();
		}
		String v_oid = request.getParameter("v_oid"); // 订单号
		String v_pmode = request.getParameter("v_pmode");// new
		// String(request.getParameter("v_pmode").getBytes("ISO-8859-1"),
		// "UTF-8"); //
		// 支付方式中文说明，如"中行长城信用卡"
		String v_pstatus = request.getParameter("v_pstatus"); // 支付结果，20支付完成；30支付失败；
		String v_pstring = request.getParameter("v_pstring");// new
		// String(request.getParameter("v_pstring").getBytes("ISO-8859-1"),
		// "UTF-8"); //
		// 对支付结果的说明，成功时（v_pstatus=20）为"支付成功"，支付失败时（v_pstatus=30）为"支付失败"
		String v_amount = request.getParameter("v_amount"); // 订单实际支付金额
		String v_moneytype = request.getParameter("v_moneytype"); // 币种
		String v_md5str = request.getParameter("v_md5str"); // MD5校验码
		String text = v_oid + v_pstatus + v_amount + v_moneytype + key; // 拼凑加密串
		String v_md5text = Md5Encrypt.md5(text).toUpperCase();
		if (v_md5str.equals(v_md5text)) {
			if ("20".equals(v_pstatus)) {
				// 支付成功，商户 根据自己业务做相应逻辑处理
				// 此处加入商户系统的逻辑处理（例如判断金额，判断支付状态(20成功,30失败)，更新订单状态等等）......
				if (remark2.equals("goods")) {
					order.setOrder_status(20);
					order.setPayTime(new Date());
					this.orderFormService.update(order);
					this.update_goods_inventory(order);// 更新商品库存
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("网银在线支付");
					ofl.setLog_user(SecurityUserHolder.getCurrentUser());
					ofl.setOf(order);
					this.orderFormLogService.save(ofl);
					mv.addObject("obj", order);
					// 付款成功，发送邮件提示
					if (this.configService.getSysConfig().isEmailEnable()) {
						this.send_order_email(request, order, order.getUser()
								.getEmail(),
								"email_tobuyer_online_pay_ok_notify");
						this.send_order_email(request, order, order.getStore()
								.getUser().getEmail(),
								"email_toseller_online_pay_ok_notify");
					}
					// 付款成功，发送短信提示
					if (this.configService.getSysConfig().isSmsEnbale()) {
						this.send_order_sms(request, order, order.getUser()
								.getMobile(),
								"sms_tobuyer_online_pay_ok_notify");
						this.send_order_sms(request, order, order.getStore()
								.getUser().getMobile(),
								"sms_toseller_online_pay_ok_notify");
					}
				}
				if (remark2.endsWith("cash")) {
					obj.setPd_status(1);
					obj.setPd_pay_status(2);
					this.predepositService.update(obj);
					User user = this.userService.getObjById(obj.getPd_user()
							.getId());
					user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
							user.getAvailableBalance(), obj.getPd_amount())));
					this.userService.update(user);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_amount(obj.getPd_amount());
					log.setPd_log_user(obj.getPd_user());
					log.setPd_op_type("充值");
					log.setPd_type("可用预存款");
					log.setPd_log_info("网银在线支付");
					this.predepositLogService.save(log);
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/predeposit_list.htm");
				}
				if (remark2.equals("gold")) {
					gold.setGold_status(1);
					gold.setGold_pay_status(2);
					this.goldRecordService.update(gold);
					User user = this.userService.getObjById(gold.getGold_user()
							.getId());
					user.setGold(user.getGold() + gold.getGold_count());
					this.userService.update(user);
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_payment(gold.getGold_payment());
					log.setGl_content("网银在线支付");
					log.setGl_money(gold.getGold_money());
					log.setGl_count(gold.getGold_count());
					log.setGl_type(0);
					log.setGl_user(gold.getGold_user());
					log.setGr(gold);
					this.goldLogService.save(log);
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "兑换" + gold.getGold_count()
							+ "金币成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/gold_record_list.htm");
				}
				if (remark2.equals("gold")) {
					ig_order.setIgo_status(20);
					ig_order.setIgo_pay_time(new Date());
					ig_order.setIgo_payment("bill");
					this.integralGoodsOrderService.update(ig_order);
					for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
						IntegralGoods goods = igc.getGoods();
						goods.setIg_goods_count(goods.getIg_goods_count()
								- igc.getCount());
						goods.setIg_exchange_count(goods.getIg_exchange_count()
								+ igc.getCount());
						this.integralGoodsService.update(goods);
					}
					mv = new JModelAndView("integral_order_finish.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("obj", ig_order);
				}
			}
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "网银在线支付失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/weixin/index.htm");

		}
		return mv;
	}

	/**
	 * paypal回调方法,paypal支付成功了后，调用该方法进行后续处理
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/wexin/paypal_return.htm")
	public ModelAndView wap_paypal_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("weixin/wap_order_finish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Enumeration en = request.getParameterNames();
		String str = "cmd=_notify-validate";
		while (en.hasMoreElements()) {
			String paramName = (String) en.nextElement();
			String paramValue = request.getParameter(paramName);
			str = str + "&" + paramName + "=" + URLEncoder.encode(paramValue);
		}
		String[] customs = CommUtil.null2String(request.getParameter("custom"))
				.split(",");
		String remark1 = customs[0];
		String remark2 = customs[1];
		String item_name = request.getParameter("item_name");
		String txnId = request.getParameter("txn_id");
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if (remark2.equals("goods")) {
			order = this.orderFormService.getObjById(CommUtil.null2Long(remark1
					.trim()));
		}
		if (remark2.equals("cash")) {
			obj = this.predepositService
					.getObjById(CommUtil.null2Long(remark1));
		}
		if (remark2.equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil
					.null2Long(remark1));
		}
		if (remark2.equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(remark1));
		}
		String txn_id = request.getParameter("txn_id");
		//
		// 建议在此将接受到的信息str记录到日志文件中以确认是否收到IPN信息
		// 将信息POST回给PayPal进行验证
		// 设置HTTP的头信息
		// 在Sandbox情况下，设置：
		// URL u = new URL("http://www.sanbox.paypal.com/cgi-bin/webscr");
		// URLConnection uc = u.openConnection();
		// uc.setDoOutput(true);
		// uc.setRequestProperty("Content-Type",
		// "application/x-www-form-urlencoded");
		// PrintWriter pw = new PrintWriter(uc.getOutputStream());
		// pw.println(str);
		// pw.close();
		// 接受PayPal对IPN回发的回复信息
		// BufferedReader in = new BufferedReader(new InputStreamReader(uc
		// .getOutputStream()));
		// String res = in.readLine();
		// in.close();
		// System.out.println("接受PayPal对IPN回发的回复信息：" + res);

		// 将POST信息分配给本地变量，可以根据您的需要添加
		// 该付款明细所有变量可参考：
		String itemName = request.getParameter("item_name");
		String paymentStatus = request.getParameter("payment_status");
		String paymentAmount = request.getParameter("mc_gross");
		String paymentCurrency = request.getParameter("mc_currency");
		String receiverEmail = request.getParameter("receiver_email");
		String payerEmail = request.getParameter("payer_email");
		// System.out.println("付款明细信息：订单编号:" + itemName + ",支付状态：" +
		// paymentStatus+ ",金额：" + paymentAmount + ",货币种类：" + paymentCurrency+
		// ",paypal支付流水号:" + txnId + ",paypal接收方账号：" + receiverEmail+
		// ",paypal支付方账号" + payerEmail);
		if (paymentStatus.equals("Completed")
				|| paymentStatus.equals("Pending")) {
			if (remark2.equals("goods")) {
				order.setOrder_status(20);
				order.setPayTime(new Date());
				this.orderFormService.update(order);
				this.update_goods_inventory(order);// 更新商品库存
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("Paypal在线支付");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(order);
				this.orderFormLogService.save(ofl);
				mv.addObject("obj", order);
				// 付款成功，发送邮件提示
				if (this.configService.getSysConfig().isEmailEnable()) {
					this.send_order_email(request, order, order.getUser()
							.getEmail(), "email_tobuyer_online_pay_ok_notify");
					this.send_order_email(request, order, order.getStore()
							.getUser().getEmail(),
							"email_toseller_online_pay_ok_notify");
				}
				// 付款成功，发送短信提示
				if (this.configService.getSysConfig().isSmsEnbale()) {
					this.send_order_sms(request, order, order.getUser()
							.getMobile(), "sms_tobuyer_online_pay_ok_notify");
					this.send_order_sms(request, order, order.getStore()
							.getUser().getMobile(),
							"sms_toseller_online_pay_ok_notify");
				}
			}
			if (remark2.endsWith("cash")) {
				obj.setPd_status(1);
				obj.setPd_pay_status(2);
				this.predepositService.update(obj);
				User user = this.userService.getObjById(obj.getPd_user()
						.getId());
				user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
						user.getAvailableBalance(), obj.getPd_amount())));
				this.userService.update(user);
				PredepositLog log = new PredepositLog();
				log.setAddTime(new Date());
				log.setPd_log_amount(obj.getPd_amount());
				log.setPd_log_user(obj.getPd_user());
				log.setPd_op_type("充值");
				log.setPd_type("可用预存款");
				log.setPd_log_info("Paypal在线支付");
				this.predepositLogService.save(log);
				mv = new JModelAndView("success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "成功充值：" + obj.getPd_amount());
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/predeposit_list.htm");
			}
			if (remark2.equals("gold")) {
				gold.setGold_status(1);
				gold.setGold_pay_status(2);
				this.goldRecordService.update(gold);
				User user = this.userService.getObjById(gold.getGold_user()
						.getId());
				user.setGold(user.getGold() + gold.getGold_count());
				this.userService.update(user);
				GoldLog log = new GoldLog();
				log.setAddTime(new Date());
				log.setGl_payment(gold.getGold_payment());
				log.setGl_content("Paypal");
				log.setGl_money(gold.getGold_money());
				log.setGl_count(gold.getGold_count());
				log.setGl_type(0);
				log.setGl_user(gold.getGold_user());
				log.setGr(gold);
				this.goldLogService.save(log);
				mv = new JModelAndView("success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "成功充值金币:" + gold.getGold_count());
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/gold_record_list.htm");
			}
			if (remark2.equals("gold")) {
				ig_order.setIgo_status(20);
				ig_order.setIgo_pay_time(new Date());
				ig_order.setIgo_payment("paypal");
				this.integralGoodsOrderService.update(ig_order);
				for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
					IntegralGoods goods = igc.getGoods();
					goods.setIg_goods_count(goods.getIg_goods_count()
							- igc.getCount());
					goods.setIg_exchange_count(goods.getIg_exchange_count()
							+ igc.getCount());
					this.integralGoodsService.update(goods);
				}
				mv = new JModelAndView("integral_order_finish.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("obj", ig_order);
			}
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "Paypal支付失败");
			mv.addObject("url", CommUtil.getURL(request) + "/weixin/index.htm");
		}
		return mv;
	}

	/**
	 * 财付通支付
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/weixin/tenpay.htm")
	public void wap_tenpay(HttpServletRequest request,
			HttpServletResponse response, String id, String type,
			String payment_id) throws IOException {
		OrderForm of = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if (type.equals("goods")) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		}
		if (type.equals("cash")) {
			obj = this.predepositService.getObjById(CommUtil.null2Long(id));
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(id));
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(id));
		}
		// 获取提交的商品价格
		String order_price = "";
		if (type.equals("goods")) {
			order_price = CommUtil.null2String(of.getTotalPrice());
		}
		if (type.equals("cash")) {
			order_price = CommUtil.null2String(obj.getPd_amount());
		}
		if (type.equals("gold")) {
			order_price = CommUtil.null2String(gold.getGold_money());
		}
		if (type.equals("integral")) {
			order_price = CommUtil.null2String(ig_order.getIgo_trans_fee());
		}
		/* 商品价格（包含运费），以分为单位 */
		double total_fee = CommUtil.null2Double(order_price) * 100;
		int fee = (int) total_fee;
		// 获取提交的商品名称
		String product_name = "";
		if (type.equals("goods")) {
			product_name = of.getOrder_id();
		}
		if (type.equals("cash")) {
			product_name = obj.getPd_sn();
		}
		if (type.equals("gold")) {
			product_name = gold.getGold_sn();
		}
		if (type.equals("integral")) {
			product_name = ig_order.getIgo_order_sn();
		}
		// 获取提交的备注信息
		String remarkexplain = "";
		if (type.equals("goods")) {
			remarkexplain = of.getMsg();
		}
		if (type.equals("cash")) {
			remarkexplain = obj.getPd_remittance_info();
		}
		if (type.equals("gold")) {
			remarkexplain = gold.getGold_exchange_info();
		}
		if (type.equals("integral")) {
			remarkexplain = ig_order.getIgo_msg();
		}
		String attach = "";
		if (type.equals("goods")) {
			attach = type + "," + of.getId().toString();
		}
		if (type.equals("cash")) {
			attach = type + "," + obj.getId().toString();
		}
		if (type.equals("gold")) {
			attach = type + "," + gold.getId().toString();
		}
		if (type.equals("integral")) {
			attach = type + "," + ig_order.getId().toString();
		}
		String desc = "商品：" + product_name;
		// 获取提交的订单号
		String out_trade_no = "";
		if (type.equals("goods")) {
			out_trade_no = of.getOrder_id();
		}
		if (type.endsWith("cash")) {
			out_trade_no = obj.getPd_sn();
		}
		if (type.endsWith("gold")) {
			out_trade_no = gold.getGold_sn();
		}
		if (type.equals("integral")) {
			out_trade_no = ig_order.getIgo_order_sn();
		}
		// 支付方式
		Payment payment = this.paymentService.getObjById(CommUtil
				.null2Long(payment_id));
		if (payment == null) {
			payment = new Payment();
		}
		String trade_mode = CommUtil.null2String(payment.getTrade_mode());
		String currTime = TenpayUtil.getCurrTime();
		// 创建支付请求对象
		RequestHandler reqHandler = new RequestHandler(request, response);
		reqHandler.init();
		// 设置密钥
		reqHandler.setKey(payment.getTenpay_key());
		// 设置支付网关
		reqHandler.setGateUrl("https://gw.tenpay.com/gateway/pay.htm");
		// -----------------------------
		// 设置支付参数
		// -----------------------------
		reqHandler.setParameter("partner", payment.getTenpay_partner()); // 商户号
		reqHandler.setParameter("out_trade_no", out_trade_no); // 商家订单号
		reqHandler.setParameter("total_fee", String.valueOf(fee)); // 商品金额,以分为单位
		reqHandler.setParameter("return_url", CommUtil.getURL(request)
				+ "/tenpay_return.htm"); // 交易完成后跳转的URL
		reqHandler.setParameter("notify_url", CommUtil.getURL(request)
				+ "/tenpay_notify.htm"); // 接收财付通通知的URL
		reqHandler.setParameter("body", desc); // 商品描述
		reqHandler.setParameter("bank_type", "DEFAULT"); // 银行类型(中介担保时此参数无效)
		reqHandler.setParameter("spbill_create_ip", request.getRemoteAddr()); // 用户的公网ip，不是商户服务器IP
		reqHandler.setParameter("fee_type", "1"); // 币种，1人民币
		reqHandler.setParameter("subject", desc); // 商品名称(中介交易时必填)
		// 系统可选参数
		reqHandler.setParameter("sign_type", "MD5"); // 签名类型,默认：MD5
		reqHandler.setParameter("service_version", "1.0"); // 版本号，默认为1.0
		reqHandler.setParameter("input_charset", "UTF-8"); // 字符编码
		reqHandler.setParameter("sign_key_index", "1"); // 密钥序号

		// 业务可选参数
		reqHandler.setParameter("attach", attach); // 附加数据，原样返回
		reqHandler.setParameter("product_fee", ""); // 商品费用，必须保证transport_fee +
		reqHandler.setParameter("transport_fee", "0"); // 物流费用，必须保证transport_fee
		reqHandler.setParameter("time_start", currTime); // 订单生成时间，格式为yyyymmddhhmmss
		reqHandler.setParameter("time_expire", ""); // 订单失效时间，格式为yyyymmddhhmmss
		reqHandler.setParameter("buyer_id", ""); // 买方财付通账号
		reqHandler.setParameter("goods_tag", ""); // 商品标记
		reqHandler.setParameter("trade_mode", trade_mode); // 交易模式，1即时到账(默认)，2中介担保，3后台选择（买家进支付中心列表选择）
		reqHandler.setParameter("transport_desc", ""); // 物流说明
		reqHandler.setParameter("trans_type", "1"); // 交易类型，1实物交易，2虚拟交易
		reqHandler.setParameter("agentid", ""); // 平台ID
		reqHandler.setParameter("agent_type", ""); // 代理模式，0无代理(默认)，1表示卡易售模式，2表示网店模式
		reqHandler.setParameter("seller_id", ""); // 卖家商户号，为空则等同于partner
		// 请求的url
		String requestUrl = reqHandler.getRequestURL();
		response.sendRedirect(requestUrl);
	}

	/**
	 * 财付通在线支付回调控制
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/weixin/tenpay_return.htm")
	public ModelAndView wap_tenpay_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("weixin/wap_order_finish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ResponseHandler resHandler = new ResponseHandler(request, response);
		String[] attachs = request.getParameter("attach").split(",");
		// 商户订单号
		String out_trade_no = resHandler.getParameter("out_trade_no");
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if (attachs[0].equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(attachs[1]));
			Map q_params = new HashMap();
			q_params.put("install", true);
			q_params.put("mark", ig_order.getIgo_payment());
			q_params.put("type", "admin");
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type",
							q_params, -1, -1);
			resHandler.setKey(payments.get(0).getTenpay_key());
		}
		if (attachs[0].equals("cash")) {
			obj = this.predepositService.getObjById(CommUtil
					.null2Long(attachs[1]));
			Map q_params = new HashMap();
			q_params.put("install", true);
			q_params.put("mark", obj.getPd_payment());
			q_params.put("type", "admin");
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type",
							q_params, -1, -1);
			resHandler.setKey(payments.get(0).getTenpay_key());
		}
		if (attachs[0].equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil
					.null2Long(attachs[1]));
			Map q_params = new HashMap();
			q_params.put("install", true);
			q_params.put("mark", gold.getGold_payment());
			q_params.put("type", "admin");
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type",
							q_params, -1, -1);
			resHandler.setKey(payments.get(0).getTenpay_key());
		}
		if (attachs[0].equals("goods")) {
			order = this.orderFormService.getObjById(CommUtil
					.null2Long(attachs[1]));
			resHandler.setKey(order.getPayment().getTenpay_key());
		}
		// System.out.println("前台回调返回参数:" + resHandler.getAllParameters());
		// 判断签名
		if (resHandler.isTenpaySign()) {
			// 通知id
			String notify_id = resHandler.getParameter("notify_id");
			// 财付通订单号
			String transaction_id = resHandler.getParameter("transaction_id");
			// 金额,以分为单位
			String total_fee = resHandler.getParameter("total_fee");
			// 如果有使用折扣券，discount有值，total_fee+discount=原请求的total_fee
			String discount = resHandler.getParameter("discount");
			// 支付结果
			String trade_state = resHandler.getParameter("trade_state");
			// 交易模式，1即时到账，2中介担保
			String trade_mode = resHandler.getParameter("trade_mode");
			if ("1".equals(trade_mode)) { // 即时到账
				if ("0".equals(trade_state)) {
					// 即时到账处理业务完毕
					if (attachs[0].equals("cash")) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.update(obj);
						User user = this.userService.getObjById(obj
								.getPd_user().getId());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(user.getAvailableBalance(),
										obj.getPd_amount())));
						this.userService.update(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("财付通及时到账");
						this.predepositLogService.save(log);
						mv = new JModelAndView("success.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "充值" + obj.getPd_amount()
								+ "成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/predeposit_list.htm");
					}
					if (attachs[0].equals("goods")) {
						order.setOrder_status(20);
						order.setPayTime(new Date());
						this.orderFormService.update(order);
						this.update_goods_inventory(order);// 更新商品库存
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("财付通及时到账支付");
						ofl.setLog_user(SecurityUserHolder.getCurrentUser());
						ofl.setOf(order);
						this.orderFormLogService.save(ofl);
						mv.addObject("obj", order);
						// 付款成功，发送邮件提示
						if (this.configService.getSysConfig().isEmailEnable()) {
							this.send_order_email(request, order, order
									.getUser().getEmail(),
									"email_tobuyer_online_pay_ok_notify");
							this.send_order_email(request, order, order
									.getStore().getUser().getEmail(),
									"email_toseller_online_pay_ok_notify");
						}
						// 付款成功，发送短信提示
						if (this.configService.getSysConfig().isSmsEnbale()) {
							this.send_order_sms(request, order, order.getUser()
									.getMobile(),
									"sms_tobuyer_online_pay_ok_notify");
							this.send_order_sms(request, order, order
									.getStore().getUser().getMobile(),
									"sms_toseller_online_pay_ok_notify");
						}
					}
					if (attachs[0].equals("gold")) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold
								.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("财付通及时到账支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
						mv = new JModelAndView("success.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "兑换" + gold.getGold_count()
								+ "金币成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/seller/gold_record_list.htm");
					}
					if (attachs[0].equals("integral")) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("bill");
						this.integralGoodsOrderService.update(ig_order);
						for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
							IntegralGoods goods = igc.getGoods();
							goods.setIg_goods_count(goods.getIg_goods_count()
									- igc.getCount());
							goods.setIg_exchange_count(goods
									.getIg_exchange_count() + igc.getCount());
							this.integralGoodsService.update(goods);
						}
						mv = new JModelAndView("integral_order_finish.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("obj", ig_order);
					}
				} else {
					mv = new JModelAndView("weixin/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "财付通支付失败！");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/weixin/index.htm");
				}
			} else if ("2".equals(trade_mode)) { // 中介担保
				if ("0".equals(trade_state)) {
					// 中介担保处理业务完毕
					if (attachs[0].equals("cash")) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.update(obj);
						User user = this.userService.getObjById(obj
								.getPd_user().getId());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(user.getAvailableBalance(),
										obj.getPd_amount())));
						this.userService.update(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("财付通中介担保付款");
						this.predepositLogService.save(log);
						mv = new JModelAndView("success.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "充值" + obj.getPd_amount()
								+ "成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/predeposit_list.htm");
					}
					if (attachs[0].equals("goods")) {
						order.setOrder_status(20);
						order.setPayTime(new Date());
						this.orderFormService.update(order);
						this.update_goods_inventory(order);// 更新商品库存
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("财付通中介担保付款成功");
						ofl.setLog_user(SecurityUserHolder.getCurrentUser());
						ofl.setOf(order);
						this.orderFormLogService.save(ofl);
						mv.addObject("obj", order);
						// 付款成功，发送邮件提示
						if (this.configService.getSysConfig().isEmailEnable()) {
							this.send_order_email(request, order, order
									.getUser().getEmail(),
									"email_tobuyer_online_pay_ok_notify");
							this.send_order_email(request, order, order
									.getStore().getUser().getEmail(),
									"email_toseller_online_pay_ok_notify");
						}
						// 付款成功，发送短信提示
						if (this.configService.getSysConfig().isSmsEnbale()) {
							this.send_order_sms(request, order, order.getUser()
									.getMobile(),
									"sms_tobuyer_online_pay_ok_notify");
							this.send_order_sms(request, order, order
									.getStore().getUser().getMobile(),
									"sms_toseller_online_pay_ok_notify");
						}
					}
					if (attachs[0].equals("gold")) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold
								.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("财付通中介担保付款成功");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
						mv = new JModelAndView("success.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "兑换" + gold.getGold_count()
								+ "金币成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/seller/gold_record_list.htm");
					}
					if (attachs[0].equals("integral")) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("bill");
						this.integralGoodsOrderService.update(ig_order);
						for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
							IntegralGoods goods = igc.getGoods();
							goods.setIg_goods_count(goods.getIg_goods_count()
									- igc.getCount());
							goods.setIg_exchange_count(goods
									.getIg_exchange_count() + igc.getCount());
							this.integralGoodsService.update(goods);
						}
						mv = new JModelAndView("integral_order_finish.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("obj", ig_order);
					}
				} else {
					mv = new JModelAndView("weixin/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "财付通支付失败！");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/weixin/index.htm");
				}
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "财付通认证签名失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/weixin/index.htm");
		}
		return mv;
	}
	
	/**
	 * 校验商品库存
	 */
	public boolean check_goods_inventory(OrderForm order){
		boolean flag = true;//默认可以购买
		for (GoodsCart gc : order.getGcs()) {
			//团购
			Goods goods = gc.getGoods();
			if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {		
						//团购商品数量小于购物车数量
						if(gg.getGg_count() < gc.getCount()){
							flag = false;
							break;
						}	
					}
				}
			}	
			List<String> gsps = new ArrayList<String>();
			for (GoodsSpecProperty gsp : gc.getGsps()) {
				gsps.add(gsp.getId().toString());
			}
			String[] gsp_list = new String[gsps.size()];
			gsps.toArray(gsp_list);
			String inventory_type = goods.getInventory_type() == null ? "all"
					: goods.getInventory_type();
			if (inventory_type.equals("all")) {
				if(goods.getGoods_inventory() < gc.getCount()){
					flag = false;
					break;
				}
			} else {
				//获取商品的库存信息
				List<HashMap> list = Json.fromJson(ArrayList.class, CommUtil.null2String(goods
								.getGoods_inventory_detail()));
				for (Map temp : list) {
					String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
					Arrays.sort(temp_ids);
					Arrays.sort(gsp_list);
					if (Arrays.equals(temp_ids, gsp_list)) {
						if(CommUtil.null2Int(temp.get("count")) < gc.getCount()){
							//商品库存数少于要购买商品数量
							flag=false;
							break;
						}	
					}
				}
			}
		}
		return flag;
	}
	
	
	public void update_goods_inventory(OrderForm order) {
		// 付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
		for (GoodsCart gc : order.getGcs()) {
			Goods goods = gc.getGoods();
			if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
						gg.setGg_def_count(gg.getGg_def_count() + gc.getCount());
						gg.setGg_count(gg.getGg_count() - gc.getCount());
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
			goods.setGoods_salenum(goods.getGoods_salenum() + gc.getCount());
			String inventory_type = goods.getInventory_type() == null ? "all"
					: goods.getInventory_type();
			if (inventory_type.equals("all")) {
				goods.setGoods_inventory(goods.getGoods_inventory()
						- gc.getCount());
			} else {
				List<HashMap> list = Json
						.fromJson(ArrayList.class, CommUtil.null2String(goods
								.getGoods_inventory_detail()));
				for (Map temp : list) {
					String[] temp_ids = CommUtil.null2String(temp.get("id"))
							.split("_");
					Arrays.sort(temp_ids);
					Arrays.sort(gsp_list);
					if (Arrays.equals(temp_ids, gsp_list)) {
						temp.put("count", CommUtil.null2Int(temp.get("count"))
								- gc.getCount());
					}
				}
				goods.setGoods_inventory_detail(Json.toJson(list,
						JsonFormat.compact()));
			}
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())
						&& gg.getGg_count() == 0) {
					goods.setGroup_buy(3);// 标识商品的状态为团购数量已经结束
				}
			}
			this.goodsService.update(goods);
			//调用lucene更新商品索引
			LuceneUtil.instance().ruLuceneIndex(goods, "u");
		}
	}

	private void send_order_email(HttpServletRequest request, OrderForm order,
			String email, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			String subject = template.getTitle();
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ "/vm/";
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

	private void send_order_sms(HttpServletRequest request, OrderForm order,
			String mobile, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ "/vm/";
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
