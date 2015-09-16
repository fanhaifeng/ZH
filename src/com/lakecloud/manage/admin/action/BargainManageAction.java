package com.lakecloud.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.lakecloud.foundation.domain.Bargain;
import com.lakecloud.foundation.domain.BargainGoods;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.Navigation;
import com.lakecloud.foundation.domain.SysConfig;
import com.lakecloud.foundation.domain.query.BargainGoodsQueryObject;
import com.lakecloud.foundation.domain.query.BargainQueryObject;
import com.lakecloud.foundation.service.IAccessoryService;
import com.lakecloud.foundation.service.IBargainGoodsService;
import com.lakecloud.foundation.service.IBargainService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.INavigationService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.manage.admin.tools.BargainManageTools;

/**
 * @info 今日特价管理控制器，用在平台管理员操作今日特价信息
 * @since V1.3
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net wang、erikchang
 * 
 */
@Controller
public class BargainManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IBargainService bargainService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IBargainGoodsService bargainGoodsService;
	@Autowired
	private INavigationService navigationService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private BargainManageTools bargainManageTools;

	/**
	 * Bargain列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "特价列表", value = "/admin/bargain_list.htm*", rtype = "admin", rname = "天天特价", rcode = "bargain_admin", rgroup = "运营")
	@RequestMapping("/admin/bargain_list.htm")
	public ModelAndView bargain_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/bargain_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		BargainQueryObject qo = new BargainQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.setOrderBy("bargain_time");
		qo.setOrderType("desc");
		IPageList pList = this.bargainService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		//
		int day_count = this.configService.getSysConfig().getBargain_validity();
		List<Date> dates = new ArrayList<Date>();
		for (int i = 0; i < day_count; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, i + 1);
			dates.add(cal.getTime());
		}
		mv.addObject("dates", dates);
		mv.addObject("bargainManageTools", bargainManageTools);
		return mv;
	}

	/**
	 * bargain添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "特价添加", value = "/admin/bargain_add.htm*", rtype = "admin", rname = "天天特价", rcode = "bargain_admin", rgroup = "运营")
	@RequestMapping("/admin/bargain_add.htm")
	public ModelAndView bargain_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/bargain_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		int day_count = this.configService.getSysConfig().getBargain_validity();
		List<Date> dates = new ArrayList<Date>();
		for (int i = 0; i < day_count; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, i + 1);
			dates.add(cal.getTime());
		}
		mv.addObject("dates", dates);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * bargain保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "特价保存", value = "/admin/bargain_save.htm*", rtype = "admin", rname = "天天特价", rcode = "bargain_admin", rgroup = "运营")
	@RequestMapping("/admin/bargain_save.htm")
	public ModelAndView bargain_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String bargain_time) {
		Map params = new HashMap();
		params.put("bt", CommUtil.formatDate(bargain_time));
		List<Bargain> bargains = this.bargainService.query(
				"select obj from Bargain obj where obj.bargain_time =:bt",
				params, 0, 1);
		String list_url = CommUtil.getURL(request) + "/admin/bargain_list.htm";
		String add_url = CommUtil.getURL(request) + "/admin/bargain_add.htm";
		if (bargains.size() > 0) {
			ModelAndView mv = new JModelAndView("admin/blue/fail.html",
					configService.getSysConfig(), this.userConfigService
							.getUserConfig(), 0, request, response);
			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "申请日期已存在,保存失败");
			if (add_url != null) {
				mv
						.addObject("add_url", add_url + "?currentPage="
								+ currentPage);
			}
			return mv;
		} else {
			WebForm wf = new WebForm();
			Bargain bargain = null;
			if (id.equals("")) {
				bargain = wf.toPo(request, Bargain.class);
				bargain.setAddTime(new Date());
			} else {
				Bargain obj = this.bargainService
						.getObjById(Long.parseLong(id));
				bargain = (Bargain) wf.toPo(request, obj);
			}

			if (id.equals("")) {
				this.bargainService.save(bargain);
			} else
				this.bargainService.update(bargain);
			ModelAndView mv = new JModelAndView("admin/blue/success.html",
					configService.getSysConfig(), this.userConfigService
							.getUserConfig(), 0, request, response);
			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "天天特价添加成功");
			if (add_url != null) {
				mv
						.addObject("add_url", add_url + "?currentPage="
								+ currentPage);
			}
			return mv;
		}
	}

	@SecurityMapping(title = "特价删除", value = "/admin/bargain_del.htm*", rtype = "admin", rname = "天天特价", rcode = "bargain_admin", rgroup = "运营")
	@RequestMapping("/admin/bargain_del.htm")
	public ModelAndView bargain_del(HttpServletRequest request,
			HttpServletResponse response, String bargain_time) {
		ModelAndView mv = new JModelAndView("admin/blue/tip.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		Map map = new HashMap();
		map.put("bt", CommUtil.formatDate(bargain_time));
		List<BargainGoods> bargainGoods = this.bargainGoodsService.query(
				"select obj from BargainGoods obj where obj.bg_time =:bt", map,
				-1, -1);
		if (bargainGoods.size() > 0) {
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/bargain_list.htm");
			mv.addObject("op_tip", "已有商品申请今日特价不可删除");
		} else {
			Map params = new HashMap();
			params.put("bt", CommUtil.formatDate(bargain_time));
			List<Bargain> bargains = this.bargainService.query(
					"select obj from Bargain obj where obj.bargain_time =:bt",
					params, 0, 1);
			this.bargainService.delete(bargains.get(0).getId());
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/bargain_list.htm");
			mv.addObject("op_tip", "删除成功已恢复通用设置");
		}
		return mv;
	}

	@SecurityMapping(title = "今日特价ajax更新", value = "/admin/bargain_ajax.htm*", rtype = "admin", rname = "天天特价", rcode = "bargain_admin", rgroup = "运营")
	@RequestMapping("/admin/bargain_ajax.htm")
	public void bargain_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Bargain obj = this.bargainService.getObjById(Long.parseLong(id));
		Field[] fields = Bargain.class.getDeclaredFields();
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
		this.bargainService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "系统特价设置", value = "/admin/set_bargain.htm*", rtype = "admin", rname = "天天特价", rcode = "bargain_admin", rgroup = "运营")
	@RequestMapping("/admin/set_bargain.htm")
	public ModelAndView set_bargain(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/set_bargain.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		int day_count = this.configService.getSysConfig().getBargain_validity();
		List<Date> dates = new ArrayList<Date>();
		for (int i = 0; i < day_count; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, i + 1);
			dates.add(cal.getTime());
		}
		mv.addObject("dates", dates);
		return mv;
	}

	@SecurityMapping(title = "时间查询设置", value = "/admin/date_query_set.htm*", rtype = "admin", rname = "天天特价", rcode = "bargain_admin", rgroup = "运营")
	@RequestMapping("/admin/date_query_set.htm")
	public ModelAndView date_query_set(HttpServletRequest request,
			HttpServletResponse response, String count, String date) {
		ModelAndView mv = new JModelAndView("admin/blue/date_query_set.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		int day_count = this.configService.getSysConfig().getBargain_validity();
		List<Date> dates = new ArrayList<Date>();
		for (int i = 0; i < day_count; i++) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(CommUtil.formatDate(date));
			cal.add(Calendar.DAY_OF_YEAR, i + 1 + CommUtil.null2Int(count));
			dates.add(cal.getTime());
		}
		mv.addObject("dates", dates);
		return mv;
	}

	@SecurityMapping(title = "通用特价设置保存", value = "/admin/set_bargain_save.htm*", rtype = "admin", rname = "天天特价", rcode = "bargain_admin", rgroup = "运营")
	@RequestMapping("/admin/set_bargain_save.htm")
	public ModelAndView set_bargain_save(HttpServletRequest request,
			HttpServletResponse response, String id, String op_title,
			String list_url) {
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig sysConfig = null;
		if (id.equals("")) {
			sysConfig = wf.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) wf.toPo(request, obj);
		}
		if (id.equals("")) {
			this.configService.save(sysConfig);
		} else {
			this.configService.update(sysConfig);
		}
		if (sysConfig.getBargain_status() == 1) {// 开启今日特价
			Map params = new HashMap();
			params.put("url", "bargain.htm");
			List<Navigation> navs = this.navigationService.query(
					"select obj from Navigation obj where obj.url=:url",
					params, -1, -1);
			if (navs.size() == 0) {
				Navigation nav = new Navigation();
				nav.setAddTime(new Date());
				nav.setDisplay(true);
				nav.setLocation(0);
				nav.setNew_win(1);
				nav.setSequence(5);
				nav.setSysNav(true);
				nav.setTitle("天天特价");
				nav.setType("diy");
				nav.setUrl("bargain.htm");
				nav.setOriginal_url("bargain.htm");
				this.navigationService.save(nav);
			}
		} else {// 关闭今日特价
			Map params = new HashMap();
			params.put("url", "bargain.htm");
			List<Navigation> navs = this.navigationService.query(
					"select obj from Navigation obj where obj.url=:url",
					params, -1, -1);
			for (Navigation nav : navs) {
				this.navigationService.delete(nav.getId());
			}
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("op_title", op_title);
		mv.addObject("list_url", list_url);
		return mv;
	}

	@SecurityMapping(title = "特价商品列表", value = "/admin/bargain_goods_list.htm*", rtype = "admin", rname = "天天特价", rcode = "bargain_admin", rgroup = "运营")
	@RequestMapping("/admin/bargain_goods_list.htm")
	public ModelAndView bargain_goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String bg_status,
			String bargain_time) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/bargain_goods_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		BargainGoodsQueryObject qo = new BargainGoodsQueryObject(currentPage,
				mv, orderBy, orderType);
		if (!CommUtil.null2String(bg_status).equals("")) {
			qo.addQuery("obj.bg_status", new SysMap("bg_status", CommUtil
					.null2Int(bg_status)), "=");
		}
		if (!CommUtil.null2String(goods_name).equals("")) {
			qo.addQuery("obj.bg_goods.goods_name", new SysMap("goods_name", "%"
					+ goods_name.trim() + "%"), "like");
		}
		qo.addQuery("obj.bg_time", new SysMap("bt", CommUtil
				.formatDate(bargain_time)), "=");
		IPageList pList = this.bargainGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("bg_status", bg_status);
		mv.addObject("goods_name", goods_name);
		mv.addObject("bargain_time", bargain_time);
		// 特价有效期设置
		int day_count = this.configService.getSysConfig().getBargain_validity();
		List<Date> dates = new ArrayList<Date>();
		for (int i = 0; i < day_count; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, i + 1);
			dates.add(cal.getTime());
		}
		mv.addObject("dates", dates);
		return mv;
	}

	@SecurityMapping(title = "特价商品通过", value = "/admin/bargain_goods_audit.htm*", rtype = "admin", rname = "天天特价", rcode = "bargain_admin", rgroup = "运营")
	@RequestMapping("/admin/bargain_goods_audit.htm")
	public String bargain_goods_audit(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String bargain_time) {
		String uri = "";
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Map map = new HashMap();
				map.put("bg_time", CommUtil.formatDate(bargain_time));
				List<BargainGoods> bargainGoods = this.bargainGoodsService
						.query(
								"select obj from BargainGoods obj where obj.bg_time =:bg_time",
								map, -1, -1);
				int audits = 1;
				for (BargainGoods bgs : bargainGoods) {
					if (bgs.getBg_status() == 1) {
						audits = audits + 1;
					}
				}
				Map params = new HashMap();
				params.put("bg_time", CommUtil.formatDate(bargain_time));
				List<Bargain> bargains = this.bargainService
						.query(
								"select obj from Bargain obj where obj.bargain_time =:bg_time",
								params, 0, 1);
				int set_audits = 0;
				if (bargains.size() > 0) {
					set_audits = bargains.get(0).getMaximum();
				} else {
					set_audits = this.configService.getSysConfig()
							.getBargain_maximum();
				}
				if (audits > set_audits) {
					uri = "redirect:bargain_audits_out.htm";
				} else {
					BargainGoods bg = this.bargainGoodsService.getObjById(Long
							.parseLong(id));
					bg.setBg_status(1);
					bg.setBg_admin_user(SecurityUserHolder.getCurrentUser());
					bg.setAudit_time(new Date());
					this.bargainGoodsService.update(bg);
					// 活动状态
					Goods goods = bg.getBg_goods();
					goods.setBargain_status(2);
					goods.setGoods_current_price(bg.getBg_price());
					this.goodsService.update(goods);
					uri = "redirect:bargain_goods_list.htm?bargain_time="
							+ bargain_time + "&currentPage=" + currentPage;
				}
			}
		}
		return uri;
	}

	@SecurityMapping(title = "特价商品审核数超出", value = "/admin/bargain_audits_out.htm*", rtype = "admin", rname = "天天特价", rcode = "bargain_admin", rgroup = "运营")
	@RequestMapping("/admin/bargain_audits_out.htm")
	public ModelAndView bargain_audits_out(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/tip.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/bargain_list.htm");
		mv.addObject("op_tip", "审核商品数已超出特价商品的最多数");
		return mv;
	}

	@SecurityMapping(title = "特价拒绝", value = "/admin/bargain_goods_refuse.htm*", rtype = "admin", rname = "天天特价", rcode = "bargain_admin", rgroup = "运营")
	@RequestMapping("/admin/bargain_goods_refuse.htm")
	public String bargain_goods_refuse(HttpServletRequest request,
			HttpServletResponse response, String bargain_time, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				BargainGoods bg = this.bargainGoodsService.getObjById(Long
						.parseLong(id));
				bg.setBg_status(-1);
				this.bargainGoodsService.update(bg);
				// 恢复商品价格,拒绝活动状态
				Goods goods = bg.getBg_goods();
				goods.setBargain_status(0);
				goods.setGoods_current_price(goods.getStore_price());
				this.goodsService.update(goods);
			}
		}
		return "redirect:bargain_goods_list.htm?bargain_time=" + bargain_time
				+ "&currentPage=" + currentPage;
	}
}