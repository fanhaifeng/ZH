package com.lakecloud.weixin.platform.view.action;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
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

import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsClass;
import com.lakecloud.foundation.domain.StoreClass;
import com.lakecloud.foundation.domain.StoreGrade;
import com.lakecloud.foundation.domain.query.StoreQueryObject;
import com.lakecloud.foundation.service.IAreaService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IStoreClassService;
import com.lakecloud.foundation.service.IStoreGradeService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.lucene.LuceneResult;
import com.lakecloud.lucene.LuceneUtil;
import com.lakecloud.lucene.LuceneVo;
import com.lakecloud.view.web.tools.StoreViewTools;
import com.lakecloud.weixin.utils.WeixinUtils;

/**
 * @info 手机app 商品搜索控制器，商城搜索支持关键字全文搜索,店铺搜索使用数据库关键字搜索
 * @since v1.2
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Controller
public class WeixinPlatformSearchViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IStoreClassService storeClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private IStoreGradeService storeGradeService;
	@Autowired
	private IAreaService areaService;

	@RequestMapping("/weixin/platform/search.htm")
	public ModelAndView search(HttpServletRequest request,
			HttpServletResponse response, String type, String keyword,
			String currentPage, String orderBy, String orderType,
			String store_price_begin, String store_price_end, String view_type,
			String sc_id, String storeGrade_id, String checkbox_id,
			String storepoint, String area_id, String area_name,
			String goods_view,String pageSize) {
		ModelAndView mv = new JModelAndView("weixin/platform/search_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (type == null || type.equals(""))
			type = "goods";
		keyword = CommUtil.decode(keyword);
		pageSize=pageSize==null?WeixinUtils._WEIXIN_PAGESIZE+"":pageSize;
		
		/**
		 * 获取cookies
		 * */
		String app_city_id=WeixinUtils.getCityIdFromCookies(request);
		//如果搜索类型是商店
		if (type.equals("store")) {
			mv = new JModelAndView("weixin/platform/store_list.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			StoreQueryObject sqo = new StoreQueryObject(currentPage, mv,
					"addTime", "desc");
			if (keyword != null && !keyword.equals("")) {
				sqo.addQuery("obj.store_name", new SysMap("store_name", "%"
						+ keyword + "%"), "like");
				mv.addObject("store_name", keyword);
			}
			//添加城市筛选
			if (!"all".equals(app_city_id) && !"-1".equals(app_city_id)) {
				sqo.addQuery("obj.area.parent.id", new SysMap("app_city_id", Long.parseLong(app_city_id)), "=");
			}
			//筛选app商店
			sqo.addQuery("obj.weixin_status", new SysMap("weixin_status",1), "=");
			if (sc_id != null && !sc_id.equals("")) {
				StoreClass storeclass = this.storeClassService
						.getObjById(CommUtil.null2Long(sc_id));
				Set<Long> ids = this.getStoreClassChildIds(storeclass);
				Map map = new HashMap();
				map.put("ids", ids);
				sqo.addQuery("obj.sc.id in (:ids)", map);
				mv.addObject("sc_id", sc_id);
			}
			if (storeGrade_id != null && !storeGrade_id.equals("")) {
				sqo.addQuery(
						"obj.grade.id",
						new SysMap("grade_id", CommUtil
								.null2Long(storeGrade_id)), "=");
				mv.addObject("storeGrade_id", storeGrade_id);
			}
			if (orderBy != null && !orderBy.equals("")) {
				sqo.setOrderBy(orderBy);
				if (orderBy.equals("addTime")) {
					orderType = "asc";
				} else {
					orderType = "desc";
				}
				sqo.setOrderType(orderType);
				mv.addObject("orderBy", orderBy);
				mv.addObject("orderType", orderType);
			}
			if (checkbox_id != null && !checkbox_id.equals("")) {
				sqo.addQuery("obj." + checkbox_id, new SysMap(
						"obj_checkbox_id", true), "=");
				mv.addObject("checkbox_id", checkbox_id);
			}
			if (storepoint != null && !storepoint.equals("")) {
				sqo.addQuery("obj.sp.store_evaluate1", new SysMap(
						"sp_store_evaluate1", new BigDecimal(storepoint)), ">=");
				mv.addObject("storepoint", storepoint);
			}
			if (area_id != null && !area_id.equals("")) {
				mv.addObject("area_id", area_id);
				Area area = this.areaService.getObjById(CommUtil
						.null2Long(area_id));
				Set<Long> area_ids = this.getAreaChildIds(area);
				Map params = new HashMap();
				params.put("ids", area_ids);
				sqo.addQuery("obj.area.id in (:ids)", params);
			}
			if (area_name != null && !area_name.equals("")) {
				mv.addObject("area_name", area_name);
				sqo.addQuery("obj.area.areaName", new SysMap("areaName", "%"
						+ area_name.trim() + "%"), "like");
				sqo.addQuery("obj.area.parent.areaName", new SysMap("areaName",
						"%" + area_name.trim() + "%"), "like", "or");
				sqo.addQuery("obj.area.parent.parent.areaName", new SysMap(
						"areaName", "%" + area_name.trim() + "%"), "like", "or");
			}
			sqo.addQuery("obj.store_status", new SysMap("store_status", 2), "=");
			sqo.setPageSize(WeixinUtils._WEIXIN_PAGESIZE);
			IPageList pList = this.storeService.list(sqo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			List<StoreClass> scs = this.storeClassService
					.query("select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc",
							null, -1, -1);
			// 查询常用地区
			Map map = new HashMap();
			map.put("common", true);
			List<Area> areas = this.areaService
					.query("select obj from Area obj where obj.common =:common order by sequence asc",
							map, -1, -1);
			mv.addObject("areas", areas);
			mv.addObject("storeViewTools", storeViewTools);
			mv.addObject("scs", scs);
			List<StoreGrade> storeGrades = this.storeGradeService.query(
					"select obj from StoreGrade obj order by sequence asc",
					null, -1, -1);
			mv.addObject("storeGrades", storeGrades);
		}
		//如果搜索类型是商品
		if (type.equals("goods") && !CommUtil.null2String(keyword).equals("")) {
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
			if(!"all".equals(app_city_id) && !"-1".equals(app_city_id)){
				keys.add(LuceneVo.CIT_ID);
				values.add(app_city_id);
				clauses.add(BooleanClause.Occur.MUST);
			}
			
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
			Sort sort = null;
			if (!CommUtil.null2String(order_by).equals("")) {
				sort = new Sort(new SortField(order_by, SortField.DOUBLE,
						order_type));// 排序false升序,true降序
			}
			pList = lucene.search(keyword,
					CommUtil.null2Int(currentPage),
					CommUtil.null2Int(pageSize),
					CommUtil.null2Double(store_price_begin),
					CommUtil.null2Double(store_price_end), null, sort,
					keys.toArray(new String[keys.size()]),values.toArray(new String[values.size()]),
					clauses.toArray(new BooleanClause.Occur[clauses.size()]),null,null);
			CommUtil.saveLucene2ModelAndView("goods", pList, mv);
			GoodsClass gc = new GoodsClass();
			gc.setClassName("商品搜索结果");
			mv.addObject("gc", gc);
			mv.addObject("store_price_end", store_price_end);
			mv.addObject("store_price_begin", store_price_begin);
			mv.addObject("keyword", keyword);
			mv.addObject("orderBy", orderBy);
			mv.addObject("orderType", orderType);
			if (CommUtil.null2String(goods_view).equals("list")) {
				goods_view = "list";
			} else {
				goods_view = "thumb";
			}
			// 当天直通车商品，按照直通车价格排序
			if (this.configService.getSysConfig().isZtc_status()) {
				Map ztc_map = new HashMap();
				ztc_map.put("ztc_status", 3);
				ztc_map.put("now_date", new Date());
				ztc_map.put("ztc_gold", 0);
				List<Goods> ztc_goods = this.goodsService
						.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc",
								ztc_map, 0, 5);
				mv.addObject("ztc_goods", ztc_goods);
			}
			mv.addObject("goods_view", goods_view);
		}
		if (CommUtil.null2String(view_type).equals("")) {
			view_type = "list";
		}
		mv.addObject("view_type", view_type);
		mv.addObject("type", type);
		return mv;
	}
	
	/**
	 * 店铺列表动态加载
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/store_data.htm")
	public ModelAndView store_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count, String type,
			String keyword, String currentPage, String orderBy,
			String orderType, String store_price_begin, String store_price_end,
			String view_type, String sc_id, String storeGrade_id,
			String checkbox_id, String storepoint, String area_id,
			String area_name, String goods_view) {
		int begin = 0;
		if (begin_count != null && !begin_count.equals("")) {
			begin = CommUtil.null2Int(begin_count);
		}
		ModelAndView mv = new JModelAndView("weixin/platform/store_data.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		keyword = CommUtil.decode(keyword);
		/**
		 * 获取cookies
		 * */
		String app_city_id = WeixinUtils.getCityIdFromCookies(request);
		// StoreQueryObject sqo = new StoreQueryObject((begin + 1) + "", mv,
		// "addTime", "desc");
		Map paramsMap = new HashMap();
		String whereString = "";
		String orderString = " order by obj.addTime desc";
		if (keyword != null && !keyword.equals("")) {
			// sqo.addQuery("obj.store_name", new SysMap("store_name", "%"
			// + keyword + "%"), "like");
			mv.addObject("store_name", keyword);
			paramsMap.put("store_name", keyword);
			whereString = whereString + " and obj.store_name=:store_name";
		}
		// 添加城市筛选
		if (!"all".equals(app_city_id) && !"-1".equals(app_city_id)) {
			// sqo.addQuery("obj.area.parent.id", new SysMap("app_city_id", Long
			// .parseLong(app_city_id)), "=");
			paramsMap.put("areaId", Long.parseLong(app_city_id));
			whereString = whereString + " and obj.area.parent.id=:areaId";
		}
		// 筛选app商店
		// sqo.addQuery("obj.weixin_status", new SysMap("weixin_status", 1),
		// "=");
		paramsMap.put("weixin_status", 1);
		whereString = whereString + " and obj.weixin_status=:weixin_status";
		if (sc_id != null && !sc_id.equals("")) {
			StoreClass storeclass = this.storeClassService.getObjById(CommUtil
					.null2Long(sc_id));
			Set<Long> ids = this.getStoreClassChildIds(storeclass);
			Map map = new HashMap();
			map.put("ids", ids);
			// sqo.addQuery("obj.sc.id in (:ids)", map);
			mv.addObject("sc_id", sc_id);
			paramsMap.put("ids", ids);
			whereString = whereString + " and obj.sc.id in (:ids)";
		}
		if (storeGrade_id != null && !storeGrade_id.equals("")) {
			// sqo.addQuery("obj.grade.id", new SysMap("grade_id", CommUtil
			// .null2Long(storeGrade_id)), "=");
			mv.addObject("storeGrade_id", storeGrade_id);
			paramsMap.put("grade_id", CommUtil.null2Long(storeGrade_id));
			whereString = whereString + " and obj.grade.id =:grade_id";
		}
		if (orderBy != null && !orderBy.equals("")) {
			// sqo.setOrderBy(orderBy);
			if (orderBy.equals("addTime")) {
				orderType = "asc";
			} else {
				orderType = "desc";
			}
			orderString = " order by obj.addTime " + orderType;
			// sqo.setOrderType(orderType);
			mv.addObject("orderBy", orderBy);
			mv.addObject("orderType", orderType);
		}
		if (checkbox_id != null && !checkbox_id.equals("")) {
			// sqo.addQuery("obj." + checkbox_id, new SysMap("obj_checkbox_id",
			// true), "=");
			mv.addObject("checkbox_id", checkbox_id);
			paramsMap.put("checkbox_id", checkbox_id);
			whereString = whereString + " and obj." + checkbox_id
					+ " =:checkbox_id";
		}
		if (storepoint != null && !storepoint.equals("")) {
			// sqo.addQuery("obj.sp.store_evaluate1", new SysMap(
			// "sp_store_evaluate1", new BigDecimal(storepoint)), ">=");
			mv.addObject("storepoint", storepoint);
			paramsMap.put("sp_store_evaluate1", new BigDecimal(storepoint));
			whereString = whereString
					+ " and obj.sp.store_evaluate1 >=:sp_store_evaluate1";
		}
		if (area_id != null && !area_id.equals("")) {
			mv.addObject("area_id", area_id);
			Area area = this.areaService
					.getObjById(CommUtil.null2Long(area_id));
			Set<Long> area_ids = this.getAreaChildIds(area);
			Map params = new HashMap();
			params.put("ids", area_ids);
			// sqo.addQuery("obj.area.id in (:ids)", params);
			paramsMap.put("area_ids", area_ids);
			whereString = whereString + " and obj.area.id in (:area_ids)";
		}
		if (area_name != null && !area_name.equals("")) {
			mv.addObject("area_name", area_name);
			// sqo.addQuery("obj.area.areaName", new SysMap("areaName", "%"
			// + area_name.trim() + "%"), "like");
			// sqo.addQuery("obj.area.parent.areaName", new SysMap("areaName",
			// "%"
			// + area_name.trim() + "%"), "like", "or");
			// sqo.addQuery("obj.area.parent.parent.areaName", new SysMap(
			// "areaName", "%" + area_name.trim() + "%"), "like", "or");
			paramsMap.put("area_name", area_name);
			whereString = whereString
					+ " and (obj.area.areaName like '%:area_name%' or obj.area.parent.areaName like '%:area_name%' or obj.area.parent.parent.areaName like '%:area_name%' )";
		}
		// sqo.addQuery("obj.store_status", new SysMap("store_status", 2), "=");
		paramsMap.put("store_status", 2);
		whereString = whereString + " and obj.store_status =:store_status";
		// sqo.setPageSize(1);
		// IPageList pList = this.storeService.list(sqo);
		// if (new Integer(pList.getPages()) >= (begin + 1)) {
		// CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		// }
		List store_list = this.storeService.query(
				"select obj from Store obj where 1=1 " + whereString
						+ orderString, paramsMap, begin, WeixinUtils._WEIXIN_PAGESIZE);
		mv.addObject("objs", store_list);
		List<StoreClass> scs = this.storeClassService
				.query(
						"select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		// 查询常用地区
		Map map = new HashMap();
		map.put("common", true);
		List<Area> areas = this.areaService
				.query(
						"select obj from Area obj where obj.common =:common order by sequence asc",
						map, -1, -1);
		mv.addObject("areas", areas);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("scs", scs);
		List<StoreGrade> storeGrades = this.storeGradeService.query(
				"select obj from StoreGrade obj order by sequence asc", null,
				-1, -1);
		mv.addObject("storeGrades", storeGrades);
		if (CommUtil.null2String(view_type).equals("")) {
			view_type = "list";
		}
		mv.addObject("view_type", view_type);
		mv.addObject("type", type);
		return mv;
	}

	private Set<Long> getStoreClassChildIds(StoreClass sc) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(sc.getId());
		for (StoreClass storeclass : sc.getChilds()) {
			Set<Long> cids = getStoreClassChildIds(storeclass);
			for (Long cid : cids) {
				ids.add(cid);
			}
		}
		return ids;
	}

	private Set<Long> getAreaChildIds(Area area) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(area.getId());
		for (Area are : area.getChilds()) {
			Set<Long> cids = getAreaChildIds(are);
			for (Long cid : cids) {
				ids.add(cid);
			}
		}
		return ids;
	}

}
