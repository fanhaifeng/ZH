package com.lakecloud.manage.admin.action;

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
import com.lakecloud.foundation.domain.GoodsFloorGoods;
import com.lakecloud.foundation.domain.query.GoodsFloorGoodsQueryObject;
import com.lakecloud.foundation.service.IGoodsFloorGoodsService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

@Controller
public class GoodsFloorGoodsManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsFloorGoodsService goodsfloorgoodsService;

	/**
	 * GoodsFloorGoods列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@RequestMapping("/admin/goodsfloorgoods_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/goodsfloorgoods_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		GoodsFloorGoodsQueryObject qo = new GoodsFloorGoodsQueryObject(
				currentPage, mv, orderBy, orderType);
		// WebForm wf = new WebForm();
		// wf.toQueryPo(request, qo,GoodsFloorGoods.class,mv);
		IPageList pList = this.goodsfloorgoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/admin/goodsfloorgoods_list.htm", "", params, pList, mv);
		return mv;
	}

	/**
	 * goodsfloorgoods添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/goodsfloorgoods_add.htm")
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/goodsfloorgoods_add.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * goodsfloorgoods编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/goodsfloorgoods_edit.htm")
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/goodsfloorgoods_add.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			GoodsFloorGoods goodsfloorgoods = this.goodsfloorgoodsService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", goodsfloorgoods);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * goodsfloorgoods保存管理
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/goodsfloorgoods_save.htm")
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		GoodsFloorGoods goodsfloorgoods = null;
		if (id.equals("")) {
			goodsfloorgoods = wf.toPo(request, GoodsFloorGoods.class);
			goodsfloorgoods.setAddTime(new Date());
		} else {
			GoodsFloorGoods obj = this.goodsfloorgoodsService.getObjById(Long
					.parseLong(id));
			goodsfloorgoods = (GoodsFloorGoods) wf.toPo(request, obj);
		}
		if (id.equals("")) {
			this.goodsfloorgoodsService.save(goodsfloorgoods);
		} else
			this.goodsfloorgoodsService.update(goodsfloorgoods);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存goodsfloorgoods成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	@RequestMapping("/admin/goodsfloorgoods_del.htm")
	public String delete(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GoodsFloorGoods goodsfloorgoods = this.goodsfloorgoodsService
						.getObjById(Long.parseLong(id));
				this.goodsfloorgoodsService.delete(Long.parseLong(id));
			}
		}
		return "redirect:goodsfloorgoods_list.htm?currentPage=" + currentPage;
	}

	@RequestMapping("/admin/goodsfloorgoods_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		GoodsFloorGoods obj = this.goodsfloorgoodsService.getObjById(Long
				.parseLong(id));
		Field[] fields = GoodsFloorGoods.class.getDeclaredFields();
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
		this.goodsfloorgoodsService.update(obj);
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