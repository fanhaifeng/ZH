package com.lakecloud.weixin.manage.buyer.action;

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

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.core.tools.database.DatabaseTools;
import com.lakecloud.foundation.domain.Address;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.service.IAddressService;
import com.lakecloud.foundation.service.IAreaService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

/**
 * @info 微信客户端买家收货地址控制器
 * @since V1.3
 * @version 1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net hz 2013-12-2
 * 
 */
@Controller
public class WeixinAddressBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private DatabaseTools databaseTools;

	/**
	 * Address列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "微信用户收货地址", value = "/weixin/buyer/address.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/address.htm")
	public ModelAndView weixin_address(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("weixin/address.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("uid", SecurityUserHolder.getCurrentUser().getId());
		params.put("status", true);
		List<Address> objs = this.addressService
				.query("select obj from Address obj where obj.user.id=:uid and obj.deleteStatus!=:status order by addTime desc",
						params, -1, -1);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("objs", objs);
		mv.addObject("areas", areas);
		return mv;
	}

	@SecurityMapping(title = "收货地址添加", value = "/weixin/buyer/address_add.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/address_add.htm")
	public ModelAndView weixin_address_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("weixin/address_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "收货地址编辑", value = "/weixin/buyer/address_edit.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/address_edit.htm")
	public ModelAndView weixin_address_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("weixin/address_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		Address obj = this.addressService.getObjById(CommUtil.null2Long(id));
		Area area = obj.getArea();
		Long areaIdSecond = area.getParent().getId();// 市
		Long areaIdFirst = null;// 省
		for (int i = 0; i < area.getLevel(); i++) {
			Area areaTemp = this.areaService.getObjById(CommUtil
					.null2Long(areaIdSecond));
			if (null != areaTemp.getParent()) {
				areaIdFirst = areaTemp.getParent().getId();
			} else {// null为省份
				break;
			}
		}
		Map mapSecond = new HashMap();
		mapSecond.put("pid", areaIdFirst);
		List<Area> areasSecond = this.areaService.query(
				"select obj from Area obj where obj.parent.id=:pid", mapSecond,
				-1, -1);
		Map mapThird = new HashMap();
		mapThird.put("pid", areaIdSecond);
		List<Area> areasThird = this.areaService.query(
				"select obj from Area obj where obj.parent.id=:pid", mapThird,
				-1, -1);
		mv.addObject("obj", obj);
		mv.addObject("areas", areas);
		mv.addObject("currentPage", currentPage);
		mv.addObject("areaIdFirst", areaIdFirst);
		mv.addObject("areaIdSecond", areaIdSecond);
		mv.addObject("areasThird", areasThird);
		mv.addObject("areasSecond", areasSecond);
		return mv;
	}

	/**
	 * address保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "收货地址保存", value = "/weixin/buyer/address_save.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/address_save.htm")
	public String weixin_address_save(HttpServletRequest request,
			HttpServletResponse response, String id, String area_id,
			String currentPage) {
		WebForm wf = new WebForm();
		Address address = null;
		if (id.equals("")) {
			address = wf.toPo(request, Address.class);
		} else {
			Address obj = this.addressService.getObjById(Long.parseLong(id));
			Address addressTemp = (Address) wf.toPo(request, obj);
			obj.setDeleteStatus(true);
			this.addressService.update(obj);
			address = new Address();
			address.setArea_info(addressTemp.getArea_info());
			if (null != addressTemp.getMobile()) {
				address.setMobile(addressTemp.getMobile());
			}
			if (null != addressTemp.getTelephone()) {
				address.setTelephone(addressTemp.getTelephone());
			}
			address.setTrueName(addressTemp.getTrueName());
			address.setZip(addressTemp.getZip());
		}
		address.setAddTime(new Date());
		address.setUser(SecurityUserHolder.getCurrentUser());
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		address.setArea(area);
		// if (id.equals("")) {
		this.addressService.save(address);
		// } else
		// this.addressService.update(address);
		return "redirect:address.htm";
	}

	@SecurityMapping(title = "收货地址删除", value = "/weixin/buyer/address_del.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/address_del.htm")
	public String weixin_address_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Address address = this.addressService.getObjById(Long
						.parseLong(id));
				address.setDeleteStatus(true);
				this.addressService.update(address);
				//this.addressService.delete(Long.parseLong(id));
			}
		}
		return "redirect:address.htm";
	}

}