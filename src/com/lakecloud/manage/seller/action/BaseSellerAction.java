package com.lakecloud.manage.seller.action;

import java.util.ArrayList;
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
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Article;
import com.lakecloud.foundation.domain.Message;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IArticleService;
import com.lakecloud.foundation.service.IMessageService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.manage.seller.Tools.MenuTools;
import com.lakecloud.view.web.tools.AreaViewTools;
import com.lakecloud.view.web.tools.OrderViewTools;
import com.lakecloud.view.web.tools.StoreViewTools;

/**
 * @info 卖家后台基础管理器 主要功能包括卖家后台的基础管理、快捷菜单设置等
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Controller
public class BaseSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private OrderViewTools orderViewTools;
	@Autowired
	private AreaViewTools areaViewTools;
	@Autowired
	private MenuTools menuTools;

	/**
	 * 卖家中心首页，该请求受系统ss权限管理，对应角色名为"用户中心"
	 * 
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "卖家中心", value = "/seller/index.htm*", rtype = "seller", rname = "卖家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/seller/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/seller_index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		List<Message> msgs = new ArrayList<Message>();
		Map params = new HashMap();
		params.put("status", 0);
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		msgs = this.messageService
				.query("select obj from Message obj where obj.status=:status and obj.toUser.id=:user_id and obj.parent.id is null",
						params, -1, -1);
		params.clear();
		params.put("class_mark", "notice");
		params.put("display", true);
		List<Article> articles = this.articleService
				.query("select obj from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc",
						params, 0, 5);
		mv.addObject("articles", articles);
		mv.addObject("user", user);
		mv.addObject("store", user.getStore());
		mv.addObject("msgs", msgs);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("orderViewTools", orderViewTools);
		mv.addObject("areaViewTools", areaViewTools);
		return mv;
	}

	@SecurityMapping(title = "卖家中心导航", value = "/seller/nav.htm*", rtype = "seller", rname = "卖家中心导航", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/seller/nav.htm")
	public ModelAndView nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/seller_nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		int store_status = 0;
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		if (store != null) {
			store_status = store.getStore_status();
		}
		String op = CommUtil.null2String(request.getAttribute("op"));
		mv.addObject("op", op);
		mv.addObject("store_status", store_status);
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId()));
		return mv;
	}

	@SecurityMapping(title = "卖家中心导航", value = "/seller/head.htm*", rtype = "seller", rname = "卖家中心导航", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/seller/nav_head.htm")
	public ModelAndView nav_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/seller_head.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String type = CommUtil.null2String(request.getAttribute("type"));
		mv.addObject("type", type.equals("") ? "goods" : type);
		mv.addObject("menuTools", this.menuTools);
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId()));
		return mv;
	}

	@SecurityMapping(title = "卖家中心快捷功能设置", value = "/seller/store_quick_menu.htm*", rtype = "seller", rname = "用户中心", rcode = "user_center_seller", rgroup = "用户中心")
	@RequestMapping("/seller/store_quick_menu.htm")
	public ModelAndView store_quick_menu(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/store_quick_menu.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "卖家中心快捷功能设置保存", value = "/seller/store_quick_menu_save.htm*", rtype = "seller", rname = "用户中心", rcode = "user_center_seller", rgroup = "用户中心")
	@RequestMapping("/seller/store_quick_menu_save.htm")
	public ModelAndView store_quick_menu_save(HttpServletRequest request,
			HttpServletResponse response, String menus) {
		String[] menu_navs = menus.split(",");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		List<Map> list = new ArrayList<Map>();
		for (String menu_nav : menu_navs) {
			if (!menu_nav.equals("")) {
				String[] infos = menu_nav.split("\\|");
				Map map = new HashMap();
				map.put("menu_url", infos[0]);
				map.put("menu_name", infos[1]);
				list.add(map);
			}
		}
		user.setStore_quick_menu(Json.toJson(list, JsonFormat.compact()));
		this.userService.update(user);
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/store_quick_menu_info.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("user", user);
		mv.addObject("menuTools", this.menuTools);
		return mv;

	}
	
	
	/**
	 * 登录页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/seller/login.htm")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("seller_login_new.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			mv.addObject("user", user);
		}
		return mv;
	}
	
	@RequestMapping("/seller/authority.htm")
	public ModelAndView authority(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("authority.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		mv.addObject("op_title", "您没有该项操作权限");
		//mv.addObject("url", CommUtil.getURL(request) + "seller/index.htm");
		return mv;
	}
}
