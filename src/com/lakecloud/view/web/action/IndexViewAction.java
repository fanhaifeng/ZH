package com.lakecloud.view.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.Md5Encrypt;
import com.lakecloud.foundation.domain.Article;
import com.lakecloud.foundation.domain.ArticleClass;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsBrand;
import com.lakecloud.foundation.domain.GoodsClass;
import com.lakecloud.foundation.domain.GoodsFloor;
import com.lakecloud.foundation.domain.GoodsFloorGoods;
import com.lakecloud.foundation.domain.Message;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.SysConfig;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IAccessoryService;
import com.lakecloud.foundation.service.IArticleClassService;
import com.lakecloud.foundation.service.IArticleService;
import com.lakecloud.foundation.service.IBargainGoodsService;
import com.lakecloud.foundation.service.IDeliveryGoodsService;
import com.lakecloud.foundation.service.IGoodsBrandService;
import com.lakecloud.foundation.service.IGoodsCartService;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.IGoodsFloorGoodsService;
import com.lakecloud.foundation.service.IGoodsFloorService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGroupGoodsService;
import com.lakecloud.foundation.service.IGroupService;
import com.lakecloud.foundation.service.IMessageService;
import com.lakecloud.foundation.service.INavigationService;
import com.lakecloud.foundation.service.IPartnerService;
import com.lakecloud.foundation.service.IRoleService;
import com.lakecloud.foundation.service.IStoreCartService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.manage.admin.tools.MsgTools;
import com.lakecloud.manage.util.CommonUtils;
import com.lakecloud.view.web.tools.GoodsFloorViewTools;
import com.lakecloud.view.web.tools.GoodsViewTools;
import com.lakecloud.view.web.tools.NavViewTools;
import com.lakecloud.view.web.tools.StoreViewTools;

