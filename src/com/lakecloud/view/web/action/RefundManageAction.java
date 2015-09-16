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
import com.lakecloud.foundation.service.IRefundService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.domain.Refund;
import com.lakecloud.foundation.domain.query.RefundQueryObject;

@Controller
public class RefundManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService  userConfigService;
	@Autowired
	private IRefundService refundService;	
	/**
	 * Refund列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@RequestMapping("/admin/refund_list.htm")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response,String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/refund_list.html", configService
				.getSysConfig(),this.userConfigService.getUserConfig(), 0, request,response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		RefundQueryObject qo = new RefundQueryObject(currentPage, mv, orderBy,
				orderType);
		// WebForm wf = new WebForm();
		// wf.toQueryPo(request, qo,Refund.class,mv);
		IPageList pList = this.refundService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/refund_list.htm","",
				params, pList, mv);
		return mv;
	}
	/**
	 * refund添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/refund_add.htm")
	public ModelAndView add(HttpServletRequest request,HttpServletResponse response,String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/refund_add.html", configService
				.getSysConfig(),this.userConfigService.getUserConfig(), 0, request,response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	/**
	 * refund编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/refund_edit.htm")
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response,String id,String currentPage)
        {
		ModelAndView mv = new JModelAndView("admin/blue/refund_add.html", configService
				.getSysConfig(),this.userConfigService.getUserConfig(), 0, request,response);
		if (id != null&&!id.equals("")) {
			Refund refund = this.refundService.getObjById(Long.parseLong(id));
				mv.addObject("obj", refund);
		    mv.addObject("currentPage", currentPage);
		    mv.addObject("edit", true);
		}
		return mv;
	}
     /**
		 * refund保存管理
		 * 
		 * @param id
		 * @return
		 */
	@RequestMapping("/admin/refund_save.htm")
	public ModelAndView save(HttpServletRequest request,HttpServletResponse response, String id,String currentPage, String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		Refund refund =null;
		if (id.equals("")) {
			 refund = wf.toPo(request, Refund.class);
			refund.setAddTime(new Date());
		}else{
			Refund obj=this.refundService.getObjById(Long.parseLong(id));
			refund = (Refund)wf.toPo(request,obj);
		}
		
		if (id.equals("")) {
			this.refundService.save(refund);
		} else
			this.refundService.update(refund);
	   ModelAndView mv = new JModelAndView("admin/blue/success.html",
					configService.getSysConfig(), this.userConfigService
							.getUserConfig(), 0, request,response);
			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "保存refund成功");
			if (add_url != null) {
				mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
			}
		return mv;
	}
	@RequestMapping("/admin/refund_del.htm")
	public String delete(HttpServletRequest request,HttpServletResponse response,String mulitId,String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
			  Refund refund = this.refundService.getObjById(Long.parseLong(id));
			  this.refundService.delete(Long.parseLong(id));
			}
		}
		return "redirect:refund_list.htm?currentPage="+currentPage;
	}
	@RequestMapping("/admin/refund_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		Refund obj = this.refundService.getObjById(Long.parseLong(id));
		Field[] fields = Refund.class.getDeclaredFields();
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
		this.refundService.update(obj);
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