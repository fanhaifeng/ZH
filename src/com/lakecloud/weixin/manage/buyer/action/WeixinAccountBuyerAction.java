package com.lakecloud.weixin.manage.buyer.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.Md5Encrypt;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.CouponInfo;
import com.lakecloud.foundation.domain.Favorite;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.Integration;
import com.lakecloud.foundation.domain.Integration_Child;
import com.lakecloud.foundation.domain.Integration_Log;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.Template;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.ICouponInfoService;
import com.lakecloud.foundation.service.IFavoriteService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IIntegrationService;
import com.lakecloud.foundation.service.IIntegration_ChildService;
import com.lakecloud.foundation.service.IIntegration_LogService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.ITemplateService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.manage.admin.tools.MsgTools;
import com.lakecloud.uc.api.UCClient;

/**
 * @info 微信客户端买家用户中心
 * @since V1.3
 * @version 1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net hz 2013-11-25
 * 
 */
@Controller
public class WeixinAccountBuyerAction {
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
	private IFavoriteService favoriteService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IIntegrationService integrationService;
	@Autowired
	private IIntegration_LogService integration_logService;
	@Autowired
	private IIntegration_ChildService integration_ChildService;
	
	

	private int orderGoodsCount(List<OrderForm> OrderForms) {
		int goods_count = 0;
		for (OrderForm order : OrderForms) {
			for (GoodsCart gc : order.getGcs()) {
				if (gc.getGoods() != null && !gc.equals("")) {
					goods_count++;
				}
			}
		}
		return goods_count;
	}

