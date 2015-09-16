package com.lakecloud.view.web.action;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;

import java.text.ParseException;
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
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.Md5Encrypt;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.core.annotation.Log;
import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.foundation.service.ICropService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.domain.Crop;
import com.lakecloud.foundation.domain.query.CropQueryObject;

@Controller
public class CropManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService  userConfigService;
	@Autowired
	private ICropService cropService;	
	/**
	 * Crop列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@RequestMapping("/admin/crop_list.htm")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response,String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/crop_list.html", configService
				.getSysConfig(),this.userConfigService.getUserConfig(), 0, request,response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		CropQueryObject qo = new CropQueryObject(currentPage, mv, orderBy,
				orderType);
		// WebForm wf = new WebForm();
		// wf.toQueryPo(request, qo,Crop.class,mv);
		IPageList pList = this.cropService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/crop_list.htm","",
				params, pList, mv);
		return mv;
	}
	/**
	 * crop添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/crop_add.htm")
	public ModelAndView add(HttpServletRequest request,HttpServletResponse response,String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/crop_add.html", configService
				.getSysConfig(),this.userConfigService.getUserConfig(), 0, request,response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	/**
	 * crop编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/crop_edit.htm")
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response,String id,String currentPage)
        {
		ModelAndView mv = new JModelAndView("admin/blue/crop_add.html", configService
				.getSysConfig(),this.userConfigService.getUserConfig(), 0, request,response);
		if (id != null&&!id.equals("")) {
			Crop crop = this.cropService.getObjById(Long.parseLong(id));
				mv.addObject("obj", crop);
		    mv.addObject("currentPage", currentPage);
		    mv.addObject("edit", true);
		}
		return mv;
	}
     /**
		 * crop保存管理
		 * 
		 * @param id
		 * @return
		 */
	@RequestMapping("/admin/crop_save.htm")
	public ModelAndView save(HttpServletRequest request,HttpServletResponse response, String id,String currentPage, String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		Crop crop =null;
		if (id.equals("")) {
			 crop = wf.toPo(request, Crop.class);
			crop.setAddTime(new Date());
		}else{
			Crop obj=this.cropService.getObjById(Long.parseLong(id));
			crop = (Crop)wf.toPo(request,obj);
		}
		
		if (id.equals("")) {
			this.cropService.save(crop);
		} else
			this.cropService.update(crop);
	   ModelAndView mv = new JModelAndView("admin/blue/success.html",
					configService.getSysConfig(), this.userConfigService
							.getUserConfig(), 0, request,response);
			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "保存crop成功");
			if (add_url != null) {
				mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
			}
		return mv;
	}
	@RequestMapping("/admin/crop_del.htm")
	public String delete(HttpServletRequest request,HttpServletResponse response,String mulitId,String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
			  Crop crop = this.cropService.getObjById(Long.parseLong(id));
			  this.cropService.delete(Long.parseLong(id));
			}
		}
		return "redirect:crop_list.htm?currentPage="+currentPage;
	}
	@RequestMapping("/admin/crop_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		Crop obj = this.cropService.getObjById(Long.parseLong(id));
		Field[] fields = Crop.class.getDeclaredFields();
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
		this.cropService.update(obj);
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