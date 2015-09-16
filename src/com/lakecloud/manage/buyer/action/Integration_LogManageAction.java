package com.lakecloud.manage.buyer.action;

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
import com.lakecloud.foundation.domain.Integration_Log;
import com.lakecloud.foundation.domain.query.Integration_LogQueryObject;
import com.lakecloud.foundation.service.IIntegration_LogService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

@Controller
public class Integration_LogManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IIntegration_LogService integration_logService;

	/**
	 * Integration_Log列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@RequestMapping("/admin/integration_log_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/integration_log_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Integration_LogQueryObject qo = new Integration_LogQueryObject(
				currentPage, mv, orderBy, orderType);
		// WebForm wf = new WebForm();
		// wf.toQueryPo(request, qo,Integration_Log.class,mv);
		IPageList pList = this.integration_logService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/admin/integration_log_list.htm", "", params, pList, mv);
		return mv;
	}

	/**
	 * integration_log添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/integration_log_add.htm")
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/integration_log_add.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * integration_log编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/integration_log_edit.htm")
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/integration_log_add.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Integration_Log integration_log = this.integration_logService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", integration_log);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * integration_log保存管理
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/integration_log_save.htm")
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		Integration_Log integration_log = null;
		if (id.equals("")) {
			integration_log = wf.toPo(request, Integration_Log.class);
			integration_log.setAddTime(new Date());
		} else {
			Integration_Log obj = this.integration_logService.getObjById(Long
					.parseLong(id));
			integration_log = (Integration_Log) wf.toPo(request, obj);
		}
		if (id.equals("")) {
			this.integration_logService.save(integration_log);
		} else
			this.integration_logService.update(integration_log);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存integration_log成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	@RequestMapping("/admin/integration_log_del.htm")
	public String delete(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Integration_Log integration_log = this.integration_logService
						.getObjById(Long.parseLong(id));
				this.integration_logService.delete(Long.parseLong(id));
			}
		}
		return "redirect:integration_log_list.htm?currentPage=" + currentPage;
	}

	@RequestMapping("/admin/integration_log_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		Integration_Log obj = this.integration_logService.getObjById(Long
				.parseLong(id));
		Field[] fields = Integration_Log.class.getDeclaredFields();
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
		this.integration_logService.update(obj);
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