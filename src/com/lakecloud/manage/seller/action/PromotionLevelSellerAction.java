package com.lakecloud.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.PromotionLevel;
import com.lakecloud.foundation.domain.query.PromotionLevelQueryObject;
import com.lakecloud.foundation.service.IPromotionLevelService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

@Controller
public class PromotionLevelSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IPromotionLevelService promotionlevelService;

	/**
	 * PromotionLevel列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@RequestMapping("/admin/promotionlevel_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/promotionlevel_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		PromotionLevelQueryObject qo = new PromotionLevelQueryObject(
				currentPage, mv, orderBy, orderType);
		// WebForm wf = new WebForm();
		// wf.toQueryPo(request, qo,PromotionLevel.class,mv);
		IPageList pList = this.promotionlevelService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/admin/promotionlevel_list.htm", "", params, pList, mv);
		return mv;
	}

	/**
	 * promotionlevel添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/promotionlevel_add.htm")
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/promotionlevel_add.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * promotionlevel编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/promotionlevel_edit.htm")
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/promotionlevel_add.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			PromotionLevel promotionlevel = this.promotionlevelService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", promotionlevel);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * promotionlevel保存管理
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/promotionlevel_save.htm")
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		PromotionLevel promotionlevel = null;
		if (id.equals("")) {
			promotionlevel = wf.toPo(request, PromotionLevel.class);
			promotionlevel.setAddTime(new Date());
		} else {
			PromotionLevel obj = this.promotionlevelService.getObjById(Long
					.parseLong(id));
			promotionlevel = (PromotionLevel) wf.toPo(request, obj);
		}
		if (id.equals("")) {
			this.promotionlevelService.save(promotionlevel);
		} else
			this.promotionlevelService.update(promotionlevel);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存promotionlevel成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	@RequestMapping("/admin/promotionlevel_del.htm")
	public String delete(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				PromotionLevel promotionlevel = this.promotionlevelService
						.getObjById(Long.parseLong(id));
				this.promotionlevelService.delete(Long.parseLong(id));
			}
		}
		return "redirect:promotionlevel_list.htm?currentPage=" + currentPage;
	}

	@RequestMapping("/admin/promotionlevel_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		PromotionLevel obj = this.promotionlevelService.getObjById(Long
				.parseLong(id));
		Field[] fields = PromotionLevel.class.getDeclaredFields();
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
		this.promotionlevelService.update(obj);
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
}