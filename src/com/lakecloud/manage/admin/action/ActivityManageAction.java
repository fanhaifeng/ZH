package com.lakecloud.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
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
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.Accessory;
import com.lakecloud.foundation.domain.Activity;
import com.lakecloud.foundation.domain.ActivityGoods;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.query.ActivityGoodsQueryObject;
import com.lakecloud.foundation.domain.query.ActivityQueryObject;
import com.lakecloud.foundation.service.IAccessoryService;
import com.lakecloud.foundation.service.IActivityGoodsService;
import com.lakecloud.foundation.service.IActivityService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

/**
 * 商城活动管理类
 * 
 * @author erikchang
 * 
 */

@Controller
public class ActivityManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoodsService goodService;

	/**
	 * Activity列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "活动列表", value = "/admin/activity_list.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_list.htm")
	public ModelAndView activity_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String q_ac_title, String ac_status,
			String beginTime, String endTime) {
		ModelAndView mv = new JModelAndView("admin/blue/activity_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		ActivityQueryObject qo = new ActivityQueryObject(currentPage, mv,
				orderBy, orderType);
		if (!CommUtil.null2String(q_ac_title).equals("")) {
			qo.addQuery("obj.ac_title", new SysMap("q_ac_title", "%"
					+ q_ac_title.trim() + "%"), "like");
		}
		if (!CommUtil.null2String(ac_status).equals("")) {
			qo.addQuery("obj.ac_status", new SysMap("ac_status", CommUtil
					.null2Int(ac_status)), "=");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			qo.addQuery("obj.ac_begin_time", new SysMap("beginTime", CommUtil
					.formatDate(beginTime)), ">=");
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			qo.addQuery("obj.ac_end_time", new SysMap("endTime", CommUtil
					.formatDate(endTime)), "<=");
		}
		IPageList pList = this.activityService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("q_ac_title", q_ac_title);
		mv.addObject("ac_status", ac_status);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		return mv;
	}

	/**
	 * activity添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "活动添加", value = "/admin/activity_add.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_add.htm")
	public ModelAndView activity_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/activity_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * activity编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "活动编辑", value = "/admin/activity_edit.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_edit.htm")
	public ModelAndView activity_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/activity_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Activity activity = this.activityService.getObjById(Long
					.parseLong(id));
			mv.addObject("obj", activity);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * activity保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "活动保存", value = "/admin/activity_save.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_save.htm")
	public ModelAndView activity_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd) {
		WebForm wf = new WebForm();
		Activity activity = null;
		if (id.equals("")) {
			activity = wf.toPo(request, Activity.class);
			activity.setAddTime(new Date());
		} else {
			Activity obj = this.activityService.getObjById(Long.parseLong(id));
			activity = (Activity) wf.toPo(request, obj);
		}
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "activity";
		Map map = new HashMap();
		try {
			String fileName = activity.getAc_acc() == null ? "" : activity
					.getAc_acc().getName();
			map = CommUtil.saveFileToServer(request, "acc", saveFilePathName,
					fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory ac_acc = new Accessory();
					ac_acc.setName(CommUtil.null2String(map.get("fileName")));
					ac_acc.setExt(CommUtil.null2String(map.get("mime")));
					ac_acc.setSize(CommUtil.null2Float(map.get("fileSize")));
					ac_acc.setPath(uploadFilePath + "/activity");
					ac_acc.setWidth(CommUtil.null2Int(map.get("width")));
					ac_acc.setHeight(CommUtil.null2Int(map.get("height")));
					ac_acc.setAddTime(new Date());
					this.accessoryService.save(ac_acc);
					activity.setAc_acc(ac_acc);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory ac_acc = activity.getAc_acc();
					ac_acc.setName(CommUtil.null2String(map.get("fileName")));
					ac_acc.setExt(CommUtil.null2String(map.get("mime")));
					ac_acc.setSize(CommUtil.null2Float(map.get("fileSize")));
					ac_acc.setPath(uploadFilePath + "/activity");
					ac_acc.setWidth(CommUtil.null2Int(map.get("width")));
					ac_acc.setHeight(CommUtil.null2Int(map.get("height")));
					ac_acc.setAddTime(new Date());
					this.accessoryService.update(ac_acc);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.activityService.save(activity);
		} else
			this.activityService.update(activity);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/activity_list.htm");
		mv.addObject("op_title", "保存商城活动成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/admin/activity_add.htm" + "?currentPage=" + currentPage);
		return mv;
	}

	@SecurityMapping(title = "活动删除", value = "/admin/activity_del.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_del.htm")
	public String activity_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Activity activity = this.activityService.getObjById(Long
						.parseLong(id));
				CommUtil.del_acc(request, activity.getAc_acc());
				this.activityService.delete(Long.parseLong(id));
			}
		}
		return "redirect:activity_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "活动AJAX更新", value = "/admin/activity_ajax.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_ajax.htm")
	public void activity_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Activity obj = this.activityService.getObjById(Long.parseLong(id));
		Field[] fields = Activity.class.getDeclaredFields();
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
		this.activityService.update(obj);
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

	@SecurityMapping(title = "活动商品列表", value = "/admin/activity_goods_list.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_goods_list.htm")
	public ModelAndView activity_goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String act_id, String goods_name, String ag_status) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/activity_goods_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ActivityGoodsQueryObject qo = new ActivityGoodsQueryObject(currentPage,
				mv, orderBy, orderType);
		qo.addQuery("obj.act.id", new SysMap("act_id", CommUtil
				.null2Long(act_id)), "=");
		if (!CommUtil.null2String(ag_status).equals("")) {
			qo.addQuery("obj.ag_status", new SysMap("ag_status", CommUtil
					.null2Int(ag_status)), "=");
		}
		if (!CommUtil.null2String(goods_name).equals("")) {
			qo.addQuery("obj.ag_goods.goods_name", new SysMap("goods_name", "%"
					+ goods_name.trim() + "goods_name"), "=");
		}
		IPageList pList = this.activityGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ag_status", ag_status);
		mv.addObject("goods_name", goods_name);
		mv.addObject("act_id", act_id);
		return mv;
	}

	@SecurityMapping(title = "活动通过", value = "/admin/activity_goods_audit.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_goods_audit.htm")
	public String activity_goods_audit(HttpServletRequest request,
			HttpServletResponse response, String act_id, String mulitId,
			String currentPage) {
		Activity act = this.activityService.getObjById(CommUtil
				.null2Long(act_id));
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				ActivityGoods ac = this.activityGoodsService.getObjById(Long
						.parseLong(id));
				ac.setAg_status(1);
				this.activityGoodsService.update(ac);
				// 活动状态
				// 活动状态，0为无活动，1为待审核，2为审核通过，3为活动商品已经卖完
				Goods goods = ac.getAg_goods();
				goods.setActivity_status(2);
				// 参加活动的商品，价格计算0.1是既8折为0.8所以这里设定个0.1
				BigDecimal num = BigDecimal.valueOf(0.1);
				// 活动时卖的价格;四舍五入保留两位小数
				BigDecimal acprice = num.multiply(act.getAc_rebate()).multiply(
						goods.getStore_price()).setScale(2,
						BigDecimal.ROUND_HALF_UP);// 销售价格
				goods.setGoods_current_price(acprice);
				this.goodService.update(goods);
			}
		}
		return "redirect:activity_goods_list.htm?act_id=" + act_id
				+ "&currentPage=" + currentPage;
	}

	@SecurityMapping(title = "活动拒绝", value = "/admin/activity_goods_refuse.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_goods_refuse.htm")
	public String activity_goods_refuse(HttpServletRequest request,
			HttpServletResponse response, String act_id, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				ActivityGoods ac = this.activityGoodsService.getObjById(Long
						.parseLong(id));
				ac.setAg_status(-1);
				this.activityGoodsService.update(ac);
				// 活动状态
				Goods goods = ac.getAg_goods();
				goods.setActivity_status(0);
				// 恢复原价
				goods.setGoods_current_price(goods.getStore_price());
				this.goodService.update(goods);
			}
		}
		return "redirect:activity_goods_list.htm?act_id=" + act_id
				+ "&currentPage=" + currentPage;
	}
}