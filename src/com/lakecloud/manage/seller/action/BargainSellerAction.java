package com.lakecloud.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Bargain;
import com.lakecloud.foundation.domain.BargainGoods;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.query.BargainGoodsQueryObject;
import com.lakecloud.foundation.service.IBargainGoodsService;
import com.lakecloud.foundation.service.IBargainService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.manage.seller.Tools.BargainSellerTools;

/**
 * @info 天天特价卖家控制器 自V1.3版开始
 * @since V1.3
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net wang
 * 
 */
@Controller
public class BargainSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IBargainGoodsService bargainGoodsService;
	@Autowired
	private IBargainService bargainService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private BargainSellerTools bargainSellerTools;

	/**
	 * BargainGoods列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "今日特价", value = "/seller/bargain.htm*", rtype = "seller", rname = "今日特价", rcode = "bargain_seller", rgroup = "促销管理")
	@RequestMapping("/seller/bargain.htm")
	public ModelAndView bargain(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/bargain.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		int day_count = this.configService.getSysConfig().getBargain_validity();
		List<Date> dates = new ArrayList<Date>();
		for (int i = 0; i < day_count; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, i + 1);
			dates.add(cal.getTime());
		}
		mv.addObject("dates", dates);
		mv.addObject("bargainSellerTools", bargainSellerTools);
		return mv;
	}

	/**
	 * 申请今日特价
	 * 
	 * @param request
	 * @param response
	 * @param bargain_time
	 * @return
	 */
	@SecurityMapping(title = "申请今日特价", value = "/seller/bargain_apply.htm*", rtype = "seller", rname = "今日特价", rcode = "bargain_seller", rgroup = "促销管理")
	@RequestMapping("/seller/bargain_apply.htm")
	public ModelAndView bargain_apply(HttpServletRequest request,
			HttpServletResponse response, String bargain_time) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/bargain_apply.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2String(bargain_time).equals("")) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, 1);
			bargain_time = CommUtil.formatShortDate(cal.getTime());
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, this.configService.getSysConfig()
				.getBargain_validity());
		if (CommUtil.formatDate(bargain_time).after(cal.getTime())) {// 如果用户申请的时间超过系统允许的时间周期，则自动变更为第二天
			cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, 1);
			bargain_time = CommUtil.formatShortDate(cal.getTime());
		}
		Map params = new HashMap();
		params.put("bt", CommUtil.formatDate(bargain_time));
		List<Bargain> bargain = this.bargainService.query(
				"select obj from Bargain obj where obj.bargain_time =:bt",
				params, 0, 1);
		int audit_count = this.bargainSellerTools
				.query_bargain_audit(bargain_time);
		int bargain_count = this.configService.getSysConfig()
				.getBargain_maximum();
		if (bargain.size() > 0) {
			bargain_count = bargain.get(0).getMaximum();
		}
		if (audit_count >= bargain_count) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "特价申请名额已满");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/bargain.htm");
		}
		mv.addObject("bargain_rebate", bargain.size() > 0 ? bargain.get(0)
				.getRebate() : this.configService.getSysConfig()
				.getBargain_rebate());
		mv.addObject("bargain_state", bargain.size() > 0 ? bargain.get(0)
				.getState() : this.configService.getSysConfig()
				.getBargain_state());
		mv.addObject("bargain_time", bargain_time);
		return mv;
	}

	/**
	 * 添加商品
	 * 
	 * @param request
	 * @param response
	 * @param goods_name
	 */
	@SecurityMapping(title = "添加商品", value = "/seller/bargain_goods.htm*", rtype = "seller", rname = "今日特价", rcode = "bargain_seller", rgroup = "促销管理")
	@RequestMapping("/seller/bargain_goods.htm")
	public void bargain_goods(HttpServletRequest request,
			HttpServletResponse response, String goods_name) {
		Map params = new HashMap();
		params.put("goods_name", "%" + goods_name.trim() + "%");
		params.put("goods_status", 0);
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("group_buy", 0);
		params.put("activity_status", 0);
		params.put("bargain_status", 0);
		List<Goods> goods_list = this.goodsService
				.query(
						"select obj from Goods obj where obj.goods_name like :goods_name and obj.goods_status=:goods_status and obj.goods_store.user.id=:user_id and obj.group_buy =:group_buy and obj.activity_status =:activity_status and obj.bargain_status =:bargain_status order by obj.addTime desc",
						params, -1, -1);
		List<Map> maps = new ArrayList<Map>();
		for (Goods goods : goods_list) {
			Map map = new HashMap();
			map.put("goods_name", goods.getGoods_name());
			map.put("goods_id", goods.getId());
			maps.add(map);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(maps, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 商品保存
	 * 
	 * @param request
	 * @param response
	 * @param goods_ids
	 * @param bargain_session
	 * @param bargain_time
	 * @param rebate
	 * @return
	 */
	@SecurityMapping(title = "商品保存", value = "/seller/bargain_apply_save.htm*", rtype = "seller", rname = "今日特价", rcode = "bargain_seller", rgroup = "促销管理")
	@RequestMapping("/seller/bargain_apply_save.htm")
	public String bargain_apply_save(HttpServletRequest request,
			HttpServletResponse response, String goods_ids,
			String bargain_time, String bg_rebate) {
		if (goods_ids != null && !goods_ids.equals("")) {
			String[] ids = goods_ids.split(",");
			for (String id : ids) {
				if (!id.equals("")) {
					BargainGoods bg = new BargainGoods();
					bg.setAddTime(new Date());
					bg.setBg_status(0);
					bg.setBg_time(CommUtil.formatDate(bargain_time));
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(id));
					goods.setBargain_status(1);
					this.goodsService.update(goods);
					// 计算商品价格
					double bg_price = CommUtil.mul(
							CommUtil.mul(0.1, bg_rebate), goods
									.getStore_price());
					BigDecimal num = BigDecimal.valueOf(0.1);
					// 计算特价商品状态折扣率
					BigDecimal reb = BigDecimal.valueOf(CommUtil
							.null2Double(bg_rebate));
					// goods.setGoods_current_price(num.multiply(reb)
					// .multiply(goods.getStore_price()));
					bg.setBg_goods(goods);
					BigDecimal now_price = BigDecimal.valueOf(CommUtil.mul(
							CommUtil.mul(num, reb), goods
									.getGoods_current_price()));
					bg.setBg_price(now_price);
					goods.setBargain_status(1);
					this.goodsService.update(goods);
					bg.setBg_goods(goods);
					bg.setBg_rebate(BigDecimal.valueOf(CommUtil
							.null2Double(bg_rebate)));
					this.bargainGoodsService.save(bg);

				}
			}
			return "redirect:bargain_apply_success.htm?bargain_time="
					+ bargain_time;
		} else {
			return "redirect:bargain_apply_error.htm";
		}

	}

	@SecurityMapping(title = "商品保存成功", value = "/seller/bargain_apply_success.htm*", rtype = "seller", rname = "今日特价", rcode = "bargain_seller", rgroup = "促销管理")
	@RequestMapping("/seller/bargain_apply_success.htm")
	public ModelAndView bargain_apply_success(HttpServletRequest request,
			HttpServletResponse response, String bargain_time) {
		ModelAndView mv = new JModelAndView("success.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		mv.addObject("op_title", "申请今日特价成功");
		mv
				.addObject("url", CommUtil.getURL(request)
						+ "/seller/bargain_goods_list.htm?bargain_time="
						+ bargain_time);
		return mv;
	}

	@SecurityMapping(title = "商品保存失败", value = "/seller/bargain_apply_error.htm*", rtype = "seller", rname = "今日特价", rcode = "bargain_seller", rgroup = "促销管理")
	@RequestMapping("/seller/bargain_apply_error.htm")
	public ModelAndView bargain_apply_error(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("error.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		mv.addObject("op_title", "至少选择一件商品");
		mv.addObject("url", CommUtil.getURL(request) + "/seller/bargain.htm");
		return mv;
	}

	/**
	 * 特价商品列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param bargain_time
	 * @return
	 */
	@SecurityMapping(title = "特价商品列表", value = "/seller/bargain_goods_list.htm*", rtype = "seller", rname = "今日特价", rcode = "bargain_seller", rgroup = "促销管理")
	@RequestMapping("/seller/bargain_goods_list.htm")
	public ModelAndView bargain_goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage,
			String bargain_time) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/bargain_goods_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		BargainGoodsQueryObject qo = new BargainGoodsQueryObject(currentPage,
				mv, "addTime", "desc");
		qo.addQuery("obj.bg_goods.goods_store.user.id", new SysMap("user_id",
				SecurityUserHolder.getCurrentUser().getId()), "=");
		qo.addQuery("obj.bg_time", new SysMap("bg_time", CommUtil
				.formatDate(bargain_time)), "=");
		qo.setPageSize(30);
		IPageList pList = this.bargainGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("bargain_time", bargain_time);
		return mv;
	}
}