/**
 * @info 商城首页控制器
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Controller
public class IndexViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IPartnerService partnerService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IArticleClassService articleClassService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private INavigationService navigationService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGoodsFloorService goodsFloorService;
	@Autowired
	private IBargainGoodsService bargainGoodsService;
	@Autowired
	private IDeliveryGoodsService deliveryGoodsService;
	@Autowired
	private IStoreCartService storeCartService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private NavViewTools navTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private GoodsFloorViewTools gf_tools;
	@Autowired
	private IGoodsFloorGoodsService goodsFloorGoodsService;

	/**
	 * 前台公用顶部页面，使用自定义标签httpInclude.include("/top.htm")完成页面读取
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/top.htm")
	public ModelAndView top(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("top.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		List<Message> msgs = new ArrayList<Message>();
		if (SecurityUserHolder.getCurrentUser() != null) {
			Map params = new HashMap();
			params.put("status", 0);
			params.put("reply_status", 1);
			params.put("from_user_id", SecurityUserHolder.getCurrentUser()
					.getId());
			params.put("to_user_id", SecurityUserHolder.getCurrentUser()
					.getId());
			msgs = this.messageService
					.query(
							"select obj from Message obj where obj.parent.id is null and (obj.status=:status and obj.toUser.id=:to_user_id) or (obj.reply_status=:reply_status and obj.fromUser.id=:from_user_id) ",
							params, -1, -1);
		}
		Store store = null;
		if (SecurityUserHolder.getCurrentUser() != null)
			store = this.storeService.getObjByProperty("user.id",
					SecurityUserHolder.getCurrentUser().getId());
		mv.addObject("store", store);
		mv.addObject("navTools", navTools);
		mv.addObject("msgs", msgs);
		// List<GoodsCart> list = new ArrayList<GoodsCart>();
		// List<StoreCart> cart = new ArrayList<StoreCart>();// 整体店铺购物车
		// List<StoreCart> user_cart = new ArrayList<StoreCart>();//
		// 当前用户未提交订单的店铺购物车
		// List<StoreCart> cookie_cart = new ArrayList<StoreCart>();//
		// 当前cookie指向的店铺购物车
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
		}
		// String cart_session_id = "";
		// Map params = new HashMap();
		// Cookie[] cookies = request.getCookies();
		// if (cookies != null) {
		// for (Cookie cookie : cookies) {
		// if (cookie.getName().equals("cart_session_id")) {
		// cart_session_id = CommUtil.null2String(cookie.getValue());
		// }
		// }
		// }
		// if (user != null) {
		// if (!cart_session_id.equals("")) {
		// // 如果用户拥有自己的店铺，删除购物车中自己店铺中的商品信息
		// if (user.getStore() != null) {
		// params.clear();
		// params.put("cart_session_id", cart_session_id);
		// params.put("user_id", user.getId());
		// params.put("sc_status", 0);
		// params.put("store_id", user.getStore().getId());
		// List<StoreCart> store_cookie_cart = this.storeCartService
		// .query("select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
		// params, -1, -1);
		// for (StoreCart sc : store_cookie_cart) {
		// for (GoodsCart gc : sc.getGcs()) {
		// gc.getGsps().clear();
		// this.goodsCartService.delete(gc.getId());
		// }
		// this.storeCartService.delete(sc.getId());
		// }
		// }
		// // 查询出cookie中的商品信息
		// params.clear();
		// params.put("cart_session_id", cart_session_id);
		// params.put("sc_status", 0);
		// cookie_cart = this.storeCartService
		// .query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
		// params, -1, -1);
		// // 查询用户未提交订单的购物车信息
		// params.clear();
		// params.put("user_id", user.getId());
		// params.put("sc_status", 0);
		// user_cart = this.storeCartService
		// .query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
		// params, -1, -1);
		// } else {
		// // 查询用户未提交订单的购物车信息
		// params.clear();
		// params.put("user_id", user.getId());
		// params.put("sc_status", 0);
		// user_cart = this.storeCartService
		// .query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
		// params, -1, -1);
		//
		// }
		// } else {
		// if (!cart_session_id.equals("")) {
		// params.clear();
		// params.put("cart_session_id", cart_session_id);
		// params.put("sc_status", 0);
		// cookie_cart = this.storeCartService
		// .query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
		// params, -1, -1);
		// }
		// }
		//
		// // 合并当前用户未提交订单的店铺购物车和当前cookie指向的店铺购物车
		// for (StoreCart sc : user_cart) {
		// boolean sc_add = true;
		// for (StoreCart sc1 : cart) {
		// if (sc1.getStore().getId().equals(sc.getStore().getId())) {
		// sc_add = false;
		// }
		// }
		// if (sc_add) {
		// cart.add(sc);
		// }
		// }
		// for (StoreCart sc : cookie_cart) {
		// boolean sc_add = true;
		// for (StoreCart sc1 : cart) {
		// if (sc1.getStore().getId().equals(sc.getStore().getId())) {
		// sc_add = false;
		// for (GoodsCart gc : sc.getGcs()) {
		// gc.setSc(sc1);
		// this.goodsCartService.update(gc);
		// }
		// this.storeCartService.delete(sc.getId());
		// }
		// }
		// if (sc_add) {
		// cart.add(sc);
		// }
		// }
		// if (cart != null) {
		// for (StoreCart sc : cart) {
		// if (sc != null) {
		// list.addAll(sc.getGcs());
		// }
		// }
		// }
		// float total_price = 0;
		// for (GoodsCart gc : list) {
		// Goods goods = this.goodsService.getObjById(gc.getGoods().getId());
		// if (CommUtil.null2String(gc.getCart_type()).equals("combin")) {
		// total_price = CommUtil.null2Float(goods.getCombin_price());
		// } else {
		// total_price = CommUtil.null2Float(CommUtil.mul(gc.getCount(),
		// goods.getGoods_current_price())) + total_price;
		// }
		// }
		// mv.addObject("total_price", total_price);
		// mv.addObject("cart", list);
		return mv;
	}

	/**
	 * 不含购物车的顶部页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/top1.htm")
	public ModelAndView top1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("top1.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		List<Message> msgs = new ArrayList<Message>();
		if (SecurityUserHolder.getCurrentUser() != null) {
			Map params = new HashMap();
			params.put("status", 0);
			params.put("reply_status", 1);
			params.put("from_user_id", SecurityUserHolder.getCurrentUser()
					.getId());
			params.put("to_user_id", SecurityUserHolder.getCurrentUser()
					.getId());
			msgs = this.messageService
					.query(
							"select obj from Message obj where obj.parent.id is null and (obj.status=:status and obj.toUser.id=:to_user_id) or (obj.reply_status=:reply_status and obj.fromUser.id=:from_user_id) ",
							params, -1, -1);
		}
		Store store = null;
		if (SecurityUserHolder.getCurrentUser() != null)
			store = this.storeService.getObjByProperty("user.id",
					SecurityUserHolder.getCurrentUser().getId());
		mv.addObject("store", store);
		mv.addObject("navTools", navTools);
		mv.addObject("msgs", msgs);
		return mv;
	}

	/**
	 * 前台公用导航主菜单页面，使用自定义标签httpInclude.include("/nav.htm")完成页面读取
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/nav.htm")
	public ModelAndView nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("nav.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		mv.addObject("navTools", navTools);
		Map params = new HashMap();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query(
						"select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, 0, 15);
		mv.addObject("gcs", gcs);
		return mv;
	}

	/**
	 * 带有全部商品分类的导航菜单，使用自定义标签httpInclude.include("/nav1.htm")完成页面读取
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/nav1.htm")
	public ModelAndView nav1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("nav1.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		Map params = new HashMap();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query(
						"select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, 0, 15);
		mv.addObject("gcs", gcs);
		mv.addObject("navTools", navTools);
		return mv;
	}

	/**
	 * 前台公用head页面，包含系统logo及全文搜索，使用自定义标签httpInclude.include("/head.htm")完成页面读取
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/head.htm")
	public ModelAndView head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("head.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		String type = CommUtil.null2String(request.getAttribute("type"));
		mv.addObject("type", type.equals("") ? "goods" : type);
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			int types = this.storeCartService.query_goods_type(user.getId());
			mv.addObject("types", types);
		}
		return mv;
	}
	
	@RequestMapping("/head2.htm")
	public ModelAndView head2(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("head2.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		String type = CommUtil.null2String(request.getAttribute("type"));
		mv.addObject("type", type.equals("") ? "goods" : type);
		return mv;
	}

	/**
	 * 用户登录页顶部，使用include加载，V1.3版本开始
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/login_head.htm")
	public ModelAndView login_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("login_head.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		return mv;
	}

	/**
	 * 首页商品楼层数据，该数据纳入系统缓存页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/floor.htm")
	public ModelAndView floor(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("floor.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		// Map params = new HashMap();
		// params.put("gf_display", true);
		// List<GoodsFloor> floors = this.goodsFloorService
		// .query(
		// "select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null order by obj.gf_sequence asc",
		// params, -1, -1);
		// mv.addObject("floors", floors);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("url", CommUtil.getURL(request));
		getFloors(request, mv);
		return mv;
	}

	/**
	 * 前台公用顶部导航页面，使用自定义标签httpInclude.include("/footer.htm")完成页面读取
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/footer.htm")
	public ModelAndView footer(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("footer.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		mv.addObject("navTools", navTools);
		return mv;
	}

	/**
	 * 商城首页
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("index.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		/**
		 * 获取cookies
		 * */
		String pc_city_id = CommonUtils.getCityIdFromCookies(request);
		Map params = new HashMap();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query(
						"select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, 0, 15);
		mv.addObject("gcs", gcs);
		params.clear();
		params.put("audit", 1);
		params.put("recommend", true);
		List<GoodsBrand> gbs = this.goodsBrandService
				.query(
						"select obj from GoodsBrand obj where obj.audit=:audit and obj.recommend=:recommend order by obj.sequence",
						params, -1, -1);
		mv.addObject("gbs", gbs);
		// params.clear();
		// List<Partner> img_partners = this.partnerService
		// .query(
		// "select obj from Partner obj where obj.image.id is not null order by obj.sequence asc",
		// params, -1, -1);
		// mv.addObject("img_partners", img_partners);
		// List<Partner> text_partners = this.partnerService
		// .query(
		// "select obj from Partner obj where obj.image.id is null order by obj.sequence asc",
		// params, -1, -1);
		// mv.addObject("text_partners", text_partners);
		params.clear();
		params.put("mark", "news");
		params.put("mark_help", "help");// 帮助中心去掉
		List<ArticleClass> acs = this.articleClassService
				.query(
						"select obj from ArticleClass obj where obj.parent.id is null and obj.mark!=:mark and obj.mark!=:mark_help order by obj.sequence asc",
						params, 0, 9);
		mv.addObject("acs", acs);
		params.clear();
		params.put("class_mark", "news");
		params.put("display", true);
		List<Article> articles = this.articleService
				.query(
						"select obj from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc",
						params, 0, 5);
		mv.addObject("articles", articles);
		// params.clear();
		if (SecurityUserHolder.getCurrentUser() != null) {
			mv.addObject("user", this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId()));
		}
		// params.clear();
		// params.put("beginTime", new Date());
		// params.put("endTime", new Date());
		// List<Group> groups = this.groupService
		// .query(
		// "select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime",
		// params, -1, -1);
		// if (groups.size() > 0) {
		// params.clear();
		// params.put("gg_status", 1);
		// params.put("gg_recommend", 1);
		// params.put("group_id", groups.get(0).getId());
		// List<GroupGoods> ggs = this.groupGoodsService
		// .query(
		// "select obj from GroupGoods obj where obj.gg_status=:gg_status and obj.gg_recommend=:gg_recommend and obj.group.id=:group_id order by obj.gg_recommend_time desc",
		// params, 0, 1);
		// if (ggs.size() > 0)
		// mv.addObject("group", ggs.get(0));
		// }
		// params.clear();
		// params.put("bg_time", CommUtil.formatDate(CommUtil
		// .formatShortDate(new Date())));
		// params.put("bg_status", 1);
		// List<BargainGoods> bgs = this.bargainGoodsService
		// .query(
		// "select obj from BargainGoods obj where obj.bg_time=:bg_time and obj.bg_status=:bg_status",
		// params, 0, 6);
		// mv.addObject("bgs", bgs);
		// params.clear();
		// params.put("d_status", 1);
		// params.put("d_begin_time", new Date());
		// params.put("d_end_time", new Date());
		// List<DeliveryGoods> dgs = this.deliveryGoodsService
		// .query(
		// "select obj from DeliveryGoods obj where obj.d_status=:d_status and obj.d_begin_time<=:d_begin_time and obj.d_end_time>=:d_end_time order by obj.d_audit_time desc",
		// params, 0, 5);
		// mv.addObject("dgs", dgs);
		mv.addObject("pc_city_id", pc_city_id);
		return mv;
	}

	/**
	 * 商城关闭时候导向的请求
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/close.htm")
	public ModelAndView close(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("close.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		return mv;
	}

	@RequestMapping("/404.htm")
	public ModelAndView error404(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("404.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		String iskyshop_view_type = CommUtil.null2String(request.getSession(
				false).getAttribute("iskyshop_view_type"));
		if (iskyshop_view_type != null && !iskyshop_view_type.equals("")) {
			if (iskyshop_view_type.equals("weixin")) {
				String store_id = CommUtil.null2String(request
						.getSession(false).getAttribute("store_id"));
				mv = new JModelAndView("weixin/404.html", configService
						.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/platform/index.htm");
			}
		}
		return mv;
	}

	@RequestMapping("/500.htm")
	public ModelAndView error500(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("500.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		String iskyshop_view_type = CommUtil.null2String(request.getSession(
				false).getAttribute("iskyshop_view_type"));
		if (iskyshop_view_type != null && !iskyshop_view_type.equals("")) {
			if (iskyshop_view_type.equals("weixin")) {
				String store_id = CommUtil.null2String(request
						.getSession(false).getAttribute("store_id"));
				mv = new JModelAndView("weixin/500.html", configService
						.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/platform/index.htm");
			}
		}
		return mv;
	}

	/**
	 * 商城商品分类导航页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_class.htm")
	public ModelAndView goods_class(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("goods_class.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		Map params = new HashMap();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query(
						"select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}

	/**
	 * 忘记密码页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/forget.htm")
	public ModelAndView forget(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("forget.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		SysConfig config = this.configService.getSysConfig();
		if (!config.isEmailEnable()) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统关闭邮件功能，不能找回密码");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 通过邮件发送新密码来找回密码
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/find_pws.htm")
	public ModelAndView find_pws(HttpServletRequest request,
			HttpServletResponse response, String userName, String email,
			String code) {
		ModelAndView mv = new JModelAndView("success.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		HttpSession session = request.getSession(false);
		String verify_code = (String) session.getAttribute("verify_code");
		if (code.toUpperCase().equals(verify_code)) {
			User user = this.userService.getObjByProperty("userName", userName);
			if (user.getEmail().equals(email.trim())) {
				String pws = CommUtil.randomString(6).toLowerCase();
				String subject = this.configService.getSysConfig().getTitle()
						+ "密码找回邮件";
				String content = user.getUsername() + ",您好！您通过密码找回功能重置密码，新密码为："
						+ pws;
				boolean ret = this.msgTools.sendEmail(email, subject, content);
				if (ret) {
					user.setPassword(Md5Encrypt.md5(pws));
					this.userService.update(user);
					mv.addObject("op_title", "新密码已经发送到邮箱:<font color=red>"
							+ email + "</font>，请查收后重新登录");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/user/login.htm");
				} else {
					mv = new JModelAndView("error.html", configService
							.getSysConfig(), this.userConfigService
							.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "邮件发送失败，密码暂未执行重置");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/forget.htm");
				}
			} else {
				mv = new JModelAndView("error.html", configService
						.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "用户名、邮箱不匹配");
				mv.addObject("url", CommUtil.getURL(request) + "/forget.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "验证码不正确");
			mv.addObject("url", CommUtil.getURL(request) + "/forget.htm");
		}
		return mv;
	}

	/**
	 * 首页推荐商品换一组请求，随机出一组推荐商品信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/switch_recommend_goods.htm")
	public ModelAndView switch_recommend_goods(HttpServletRequest request,
			HttpServletResponse response, String recommend_goods_random) {
		ModelAndView mv = new JModelAndView("switch_recommend_goods.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		/**
		 * 获取cookies
		 * */
		String pc_city_id = CommonUtils.getCityIdFromCookies(request);
		Map params = new HashMap();
		params.put("store_recommend", true);
		params.put("goods_status", 0);
		String hql = "select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status";
		// 添加城市筛选
		if (!"all".equals(pc_city_id) && !"-1".equals(pc_city_id)) {
			hql += " and obj.goods_store.area.parent.id=:pc_city_id ";
			params.put("pc_city_id", Long.parseLong(pc_city_id));
		}
		hql += " order by obj.store_recommend_time desc";
		List<Goods> store_reommend_goods_list = this.goodsService.query(hql,
				params, -1, -1);
		List<Goods> store_reommend_goods = new ArrayList<Goods>();
		int begin = CommUtil.null2Int(recommend_goods_random) * 5;
		if (begin > store_reommend_goods_list.size() - 1) {
			begin = 0;
		}
		int max = begin + 5;
		if (max > store_reommend_goods_list.size()) {
			begin = begin - (max - store_reommend_goods_list.size());
			max = max - 1;
		}
		for (int i = 0; i < store_reommend_goods_list.size(); i++) {
			if (i >= begin && i < max) {
				store_reommend_goods.add(store_reommend_goods_list.get(i));
			}
		}
		mv.addObject("store_reommend_goods", store_reommend_goods);
		return mv;
	}

	/**
	 * 系统只允许单用户登录，第二次登陆后提出先前用户的请求
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/outline.htm")
	public ModelAndView outline(HttpServletRequest request,
			HttpServletResponse response) {
		String c_lastLogin_role = "";
		String return_url = "";
		ModelAndView mv = new JModelAndView("error.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		Cookie[] cookies = request.getCookies();// 这样便可以获取一个cookie数组
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("lastLogin_role")) {
				c_lastLogin_role = cookie.getValue();
			}
		}
		if (c_lastLogin_role.equals("admin")) {
			return_url = CommUtil.getURL(request) + "/admin/login.htm";
		} else if (c_lastLogin_role.equals("seller")) {
			return_url = CommUtil.getURL(request) + "/seller/login.htm";
		} else if (c_lastLogin_role.equals("weixin")) {
			return_url = CommUtil.getURL(request)
					+ "/weixin/platform/index.htm";
			mv = new JModelAndView("weixin/error.html", configService
					.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
		} else {
			return_url = CommUtil.getURL(request) + "/index.htm";
		}
		mv.addObject("op_title", "该用户在其他地点登录，您被迫下线！");
		mv.addObject("url", return_url);
		return mv;
	}

	/**
	 * 城市的顶部页面
	 */
	@RequestMapping("/city.htm")
	public ModelAndView city(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("city.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		return mv;
	}

	private void getFloors(HttpServletRequest request, ModelAndView mv) {
		List<GoodsFloorGoods> list = null;
		String hql = "";
		Map params = new HashMap();
		params.put("gf_display", true);
		List<GoodsFloor> floors = this.goodsFloorService
				.query(
						"select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null order by obj.gf_sequence asc",
						params, -1, -1);
		/**
		 * 获取cookies
		 * */
		String pc_city_id = CommonUtils.getCityIdFromCookies(request);
		// 添加城市筛选
		if (!"all".equals(pc_city_id) && !"-1".equals(pc_city_id)) {
			hql = " and obj.goods_store.area.parent.id=:pc_city_id ";
			params.put("pc_city_id", Long.parseLong(pc_city_id));
		}
		list = this.goodsFloorGoodsService
				.query(
						"select obj from GoodsFloorGoods obj where obj.goodsFloor.gf_display=:gf_display and obj.goodsFloor.parent.id is null and obj.goods.goods_status=0"
								+ hql
								+ " order by obj.goodsFloor.gf_sequence asc",
						params, -1, -1);
		// mv.addObject("floors", set_gf_list_goods_json(floors, pc_city_id));
		mv.addObject("floors", floors);
		mv.addObject("goodsFloorGoods", list);
		// mv.addObject("gf_tools", this.gf_tools);
		// mv.addObject("url", CommUtil.getURL(request));
	}

	/**
	 * unused,GoodsFloor中的gf_list_goods根据区域进行过滤
	 */
	private List<GoodsFloor> set_gf_list_goods_json(List<GoodsFloor> floors,
			String pc_city_id) {
		String json_neo = "{";
		for (GoodsFloor goodsFloor : floors) {
			String json_original = goodsFloor.getGf_list_goods();
			if (json_original != null && !json_original.equals("")) {
				if (!"all".equals(pc_city_id) && !"-1".equals(pc_city_id)) {
					Map list = Json.fromJson(Map.class, json_original);
					Goods goods;
					for (int i = 6; i >= 1; i--) {
						// goods = this.goodsService.getObjById(CommUtil
						// .null2Long(list.get("goods_id" + i)));
						Map params = new HashMap();
						String hql = "select obj from Goods obj where obj.id=:goods_id and obj.goods_store.area.parent.id=:pc_city_id ";
						params.put("goods_id", CommUtil.null2Long(list
								.get("goods_id" + i)));
						params.put("pc_city_id", Long.parseLong(pc_city_id));
						List<Goods> goods_list = this.goodsService.query(hql,
								params, -1, -1);
						if (null != goods_list && goods_list.size() > 0) {
							goods = goods_list.get(0);
							json_neo = json_neo + "\"" + "goods_id" + i
									+ "\":\"" + goods.getId() + "\",";
						}
					}
					if (json_neo.contains("goods_id")) {
						json_neo = json_neo + "\"list_title\":\"商品排行\"}";
					} else {
						json_neo = null;
					}
					goodsFloor.setGf_list_goods_json(json_neo);
				} else {
					goodsFloor.setGf_list_goods_json(goodsFloor
							.getGf_list_goods());
				}
			}
		}
		return floors;
	}

	/**
	 * 首页推荐商品，推荐店铺
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/index_goods_stores.htm")
	public ModelAndView index_goods_stores(HttpServletRequest request,
			HttpServletResponse response, String city_id) {
		// String pc_city_id = CommUtil.null2String(request
		// .getAttribute("city_id"));
		ModelAndView mv = new JModelAndView("index_goods_stores.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("store_recommend", true);
		params.put("goods_status", 0);
		String hql = "select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status ";
		// 添加城市筛选
		if (!"all".equals(city_id) && !"-1".equals(city_id)) {
			hql += " and obj.goods_store.area.parent.id=:pc_city_id ";
			params.put("pc_city_id", Long.parseLong(city_id));
		}
		hql += " order by obj.store_recommend_time desc";
		List<Goods> store_reommend_goods_list = this.goodsService.query(hql,
				params, -1, -1);
		List<Goods> store_reommend_goods = new ArrayList<Goods>();
		int max = store_reommend_goods_list.size() >= 5 ? 4
				: (store_reommend_goods_list.size() - 1);
		for (int i = 0; i <= max; i++) {
			store_reommend_goods.add(store_reommend_goods_list.get(i));
		}
		mv.addObject("store_reommend_goods", store_reommend_goods);
		mv.addObject("store_reommend_goods_count", Math.ceil(CommUtil.div(
				store_reommend_goods_list.size(), 5)));
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("pc_city_id", city_id);
		return mv;
	}
}
