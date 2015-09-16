package com.lakecloud.weixin.platform.view.action;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Activity;
import com.lakecloud.foundation.domain.ActivityGoods;
import com.lakecloud.foundation.domain.BargainGoods;
import com.lakecloud.foundation.domain.DeliveryGoods;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsBrand;
import com.lakecloud.foundation.domain.GoodsBrandCategory;
import com.lakecloud.foundation.domain.GoodsClass;
import com.lakecloud.foundation.domain.GoodsFloor;
import com.lakecloud.foundation.domain.GoodsFloorGoods;
import com.lakecloud.foundation.domain.GroupGoods;
import com.lakecloud.foundation.service.IActivityGoodsService;
import com.lakecloud.foundation.service.IActivityService;
import com.lakecloud.foundation.service.IBargainGoodsService;
import com.lakecloud.foundation.service.IDeliveryGoodsService;
import com.lakecloud.foundation.service.IGoodsBrandCategoryService;
import com.lakecloud.foundation.service.IGoodsBrandService;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.IGoodsFloorGoodsService;
import com.lakecloud.foundation.service.IGoodsFloorService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGroupGoodsService;
import com.lakecloud.foundation.service.IPromotionService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.lucene.LuceneResult;
import com.lakecloud.lucene.LuceneUtil;
import com.lakecloud.lucene.LuceneVo;
import com.lakecloud.view.web.tools.GoodsFloorViewTools;
import com.lakecloud.weixin.utils.WeixinUtils;

/**
 * @info 微信商城客户端商品管理控制器，用来查看微信商城中的商品
 * @since V1.3
 * @version 1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net hz
 * 
 */
