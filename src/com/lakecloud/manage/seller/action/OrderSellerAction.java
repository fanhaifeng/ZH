package com.lakecloud.manage.seller.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.SendMessageUtil;
import com.lakecloud.foundation.domain.Charge;
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.GoodsReturn;
import com.lakecloud.foundation.domain.GoodsReturnItem;
import com.lakecloud.foundation.domain.GoodsReturnLog;
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.Integration;
import com.lakecloud.foundation.domain.Integration_Child;
import com.lakecloud.foundation.domain.Integration_Log;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.OrderFormLog;
import com.lakecloud.foundation.domain.RefundLog;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.Template;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.query.OrderFormQueryObject;
import com.lakecloud.foundation.domain.virtual.TransInfo;
import com.lakecloud.foundation.service.IChargeService;
import com.lakecloud.foundation.service.IEvaluateService;
import com.lakecloud.foundation.service.IExpressCompanyService;
import com.lakecloud.foundation.service.IGoodsCartService;
import com.lakecloud.foundation.service.IGoodsReturnItemService;
import com.lakecloud.foundation.service.IGoodsReturnLogService;
import com.lakecloud.foundation.service.IGoodsReturnService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGroupGoodsService;
import com.lakecloud.foundation.service.IIntegralLogService;
import com.lakecloud.foundation.service.IIntegrationService;
import com.lakecloud.foundation.service.IIntegration_ChildService;
import com.lakecloud.foundation.service.IIntegration_LogService;
import com.lakecloud.foundation.service.IOrderFormLogService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IPaymentService;
import com.lakecloud.foundation.service.IRefundLogService;
import com.lakecloud.foundation.service.IRefundService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.ITemplateService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.manage.admin.tools.MsgTools;
import com.lakecloud.manage.admin.tools.PaymentTools;
import com.lakecloud.view.web.tools.StoreViewTools;
import com.lakecloud.weixin.store.view.action.WeixinStorePayViewAction;