	/**
	 * 用户中心
	 * 
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "用户中心", value = "/weixin/buyer/account.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/account.htm")
	public ModelAndView weixin_buyer_account(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/account.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Map map = new HashMap();
		map.put("uid", user.getId());
		map.put("status", 0);
		List<CouponInfo> CouponInfos = this.couponInfoService
				.query(
						"select obj from CouponInfo obj where obj.user.id=:uid and obj.status=:status",
						map, -1, -1);
		map.clear();
		map.put("order_status", 10);
		map.put("uid", user.getId());
		List<OrderForm> order_status_10 = this.orderFormService
				.query(
						"select obj from OrderForm obj where obj.order_status=:order_status and obj.user.id=:uid order by addTime desc",
						map, -1, -1);
		map.clear();
		List<Integer> list = new ArrayList<Integer>();
		list.add(20);
		list.add(16);
		map.put("order_status", list);
		map.put("uid", user.getId());
		List<OrderForm> order_status_20 = this.orderFormService
				.query(
						"select obj from OrderForm obj where obj.order_status in :order_status and obj.user.id=:uid order by addTime desc",
						map, -1, -1);
		map.clear();
		map.put("order_status", 30);
		map.put("uid", user.getId());
		List<OrderForm> order_status_30 = this.orderFormService
				.query(
						"select obj from OrderForm obj where obj.order_status=:order_status and obj.user.id=:uid order by addTime desc",
						map, -1, -1);
		map.clear();
		map.put("order_status", 40);
		map.put("uid", user.getId());
		List<OrderForm> order_status_40 = this.orderFormService
				.query(
						"select obj from OrderForm obj where obj.order_status=:order_status and obj.user.id=:uid order by addTime desc",
						map, -1, -1);
		mv.addObject("goods_count_10", order_status_10.size());
		mv.addObject("goods_count_20", order_status_20.size());
		mv.addObject("goods_count_30", order_status_30.size());
		mv.addObject("goods_count_40", order_status_40.size());
		mv.addObject("Coupons", CouponInfos.size());
		mv.addObject("user", user);
		return mv;
	}

	/**
	 * 我的收藏列表页
	 * 
	 * @param beginCount
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "微信用户我的收藏", value = "/weixin/buyer/favorite_goods.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/favorite_goods.htm")
	public ModelAndView weixin_favorite_goods(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/favorite_goods.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("type", 0);
		params.put("uid", SecurityUserHolder.getCurrentUser().getId());
		List<Favorite> objs = this.favoriteService
				.query(
						"select obj from Favorite obj where obj.type=:type and obj.user.id=:uid",
						params, 0, 6);
		mv.addObject("objs", objs);
		return mv;
	}

	/**
	 * 我的农豆页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/weixin/buyer/mybean.htm")
	public ModelAndView mybean(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/mybean.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		//查询用户智慧豆
		String query="from Integration_Child obj where obj.user.id=:uid and obj.type=0";
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("uid", user.getId());
		List<Integration_Child> list_zh = this.integration_ChildService.query(query, params, -1, -1);
		if(list_zh!=null&&list_zh.size()==1){
			mv.addObject("integration_zh",list_zh.get(0).getIntegrals());
		}
	  //查询用户农豆
		query="from Integration_Child obj where obj.user.id=:uid and obj.type=1";
		params.clear();
		params.put("uid", user.getId());
		Integer total=0;
		Integer total_over=0;
		List<Integration_Child> list_dp = this.integration_ChildService.query(query, params, -1, -1);
		if(list_dp!=null&&list_dp.size()>0){
			for (Integration_Child integration_Child : list_dp) {
			total+=integration_Child.getIntegrals();
			total_over+=integration_Child.getOverdue_integrals()==null?0:integration_Child.getOverdue_integrals();
      }
			mv.addObject("integration_dp_total",total);
			mv.addObject("integration_dp_over",total_over);
		}		
		//查询当前用户积分
		query="from Integration_Log obj where obj.user.id=:uid order by obj.addTime desc";
		params.clear();
		params=new HashMap<String, Object>();
		params.put("uid", user.getId());
		List<Integration_Log> list = this.integration_logService.query(query, params, 0, 6);
		mv.addObject("objs",list);
		return mv;
	}
	/**
	 * 我的农豆ajax加载页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/weixin/buyer/mybean_ajax.htm")
	public ModelAndView mybean_ajax(HttpServletRequest request,
			HttpServletResponse response,String beginCount) {
		ModelAndView mv = new JModelAndView("weixin/mybean_data.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		int begin = 0;
		if (beginCount != null && !beginCount.equals("")) {
			begin = CommUtil.null2Int(beginCount);
		}
		//查询当前用户积分
		Integration integration = this.integrationService.getObjByProperty("user.id", user.getId());
		String query="from Integration_Log obj where obj.user.id=:uid order by obj.addTime desc";
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("uid", user.getId());
		List<Integration_Log> list = this.integration_logService.query(query, params, begin, 6);
		mv.addObject("objs",list);
		mv.addObject("integration",integration);
		return mv;
	}
	/**
	 * 我的收藏列表页-店铺
	 * 
	 * @param beginCount
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "微信用户我的收藏", value = "/weixin/buyer/favorite_store.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/favorite_store.htm")
	public ModelAndView weixin_favorite_store(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/favorite_store.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("type", 1);
		params.put("uid", SecurityUserHolder.getCurrentUser().getId());
		List<Favorite> objs = this.favoriteService
				.query(
						"select obj from Favorite obj where obj.type=:type and obj.user.id=:uid",
						params, 0, 6);
		mv.addObject("objs", objs);
		return mv;
	}

	/**
	 * 我的收藏列表页ajax加载
	 * 
	 * @param beginCount
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "微信用户我的收藏", value = "/weixin/buyer/favorite_goods_ajax.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/favorite_goods_ajax.htm")
	public ModelAndView weixin_favorite_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String beginCount) {
		int begin = 0;
		if (beginCount != null && !beginCount.equals("")) {
			begin = CommUtil.null2Int(beginCount);
		}
		ModelAndView mv = new JModelAndView("weixin/favorite_goods_data.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("type", 0);
		params.put("uid", SecurityUserHolder.getCurrentUser().getId());
		List<Favorite> objs = this.favoriteService
				.query(
						"select obj from Favorite obj where obj.type=:type and obj.user.id=:uid",
						params, begin, 6);
		mv.addObject("objs", objs);
		return mv;
	}

	/**
	 * 我的收藏列表页ajax加载-店铺
	 * 
	 * @param beginCount
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "微信用户我的收藏", value = "/weixin/buyer/favorite_store_ajax.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/favorite_store_ajax.htm")
	public ModelAndView weixin_favorite_store_ajax(HttpServletRequest request,
			HttpServletResponse response, String beginCount) {
		int begin = 0;
		if (beginCount != null && !beginCount.equals("")) {
			begin = CommUtil.null2Int(beginCount);
		}
		ModelAndView mv = new JModelAndView("weixin/favorite_store_data.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("type", 1);
		params.put("uid", SecurityUserHolder.getCurrentUser().getId());
		List<Favorite> objs = this.favoriteService
				.query(
						"select obj from Favorite obj where obj.type=:type and obj.user.id=:uid",
						params, begin, 6);
		mv.addObject("objs", objs);
		return mv;
	}

	// @SecurityMapping(title = "微信用户惠券列表", value = "/weixin/buyer/coupon.htm*",
	// rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup =
	// "微信用户中心")
	// @RequestMapping("/weixin/buyer/coupon.htm")
	// public ModelAndView weixin_coupon(HttpServletRequest request,
	// HttpServletResponse response) {
	// ModelAndView mv = new JModelAndView("weixin/coupon.html",
	// configService.getSysConfig(),
	// this.userConfigService.getUserConfig(), 1, request, response);
	// Map params = new HashMap();
	// params.put("uid", SecurityUserHolder.getCurrentUser().getId());
	// List<CouponInfo> objs = this.couponInfoService
	// .query("select obj from CouponInfo obj where obj.user.id=:uid order by addTime desc",
	// params, 0, 6);
	// mv.addObject("objs", objs);
	// return mv;
	// }
	@SecurityMapping(title = "微信用户惠券列表ajax加载", value = "/weixin/buyer/coupon_ajax.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/coupon_ajax.htm")
	public ModelAndView weixin_coupon_ajax(HttpServletRequest request,
			HttpServletResponse response, String beginCount) {
		ModelAndView mv = new JModelAndView("weixin/coupon_data.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		int begin = 0;
		if (beginCount != null && !beginCount.equals("")) {
			begin = CommUtil.null2Int(beginCount);
		}
		Map params = new HashMap();
		params.put("uid", SecurityUserHolder.getCurrentUser().getId());
		List<CouponInfo> objs = this.couponInfoService
				.query(
						"select obj from CouponInfo obj where obj.user.id=:uid order by addTime desc",
						params, begin, 6);
		mv.addObject("objs", objs);
		return mv;
	}

	@SecurityMapping(title = "微信修改密码", value = "/weixin/buyer/changepass.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/changepass.htm")
	public ModelAndView weixin_coupon(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/changepass.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		return mv;
	}

	@SecurityMapping(title = "密码修改保存", value = "/buyer/changepass.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/buyer/changepass.htm")
	public ModelAndView account_password_save(HttpServletRequest request,
			HttpServletResponse response, String old_password,
			String new_password) throws Exception {
		ModelAndView mv = new JModelAndView("Weixin_success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		WebForm wf = new WebForm();
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (user.getPassword().equals(
				Md5Encrypt.md5(old_password).toLowerCase())) {
			user.setPassword(Md5Encrypt.md5(new_password).toLowerCase());
			boolean ret = this.userService.update(user);
			if (ret && this.configService.getSysConfig().isUc_bbs()) {
				UCClient uc = new UCClient();
				String uc_pws_ret = uc.uc_user_edit(user.getUsername(),
						CommUtil.null2String(old_password), CommUtil
								.null2String(new_password), CommUtil
								.null2String(user.getEmail()), 1, 0, 0);
				// System.out.println(uc_pws_ret);
			}
			mv.addObject("op_title", "密码修改成功");
			this.send_sms(request, "sms_tobuyer_pws_modify_notify");
		} else {
			mv = new JModelAndView("Weixin_error.html", configService
					.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "原始密码输入错误，修改失败");
		}
		mv.addObject("url", CommUtil.getURL(request)
				+ "/weixin/buyer/account.htm");
		return mv;
	}

	private void send_sms(HttpServletRequest request, String mark)
			throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			String mobile = user.getMobile();
			if (mobile != null && !mobile.equals("")) {
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
				p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, request
						.getRealPath("/")
						+ "vm" + File.separator);
				p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
				p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
				Velocity.init(p);
				org.apache.velocity.Template blank = Velocity.getTemplate(
						"msg.vm", "UTF-8");
				VelocityContext context = new VelocityContext();
				context.put("user", user);
				context.put("config", this.configService.getSysConfig());
				context.put("send_time", CommUtil.formatLongDate(new Date()));
				context.put("webPath", CommUtil.getURL(request));
				StringWriter writer = new StringWriter();
				blank.merge(context, writer);
				// System.out.println(writer.toString());
				String content = writer.toString();
				this.msgTools.sendSMS(mobile, content);
			}
		}
	}

	/**
	 * 我的收藏列表页-删除
	 * 
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "微信用户我的收藏", value = "/weixin/buyer/favorite_del.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/favorite_del.htm")
	public String weixin_favorite_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String type) {
		String returnString = "";
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Favorite favorite = this.favoriteService.getObjById(Long
						.parseLong(id));
				if ("1".equals(type)) {// 店铺
					Store store = favorite.getStore();
					store.setFavorite_count(store.getFavorite_count() - 1);
					this.storeService.update(store);
					returnString = "redirect:favorite_store.htm?currentPage="
							+ currentPage;
				} else {
					Goods good = favorite.getGoods();
					good.setGoods_collect(good.getGoods_collect()-1);
					this.goodsService.update(good);
					returnString = "redirect:favorite_goods.htm?currentPage="
							+ currentPage;
				}
				this.favoriteService.delete(Long.parseLong(id));
			}
		}
		return returnString;
	}
}
