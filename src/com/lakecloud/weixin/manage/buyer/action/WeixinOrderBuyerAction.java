package com.lakecloud.weixin.manage.buyer.action;

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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.dao.IGenericDAO;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.SendMessageUtil;
import com.lakecloud.foundation.domain.Charge;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Template;
import com.lakecloud.foundation.domain.virtual.TransInfo;
import com.lakecloud.foundation.service.IChargeService;
import com.lakecloud.foundation.service.ICouponInfoService;
import com.lakecloud.foundation.service.IEvaluateService;
import com.lakecloud.foundation.service.IExpressCompanyService;
import com.lakecloud.foundation.service.IGoodsCartService;
import com.lakecloud.foundation.service.IGoodsReturnItemService;
import com.lakecloud.foundation.service.IGoodsReturnService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGroupGoodsService;
import com.lakecloud.foundation.service.IOrderFormLogService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IPaymentService;
import com.lakecloud.foundation.service.IPredepositLogService;
import com.lakecloud.foundation.service.IStorePointService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.ITemplateService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.lucene.LuceneUtil;
import com.lakecloud.manage.admin.tools.MsgTools;

/**
 * @info 微信客户端买家订单控制器，用于查看订单列表、查看订单详情、支付订单，确认收货
 * @since V1.3
 * @version 1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net hz 2013-11-26
 * 
 */
