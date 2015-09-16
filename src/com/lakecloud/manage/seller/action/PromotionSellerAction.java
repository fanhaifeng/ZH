package com.lakecloud.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.Promotion;
import com.lakecloud.foundation.domain.PromotionLevel;
import com.lakecloud.foundation.domain.query.PromotionQueryObject;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IPromotionLevelService;
import com.lakecloud.foundation.service.IPromotionService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.manage.seller.Tools.DateFormatUtil;

@Controller
public class PromotionSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IPromotionService promotionService;
	@Autowired
	private IPromotionLevelService promotionlevelService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	/**
	 * 三个空格，下拉框间隔用
	 */
	public static final String THREE_BLANKS = "&nbsp;&nbsp;&nbsp;";

	/**
	 * Promotion列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "直降促销列表", value = "/seller/promotion_list.htm*", rtype = "seller", rname = "直降促销", rcode = "promotion_seller", rgroup = "促销管理")
	@RequestMapping("/seller/promotion_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String name) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/promotion_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";// &name=" + name
		PromotionQueryObject qo = new PromotionQueryObject(currentPage, mv,
				"updatetime", "desc");
		qo.addQuery("obj.create_user", new SysMap("create_user",
				SecurityUserHolder.getCurrentUser().getUsername()), "=");
		qo.addQuery("obj.status", new SysMap("status", "4"), "!=");
		if (!CommUtil.null2String(name).equals("")) {
			qo.addQuery("obj.name", new SysMap("name", "%" + name + "%"),
					"like");
		}
		// WebForm wf = new WebForm();
		// wf.toQueryPo(request, qo,Promotion.class,mv);
		IPageList pList = this.promotionService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/seller/promotion_list.htm",
				"", params, pList, mv);
		mv.addObject("name", name);
		return mv;
	}

	/**
	 * promotion添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/seller/promotion_add.htm")
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/promotion_add.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		// Store store = this.userService.getObjById(
		// SecurityUserHolder.getCurrentUser().getId()).getStore();
		// mv.addObject("store", store);
		return mv;
	}

	/**
	 * promotion编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/seller/promotion_edit.htm")
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/promotion_add.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Promotion promotion = this.promotionService.getObjById(Long
					.parseLong(id));
			mv.addObject("obj", promotion);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
			mv.addObject("appendHtml", goodOptions(promotion, "edit"));
		}
		return mv;
	}

	/**
	 * promotion保存管理
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/seller/promotion_save.htm")
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String goods_ids) {
		WebForm wf = new WebForm();
		Promotion promotion = null;
		if (id.equals("")) {
			promotion = wf.toPo(request, Promotion.class);
			promotion.setAddTime(new Date());
		} else {
			Promotion obj = this.promotionService
					.getObjById(Long.parseLong(id));
			promotion = (Promotion) wf.toPo(request, obj);
		}
		String starttime = request.getParameter("starttime");
		String endtime = request.getParameter("endtime");
		String[] goods_id = goods_ids.split(",");
		promotion.setStarttime(DateFormatUtil.string2Timestamp(starttime));
		promotion.setEndtime(DateFormatUtil.string2Timestamp(endtime));
		promotion.setUpdatetime(new Timestamp(System.currentTimeMillis()));
		promotion.setCreate_user(SecurityUserHolder.getCurrentUser()
				.getUsername());
		promotion.setField1(SecurityUserHolder.getCurrentUser().getStore()
				.getId().toString());
		if (id.equals("")) {
			promotion.setStatus("0");
			promotion.setCreatetime(new Timestamp(System.currentTimeMillis()));
			this.promotionService.save(promotion);
		} else {
			this.promotionService.update(promotion);
			deletePromotionLevel(promotion);
		}
		savePromotionLevel(promotion, goods_id);
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/success.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存促销成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		mv.addObject("url", CommUtil.getURL(request)
				+ "/seller/promotion_list.htm");
		return mv;
	}

	@RequestMapping("/seller/promotion_del.htm")
	public String delete(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Promotion promotion = this.promotionService.getObjById(Long
						.parseLong(id));
				this.promotionService.delete(Long.parseLong(id));
			}
		}
		return "redirect:promotion_list.htm?currentPage=" + currentPage;
	}

	@RequestMapping("/seller/promotion_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		Promotion obj = this.promotionService.getObjById(Long.parseLong(id));
		Field[] fields = Promotion.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			// System.out.println(field.getName());
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if (field.getType().getName().equals("int")) {
					clz = Class.forName("java.lang.Integer");
				}
				if (field.getType().getName().equals("boolean")) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!value.equals("")) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper
							.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		if ("1".equals(value)) {// 激活设置field3=null
			obj.setField3(null);
		}
		this.promotionService.update(obj);
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

	@RequestMapping("/seller/promotion_view.htm")
	public ModelAndView view_detail(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/promotion_view.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Promotion promotion = this.promotionService.getObjById(Long
					.parseLong(id));
			mv.addObject("obj", promotion);
			mv.addObject("currentPage", currentPage);
			mv.addObject("appendHtml", goodOptions(promotion, "view"));
		}
		return mv;
	}

	private void savePromotionLevel(Promotion promotion, String[] goods_id) {
		List<String> list = new ArrayList<String>();
		for (String goodId : goods_id) {
			if (!list.contains(goodId)) {
				list.add(goodId);
				try {
					PromotionLevel promotionlevel = new PromotionLevel();
					promotionlevel.setAddTime(new Date());
					promotionlevel.setPromotion(promotion);
					promotionlevel.setUser_username(promotion.getCreate_user());
					promotionlevel.setGoods_id(Long.parseLong(goodId));
					promotionlevel.setIsdirectory("0");
					this.promotionlevelService.save(promotionlevel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void deletePromotionLevel(Promotion promotion) {
		List<PromotionLevel> promotionLevelList = promotion
				.getPromotionLevels();
		for (PromotionLevel promotionLevel : promotionLevelList) {
			try {
				this.promotionlevelService.delete(promotionLevel.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String goodOptions(Promotion promotion, String type) {
		String appendHtml = "";
		List<PromotionLevel> promotionLevelList = promotion
				.getPromotionLevels();
		if ("edit".equals(type)) {
			for (PromotionLevel promotionLevel : promotionLevelList) {
				Goods goods = goodsService.getObjById(promotionLevel
						.getGoods_id());
				appendHtml = appendHtml + "<option selected='selected' value='"
						+ promotionLevel.getGoods_id() + "'>"
						+ goods.getGoods_name() + THREE_BLANKS
						+ goods.getGoods_serial() + "</option>";
			}
		} else {
			for (PromotionLevel promotionLevel : promotionLevelList) {
				Goods goods = goodsService.getObjById(promotionLevel
						.getGoods_id());
				appendHtml = appendHtml + goods.getGoods_name() + THREE_BLANKS
						+ goods.getGoods_serial() + "<br/>";
			}
		}
		return appendHtml;
	}

	@RequestMapping("/seller/promotion_count.htm")
	public void count(HttpServletRequest request, HttpServletResponse response,
			String currentPage, String orderBy, String orderType, String sort,
			String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/promotion_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		PromotionQueryObject qo = new PromotionQueryObject(null, mv,
				"updatetime", "desc");
		qo.addQuery("obj.create_user", new SysMap("create_user",
				SecurityUserHolder.getCurrentUser().getUsername()), "=");
		qo.addQuery("obj.status", new SysMap("status", "4"), "!=");
		try {
			if (!CommUtil.null2String(sort).equals("")) {
				qo.addQuery("obj.sort",
						new SysMap("sort", Long.parseLong(sort)), "=");
			}
			if (!CommUtil.null2String(id).equals("")) {
				qo.addQuery("obj.id", new SysMap("id", Long.parseLong(id)),
						"!=");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		IPageList pList = this.promotionService.list(qo);
		Object val = pList.getRowCount();
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
}