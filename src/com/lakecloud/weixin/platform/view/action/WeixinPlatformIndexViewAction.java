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
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsBrand;
import com.lakecloud.foundation.domain.GoodsFloor;
import com.lakecloud.foundation.domain.GoodsFloorGoods;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IAreaService;
import com.lakecloud.foundation.service.IGoodsBrandService;
import com.lakecloud.foundation.service.IGoodsFloorGoodsService;
import com.lakecloud.foundation.service.IGoodsFloorService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.view.web.tools.GoodsFloorViewTools;
import com.lakecloud.weixin.utils.WeixinUtils;

/**
 * @info 微信商城客户端首页控制器，
 * @since V1.3
 * @version 1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net hz
 * 
 */
@Controller
public class WeixinPlatformIndexViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsFloorService goodsFloorService;
	@Autowired
	private GoodsFloorViewTools gf_tools;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IGoodsFloorGoodsService goodsFloorGoodsService;

	/**
	 * app商城首页
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/weixin/platform/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/platform/index.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("iskyshop_view_type", "weixin");
		String app_city_id = get_app_city_id(request, mv);
		Map map = new HashMap();
		map.put("store_recommend", true);
		map.put("goods_status", 0);
		String hql = "select obj from Goods obj where obj.store_recommend=:store_recommend and "
				+ " obj.goods_status=:goods_status ";
		// 添加城市筛选
		if (!"all".equals(app_city_id) && !"-1".equals(app_city_id)) {
			hql += " and obj.goods_store.area.parent.id=:app_city_id ";
			map.put("app_city_id", Long.parseLong(app_city_id));
		}
		hql += " order by obj.store_recommend_time desc";
		List<Goods> store_reommend_goods_list = this.goodsService.query(hql,
				map, 0, 4);// 只取前4条热销推荐广告位数据
		mv.addObject("store_reommend_goods", store_reommend_goods_list);
		mv.addObject("store_reommend_goods_count", Math.ceil(CommUtil.div(
				store_reommend_goods_list.size(), 5)));
		map.clear();
		map.put("recommend", true);
		map.put("audit", 1);
		List<GoodsBrand> brands_recommend = this.brandService
				.query(
						"select obj from GoodsBrand obj where obj.recommend=:recommend and obj.audit=:audit order by obj.addTime desc",
						map, 0, 4);// 品牌推荐展示前4个品牌
		mv.addObject("brands_recommend", brands_recommend);
		mv.addObject("store_id", request.getSession(false).getAttribute(
				"store_id"));
		getFloors(request, mv);
		return mv;
	}

	@RequestMapping("/weixin/platform/nav_bottom.htm")
	public ModelAndView nav_bottom(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/platform/nav_bottom.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		if (op != null && !op.equals("")) {
			mv.addObject("op", op);
		}
		return mv;
	}

	@RequestMapping("/weixin/platform/footer.htm")
	public ModelAndView footer(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/platform/footer.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		return mv;
	}

	/**
	 * 首页商品楼层数据，该数据纳入系统缓存页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/weixin/platform/floor.htm")
	public ModelAndView floor(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/platform/floor.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		List<GoodsFloorGoods> list = null;
		String hql = "";
		Map params = new HashMap();
		params.put("gf_display", true);
		List<GoodsFloor> floors = this.goodsFloorService
				.query(
						"select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null order by obj.gf_sequence asc",
						params, -1, -1);
		String app_city_id = get_app_city_id(request, mv);
		// 添加城市筛选
		if (!"all".equals(app_city_id) && !"-1".equals(app_city_id)) {
			hql = " and obj.goods_store.area.parent.id=:app_city_id ";
			params.put("app_city_id", Long.parseLong(app_city_id));
		}
		list = this.goodsFloorGoodsService
				.query(
						"select obj from GoodsFloorGoods obj where obj.goodsFloor.gf_display=:gf_display and obj.goodsFloor.parent.id is null"
								+ hql
								+ " order by obj.goodsFloor.gf_sequence asc",
						params, -1, -1);
		// List<GoodsFloor> floors = new ArrayList<GoodsFloor>();
		// List<Long> goodsFloorIds = new ArrayList<Long>();
		// for (GoodsFloorGoods goodsFloorGoods : list) {
		// if (!goodsFloorIds
		// .contains(goodsFloorGoods.getGoodsFloor().getId())) {
		// goodsFloorIds.add(goodsFloorGoods.getGoodsFloor().getId());
		// floors.add(goodsFloorGoods.getGoodsFloor());
		// }
		// }
		mv.addObject("floors", floors);
		mv.addObject("goodsFloorGoods", list);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("url", CommUtil.getURL(request));
		return mv;
	}

	@RequestMapping("/weixin/platform/city_choose.htm")
	public ModelAndView cityChooseView(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/platform/city_choose.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		List<Area> provinces = this.areaService
				.query(
						"select obj from Area obj where obj.level=0  order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("provinces", provinces);
		return mv;
	}

	private void getFloors(HttpServletRequest request, ModelAndView mv) {
		List<GoodsFloorGoods> list = null;
		String hql = "";
		Map params = new HashMap();
		params.put("gf_display", true);
		List<GoodsFloor> floors = this.goodsFloorService
				.query(
						"select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null order by obj.gf_sequence asc",
						params, -1, -1);
		String app_city_id = get_app_city_id(request, mv);
		// 添加城市筛选
		if (!"all".equals(app_city_id) && !"-1".equals(app_city_id)) {
			hql = " and obj.goods_store.area.parent.id=:app_city_id ";
			params.put("app_city_id", Long.parseLong(app_city_id));
		}
		list = this.goodsFloorGoodsService
				.query(
						"select obj from GoodsFloorGoods obj where obj.goodsFloor.gf_display=:gf_display and obj.goodsFloor.parent.id is null and obj.goods.goods_status=0"
								+ hql
								+ " order by obj.goodsFloor.gf_sequence asc",
						params, -1, -1);
		mv.addObject("floors", floors);
		mv.addObject("goodsFloorGoods", list);
		// mv.addObject("gf_tools", this.gf_tools);
		// mv.addObject("url", CommUtil.getURL(request));
	}

	/**
	 * 判断是否用户登录到达首页 1.用户登录，则首页地区定位到用户注册地区 2.不是用户登录，则从cookie中获取
	 */
	private String get_app_city_id(HttpServletRequest request, ModelAndView mv) {
		String app_city_id = "";
		String login_flag = request.getParameter("login_flag");
		if ("1".equals(login_flag)) {
			mv.addObject("login_flag", login_flag);
			User user = SecurityUserHolder.getCurrentUser();
			app_city_id = user.getArea().getId() == null ? "" : user.getArea()
					.getId().toString();
		} else {
			mv.addObject("login_flag", "0");
			request.getSession(false).setAttribute("iskyshop_view_type",
					"weixin");
			app_city_id = WeixinUtils.getCityIdFromCookies(request);// 获取cookies
		}
		return app_city_id;
	}
}
