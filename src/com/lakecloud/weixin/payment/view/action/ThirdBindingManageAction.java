package com.lakecloud.weixin.payment.view.action;

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
import com.lakecloud.foundation.domain.ThirdBinding;
import com.lakecloud.foundation.domain.query.ThirdBindingQueryObject;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IThirdBindingService;
import com.lakecloud.foundation.service.IUserConfigService;

@Controller
public class ThirdBindingManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IThirdBindingService thirdbindingService;

	/**
	 * ThirdBinding列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@RequestMapping("/admin/thirdbinding_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/thirdbinding_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		ThirdBindingQueryObject qo = new ThirdBindingQueryObject(currentPage,
				mv, orderBy, orderType);
		// WebForm wf = new WebForm();
		// wf.toQueryPo(request, qo,ThirdBinding.class,mv);
		IPageList pList = this.thirdbindingService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/admin/thirdbinding_list.htm", "", params, pList, mv);
		return mv;
	}

	/**
	 * thirdbinding添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/thirdbinding_add.htm")
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/thirdbinding_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * thirdbinding编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/thirdbinding_edit.htm")
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/thirdbinding_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			ThirdBinding thirdbinding = this.thirdbindingService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", thirdbinding);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * thirdbinding保存管理
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/thirdbinding_save.htm")
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		ThirdBinding thirdbinding = null;
		if (id.equals("")) {
			thirdbinding = wf.toPo(request, ThirdBinding.class);
			thirdbinding.setAddTime(new Date());
		} else {
			ThirdBinding obj = this.thirdbindingService.getObjById(Long
					.parseLong(id));
			thirdbinding = (ThirdBinding) wf.toPo(request, obj);
		}
		if (id.equals("")) {
			this.thirdbindingService.save(thirdbinding);
		} else
			this.thirdbindingService.update(thirdbinding);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存thirdbinding成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	@RequestMapping("/admin/thirdbinding_del.htm")
	public String delete(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				ThirdBinding thirdbinding = this.thirdbindingService
						.getObjById(Long.parseLong(id));
				this.thirdbindingService.delete(Long.parseLong(id));
			}
		}
		return "redirect:thirdbinding_list.htm?currentPage=" + currentPage;
	}

	@RequestMapping("/admin/thirdbinding_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		ThirdBinding obj = this.thirdbindingService.getObjById(Long
				.parseLong(id));
		Field[] fields = ThirdBinding.class.getDeclaredFields();
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
		this.thirdbindingService.update(obj);
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