@Controller
public class WeixinOrderBuyerAction {
	@Resource(name = "orderFormDAO")
	private IGenericDAO<OrderForm> orderFormDao;
	@Resource(name = "chargeDAO")
	private IGenericDAO<Charge> chargeDao;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IStorePointService storePointService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IGoodsReturnItemService goodsReturnItemService;
	@Autowired
	private IGoodsReturnService goodsReturnService;
	@Autowired
	private IExpressCompanyService expressCompayService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IChargeService chargeService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGoodsService goodsService;
	/**
	 * 订单列表
	 * 
	 * @param request
	 * @param response
	 * @param order_status
	 *            ：订单状态，根据订单状态不同查询相应的订单
	 * @return
	 */
	@SecurityMapping(title = "订单列表", value = "/weixin/buyer/order.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order.htm")
	public ModelAndView weixin_order(HttpServletRequest request,
			HttpServletResponse response, String order_status) {
		ModelAndView mv = new JModelAndView("weixin/buyer_order.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<OrderForm> orders = null;
		Map map = new HashMap();
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		
		if (!CommUtil.null2String(order_status).equals("")) {
			if(order_status.contains("_")){
				List<Integer> list = new ArrayList<Integer>();
				String[] statusList = order_status.split("_");
				for(int i =0;i<statusList.length;i++){
					list.add(Integer.parseInt(statusList[i]));
				}
				map.put("order_status",list);
				orders = this.orderFormService
				.query("select obj from OrderForm obj where obj.order_status in :order_status and obj.user.id=:user_id order by addTime desc",
						map, 0, 6);
				mv.addObject("order_status", order_status);
			}else{
			map.put("order_status", CommUtil.null2Int(order_status));
			orders = this.orderFormService
					.query("select obj from OrderForm obj where obj.order_status=:order_status and obj.user.id=:user_id order by addTime desc",
							map, 0, 6);
			mv.addObject("order_status", order_status);
			}
		} else {
			orders = this.orderFormService
					.query("select obj from OrderForm obj where obj.user.id=:user_id order by addTime desc",
							map, 0, 6);
		}
		mv.addObject("objs", orders);
		return mv;
	}

	/**
	 * 订单列表ajax加载
	 * 
	 * @param request
	 * @param response
	 * @param order_status
	 *            ：订单状态
	 * @param beginCount
	 *            ：订单查询开始值，每次查询6条数据
	 * @return
	 */
	@SecurityMapping(title = "订单列表ajax加载", value = "/weixin/buyer/order_ajax.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_ajax.htm")
	public ModelAndView weixin_order_data(HttpServletRequest request,
			HttpServletResponse response, String order_status, String beginCount) {
		ModelAndView mv = new JModelAndView("weixin/buyer_order_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<OrderForm> orders = null;
		Map map = new HashMap();
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		if (!CommUtil.null2String(order_status).equals("")) {
			if(order_status.contains("_")){
				List<Integer> list = new ArrayList<Integer>();
				String[] statusList = order_status.split("_");
				for(int i =0;i<statusList.length;i++){
					list.add(Integer.parseInt(statusList[i]));
				}
				map.put("order_status",list);
				orders = this.orderFormService
				.query("select obj from OrderForm obj where obj.order_status in :order_status and obj.user.id=:user_id order by addTime desc",
						map, CommUtil.null2Int(beginCount), 6);
				mv.addObject("order_status", order_status);
			}else{
			map.put("order_status", CommUtil.null2Int(order_status));
			orders = this.orderFormService
					.query("select obj from OrderForm obj where obj.order_status=:order_status and obj.user.id=:user_id order by addTime desc",
							map, CommUtil.null2Int(beginCount), 6);
			mv.addObject("order_status", order_status);
			}
		} else {
			orders = this.orderFormService
					.query("select obj from OrderForm obj where obj.user.id=:user_id order by addTime desc",
							map, CommUtil.null2Int(beginCount), 6);
		}
		mv.addObject("objs", orders);
		mv.addObject("beginCount", beginCount);
		return mv;
	}
	
	@SecurityMapping(title = "买家赊销列表加载", value = "/weixin/buyer/charge_list.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/charge_list.htm")
	public ModelAndView weixin_order_data1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/charge_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<OrderForm> orders = null;
		Map map = new HashMap();
		
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		orders = this.orderFormService
				.query("select obj from OrderForm obj where obj.user.id=:user_id and  obj.charge_Num >0 order by addTime desc",
						map, 0, 6);
		mv.addObject("objs", orders);
		return mv;
	}
	
	/**
	 * 获取当前登录用户的赊销订单
	 * @param request
	 * @param response
	 * @param beginCount
	 * @return
	 */
	@SecurityMapping(title = "买家赊销列表ajax加载", value = "/weixin/buyer/charge_list_ajax.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/charge_list_ajax.htm")
	public ModelAndView weixin_order_data_ajax(HttpServletRequest request,
			HttpServletResponse response,String beginCount) {
		ModelAndView mv = new JModelAndView("weixin/buyer_order_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<OrderForm> orders = null;
		Map map = new HashMap();
		
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		orders = this.orderFormService
				.query("select obj from OrderForm obj where obj.user.id=:user_id and  obj.charge_Num >0 order by addTime desc",
						map, CommUtil.null2Int(beginCount), 6);
		mv.addObject("objs", orders);
		mv.addObject("beginCount", beginCount);
		return mv;
	}
	
	
	@SecurityMapping(title = "卖家赊销列表", value = "/weixin/buyer/charge_list_seller.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/charge_list_seller.htm")
	public ModelAndView weixin_seller_order_data(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/charge_list_seller.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<OrderForm> orders = null;
		Map map = new HashMap();
		
		map.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
		orders = this.orderFormService
				.query("select obj from OrderForm obj where obj.store.id=:store_id and  obj.charge_Num >0 order by addTime desc",
						map, 0, 6);
		mv.addObject("objs", orders);
		return mv;
	}
	
	
	@SecurityMapping(title = "卖家赊销列表ajax加载", value = "/weixin/buyer/charge_list_seller_ajax.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/charge_list_seller_ajax.htm")
	public ModelAndView weixin_seller_order_data_ajax(HttpServletRequest request,
			HttpServletResponse response,String beginCount) {
		ModelAndView mv = new JModelAndView("weixin/buyer_order_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<OrderForm> orders = null;
		Map map = new HashMap();
		
		map.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
		orders = this.orderFormService
				.query("select obj from OrderForm obj where obj.store.id=:store_id and  obj.charge_Num >0 order by addTime desc",
						map, CommUtil.null2Int(beginCount), 6);
		mv.addObject("objs", orders);
		mv.addObject("beginCount", beginCount);
		return mv;
	}
	

	
	@SecurityMapping(title = "经销商所有授信的会员", value = "/weixin/buyer/charge_user_list.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/charge_user_list.htm")
	public ModelAndView charge_user_list(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/charge_user_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Charge> charges = null;
		Map<String, Long> map = new HashMap();
		
		map.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
		charges = this.chargeService
				.query("select obj from Charge obj where obj.store.id=:store_id order by addTime desc",
						map, 0, 6);
		mv.addObject("objs", charges);
		return mv;
	}
	
	
	@SecurityMapping(title = "经销商所有授信的会员ajax加载", value = "/weixin/buyer/charge_user_list_ajax.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/charge_user_list_ajax.htm")
	public ModelAndView charge_user_list_ajax(HttpServletRequest request,
			HttpServletResponse response,String beginCount) {
		ModelAndView mv = new JModelAndView("weixin/charge_user_list_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Charge> charges = null;
		Map map = new HashMap();
		
		map.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
		charges = this.chargeService
				.query("select obj from Charge obj where obj.store.id=:store_id order by addTime desc",
						map, CommUtil.null2Int(beginCount), 6);
		mv.addObject("objs", charges);
		mv.addObject("beginCount", beginCount);
		return mv;
	}
	
	
	@SecurityMapping(title = "买家赊销信息列表加载", value = "/weixin/buyer/charge_buyer_list.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/charge_buyer_list.htm")
	public ModelAndView charge_buyer_list(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/charge_buyer_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Charge> orders = null;
		Map map = new HashMap();
		
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		orders = this.chargeService
				.query("select obj from Charge obj where obj.user.id=:user_id order by addTime desc",
						map, 0, 6);
		mv.addObject("objs", orders);
		return mv;
	}
	
	@SecurityMapping(title = "买家赊销信息列表加载", value = "/weixin/buyer/charge_buyer_list_ajax.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/charge_buyer_list_ajax.htm")
	public ModelAndView charge_buyer_list_ajax(HttpServletRequest request,
			HttpServletResponse response,String beginCount) {
		ModelAndView mv = new JModelAndView("weixin/charge_buyer_list_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Charge> orders = null;
		Map map = new HashMap();
		
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		orders = this.chargeService
				.query("select obj from Charge obj where obj.user.id=:user_id order by addTime desc",
						map, CommUtil.null2Int(beginCount), 6);
		mv.addObject("objs", orders);
		mv.addObject("beginCount", beginCount);
		return mv;
	}
	
	/**
	 * 订单详情
	 * 
	 * @param request
	 * @param response
	 * @param id
	 *            :订单id
	 * @return
	 */
	@SecurityMapping(title = "订单详情", value = "/weixin/buyer/order_view.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_view.htm")
	public ModelAndView weixin_order_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("weixin/order_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getUser() != null
				&& obj.getUser().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
			try {
				mv.addObject("payment", obj.getTotalPrice().subtract(obj.getCharge_Num()));
			} catch (Exception e) {
				mv.addObject("payment", 0);
			}
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单取消", value = "/weixin/buyer/order_cancel.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_cancel.htm")
	public ModelAndView weixin_order_cancel(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("weixin/buyer_order_cancel.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if(obj.getOrder_status()==10||obj.getOrder_status()==15){
		if (obj.getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		}else{
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单无法取消！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单取消保存", value = "/weixin/buyer/order_cancel_save.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_cancel_save.htm")
	public Object weixin_order_cancel_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info) throws Exception {
		return this.orderFormService.weixin_order_cancel_save(request, response, id, currentPage, state_info, other_state_info);
	}

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
	@SecurityMapping(title = "收货确认保存", value = "/weixin/buyer/order_cofirm_save.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_cofirm_save.htm")
	public String weixin_order_cofirm_save(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return this.orderFormService.weixin_order_cofirm_save(request, response);
	}

	@SecurityMapping(title = "提示是否确认收货", value = "/weixin/buyer/order_cofirm_prompt.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/weixin/buyer/order_cofirm_prompt.htm")
	public ModelAndView order_cofirm_prompt(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("weixin/confirm.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		HttpSession session = request.getSession(false);
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		session.setAttribute("ofid", CommUtil.null2Long(id));
		mv.addObject("globle_title","确认收货");
		mv.addObject("title","确认收货");
		mv.addObject("path","确认收货"); 
		mv.addObject("message", "您是否确已经收到该订单的货品? 订单号： "+of.getOrder_id()+" 注意：如果你尚未收到货品请不要点击“确认”。大部分被骗案件都是由于提前确认付款被骗的，请谨慎操作！");
		mv.addObject("sure", "order_cofirm_save.htm");
		return mv;
	}

	@SecurityMapping(title = "买家评价", value = "/weixin/buyer/order_evaluate.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_evaluate.htm")
	public ModelAndView weixin_order_evaluate(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("weixin/buyer_order_evaluate.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
			if (obj.getOrder_status() >= 50) {
				mv = new JModelAndView("weixin/success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单已经评价！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/buyer/order.htm");
			}
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "买家评价保存", value = "/weixin/buyer/order_evaluate_save.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_evaluate_save.htm")
	public ModelAndView weixin_order_evaluate_save(HttpServletRequest request,
			HttpServletResponse response, String id) throws Exception {
		return this.orderFormService.weixin_order_evaluate_save(request, response, id);
	}

	/**
	 * 微信查看订单物流
	 * 
	 * @param request
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "订单列表", value = "/weixin/buyer/ship_view.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/ship_view.htm")
	public ModelAndView weixin_ship_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("weixin/ship_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
			TransInfo transInfo = this.query_ship_getData(CommUtil
					.null2String(obj.getId()));
			mv.addObject("transInfo", transInfo);
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	private TransInfo query_ship_getData(String id) {
		TransInfo info = new TransInfo();
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		try {
			String query_url = "http://api.kuaidi100.com/api?id="
					+ this.configService.getSysConfig().getKuaidi_id()
					+ "&com="
					+ (obj.getEc() != null ? obj.getEc().getCompany_mark() : "")
					+ "&nu=" + obj.getShipCode() + "&show=0&muti=1&order=asc";
			URL url = new URL(query_url);
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

	private void send_email(HttpServletRequest request, OrderForm order,
			String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template.isOpen()) {
			String email = order.getStore().getUser().getEmail();
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
				e.printStackTrace();
			}
		 }
		}

	@RequestMapping("/weixin/check_goods_order.htm")
	public void check_goods_order(HttpServletRequest request, HttpServletResponse response, String order_id)
			throws ClassNotFoundException {
		OrderForm sc = this.orderFormService.getObjById(Long.valueOf(order_id));
		String val = "";
		if (sc != null) {
			if (null != sc.getGcs()) {
				List<GoodsCart> goodsCartList = sc.getGcs();
				for (GoodsCart goodsCart : goodsCartList) {
					Goods goods = goodsCart.getGoods();
					if (goods.getGoods_status() != 0) {
						val = goods.getGoods_name();
						break;
					}
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
	@SecurityMapping(title = "买家退货申请", value = "/weixin/buyer/order_return_apply.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_return_apply.htm")
	public ModelAndView order_return_apply(HttpServletRequest request,
			HttpServletResponse response, String id, String view) {
		ModelAndView mv;
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv = new JModelAndView("weixin/order_return_apply.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("obj", obj);
			if (view != null && !view.equals("")) {
				mv.addObject("view", true);
			}
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + obj.getOrder_id() + "的订单！");
			mv.addObject("url", CommUtil.getURL(request)+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "买家退货申请保存", value = "/weixin/buyer/order_return_apply_save.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_return_apply_save.htm")
	public ModelAndView order_return_apply_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String return_content) throws Exception {
		ModelAndView mv;
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			obj.setOrder_status(45); 
			obj.setReturn_content(return_content);
			this.orderFormService.update(obj); 
			//发送消息同pc端
			mv = new JModelAndView("weixin/success.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("obj", obj);
			mv.addObject("url", CommUtil.getURL(request)+ "/weixin/buyer/order.htm");
			//捕捉发送短信异常
			try {
				if (this.configService.getSysConfig().isEmailEnable()) {
					this.send_email(request, obj,
							"email_toseller_order_return_apply_notify");
				}
				if (this.configService.getSysConfig().isSmsEnbale()) {
					String phone=obj.getStore().getUser().getTelephone();
					if(phone==null)
						phone=obj.getStore().getUser().getMobile();
					this.send_sms(request, obj, phone,
							"sms_toseller_order_return_apply_notify");
				}
				mv.addObject("op_title", "退货申请提交成功!");
			} catch (Exception e) {
				mv.addObject("op_title", "退货申请提交成功!");
			}
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + obj.getOrder_id() + "的订单！");
			mv.addObject("url", CommUtil.getURL(request)+ "/weixin/buyer/order.htm");
		}
		return mv;
	}
	
	/**
	 * 个人中心客服电话
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "客服电话", value = "/weixin/buyer/customerService.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/customerService.htm")
	public ModelAndView goods_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/customerService.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		return mv;
	}
	
	
	//取消订单后，释放库存
	public void release_goods_inventory(OrderForm order) {
		// 取消订单后，释放商品库存，如果是团购商品，则释放团购库存
		for (GoodsCart gc : order.getGcs()) {
			Goods goods = gc.getGoods();
//			if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
//				for (GroupGoods gg : goods.getGroup_goods_list()) {
//					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
//						gg.setGg_def_count(gg.getGg_def_count() - gc.getCount());
//						gg.setGg_count(gg.getGg_count() + gc.getCount());
//						this.groupGoodsService.update(gg);
//					}
//				}
//			}
//			List<String> gsps = new ArrayList<String>();
//			for (GoodsSpecProperty gsp : gc.getGsps()) {
//				gsps.add(gsp.getId().toString());
//			}
//			String[] gsp_list = new String[gsps.size()];
//			gsps.toArray(gsp_list);
			//取消订单返回赊销金额
			goods.setGoods_salenum(goods.getGoods_salenum() - gc.getCount());
			String query="select from Charge where user.id=:uid and store.id=:sid";
			Map<String,Object> params=new HashMap<String, Object>();
			params.put("uid", order.getUser().getId());
			params.put("sid", order.getStore().getId());
			List<Charge> list = this.chargeDao.query(query, params, -1, -1);
			try {
				Charge charge=list.get(0);				
				charge.setPaymentNum(order.getCharge_Num().add(charge.getPaymentNum()));
				this.chargeDao.update(charge);
			} catch (Exception e) {
				System.out.println("买家赊销信息为空或异常！");
			}finally{
				order.setCharge_Num(new BigDecimal(0));
				this.orderFormDao.update(order);
			}			
			String inventory_type = goods.getInventory_type() == null ? "all"
					: goods.getInventory_type();
			if (inventory_type.equals("all")) {
				goods.setGoods_inventory(goods.getGoods_inventory()
						+ gc.getCount());
			} 
//				else {
//				List<HashMap> list = Json
//						.fromJson(ArrayList.class, CommUtil.null2String(goods
//								.getGoods_inventory_detail()));
//				for (Map temp : list) {
//					String[] temp_ids = CommUtil.null2String(temp.get("id"))
//							.split("_");
//					Arrays.sort(temp_ids);
//					Arrays.sort(gsp_list);
//					if (Arrays.equals(temp_ids, gsp_list)) {
//						temp.put("count", CommUtil.null2Int(temp.get("count"))
//								+ gc.getCount());
//					}
//				}
//				goods.setGoods_inventory_detail(Json.toJson(list,
//						JsonFormat.compact()));
//			}
//			for (GroupGoods gg : goods.getGroup_goods_list()) {
//				if (gg.getGroup().getId().equals(goods.getGroup().getId())
//						&& gg.getGg_count() == 0) {
//					goods.setGroup_buy(3);// 标识商品的状态为团购数量已经结束
//				}
//			}
			this.goodsService.update(goods);
			// 更新lucene索引
			LuceneUtil.instance().ruLuceneIndex(goods,"u");
		}
	}
}