/**
 * @info 卖家订单控制器，卖家中心订单管理所有控制器都在这里
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Controller
public class OrderSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IRefundLogService refundLogService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsReturnService goodsReturnService;
	@Autowired
	private IGoodsReturnItemService goodsReturnItemService;
	@Autowired
	private IGoodsReturnLogService goodsReturnLogService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IExpressCompanyService expressCompayService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private IChargeService chargeService;
	@Autowired
	private IRefundService refundService;
	@Autowired
	private WeixinStorePayViewAction wsp;
	
	
	
	@SecurityMapping(title = "卖家订单列表", value = "/seller/order.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order.htm")
	public ModelAndView order(HttpServletRequest request,
			HttpServletResponse response, String currentPage,
			String order_status, String order_id, String beginTime,
			String endTime, String buyer_userName) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/seller_order.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
				"addTime", "desc");
		ofqo.addQuery("obj.store.user.id", new SysMap("user_id",
				SecurityUserHolder.getCurrentUser().getId()), "=");
		if (!CommUtil.null2String(order_status).equals("")) {
			if (order_status.equals("order_submit")) {// 已经提交
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 10), "=");
			}
			if (order_status.equals("order_pay")) {// 已经付款
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status_min", 15), ">=");
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status_max", 20), "<=");
			}
			if (order_status.equals("order_shipping")) {// 已经发货
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 30), "=");
			}
			if (order_status.equals("order_receive")) {// 已经收货
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 40), "=");
			}
			if (order_status.equals("order_evaluate")) {// 等待评价
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 50), "=");
			}
			if (order_status.equals("order_finish")) {// 已经完成
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 60), "=");
			}
			if (order_status.equals("order_cancel")) {// 已经取消
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 0), "=");
			}
		}
		if (!CommUtil.null2String(order_id).equals("")) {
			ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id
					+ "%"), "like");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			ofqo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			ofqo.addQuery("obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
		}
		if (!CommUtil.null2String(buyer_userName).equals("")) {
			ofqo.addQuery("obj.user.userName", new SysMap("userName",
					buyer_userName), "=");
		}
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("order_id", order_id);
		mv.addObject("order_status", order_status == null ? "all"
				: order_status);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("buyer_userName", buyer_userName);
		return mv;
	}

	@SecurityMapping(title = "卖家订单详情", value = "/seller/order_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_view.htm")
	public ModelAndView order_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/order_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId()
				.equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
			mv.addObject("obj", obj);
			TransInfo transInfo = this.query_ship_getData(CommUtil
					.null2String(obj.getId()));
			mv.addObject("transInfo", transInfo);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	
	@SecurityMapping(title = "卖家确认还款", value = "/seller/seller_confirm_refund_order.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/seller_confirm_refund_order.htm")
	public ModelAndView seller_confirm_refund_order(HttpServletRequest request,
			HttpServletResponse response,String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/seller_confirm_refund_order.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);			
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);		
		return mv;
	}
	
	
	/**
	 * 查询订单的多条还款记录
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "还款历史查询", value = "/seller/seller_confirm_refund_history.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/seller_confirm_refund_history.htm")
	public ModelAndView seller_confirm_refund_history(HttpServletRequest request,
			HttpServletResponse response,String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/seller_confirm_refund_history.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		String query ="select obj from Refund obj where obj.of.id=:order_id";
		Map params = new HashMap();
		params.put("order_id", Long.parseLong(id));
		
		List<Charge>  chargeList = this.chargeService.query(query, params, -1, -1);
		mv.addObject("objs", chargeList);
		return mv;
	}
	

	/**
	 * 赊销金额还款
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "赊销金额还款", value = "/seller/seller_confirm_refund.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/seller_confirm_refund.htm")
	public ModelAndView seller_confirm_refund(HttpServletRequest request,
			HttpServletResponse response,String order_id,String needpay) {			
		OrderForm obj = this.chargeService.seller_confirm_refund(order_id, needpay);
		return new  ModelAndView("redirect:/seller/seller_refund_list.htm?buyer_userName="+obj.getUser().getUserName());	
	}
	
	
	@SecurityMapping(title = "卖家取消订单", value = "/seller/order_cancel.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_cancel.htm")
	public ModelAndView order_cancel(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/seller_order_cancel.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if(obj.getOrder_status()<30&&obj.getOrder_status()>0){
		if (obj.getStore().getId()
				.equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}}else{
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单无法取消！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家取消订单保存", value = "/seller/order_cancel_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_cancel_save.htm")
	public Object order_cancel_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info) throws Exception {
		String oper="seller";
		return this.orderFormService.order_cancel_save(request, response, id, currentPage, state_info, other_state_info,oper);
	}
	@SecurityMapping(title = "卖家调整订单费用", value = "/seller/order_fee.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_fee.htm")
	public ModelAndView order_fee(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/seller_order_fee.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId()
				.equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家调整订单费用保存", value = "/seller/order_fee_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_fee_save.htm")
	public Object order_fee_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String goods_amount, String totalPrice)
			throws Exception {
		return this.orderFormService.order_fee_save(request, response, id, currentPage, goods_amount, totalPrice);
	}

	@SecurityMapping(title = "线下付款确认", value = "/seller/seller_order_outline.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/seller_order_outline.htm")
	public ModelAndView seller_order_outline(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/seller_order_outline.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId()
				.equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "线下付款确认保存", value = "/seller/seller_order_outline_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/seller_order_outline_save.htm")
	public String seller_order_outline_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info) throws Exception {
		return this.orderFormService.seller_order_outline_save(request, response, id, currentPage, state_info);
	}

	@SecurityMapping(title = "卖家确认发货", value = "/seller/order_shipping.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_shipping.htm")
	public ModelAndView order_shipping(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv;
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId()
				.equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
			mv = new JModelAndView("user/default/usercenter/seller_order_shipping.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("obj", obj);		
			//货到付款且库存不足才去检查
			/**
			if( obj.getOrder_status() == 16 && !wsp.check_goods_inventory(obj)){
				mv.addObject("error",true);
			}
			*/
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家确认发货保存", value = "/seller/order_shipping_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_shipping_save.htm")
	public Object order_shipping_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage, String state_info) throws Exception {
		return this.orderFormService.order_shipping_save(request, response, id, currentPage, state_info);
	}

	@SecurityMapping(title = "卖家修改物流", value = "/seller/order_shipping_code.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_shipping_code.htm")
	public ModelAndView order_shipping_code(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/seller_order_shipping_code.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId()
				.equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家修改物流保存", value = "/seller/order_shipping_code_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_shipping_code_save.htm")
	public String order_shipping_code_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String shipCode, String state_info) {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId()
				.equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
			obj.setShipCode(shipCode);
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("修改物流信息");
			ofl.setState_info(state_info);
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "卖家退款", value = "/seller/order_refund.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_refund.htm")
	public ModelAndView order_refund(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/seller_order_refund.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId()
				.equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
			mv.addObject("paymentTools", paymentTools);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家退款保存", value = "/seller/order_refund_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_refund_save.htm")
	public String order_refund_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String refund, String refund_log, String refund_type) {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId()
				.equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
			obj.setRefund(BigDecimal.valueOf(CommUtil.add(obj.getRefund(),
					refund)));
			this.orderFormService.update(obj);
			// 如果是预存款账户，则执行卖家减少预存款，买家增加预存款
			String type = "预存款";
			if (type.equals(refund_type)) {
				User seller = this.userService.getObjById(obj.getStore()
						.getUser().getId());
				seller.setAvailableBalance(BigDecimal.valueOf(CommUtil
						.subtract(seller.getAvailableBalance(), BigDecimal
								.valueOf(CommUtil.null2Double(refund)))));
				this.userService.update(seller);
				User buyer = obj.getUser();
				buyer.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
						buyer.getAvailableBalance(),
						BigDecimal.valueOf(CommUtil.null2Double(refund)))));
				this.userService.update(buyer);
			}
			RefundLog log = new RefundLog();
			log.setAddTime(new Date());
			log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date())
					+ obj.getUser().getId().toString());
			log.setOf(obj);
			log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(refund)));
			log.setRefund_log(refund_log);
			log.setRefund_type(refund_type);
			log.setRefund_user(SecurityUserHolder.getCurrentUser());
			this.refundLogService.save(log);
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "卖家退货", value = "/seller/order_return.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_return.htm")
	public ModelAndView order_return(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/seller_order_return.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId()
				.equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家退货保存", value = "/seller/order_return_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_return_save.htm")
	public String order_return_save(HttpServletRequest request,
			HttpServletResponse response, String id, String return_info,
			String currentPage) {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId()
				.equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
			java.util.Enumeration enum1 = request.getParameterNames();
			GoodsReturn gr = new GoodsReturn();
			gr.setAddTime(new Date());
			gr.setOf(obj);
			gr.setReturn_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date())
					+ obj.getUser().getId().toString());
			gr.setUser(SecurityUserHolder.getCurrentUser());
			gr.setReturn_info(return_info);
			this.goodsReturnService.save(gr);
			while (enum1.hasMoreElements()) {
				String paramName = (String) enum1.nextElement();
				if (paramName.indexOf("refund_count_") >= 0) {
					GoodsCart gc = this.goodsCartService.getObjById(CommUtil
							.null2Long(paramName.substring(13)));
					int count = CommUtil.null2Int(request
							.getParameter(paramName));
					if (count > 0) {
						gc.setCount(gc.getCount() - count);
						this.goodsCartService.update(gc);
						GoodsReturnItem item = new GoodsReturnItem();
						item.setAddTime(new Date());
						item.setCount(count);
						item.setGoods(gc.getGoods());
						item.setGr(gr);
						for (GoodsSpecProperty gsp : gc.getGsps()) {
							item.getGsps().add(gsp);
						}
						item.setSpec_info(gc.getSpec_info());
						this.goodsReturnItemService.save(item);
						// 如果是预存款，则恢复预存款给买家,同时卖家减少预存款
						/*
						if (obj.getPayment().getMark().equals("balance")) {
							BigDecimal balance = goods.getGoods_current_price();
							User seller = this.userService
									.getObjById(SecurityUserHolder
											.getCurrentUser().getId());
							// 如果系统开启分润，计算分润后的预付款，在将卖家减少该预付款
							if (this.configService.getSysConfig()
									.getBalance_fenrun() == 1) {
								Map params = new HashMap();
								params.put("type", "admin");
								params.put("mark", "balance");
								List<Payment> payments = this.paymentService
										.query("select obj from Payment obj where obj.type=:type and obj.mark=:mark",
												params, -1, -1);
								Payment shop_payment = new Payment();
								if (payments.size() > 0) {
									shop_payment = payments.get(0);
								}
								// 按照分润比例计算平台应得利润金额
								double shop_availableBalance = CommUtil
										.null2Double(balance)
										* CommUtil.null2Double(shop_payment
												.getBalance_divide_rate());
								balance = BigDecimal.valueOf(CommUtil
										.null2Double(balance)
										- shop_availableBalance);
							}
							seller.setAvailableBalance(BigDecimal
									.valueOf(CommUtil.subtract(
											seller.getAvailableBalance(),
											balance)));
							this.userService.update(seller);
							User buyer = obj.getUser();
							buyer.setAvailableBalance(BigDecimal
									.valueOf(CommUtil.add(
											buyer.getAvailableBalance(),
											balance)));
							this.userService.update(buyer);
						}
						*/
					}
				}
			}
			GoodsReturnLog grl = new GoodsReturnLog();
			grl.setAddTime(new Date());
			grl.setGr(gr);
			grl.setOf(obj);
			grl.setReturn_user(SecurityUserHolder.getCurrentUser());
			this.goodsReturnLogService.save(grl);
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "卖家评价", value = "/seller/order_evaluate.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_evaluate.htm")
	public ModelAndView order_evaluate(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/seller_order_evaluate.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId()
				.equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家评价保存", value = "/seller/order_evaluate_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_evaluate_save.htm")
	public ModelAndView order_evaluate_save(HttpServletRequest request,
			HttpServletResponse response, String id, String evaluate_info,
			String evaluate_seller_val) {
		return this.orderFormService.order_evaluate_save(request, response, id, evaluate_info, evaluate_seller_val);
	}

	@SecurityMapping(title = "打印订单", value = "/seller/order_print.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_print.htm")
	public ModelAndView order_print(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/order_print.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			OrderForm orderform = this.orderFormService.getObjById(CommUtil
					.null2Long(id));
			mv.addObject("obj", orderform);
		}
		return mv;
	}

	@SecurityMapping(title = "卖家物流详情", value = "/seller/ship_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/ship_view.htm")
	public ModelAndView order_ship_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/order_ship_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId()
				.equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
			mv.addObject("obj", obj);
			TransInfo transInfo = this.query_ship_getData(CommUtil
					.null2String(obj.getId()));
			mv.addObject("transInfo", transInfo);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	private TransInfo query_ship_getData(String id) {
		TransInfo info = new TransInfo();
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		try {
			URL url = new URL(
					"http://api.kuaidi100.com/api?id="
							+ this.configService.getSysConfig().getKuaidi_id()
							+ "&com="
							+ (obj.getEc() != null ? obj.getEc()
									.getCompany_mark() : "") + "&nu="
							+ obj.getShipCode() + "&show=0&muti=1&order=asc");
			URLConnection con = url.openConnection();
			con.setAllowUserInteraction(false);
			InputStream urlStream = url.openStream();
			String type = con.guessContentTypeFromStream(urlStream);
			String charSet = null;
			if (type == null)
				type = con.getContentType();
			if (type == null || type.trim().length() == 0
					|| type.trim().indexOf("text/html") < 0)
				return info;
			if (type.indexOf("charset=") > 0)
				charSet = type.substring(type.indexOf("charset=") + 8);
			byte b[] = new byte[10000];
			int numRead = urlStream.read(b);
			String content = new String(b, 0, numRead, charSet);
			while (numRead != -1) {
				numRead = urlStream.read(b);
				if (numRead != -1) {
					// String newContent = new String(b, 0, numRead);
					String newContent = new String(b, 0, numRead, charSet);
					content += newContent;
				}
			}
			info = Json.fromJson(TransInfo.class, content);
			urlStream.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return info;
	}

	@SecurityMapping(title = "卖家物流详情", value = "/seller/order_query_userinfor.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_query_userinfor.htm")
	public ModelAndView seller_query_userinfor(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/seller_query_userinfor.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		return mv;
	}

	@SecurityMapping(title = "买家退货申请详情", value = "/seller/seller_order_return_apply_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/seller_order_return_apply_view.htm")
	public ModelAndView seller_order_return_apply_view(
			HttpServletRequest request, HttpServletResponse response,
			String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/seller_order_return_apply_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家保存退货申请", value = "/seller/seller_order_return.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/seller_order_return.htm")
	public Object seller_order_return(HttpServletRequest request,
			HttpServletResponse response, String id, String gr_id,
			String currentPage, String mark) throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		OrderFormLog ofl = new OrderFormLog();
		ofl.setAddTime(new Date());
		ofl.setLog_info("卖家处理退货申请");
		ofl.setLog_user(SecurityUserHolder.getCurrentUser());
		ofl.setOf(obj);
		if (mark.equals("true")) {
			if (obj.getStore()
					.getId()
					.equals(SecurityUserHolder.getCurrentUser().getStore()
							.getId())) {
				java.util.Enumeration enum1 = request.getParameterNames();
				GoodsReturn gr = this.goodsReturnService.getObjById(CommUtil
						.null2Long(gr_id));
				obj.setOrder_status(46);
				int auto_order_return = this.configService.getSysConfig()
						.getAuto_order_return();
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, auto_order_return);
				obj.setReturn_shipTime(cal.getTime());
				try {
					if (this.configService.getSysConfig().isEmailEnable()) {
						this.orderFormService.send_email(request, obj,
								"email_tobuyer_order_return_apply_ok_notify");
					}
					if (this.configService.getSysConfig().isSmsEnbale()) {
						this.send_sms(request, obj, obj.getUser().getMobile(),
								"sms_tobuyer_order_return_apply_ok_notify");
					}
				} catch (Exception e) {
				}
			}
		ofl.setState_info("同意");
		} else {
			obj.setOrder_status(48);
			try {
				if (this.configService.getSysConfig().isEmailEnable()) {
					this.orderFormService.send_email(request, obj,
							"email_tobuyer_order_return_apply_refuse_notify");
				}
				if (this.configService.getSysConfig().isSmsEnbale()) {
					this.send_sms(request, obj, obj.getUser().getMobile(),
							"sms_tobuyer_order_return_apply_refuse_notify");
				}
			} catch (Exception e) {
			}
			ofl.setState_info("拒绝");
		}
		this.orderFormLogService.save(ofl);
		this.orderFormService.update(obj);
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "确认买家退货", value = "/seller/seller_order_return_confirm.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/seller_order_return_confirm.htm")
	public ModelAndView seller_order_return_confirm(HttpServletRequest request,
			HttpServletResponse response, String id) {		
		return this.orderFormService.seller_order_return_confirm(request, response, id);
	}

	@SecurityMapping(title = "买家退货物流详情", value = "/seller/seller_order_return_ship_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/seller_order_return_ship_view.htm")
	public ModelAndView seller_order_return_ship_view(
			HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getStore().getId()
				.equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
			if (obj.getReturn_shipCode() != null
					&& !obj.getReturn_shipCode().equals("")
					&& obj.getReturn_ec() != null
					&& !obj.getReturn_ec().equals("")) {
				mv = new JModelAndView(
						"user/default/usercenter/seller_order_return_ship_view.html",
						configService.getSysConfig(), this.userConfigService
								.getUserConfig(), 0, request, response);
				TransInfo transInfo = this.query_return_ship(CommUtil
						.null2String(obj.getId()));
				mv.addObject("obj", obj);
				mv.addObject("transInfo", transInfo);
			} else {
				mv.addObject("op_title", "买家没有提交退货物流信息");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/order.htm");
			}
		} else {
			mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	private TransInfo query_return_ship(String id) {
		TransInfo info = new TransInfo();
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		try {
			URL url = new URL("http://api.kuaidi100.com/api?id="
					+ this.configService.getSysConfig().getKuaidi_id()
					+ "&com="
					+ (obj.getReturn_ec() != null ? obj.getReturn_ec()
							.getCompany_mark() : "") + "&nu="
					+ obj.getReturn_shipCode() + "&show=0&muti=1&order=asc");
			URLConnection con = url.openConnection();
			con.setAllowUserInteraction(false);
			InputStream urlStream = url.openStream();
			String type = con.guessContentTypeFromStream(urlStream);
			String charSet = null;
			if (type == null)
				type = con.getContentType();
			if (type == null || type.trim().length() == 0
					|| type.trim().indexOf("text/html") < 0)
				return info;
			if (type.indexOf("charset=") > 0)
				charSet = type.substring(type.indexOf("charset=") + 8);
			byte b[] = new byte[10000];
			int numRead = urlStream.read(b);
			String content = new String(b, 0, numRead, charSet);
			while (numRead != -1) {
				numRead = urlStream.read(b);
				if (numRead != -1) {
					// String newContent = new String(b, 0, numRead);
					String newContent = new String(b, 0, numRead, charSet);
					content += newContent;
				}
			}
			info = Json.fromJson(TransInfo.class, content);
			urlStream.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return info;
	}

	private void send_sms(HttpServletRequest request, OrderForm order,
			String mobile, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			SendMessageUtil sendmessage = new SendMessageUtil();
			String path = request.getSession().getServletContext()
					.getRealPath("/")
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
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,path);
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Autowired
	private IIntegrationService is;
	@Autowired
	private IIntegration_ChildService ics;
	@Autowired
	private IIntegration_LogService ils;
	/**
	 * 初始化农豆
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/seller/cshnd.htm")
	public void cshnd(HttpServletRequest request,
			HttpServletResponse response,String nd2,String gqnd2,String js) {
		int icm=0;
		int ilm=0;
		int js2 = CommUtil.null2Int(js);
		int nd = CommUtil.null2Int(nd2)*js2;
		int gqnd = CommUtil.null2Int(gqnd2)*js2;
		Integration_Log ilog=new Integration_Log();
		ilog.setAddTime(new Date());
		User user = SecurityUserHolder.getCurrentUser();
		Store store = user.getStore();
		Integration il = this.is.getObjByProperty("user.id", user.getId());
		if(il==null){
			il=new Integration();
			il.setAddTime(new Date());
			il.setIntegrals(0);
			il.setOverdue_integrals(0);
			il.setUser(user);
			ilm=1;
		}
		ilog.setBe_integrals(il.getIntegrals());
		String query="from Integration_Child obj where obj.type=0 and obj.user.id=:uid";
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("uid", user.getId());
		List<Integration_Child> ics2 = this.ics.query(query, params, -1, -1);
		Integration_Child ic;
		if(ics2.size()==0){
			ic=new Integration_Child();
			ic.setAddTime(new Date());
			ic.setInteg(il);
			ic.setIntegrals(0);
			ic.setOverdue_integrals(0);
			ic.setType(0);
			ic.setUser(user);
			icm=1;
		}else
			ic=ics2.get(0);
		//
		il.setIntegrals(il.getIntegrals()+nd);
		il.setOverdue_integrals(il.getOverdue_integrals()+gqnd);
		//
		ic.setIntegrals(ic.getIntegrals()+nd);
		ic.setOverdue_integrals(ic.getOverdue_integrals()+gqnd);
		//		
		ilog.setAf_integrals(il.getIntegrals());
		ilog.setGettype(ilm==1?1:-1);
		ilog.setInteg(il);
		ilog.setIntegrals(nd>0?nd:(nd*-1));
		ilog.setOverdue_integrals(gqnd>0?gqnd:(gqnd*-1));
		ilog.setType(js2);
		ilog.setUser(user);
		if(icm==0)
			this.is.update(il);
		else
			this.is.save(il);
		if(ilm==0)
			this.ics.update(ic);
		else
			this.ics.save(ic);
		this.ils.save(ilog);
	}

}