@Controller
public class WeixinPlatformGoodsViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IBargainGoodsService bargainGoodsService;
	@Autowired
	private IGoodsBrandCategoryService goodsBrandCategorySerivce;
	@Autowired
	private IGoodsBrandService goodsBrandSerivce;
	@Autowired
	private IDeliveryGoodsService deliverygoodsSerivce;
	@Autowired
	private IGoodsClassService gcSerivce;
	@Autowired
	private IPromotionService promotionService;
	@Autowired
	private IGoodsFloorService goodsFloorService;
	@Autowired
	private GoodsFloorViewTools gf_tools;
	@Autowired
	private IGoodsFloorGoodsService goodsFloorGoodsService;
	/**
	 * 商品列表
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/goods_list.htm")
	public ModelAndView goods_list(HttpServletRequest request,
			HttpServletResponse response, String type,String floorId) {
		ModelAndView mv = new JModelAndView("weixin/platform/goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("iskyshop_view_type", "weixin");
		String op_title = "商品列表";
		List<Goods> goods_list = null;
		if (type != null && !type.equals("")) {
			if (type.equals("hot")) {
				op_title = "热卖宝贝";
				Map params = new HashMap();
				String queryString = "";
				String app_city_id = WeixinUtils.getCityIdFromCookies(request);
				String hql = "";
				if (null == floorId) {
					params.put("store_recommend", true);
					params.put("goods_status", 0);
					if (!"all".equals(app_city_id) && !"-1".equals(app_city_id)) {
						hql = " and obj.goods_store.area.parent.id=:app_city_id ";
						params.put("app_city_id", Long.parseLong(app_city_id));
					}
					queryString = " obj.store_recommend=:store_recommend and obj.goods_status=:goods_status";
					goods_list = this.goodsService.query(
							"select obj from Goods obj where " + queryString
									+ hql + " order by obj.goods_salenum desc",
							params, 0, 12);
				} else {
					Map map = new HashMap();
					map.put("gf_display", true);
					GoodsFloor goodsFloor = this.goodsFloorService
							.getObjById(Long.parseLong(floorId));
					map.put("goodsFloor", goodsFloor);
					hql = " and obj.goodsFloor=:goodsFloor";
					if (!"all".equals(app_city_id) && !"-1".equals(app_city_id)) {
						hql = hql
								+ " and obj.goods_store.area.parent.id=:app_city_id ";
						map.put("app_city_id", Long.parseLong(app_city_id));
					}
					// List<GoodsFloor> floors = this.goodsFloorService
					// .query(
					// "select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null order by obj.gf_sequence asc",
					// map, -1, -1);
					// goods_list = new ArrayList<Goods>();
					// for (GoodsFloor goodsFloor : floors) {
					// if (goodsFloor.getId().toString().equals(floorId)) {
					// List<GoodsFloor> goodsFloorList = goodsFloor
					// .getChilds();
					// for (GoodsFloor gf : goodsFloorList) {
					// goods_list.addAll(gf_tools.generic_goods(gf
					// .getGf_gc_goods()));
					// }
					// }
					// }
					List<GoodsFloorGoods> list = this.goodsFloorGoodsService
							.query(
									"select obj from GoodsFloorGoods obj where obj.goodsFloor.gf_display=:gf_display and obj.goodsFloor.parent.id is null and obj.goods.goods_status=0"
											+ hql
											+ " order by obj.goodsFloor.gf_sequence asc",
									map, -1, -1);
					goods_list = new ArrayList<Goods>();
					for (GoodsFloorGoods goodsFloorGoods : list) {
						goods_list.add(goodsFloorGoods.getGoods());
					}
				}
			}
			if (type.equals("recommend")) {
				op_title = "商城推荐";
				Map params = new HashMap();
				params.put("goods_status", 0);
				params.put("weixin_status", 1);
				goods_list = this.goodsService
						.query(
								"select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status order by obj.weixin_shop_recommendTime asc",
								params, 0, 12);
			}
			mv.addObject("type", type);
		}
		mv.addObject("objs", goods_list);
		mv.addObject("op_title", op_title);
		return mv;
	}

	/**
	 * 商品列表动态加载
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/goods_data.htm")
	public ModelAndView goods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count, String type) {
		ModelAndView mv = new JModelAndView("weixin/platform/goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Goods> goods_list = null;
		int begin = 0;
		if (begin_count != null && !begin_count.equals("")) {
			begin = CommUtil.null2Int(begin_count);
		}
		if (type != null && !type.equals("")) {
			if (type.equals("hot")) {
				Map params = new HashMap();
				params.put("goods_status", 0);
				params.put("weixin_status", 1);
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status order by obj.goods_salenum desc",
								params, begin, 12);
			}
			if (type.equals("recommend")) {
				Map params = new HashMap();
				params.put("goods_status", 0);
				params.put("weixin_status", 1);
				params.put("weixin_shop_recommend", true);
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status and obj.weixin_shop_recommend=:weixin_shop_recommend order by obj.weixin_shop_recommendTime asc",
								params, begin, 12);
			}
		}
		mv.addObject("objs", goods_list);
		return mv;
	}

	/***
	 * 搜索列表
	 * 
	 * @param request
	 * @param response
	 * @param gbc_id
	 *            ：品牌类型id
	 * @param gb_id
	 *            ：品牌id
	 * @param keyword
	 *            ：关键字
	 * @return
	 */

	@RequestMapping("/weixin/platform/search_goods_list.htm")
	public ModelAndView search_goods_list(HttpServletRequest request,
			HttpServletResponse response, String gbc_id, String gb_id,
			String keyword, String gc_id,String orderType,String orderBy,
			String store_price_begin,String store_price_end,String currentPage,String pageSize ,String storeId) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/search_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String op_title = "商品列表";
		pageSize=pageSize==null?WeixinUtils._WEIXIN_PAGESIZE+"":pageSize;
		/**
		 * 读取cookies
		 * */
		String app_city_id=WeixinUtils.getCityIdFromCookies(request);//城市id
		/**
		 * lucence处理
		 * 
		 * */
		String path = System.getProperty("user.dir") + File.separator
		+ "luence" + File.separator + "goods";
		LuceneUtil lucene = LuceneUtil.instance();
		lucene.setIndex_path(path);
		boolean order_type = true;
		String order_by = "";
		if (CommUtil.null2String(orderType).equals("asc")) {
			order_type = false;
		}
		if (CommUtil.null2String(orderType).equals("")) {
			orderType = "desc";
		}
		if (CommUtil.null2String(orderBy).equals("store_price")) {
			order_by = "store_price";
		}
		if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
			order_by = "goods_salenum";
		}
		if (CommUtil.null2String(orderBy).equals("goods_collect")) {
			order_by = "goods_collect";
		}
		if (CommUtil.null2String(orderBy).equals("goods_addTime")) {
			order_by = "addTime";
		}
		if (CommUtil.null2String(orderBy).equals("current_price")) {
			order_by = "goods_current_price";
		}
		Sort sort = null;
		if (!CommUtil.null2String(order_by).equals("")) {
			sort = new Sort(new SortField(order_by, SortField.DOUBLE,
					order_type));// 排序false升序,true降序
		}
		LuceneResult pList=new LuceneResult();
		List<String> keys=new ArrayList<String>();
		List<String> values=new ArrayList<String>();
		List<BooleanClause.Occur> clauses=new ArrayList<BooleanClause.Occur>();
		keys.add(LuceneVo.GOODS_STATUS);
		values.add("0");
		clauses.add(BooleanClause.Occur.MUST);
		keys.add(LuceneVo.WEIXIN_STATUS);
		values.add("1");
		clauses.add(BooleanClause.Occur.MUST);
		if(storeId!=null && !storeId.equals("")){
			keys.add(LuceneVo.STORE_ID);
			values.add(storeId);
			clauses.add(BooleanClause.Occur.MUST);
		}
		if(gc_id!=null && !gc_id.equals("")){//分类
			keys.add(LuceneVo.GC_ID);
			values.add(gc_id);
			clauses.add(BooleanClause.Occur.MUST);
		}
		if(gb_id!=null && !gb_id.equals("")){//品牌id
			values.add(gb_id+"");
			keys.add(LuceneVo.BRAND_ID);
			clauses.add(BooleanClause.Occur.MUST);
		}
		if(gbc_id!=null && !gbc_id.equals("")){//品牌分类
			GoodsBrandCategory gbc = this.goodsBrandCategorySerivce
			.getObjById(CommUtil.null2Long(gbc_id));
			op_title = gbc.getName();
			Set<Long> ids = this.getBrandIds(gbc);
			for (Long id : ids) {  
				values.add(id+"");
				keys.add(LuceneVo.BRAND_ID);
				clauses.add(BooleanClause.Occur.SHOULD);
			} 
		}
		if(!"all".equals(app_city_id) && !"-1".equals(app_city_id)){
			keys.add(LuceneVo.CIT_ID);
			values.add(app_city_id);
			clauses.add(BooleanClause.Occur.MUST);
		}
		if (keyword != null && !CommUtil.null2String(keyword).equals("")) {
			pList = lucene.search(keyword,
					CommUtil.null2Int(currentPage),
					CommUtil.null2Int(pageSize), 
					CommUtil.null2Double(store_price_begin),
					CommUtil.null2Double(store_price_end), null, sort,
					keys.toArray(new String[keys.size()]),values.toArray(new String[values.size()]),
					clauses.toArray(new BooleanClause.Occur[clauses.size()]),null,null
			);
			mv.addObject("keyword", CommUtil.null2String(keyword));
			op_title = "“" + CommUtil.null2String(keyword) + "”搜索结果";
		}else if(gc_id != null && !gc_id.equals("")) {
			GoodsClass gc = this.gcSerivce
					.getObjById(CommUtil.null2Long(gc_id));
			op_title = "“" + gc.getClassName() + "”搜索结果";
			//分类搜索
			pList = lucene.searchByConditions(keys.toArray(new String[keys.size()]),values.toArray(new String[values.size()]), 
					clauses.toArray(new BooleanClause.Occur[clauses.size()]),
					CommUtil.null2Int(currentPage), 
					CommUtil.null2Int(pageSize), 
					CommUtil.null2Double(store_price_begin),
					CommUtil.null2Double(store_price_end),
					null, sort);
		}else if (gb_id != null && !gb_id.equals("")) {
			GoodsBrand gb = this.goodsBrandSerivce.getObjById(CommUtil
					.null2Long(gb_id));
			op_title = gb.getName();
			//品牌ID 
			pList = lucene.searchByConditions(keys.toArray(new String[keys.size()]),values.toArray(new String[values.size()]),
					clauses.toArray(new BooleanClause.Occur[clauses.size()]),
					CommUtil.null2Int(currentPage),
					CommUtil.null2Int(pageSize), 
					CommUtil.null2Double(store_price_begin),
					CommUtil.null2Double(store_price_end),
					null, sort);
		}else if (gbc_id != null && !gbc_id.equals("")) {
			//品牌分类 
			pList = lucene.searchByConditions(keys.toArray(new String[keys.size()]),
					values.toArray(new String[values.size()]),clauses.toArray(new BooleanClause.Occur[clauses.size()]),
					CommUtil.null2Int(currentPage), 
					CommUtil.null2Int(pageSize), 
					CommUtil.null2Double(store_price_begin),
					CommUtil.null2Double(store_price_end),
					null, sort);
		}
		mv.addObject("gb_id", gb_id);
		mv.addObject("gc_id",gc_id);
		mv.addObject("gbc_id", gbc_id);
		mv.addObject("objs", pList.getGoods_list());
		mv.addObject("op_title", op_title);
		mv.addObject("currentPage",CommUtil.null2Int(currentPage)+1);
		mv.addObject("totalPage",pList.getPages());
		mv.addObject("storeId",storeId);
		mv.addObject("orderType",orderType);
		mv.addObject("orderBy",orderBy);
		request.getSession(false).setAttribute("iskyshop_view_type", "weixin");
		return mv;
	}

	/***
	 * 搜索列表动态加载
	 * 
	 * @param request
	 * @param response
	 * @param gbc_id
	 *            ：品牌类型id
	 * @param gb_id
	 *            ：品牌id
	 * @param keyword
	 *            ：关键字
	 * @return
	 */
	@RequestMapping("/weixin/platform/search_goods_data.htm")
	public ModelAndView search_goods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count, String gbc_id,
			String gb_id, String keyword, String gc_id,
			String orderType,String orderBy,String pageSize,String store_price_begin,
			String store_price_end,String currentPage,String storeId) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/search_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int begin = 0;
		if (begin_count != null && !begin_count.equals("")) {
			begin = CommUtil.null2Int(begin_count);
		}
		pageSize=pageSize==null?WeixinUtils._WEIXIN_PAGESIZE+"":pageSize;
		//String currentPage=begin/CommUtil.null2Int(pageSize)+"";
		/**
		 * 读取cookies
		 * */
		String app_city_id=WeixinUtils.getCityIdFromCookies(request);//城市id
		/**
		 * lucence处理
		 * 
		 * */
		String path = System.getProperty("user.dir") + File.separator
		+ "luence" + File.separator + "goods";
		LuceneUtil lucene = LuceneUtil.instance();
		lucene.setIndex_path(path);
		boolean order_type = true;
		String order_by = "";
		if (CommUtil.null2String(orderType).equals("asc")) {
			order_type = false;
		}
		if (CommUtil.null2String(orderType).equals("")) {
			orderType = "desc";
		}
		if (CommUtil.null2String(orderBy).equals("store_price")) {
			order_by = "store_price";
		}
		if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
			order_by = "goods_salenum";
		}
		if (CommUtil.null2String(orderBy).equals("goods_collect")) {
			order_by = "goods_collect";
		}
		if (CommUtil.null2String(orderBy).equals("current_price")) {
			order_by = "goods_current_price";
		}
		Sort sort = null;
		if (!CommUtil.null2String(order_by).equals("")) {
			sort = new Sort(new SortField(order_by, SortField.DOUBLE,
					order_type));// 排序false升序,true降序
		}
		LuceneResult pList=new LuceneResult();
		List<String> keys=new ArrayList<String>();
		List<String> values=new ArrayList<String>();
		List<BooleanClause.Occur> clauses=new ArrayList<BooleanClause.Occur>();
		keys.add(LuceneVo.GOODS_STATUS);
		values.add("0");
		clauses.add(BooleanClause.Occur.MUST);
		keys.add(LuceneVo.WEIXIN_STATUS);
		values.add("1");
		clauses.add(BooleanClause.Occur.MUST);;
		if(gc_id!=null && !gc_id.equals("")){//分类
			keys.add(LuceneVo.GC_ID);
			values.add(gc_id);
			clauses.add(BooleanClause.Occur.MUST);
		}
		if(gb_id!=null && !gb_id.equals("")){//品牌id
			values.add(gb_id+"");
			keys.add(LuceneVo.BRAND_ID);
			clauses.add(BooleanClause.Occur.MUST);
		}
		if(gbc_id!=null && !gbc_id.equals("")){//品牌分类
			GoodsBrandCategory gbc = this.goodsBrandCategorySerivce
			.getObjById(CommUtil.null2Long(gbc_id));
			Set<Long> ids = this.getBrandIds(gbc);
			for (Long id : ids) {  
				values.add(id+"");
				keys.add(LuceneVo.BRAND_ID);
				clauses.add(BooleanClause.Occur.SHOULD);
			} 
		}
		
		if(!"all".equals(app_city_id) && !"-1".equals(app_city_id)){
			keys.add(LuceneVo.CIT_ID);
			values.add(app_city_id);
			clauses.add(BooleanClause.Occur.MUST);
		}
		if(storeId!=null && !storeId.equals("")){
			keys.add(LuceneVo.STORE_ID);
			values.add(storeId);
			clauses.add(BooleanClause.Occur.MUST);
		}
		if (keyword != null && !CommUtil.null2String(keyword).equals("")) {
			pList = lucene.search(keyword,
					CommUtil.null2Int(currentPage),
					CommUtil.null2Int(pageSize),
					CommUtil.null2Double(store_price_begin),
					CommUtil.null2Double(store_price_end), null, sort,
					keys.toArray(new String[keys.size()]),values.toArray(new String[values.size()]),
					clauses.toArray(new BooleanClause.Occur[clauses.size()]),null,null);
			mv.addObject("keyword", CommUtil.null2String(keyword));
		}else if (gc_id != null && !gc_id.equals("")) {
			//商品分类
			pList = lucene.searchByConditions(keys.toArray(new String[keys.size()]),values.toArray(new String[values.size()]), 
					clauses.toArray(new BooleanClause.Occur[clauses.size()]),
					CommUtil.null2Int(currentPage), 
					CommUtil.null2Int(pageSize), 
					CommUtil.null2Double(store_price_begin),
					CommUtil.null2Double(store_price_end),
					null, sort);
	  }else if (gb_id != null && !gb_id.equals("")) {
			//品牌ID
			pList = lucene.searchByConditions(keys.toArray(new String[keys.size()]),values.toArray(new String[values.size()]),
					clauses.toArray(new BooleanClause.Occur[clauses.size()]),
					CommUtil.null2Int(currentPage),
					CommUtil.null2Int(pageSize), 
					CommUtil.null2Double(store_price_begin),
					CommUtil.null2Double(store_price_end),
					null, sort);
	   }else if (gbc_id != null && !gbc_id.equals("")) {
		//分类
		pList = lucene.searchByConditions(keys.toArray(new String[keys.size()]),
				values.toArray(new String[values.size()]),clauses.toArray(new BooleanClause.Occur[clauses.size()]),
				CommUtil.null2Int(currentPage), 
				CommUtil.null2Int(pageSize), 
				CommUtil.null2Double(store_price_begin),
				CommUtil.null2Double(store_price_end),
				null, sort);
	  }
	mv.addObject("gc_id", gc_id);
	mv.addObject("gb_id", gb_id);
	mv.addObject("gbc_id", gbc_id);
	mv.addObject("currentPage",(CommUtil.null2Int(currentPage)+1));
	mv.addObject("totalPage",pList.getPages());
	mv.addObject("objs", pList.getGoods_list());
	return mv;
}

	private Set<Long> getBrandIds(GoodsBrandCategory gbc) {
		Set<Long> ids = new HashSet<Long>();
		for (GoodsBrand gb : gbc.getBrands()) {
			ids.add(gb.getId());
		}
		return ids;
	}

	/**
	 * 精品团购列表
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/group_goods_list.htm")
	public ModelAndView group_goods_list(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/group_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("iskyshop_view_type", "weixin");
		Map params = new HashMap();
		params.put("gg_status", 1);// 团购商品状态
		params.put("store_weixin_status", 1);// 店铺开通微信状态
		params.put("group_status", 0);// 团购商品对应团购活动状态
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		List<GroupGoods> goods_list = this.groupgoodsService
				.query("select obj from GroupGoods obj where obj.gg_goods.goods_store.weixin_status=:store_weixin_status and obj.gg_status=:gg_status and obj.group.status=:group_status and obj.group.beginTime<=:beginTime and obj.group.endTime>=:endTime",
						params, 0, 12);
		mv.addObject("objs", goods_list);
		return mv;
	}

	/**
	 * 精品团购动态加载
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/group_goods_data.htm")
	public ModelAndView group_goods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/group_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int begin = 0;
		if (begin_count != null && !begin_count.equals("")) {
			begin = CommUtil.null2Int(begin_count);
		}
		Map params = new HashMap();
		params.put("gg_status", 1);// 团购商品状态
		params.put("store_weixin_status", 1);// 店铺开通微信状态
		params.put("group_status", 0);// 团购商品对应团购活动状态
		List<GroupGoods> goods_list = this.groupgoodsService
				.query("select obj from GroupGoods obj where obj.gg_goods.goods_store.weixin_status=:store_weixin_status and obj.gg_status=:gg_status and obj.group.status=:group_status",
						params, begin, 12);
		mv.addObject("objs", goods_list);
		return mv;
	}

	/**
	 * 活动商品列表
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/activity_goods_list.htm")
	public ModelAndView activity_goods_list(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/activity_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("iskyshop_view_type", "weixin");
		Map map = new HashMap();
		map.put("ac_status", 1);
		map.put("ac_begin_time", new Date());
		map.put("ac_end_time", new Date());
		List<Activity> activities = this.activityService
				.query("select obj from Activity obj where obj.ac_status=:ac_status and obj.ac_begin_time<=:ac_begin_time and obj.ac_end_time>=:ac_end_time",
						map, 0, 1);
		if (activities.size() > 0) {
			map.clear();
			map.put("ag_status", 1);
			map.put("act_id", activities.get(0).getId());
			map.put("store_weixin_status", 1);// 店铺开通微信状态
			List<ActivityGoods> act_goods = this.activityGoodsService
					.query("select obj from ActivityGoods obj where obj.ag_status=:ag_status and obj.act.id=:act_id and obj.ag_goods.goods_store.weixin_status=:store_weixin_status",
							map, 0, 12);
			mv.addObject("objs", act_goods);
		}
		return mv;
	}

	/**
	 * 活动商品列表动态加载
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/activity_goods_data.htm")
	public ModelAndView activity_goods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/activity_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int begin = 0;
		if (begin_count != null && !begin_count.equals("")) {
			begin = CommUtil.null2Int(begin_count);
		}
		Map map = new HashMap();
		map.put("ac_status", 1);
		map.put("ac_begin_time", new Date());
		map.put("ac_end_time", new Date());
		List<Activity> activities = this.activityService
				.query("select obj from Activity obj where obj.ac_status=:ac_status and obj.ac_begin_time<=:ac_begin_time and obj.ac_end_time>=:ac_end_time",
						map, 0, 1);
		if (activities.size() > 0) {
			map.clear();
			map.put("ag_status", 1);
			map.put("act_id", activities.get(0).getId());
			map.put("store_weixin_status", 1);// 店铺开通微信状态
			List<ActivityGoods> act_goods = this.activityGoodsService
					.query("select obj from ActivityGoods obj where obj.ag_status=:ag_status and obj.act.id=:act_id and obj.ag_goods.goods_store.weixin_status=:store_weixin_status",
							map, begin, 12);
			mv.addObject("objs", act_goods);
		}
		return mv;
	}

	/**
	 * 天天特价商品列表
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/bargain_goods_list.htm")
	public ModelAndView bargain_goods_list(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/bargain_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("iskyshop_view_type", "weixin");
		Map map = new HashMap();
		map.put("bg_status", 1);
		map.put("bg_time",
				CommUtil.formatDate(CommUtil.formatShortDate(new Date())));// 特价时间
		map.put("store_weixin_status", 1);// 店铺开通微信状态
		List<BargainGoods> bar_goods = this.bargainGoodsService
				.query("select obj from BargainGoods obj where obj.bg_status=:bg_status and obj.bg_time=:bg_time and obj.bg_goods.goods_store.weixin_status=:store_weixin_status",
						map, 0, 12);
		mv.addObject("objs", bar_goods);
		return mv;
	}

	/**
	 * 天天特价商品列表动态加载
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/bargain_goods_data.htm")
	public ModelAndView bargain_goods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/bargain_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int begin = 0;
		if (begin_count != null && !begin_count.equals("")) {
			begin = CommUtil.null2Int(begin_count);
		}
		Map map = new HashMap();
		map.put("bg_status", 1);
		map.put("bg_time",
				CommUtil.formatDate(CommUtil.formatShortDate(new Date())));// 特价时间
		map.put("store_weixin_status", 1);// 店铺开通微信状态
		List<BargainGoods> bar_goods = this.bargainGoodsService
				.query("select obj from BargainGoods obj where obj.bg_status=:bg_status and obj.bg_time=:bg_time and obj.bg_goods.goods_store.weixin_status=:store_weixin_status",
						map, begin, 12);
		mv.addObject("objs", bar_goods);
		return mv;
	}

	/**
	 * 组合销售商品列表
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/combin_goods_list.htm")
	public ModelAndView combin_goods_list(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/combin_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("iskyshop_view_type", "weixin");
		Map params = new HashMap();
		params.put("goods_status", 0);
		params.put("store_weixin_status", 1);
		params.put("combin_status", 2);
		params.put("combin_begin_time", new Date());
		params.put("combin_end_time", new Date());
		List<Goods> goods_list = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.weixin_status=:store_weixin_status and obj.goods_status=:goods_status and obj.combin_status=:combin_status and obj.combin_begin_time<=:combin_begin_time and obj.combin_end_time>=:combin_end_time",
						params, 0, 12);
		mv.addObject("objs", goods_list);
		return mv;
	}

	/**
	 * 组合销售商品列表动态加载
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/combin_goods_data.htm")
	public ModelAndView combin_goods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/combin_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int begin = 0;
		if (begin_count != null && !begin_count.equals("")) {
			begin = CommUtil.null2Int(begin_count);
		}
		Map params = new HashMap();
		params.put("goods_status", 0);
		params.put("store_weixin_status", 1);
		params.put("combin_status", 2);
		params.put("combin_begin_time", new Date());
		params.put("combin_end_time", new Date());
		List<Goods> goods_list = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.weixin_status=:store_weixin_status and obj.goods_status=:goods_status and obj.combin_status=:combin_status and obj.combin_begin_time<=:combin_begin_time and obj.combin_end_time>=:combin_end_time",
						params, begin, 12);
		mv.addObject("objs", goods_list);
		return mv;
	}

	/**
	 * 买就送商品列表
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/delivery_goods_list.htm")
	public ModelAndView delivery_goods_list(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/delivery_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("iskyshop_view_type", "weixin");
		Map params = new HashMap();
		params.put("d_status", 1);
		params.put("store_weixin_status", 1);
		params.put("d_begin_time", new Date());
		params.put("d_end_time", new Date());
		List<DeliveryGoods> goods_list = this.deliverygoodsSerivce
				.query("select obj from DeliveryGoods obj where obj.d_goods.goods_store.weixin_status=:store_weixin_status and obj.d_status=:d_status and obj.d_begin_time<=:d_begin_time and obj.d_end_time>=:d_end_time",
						params, 0, 12);
		mv.addObject("objs", goods_list);
		return mv;
	}

	/**
	 * 买就送商品列表动态加载
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/delivery_goods_data.htm")
	public ModelAndView delivery_goods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/delivery_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int begin = 0;
		if (begin_count != null && !begin_count.equals("")) {
			begin = CommUtil.null2Int(begin_count);
		}
		Map params = new HashMap();
		params.put("d_status", 0);
		params.put("weixin_status", 1);
		params.put("d_begin_time", new Date());
		params.put("d_end_time", new Date());
		List<DeliveryGoods> goods_list = this.deliverygoodsSerivce
				.query("select obj from DeliveryGoods obj where obj.d_goods.goods_store.weixin_status=:weixin_status and obj.d_status=:d_status and obj.d_begin_time<=:d_begin_time and obj.d_end_time>=:d_end_time",
						params, begin, 12);
		mv.addObject("objs", goods_list);
		return mv;
	}

	//店铺用户中心菜单栏
	@RequestMapping("/weixin/goods_all_list.htm")
	public ModelAndView goods_all_list(HttpServletRequest request,
			HttpServletResponse response, String storeId,String type,String key) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String op_title = "商品列表";
		List<Goods> goods_list = new ArrayList<Goods>();
//		if(type.equals("all")){
//			//获取该店铺所有商品
//			Map param = new HashMap();
//			param.put("goods_status", 0);
//			param.put("store_id",CommUtil.null2Long(storeId));
//			goods_list = this.goodsService
//					.query(
//							"select obj from Goods obj where obj.goods_status=:goods_status and obj.goods_store.id=:store_id",
//							param, -1, -1);	
//		}else 
			if(type.equals("new")){
			//获取该店铺的上新商品
		    Date date = new Date();
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Timestamp endtime =new Timestamp(date.getTime());
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTime(endtime);
		    calendar.add(Calendar.MONTH, -1);
		    Timestamp starttime = Timestamp.valueOf(sdf.format(calendar.getTime()));//获取当前时间以及过去一个月的日期
			System.out.println("starttime:"+starttime+"endtime"+endtime);
			Map param = new HashMap();
			param.put("starttime",starttime);
			param.put("end" +
					"time",endtime);
			param.put("goods_status", 0);
			param.put("store_id",CommUtil.null2Long(storeId));
			goods_list = this.goodsService
					.query(
							"select obj from Goods obj where obj.addTime>=:starttime and obj.addTime<=:endtime and obj.goods_status=:goods_status and obj.goods_store.id=:store_id",
							param, -1, -1);
		}else{
			//促销
			Map paramp = new HashMap();
			paramp.put("store_id",CommUtil.null2Long(storeId));
			paramp.put("goods_status", 0);
//			paramp.put("status","1");//0未激活，1激活，2停用，4删除
//			List<Promotion> store_goodsp = this.promotionService
//					.query(
//							"select obj from Promotion obj where obj.field1=:store_id and obj.status=:status",
//							paramp, -1, -1);
//			for(int i = 0;i<store_goodsp.size();i++){
//				Promotion promotion = store_goodsp.get(i);
//				List<PromotionLevel> prolev_list =promotion.getPromotionLevels();
//				for(int j= 0;j<prolev_list.size();j++){
//					Long goodsid =prolev_list.get(j).getGoods_id();
//					 Goods goods = this.goodsService.getObjById(goodsid);
//					if(goods_list.contains(goods)){
//						
//					}else{
//					 goods_list.add(goods);
//					}
//				}
//			}
			goods_list = this.goodsService
			.query(
					"select obj from Goods obj where obj.goods_price!=obj.goods_current_price and obj.goods_store.id=:store_id and obj.goods_status=:goods_status",
					paramp, -1, -1);
		}
		
		
		mv.addObject("objs", goods_list);
		mv.addObject("op_title", op_title);
		mv.addObject("key", key);
		return mv;
	}
	
}
