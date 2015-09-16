package com.lakecloud.weixin.platform.view.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.foundation.domain.GoodsBrand;
import com.lakecloud.foundation.domain.GoodsBrandCategory;
import com.lakecloud.foundation.service.IGoodsBrandCategoryService;
import com.lakecloud.foundation.service.IGoodsBrandService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

/**
 * @info 微信商城客户端商品品牌管理控制器，
 * @since V1.3
 * @version 1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net hz
 * 
 */
@Controller
public class WeixinPlatformBrandViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsBrandCategoryService goodsBrandCategorySerivce;
	@Autowired
	private IGoodsBrandService brandService;

	/**
	 * 商品品牌列表
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/brand_list.htm")
	public ModelAndView brand_list(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView("weixin/platform/brand_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("iskyshop_view_type", "weixin");
		List<GoodsBrandCategory> gbcs = this.goodsBrandCategorySerivce
				.query(
						"select obj from GoodsBrandCategory obj  order by obj.addTime asc",
						null, -1, -1);
		mv.addObject("gbcs", gbcs);
		return mv;
	}

	@RequestMapping("/weixin/platform/brand_list_home.htm")
	public ModelAndView brand_list_home(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/brand_list_home.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("iskyshop_view_type", "weixin");
		Map map = new HashMap();
		map.put("recommend", true);
		map.put("audit", 1);
		List<GoodsBrand> brands_recommend = this.brandService
				.query(
						"select obj from GoodsBrand obj where obj.recommend=:recommend and obj.audit=:audit order by obj.addTime desc",
						map, -1, -1);
		mv.addObject("brands_recommend", brands_recommend);
		return mv;
	}
}
