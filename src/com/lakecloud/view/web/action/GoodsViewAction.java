package com.lakecloud.view.web.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.ip.IPSeeker;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Consult;
import com.lakecloud.foundation.domain.Evaluate;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsClass;
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.Group;
import com.lakecloud.foundation.domain.GroupGoods;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreClass;
import com.lakecloud.foundation.domain.UserGoodsClass;
import com.lakecloud.foundation.domain.query.ConsultQueryObject;
import com.lakecloud.foundation.domain.query.EvaluateQueryObject;
import com.lakecloud.foundation.domain.query.GoodsCartQueryObject;
import com.lakecloud.foundation.domain.query.GoodsQueryObject;
import com.lakecloud.foundation.service.IAreaService;
import com.lakecloud.foundation.service.IConsultService;
import com.lakecloud.foundation.service.IEvaluateService;
import com.lakecloud.foundation.service.IGoodsBrandService;
import com.lakecloud.foundation.service.IGoodsCartService;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGoodsSpecPropertyService;
import com.lakecloud.foundation.service.IGoodsTypePropertyService;
import com.lakecloud.foundation.service.IStoreClassService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserGoodsClassService;
import com.lakecloud.lucene.LuceneResult;
import com.lakecloud.lucene.LuceneUtil;
import com.lakecloud.lucene.LuceneVo;
import com.lakecloud.manage.admin.tools.UserTools;
import com.lakecloud.manage.seller.Tools.TransportTools;
import com.lakecloud.manage.util.CommonUtils;
import com.lakecloud.view.web.tools.AreaViewTools;
import com.lakecloud.view.web.tools.GoodsViewTools;
import com.lakecloud.view.web.tools.StoreViewTools;

/**
 * @info 商品前台控制器
 * @since v1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Controller
public class GoodsViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IConsultService consultService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private IGoodsTypePropertyService goodsTypePropertyService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IStoreClassService storeClassService;
	@Autowired
	private AreaViewTools areaViewTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private TransportTools transportTools;

	/**
	 * 根据单个店铺分类查看对应的商品
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/goods_list.htm")
	public ModelAndView goods_list(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String store_id,
			String recommend,String newgoods, String currentPage, String orderBy,
			String orderType, String begin_price, String end_price) {
		UserGoodsClass ugc = this.userGoodsClassService.getObjById(CommUtil
				.null2Long(gc_id));
		String template = "default";
		Store store = this.storeService
				.getObjById(CommUtil.null2Long(store_id));
		if (store != null) {
			if (store.getTemplate() != null && !store.getTemplate().equals("")) {
				template = store.getTemplate();
			}
			ModelAndView mv = new JModelAndView(template + "/goods_list.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv,
					orderBy, orderType);
			gqo.addQuery("obj.goods_store.id", new SysMap("goods_store_id",
					store.getId()), "=");
			gqo.addQuery("obj.goods_status", new SysMap("goods_status",
					0), "=");
			if (ugc != null) {
				Set<Long> ids = this.genericUserGcIds(ugc);
				List<UserGoodsClass> ugc_list = new ArrayList<UserGoodsClass>();
				for (Long g_id : ids) {
					UserGoodsClass temp_ugc = this.userGoodsClassService
							.getObjById(g_id);
					ugc_list.add(temp_ugc);
				}
				gqo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");
				for (int i = 0; i < ugc_list.size(); i++) {
					gqo.addQuery("ugc" + i, ugc_list.get(i), "obj.goods_ugcs",
							"member of", "or");
				}
			} else {
				ugc = new UserGoodsClass();
				ugc.setClassName("全部商品");
				mv.addObject("ugc", ugc);
			}
			if (recommend != null && !recommend.equals("")) {
				gqo.addQuery("obj.goods_recommend", new SysMap(
						"goods_recommend", CommUtil.null2Boolean(recommend)),
						"=");
			}
			if (newgoods != null && !newgoods.equals("")) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    Timestamp endtime =new Timestamp(date.getTime());
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(endtime);
				calendar.add(Calendar.MONTH, -1);
				Timestamp starttime = Timestamp.valueOf(sdf.format(calendar.getTime()));
				gqo.addQuery("obj.addTime", new SysMap(
						"starttime",starttime),
						">=");
				gqo.addQuery("obj.addTime", new SysMap(
						"endtime",endtime),
						"<=");
			}
			gqo.setPageSize(20);
			if (begin_price != null && !begin_price.equals("")) {
				gqo.addQuery("obj.goods_current_price", new SysMap("begin_price",
						BigDecimal.valueOf(CommUtil.null2Double(begin_price))),
						">=");
			}
			if (end_price != null && !end_price.equals("")) {
				gqo.addQuery("obj.goods_current_price", new SysMap("end_price",
						BigDecimal.valueOf(CommUtil.null2Double(end_price))),
						"<=");
			}
			IPageList pList = this.goodsService.list(gqo);
			String url = this.configService.getSysConfig().getAddress();
			if (url == null || url.equals("")) {
				url = CommUtil.getURL(request);
			}
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("ugc", ugc);
			mv.addObject("store", store);
			mv.addObject("recommend", recommend);
			mv.addObject("newgoods", newgoods);
			mv.addObject("begin_price", begin_price);
			mv.addObject("end_price", end_price);
			mv.addObject("goodsViewTools", goodsViewTools);
			mv.addObject("storeViewTools", storeViewTools);
			mv.addObject("areaViewTools", areaViewTools);
			return mv;
		} else {
			ModelAndView mv = new JModelAndView("error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "请求参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			return mv;
		}
	}

	private Set<Long> genericUserGcIds(UserGoodsClass ugc) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(ugc.getId());
		for (UserGoodsClass child : ugc.getChilds()) {
			Set<Long> cids = genericUserGcIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	/**
	 * 查看店铺商品详细信息
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/goods.htm")
	public ModelAndView goods(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = null;
		Goods obj = this.goodsService.getObjById(Long.parseLong(id));
		if (obj!=null&&obj.getGoods_status() == 0) {
			//String template = "default";
			/*
			if (obj.getGoods_store().getTemplate() != null
					&& !obj.getGoods_store().getTemplate().equals("")) {
				template = obj.getGoods_store().getTemplate();
			}
			*/
			mv = new JModelAndView("default/store_goods.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			//obj.setGoods_click(obj.getGoods_click() + 1);
			/*
			if (this.configService.getSysConfig().isZtc_status()
					&& obj.getZtc_status() == 2) {
				obj.setZtc_click_num(obj.getZtc_click_num() + 1);
			}*/
			/*
			if (obj.getGroup() != null && obj.getGroup_buy() == 2) {// 如果是团购商品，检查团购是否过期
				Group group = obj.getGroup();
				if (group.getEndTime().before(new Date())) {
					obj.setGroup(null);
					obj.setGroup_buy(0);
					obj.setGoods_current_price(obj.getStore_price());
				}
			}
            */
			//this.goodsService.update(obj);
			Store store = obj.getGoods_store();
			if (store.getStore_status() == 2) {
				mv.addObject("obj", obj);
				mv.addObject("store", store);
				/*
				Map params = new HashMap();
				params.put("user_id", obj.getGoods_store().getUser().getId());
				params.put("display", true);
				List<UserGoodsClass> ugcs = this.userGoodsClassService
						.query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
								params, -1, -1);
				mv.addObject("ugcs", ugcs);
				params.clear();
				*/
				/*
				params.put("goods_id", obj.getId());
				params.put("evaluate_type", "buyer");
				List<Evaluate> evas = this.evaluateService
						.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type",
								params, -1, -1);
				mv.addObject("eva_count", evas.size());
				*/
				mv.addObject("goodsViewTools", goodsViewTools);
				/*
				mv.addObject("storeViewTools", storeViewTools);
				mv.addObject("areaViewTools", areaViewTools);
				*/
				/*
				mv.addObject("transportTools", transportTools);
				*/
				// 添加到浏览过的商品
				/*
				List<Goods> user_viewed_goods = (List<Goods>) request
						.getSession(false).getAttribute("user_viewed_goods");
				if (user_viewed_goods == null) {
					user_viewed_goods = new ArrayList<Goods>();
				}
				boolean add = true;
				for (Goods goods : user_viewed_goods) {
					if (goods.getId().equals(obj.getId())) {
						add = false;
						break;
					}
				}
				if (add) {
					if (user_viewed_goods.size() >= 4) {
						user_viewed_goods.set(1, obj);
					} else
						user_viewed_goods.add(obj);
				}
				request.getSession(false).setAttribute("user_viewed_goods",
						user_viewed_goods);
						*/
				// 计算当期访问用户的IP地址，并计算对应的运费信息
				/*
				String current_ip = CommUtil.getIpAddr(request);// 获得本机IP
				if (CommUtil.isIp(current_ip)) {
					IPSeeker ip = new IPSeeker(null, null);
					String current_city = ip.getIPLocation(current_ip)
							.getCountry();
					System.out.println(current_city + ":"
							+ ip.getIPLocation(current_ip).getArea());
					mv.addObject("current_city", current_city);
				} else {
					mv.addObject("current_city", "未知地区");
				}*/
				// 查询运费地区
				/*
				List<Area> areas = this.areaService
						.query("select obj from Area obj where obj.parent.id is null order by obj.sequence asc",
								null, -1, -1);
				mv.addObject("areas", areas);
				this.generic_evaluate(obj.getGoods_store(), mv);
				*/
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "店铺未开通，拒绝访问");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该商品未上架，不允许查看");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}
	/**
	 * ajax加载店铺推荐商品列表 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/goods_recommend_list.htm")
	public ModelAndView goods_recommend_list(HttpServletRequest request,
			HttpServletResponse response, String store_id,String goods_id) {
		Goods obj = this.goodsService.getObjById(Long.parseLong(goods_id));
		obj.setGoods_click(obj.getGoods_click() + 1);
		this.goodsService.update(obj);
		ModelAndView mv = null;
		Store store = this.storeService.getObjById(CommUtil.null2Long(store_id));
		List<Goods> user_viewed_goods = (List<Goods>) request
				.getSession(false).getAttribute("user_viewed_goods");
		if (user_viewed_goods == null) {
			user_viewed_goods = new ArrayList<Goods>();
		}
		boolean add = true;
		for (Goods goods : user_viewed_goods) {
			if (goods.getId().equals(CommUtil.null2Long(goods_id))) {
				add = false;
				break;
			}
		}
		if (add) {
			if (user_viewed_goods.size() >= 4) {
				user_viewed_goods.set(1, obj);
			} else
				user_viewed_goods.add(obj);
		}
		request.getSession(false).setAttribute("user_viewed_goods",
				user_viewed_goods);
		if (store!=null&&store.getStore_status() == 2) {
			mv = new JModelAndView("default" + "/store_goods_recommend.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("store", store);
			GoodsQueryObject gqo = new GoodsQueryObject();
			gqo.setPageSize(4);
			gqo.addQuery("obj.goods_store.id", new SysMap("store_id", store.getId()), "=");
			gqo.addQuery("obj.goods_recommend", new SysMap(
					"goods_recommend", true), "=");
			gqo.addQuery("obj.id", new SysMap("id", CommUtil.null2Long(goods_id)), "!=");
			gqo.setOrderBy("addTime");
			gqo.setOrderType("desc");
			gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0),
					"=");
			mv.addObject("goods_recommend_list", this.goodsService
					.list(gqo).getResult());
		}
		return mv;
	}
	
	/**
	 * ajax加载店铺推荐商品列表 homepage
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_recommend_list_homepage.htm")
	public ModelAndView goods_recommend_list_homepage(
			HttpServletRequest request, HttpServletResponse response,
			String currentPage) {
		String template = "default";
		ModelAndView mv = new JModelAndView(template
				+ "/store_goods_recommend_homepage.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, "", "");
		gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		gqo.addQuery("obj.store_recommend",
				new SysMap("store_recommend", true), "=");
		String pc_city_id = CommonUtils.getCityIdFromCookies(request);
		// 添加城市筛选
		if (!"all".equals(pc_city_id) && !"-1".equals(pc_city_id)) {
			gqo.addQuery("obj.goods_store.area.parent.id", new SysMap(
					"pc_city_id", Long.parseLong(pc_city_id)), "=");
		}
		gqo.setPageSize(20);
		IPageList pList = this.goodsService.list(gqo);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		// mv.addObject("goodsViewTools", goodsViewTools);
		// mv.addObject("storeViewTools", storeViewTools);
		// mv.addObject("areaViewTools", areaViewTools);
		return mv;
	}
	
	/**
	 * 根据商城分类查看商品列表
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping("/store_goods_list.htm")
	public ModelAndView store_goods_list(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String store_price_begin,
			String store_price_end, String brand_ids, String gs_ids,
			String properties, String op, String goods_name, String area_name,
			String area_id, String goods_view, String all_property_status,
			String detail_property_status,String storeId,String gb_id,String pageSize) {
		ModelAndView mv = new JModelAndView("store_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsClass gc = this.goodsClassService.getObjById(CommUtil
				.null2Long(gc_id));
		mv.addObject("gc", gc);
		pageSize=pageSize==null?CommonUtils._PAGESIZE+"":pageSize;
		/**
		 * pc端获取cookies
		 * */
		String pc_city_id=CommonUtils.getCityIdFromCookies(request);
		//lucene搜索商品
		LuceneResult pList=new LuceneResult();
		List<String> keys=new ArrayList<String>();
		List<String> values=new ArrayList<String>();
		List<BooleanClause.Occur> clauses=new ArrayList<BooleanClause.Occur>();
		List<Query> querys=new ArrayList<Query>();
		BooleanQuery queryWhere= new BooleanQuery();
		/*keys.add(LuceneVo.GOODS_STATUS);
		values.add("0");
		clauses.add(BooleanClause.Occur.MUST);*/
		Term t =new Term(LuceneVo.GOODS_STATUS,"0");
        TermQuery termQuery=new TermQuery(t);
        queryWhere.add(termQuery, Occur.MUST);
		/*keys.add(LuceneVo.WEIXIN_STATUS);
		values.add("1");
		clauses.add(BooleanClause.Occur.MUST);;*/
		if(!"all".equals(pc_city_id) && !"-1".equals(pc_city_id)){
			/*keys.add(LuceneVo.CIT_ID);
			values.add(pc_city_id);
			clauses.add(BooleanClause.Occur.MUST);*/
			t =new Term(LuceneVo.CIT_ID,pc_city_id);
	        termQuery=new TermQuery(t);
	        queryWhere.add(termQuery, Occur.MUST);
		}
		if(storeId!=null && !storeId.equals("")){
			/*keys.add(LuceneVo.STORE_ID);
			values.add(storeId);
			clauses.add(BooleanClause.Occur.MUST);*/
			t =new Term(LuceneVo.STORE_ID,storeId);
	        termQuery=new TermQuery(t);
	        queryWhere.add(termQuery, Occur.MUST);
		}
		Set<Long> ids = this.genericIds(gc);
		if(ids!=null&&ids.size()>0){//分类
			//queryWhere+="(";
			BooleanQuery query= new BooleanQuery();
			for (Long id : ids) { 
				t =new Term(LuceneVo.GC_ID,id+"");
		        termQuery=new TermQuery(t);
		        query.add(termQuery, Occur.SHOULD);
				//queryWhere+=LuceneVo.GC_ID+":"+id+" OR ";
			}
			queryWhere.add(query,Occur.MUST);
			//queryWhere+=LuceneVo.GC_ID+":"+gcId+")";
		}
		if(gb_id!=null && !gb_id.equals("")){//品牌id
			/*values.add(gb_id+"");
			keys.add(LuceneVo.BRAND_ID);
			clauses.add(BooleanClause.Occur.MUST);*/
			t =new Term(LuceneVo.BRAND_ID,gb_id+"");
	        termQuery=new TermQuery(t);
	        queryWhere.add(termQuery, Occur.MUST);
		}
		String path = System.getProperty("user.dir") + File.separator
				+ "luence" + File.separator + "goods";
		LuceneUtil lucene = LuceneUtil.instance();
		lucene.setIndex_path(path);
		boolean order_type = true;
		
		if (orderBy == null || orderBy.equals("")) {
			orderBy = "goods_addTime";
		}
		String order_by = "";
		if (CommUtil.null2String(orderType).equals("asc")) {
			order_type = false;
		}
		if (CommUtil.null2String(orderType).equals("")) {
			orderType = "desc";
		}
		if (CommUtil.null2String(orderBy).equals("store_price")) {
			order_by = LuceneVo.STORE_PRICE;
		}
		if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
			order_by = LuceneVo.GOODS_SALENUM;
		}
		if (CommUtil.null2String(orderBy).equals("goods_collect")) {
			order_by = LuceneVo.GOODS_COLLECT;
		}
		if (CommUtil.null2String(orderBy).equals("goods_addTime")||CommUtil.null2String(orderBy).equals("new_product")) {
			order_by = LuceneVo.ADD_TIME;
		}
		if (CommUtil.null2String(orderBy).equals("current_price")) {
			order_by = LuceneVo.GOODS_CURRENT_PRICE;
		}
		if(CommUtil.null2String(orderBy).equals("new_product")){
			Calendar now=Calendar.getInstance();
			now.add(Calendar.MONTH,-1);
			Query queryRange = NumericRangeQuery.newLongRange(LuceneVo.ADD_TIME, now.getTimeInMillis()
					,new Date().getTime(), true, true);
			querys.add(queryRange);
		}
		Sort sort = null;
		if(!CommUtil.null2String(order_by).equals("")){
			if(LuceneVo.GOODS_COLLECT.equals(order_by)||LuceneVo.GOODS_SALENUM.equals(order_by)){
				sort = new Sort(new SortField(order_by, SortField.INT,
						order_type));// 排序false升序,true降序
			}else if(LuceneVo.ADD_TIME.equals(order_by)){
				sort = new Sort(new SortField(order_by, SortField.LONG,
						order_type));// 排序false升序,true降序
			}else{
				sort = new Sort(new SortField(order_by, SortField.DOUBLE,
						order_type));// 排序false升序,true降序
			}
		}
		if (op != null && !op.equals("")) {
			mv.addObject("op", op);
		}
	
		GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		Map paras = new HashMap();
		paras.put("ids", ids);
		gqo.addQuery("obj.gc.id in (:ids)", paras);
		if (store_price_begin != null && !store_price_begin.equals("")) {
			mv.addObject("store_price_begin", store_price_begin);
		}
		if (store_price_end != null && !store_price_end.equals("")) {
			mv.addObject("store_price_end", store_price_end);
		}
		if (goods_name != null && !goods_name.equals("")) {
			mv.addObject("goods_name", goods_name);
		}

		/*if (area_id != null && !area_id.equals("")) {
			Area area = this.areaService
					.getObjById(CommUtil.null2Long(area_id));
			mv.addObject("area", area);
			Set<Long> area_ids = this.getAreaChildIds(area);
			Map p_area = new HashMap();
			p_area.put("area_ids", area_ids);
			gqo.addQuery("obj.goods_store.area.id in (:area_ids)", p_area);
		}
		if (area_name != null && !area_name.equals("")) {
			mv.addObject("area_name", area_name);
			Map like_area = new HashMap();
			like_area.put("area_name", area_name + "%");
			List<Area> likes_areas = this.areaService
					.query("select obj from Area obj where obj.areaName like:area_name",
							like_area, -1, -1);
			Set<Long> like_area_ids = this.getArrayAreaChildIds(likes_areas);
			like_area.clear();
			like_area.put("like_area_ids", like_area_ids);
			gqo.addQuery("obj.goods_store.area.id in (:like_area_ids)",
					like_area);
		}
*/
		gqo.addQuery("obj.goods_store.store_status", new SysMap("store_status",
				2), "=");
		gqo.setPageSize(20);
		gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		List<Map> goods_property = new ArrayList<Map>();
		if (!CommUtil.null2String(brand_ids).equals("")) {//品牌查询
			String[] brand_id_list = brand_ids.substring(1).split("\\|");
			BooleanQuery query= new BooleanQuery();
			for (int i = 0; i < brand_id_list.length; i++) {
				Map map = new HashMap();
				String[] brand_info_list = brand_id_list[i].split(",");
				t =new Term(LuceneVo.BRAND_ID,brand_info_list[0]);
				TermQuery queryBrand=new TermQuery(t);
				query.add(queryBrand, Occur.SHOULD);
				map.put("name", "品牌");
				map.put("value",brand_info_list[1]);
				map.put("type", "brand");
				map.put("id",brand_info_list[0]);
				map.put("gs_info",brand_id_list[i]);
				goods_property.add(map);
				
			}
			queryWhere.add(query,Occur.MUST);
			mv.addObject("brand_ids", brand_ids);
		}
		if (!CommUtil.null2String(gs_ids).equals("")) {//规格
			queryWhere=this.processFacet("gs",gs_ids,queryWhere,goods_property);//处理规格
			mv.addObject("gs_ids", gs_ids);
		}
		if (!CommUtil.null2String(properties).equals("")) {
			queryWhere=this.processFacet("properties",properties,queryWhere,goods_property);//处理属性
			mv.addObject("properties", properties);
		}
		// 查询常用地区
		/*Map params = new HashMap();
		params.put("common", true);
		List<Area> areas = this.areaService
				.query("select obj from Area obj where obj.common=:common order by sequence asc",
						params, -1, -1);
		mv.addObject("areas", areas);*/
		// System.out.println("qo:" + gqo.getQuery());
		//分类搜索
		pList = lucene.search(goods_name,
				CommUtil.null2Int(currentPage),
				CommUtil.null2Int(pageSize), 
				CommUtil.null2Double(store_price_begin),
				CommUtil.null2Double(store_price_end), null, sort,
				keys.toArray(new String[keys.size()]),values.toArray(new String[values.size()]),
				clauses.toArray(new BooleanClause.Occur[clauses.size()]),queryWhere,querys
		);
		//IPageList iList = this.goodsService.list(gqo);
		CommUtil.saveLuceneResult2ModelAndView("", "", "", pList, mv);
		mv.addObject("gc", gc);
		mv.addObject("orderBy", orderBy);
		mv.addObject("user_viewed_goods", request.getSession(false)
				.getAttribute("user_viewed_goods"));
		mv.addObject("goods_property", goods_property);
		if (CommUtil.null2String(goods_view).equals("list")) {
			goods_view = "list";
		} else {
			goods_view = "thumb";
		}
		// 当天直通车商品，按照直通车价格排序
		/*if (this.configService.getSysConfig().isZtc_status()) {
			List<Goods> ztc_goods = null;
			Map ztc_map = new HashMap();
			ztc_map.put("ztc_status", 3);
			ztc_map.put("now_date", new Date());
			ztc_map.put("ztc_gold", 0);
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				ztc_goods = this.goodsService
						.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
								+ "order by obj.ztc_dredge_price desc",
								ztc_map, 0, 5);
			}
			if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
				ztc_map.put("gc_ids", ids);
				ztc_goods = this.goodsService
						.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold and obj.gc.id in (:gc_ids) "
								+ "order by obj.ztc_dredge_price desc",
								ztc_map, 0, 5);
			}
			mv.addObject("ztc_goods", ztc_goods);
		}*/
		if (detail_property_status != null
				&& !detail_property_status.equals("")) {
			mv.addObject("detail_property_status", detail_property_status);
			String temp_str[] = detail_property_status.split(",");
			Map pro_map = new HashMap();
			List pro_list = new ArrayList();
			for (String property_status : temp_str) {
				if (property_status != null && !property_status.equals("")) {
					String mark[] = property_status.split("_");
					pro_map.put(mark[0], mark[1]);
					pro_list.add(mark[0]);
				}
			}
			mv.addObject("pro_list", pro_list);
			mv.addObject("pro_map", pro_map);
		}
		mv.addObject("goods_view", goods_view);
		mv.addObject("all_property_status", all_property_status);
		return mv;
	}
	
	/**
	 * 处理筛选条件 String type
	 * @param type 类型 规格或属性
	 * @param gs_ids
	 * @param queryWhere
	 * @param goods_property
	 * @return BooleanQuery
	 */
	private BooleanQuery processFacet(String type,String gs_ids,BooleanQuery queryWhere,List<Map> goods_property){
		String[] gs_id_list = gs_ids.substring(1).split("\\|");
		String COLUMN="";
		if(type.equals("gs")){
			COLUMN=LuceneVo.GOODS_SPECIFICATION;
		}else if(type.equals("properties")){
			COLUMN=LuceneVo.GOODS_TYPE_PROPERTY;
		}
		if(gs_id_list.length>0){
			String gsId=null;
			BooleanQuery query= new BooleanQuery();
			for (int i=0;i< gs_id_list.length;i++) {
				String[] gs_info_list = gs_id_list[i].split(",");
				Term t =new Term(COLUMN+"_"+gs_info_list[0],gs_info_list[2]+"_"+gs_info_list[3].trim());
				TermQuery querySepc=new TermQuery(t);
				if(gsId!=null && gsId.equals(gs_info_list[0])){
			        query.add(querySepc, Occur.SHOULD);
					if(i==gs_id_list.length-1){
						queryWhere.add(query,Occur.MUST);
					}
				}else {
					if(i==0){
				        query.add(querySepc, Occur.SHOULD);
						if(i==gs_id_list.length-1){
							queryWhere.add(query,Occur.MUST);
						}
					}else{
						queryWhere.add(query,Occur.MUST);
						query= new BooleanQuery();
						if(i!=gs_id_list.length-1){
					        query.add(querySepc, Occur.SHOULD);
						}else{
							query.add(querySepc, Occur.SHOULD);
							queryWhere.add(query,Occur.MUST);
						}
					}
				}	
			
				gsId=gs_info_list[0];
				
				Map map = new HashMap();
				map.put("name",gs_info_list[1]);
				map.put("gs_info",gs_id_list[i]);
				map.put("value", gs_info_list[3]);
				map.put("type", type);
				map.put("id", gs_info_list[2]);
				goods_property.add(map);
			}
		}
		
		return queryWhere;
	}

	private Set<Long> getArrayAreaChildIds(List<Area> areas) {
		Set<Long> ids = new HashSet<Long>();
		for (Area area : areas) {
			ids.add(area.getId());
			for (Area are : area.getChilds()) {
				Set<Long> cids = getAreaChildIds(are);
				for (Long cid : cids) {
					ids.add(cid);
				}
			}
		}
		return ids;
	}

	/**
	 * 直通车商品列表
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping("/ztc_goods_list.htm")
	public ModelAndView ztc_goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_view) {
		ModelAndView mv = new JModelAndView("ztc_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		gqo.addQuery("obj.ztc_status", new SysMap("ztc_status", 3), "=");
		gqo.addQuery("obj.ztc_begin_time", new SysMap("ztc_begin_time",
				new Date()), "<=");
		gqo.addQuery("obj.ztc_gold", new SysMap("ztc_gold", 0), ">");
		gqo.setOrderBy("ztc_dredge_price");
		gqo.setOrderType("desc");
		gqo.setPageSize(20);
		IPageList pList = this.goodsService.list(gqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("goods_view", goods_view);
		mv.addObject("user_viewed_goods", request.getSession(false)
				.getAttribute("user_viewed_goods"));
		return mv;
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

	private List<List<GoodsSpecProperty>> generic_gsp(String gs_ids) {
		List<List<GoodsSpecProperty>> list = new ArrayList<List<GoodsSpecProperty>>();
		String[] gs_id_list = gs_ids.substring(1).split("\\|");
		for (String gd_id_info : gs_id_list) {
			String[] gs_info_list = gd_id_info.split(",");
			GoodsSpecProperty gsp = this.goodsSpecPropertyService
					.getObjById(CommUtil.null2Long(gs_info_list[0]));
			boolean create = true;
			for (List<GoodsSpecProperty> gsp_list : list) {
				for (GoodsSpecProperty gsp_temp : gsp_list) {
					if (gsp_temp.getSpec().getId()
							.equals(gsp.getSpec().getId())) {
						gsp_list.add(gsp);
						create = false;
						break;
					}
				}
			}
			if (create) {
				List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();
				gsps.add(gsp);
				list.add(gsps);
			}
		}
		return list;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param goods_id
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/goods_evaluation.htm")
	public ModelAndView goods_evaluation(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_id,
			String currentPage) {
		String template = "default";
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		if (store != null) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(
				template + "/goods_evaluation.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.evaluate_goods.id",
				new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"),
				"=");
		qo.addQuery("obj.evaluate_status", new SysMap("evaluate_status", 0),
				"=");
		qo.setPageSize(8);
		IPageList pList = this.evaluateService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/goods_evaluation.htm", "", "", pList, mv);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("store", store);
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		mv.addObject("goods", goods);
		return mv;
	}

	@RequestMapping("/goods_detail.htm")
	public ModelAndView goods_detail(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_id) {
		String template = "default";
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		if (store != null) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(template + "/goods_detail.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		mv.addObject("obj", goods);
		this.generic_evaluate(goods.getGoods_store(), mv);
		this.userTools.query_user();
		return mv;
	}

	@RequestMapping("/goods_order.htm")
	public ModelAndView goods_order(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_id,
			String currentPage) {
		String template = "default";
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		if (store != null) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(template + "/goods_order.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsCartQueryObject qo = new GoodsCartQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.goods.id",
				new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		qo.addQuery("obj.of.order_status", new SysMap("order_status", 20), ">=");
		qo.setPageSize(8);
		IPageList pList = this.goodsCartService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/goods_order.htm", "", "", pList, mv);
		mv.addObject("storeViewTools", storeViewTools);
		return mv;
	}
	@SuppressWarnings("unchecked")
	@RequestMapping("/goods_parameters.htm")
	public ModelAndView goods_parameters(HttpServletRequest request,
			HttpServletResponse response,String goods_id,
			String currentPage) {
		String template = "default";
		if(goods_id!=null&&!goods_id.equals("")){
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
			ModelAndView mv = new JModelAndView(template + "/goods_parameters.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			try {
				JSONArray array = (JSONArray) JSONValue.parse(goods
						.getGoods_property());
				List<List<HashMap<String, String>>> list=new ArrayList<List<HashMap<String,String>>>();
				for (int i=0;i<array.size();) {
					int col=3;//列数
					List<HashMap<String, String>> listSon=new ArrayList<HashMap<String,String>>();
					for(;col>0;col--){
						if(i>=array.size())
							break;
						listSon.add((HashMap<String, String>) array.get(i));
						i++;
					}
					list.add(listSon);
				}
				mv.addObject("propertys", list);
				return mv;
			} catch (Exception e) {
			}
		}
		return null;
	}
	@RequestMapping("/goods_consult.htm")
	public ModelAndView goods_consult(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_id,
			String currentPage) {
		String template = "default";
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		if (store != null) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(template + "/goods_consult.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.goods.id",
				new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		IPageList pList = this.consultService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/goods_consult.htm", "", "", pList, mv);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("goods_id", goods_id);
		return mv;
	}

	@RequestMapping("/goods_consult_save.htm")
	public ModelAndView goods_consult_save(HttpServletRequest request,
			HttpServletResponse response, String goods_id,
			String consult_content, String consult_email, String Anonymous,
			String consult_code) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String verify_code = CommUtil.null2String(request.getSession(false)
				.getAttribute("consult_code"));
		boolean visit_consult = true;
		if (!this.configService.getSysConfig().isVisitorConsult()) {
			if (SecurityUserHolder.getCurrentUser() == null) {
				visit_consult = false;
			}
			if (CommUtil.null2Boolean(Anonymous)) {
				visit_consult = false;
			}
		}
		if (visit_consult) {
			if (CommUtil.null2String(consult_code).equals(verify_code)) {
				Consult obj = new Consult();
				obj.setAddTime(new Date());
				obj.setConsult_content(consult_content);
				obj.setConsult_email(consult_email);
				if (!CommUtil.null2Boolean(Anonymous)) {
					obj.setConsult_user(SecurityUserHolder.getCurrentUser());
					mv.addObject("op_title", "咨询发布成功");
				}
				obj.setGoods(this.goodsService.getObjById(CommUtil
						.null2Long(goods_id)));
				this.consultService.save(obj);
				request.getSession(false).removeAttribute("consult_code");
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "验证码错误，咨询发布失败");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "不允许游客咨询");
		}
		mv.addObject("url", CommUtil.getURL(request) + "/goods_" + goods_id
				+ ".htm");
		return mv;
	}

	@RequestMapping("/load_goods_gsp.htm")
	public void load_goods_gsp(HttpServletRequest request,
			HttpServletResponse response, String gsp, String id) {
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		Map map = new HashMap();
		int count = 0;
		double price = 0;
		if (goods.getGroup() != null && goods.getGroup_buy() == 2) {// 团购商品统一按照团购价格处理
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
					count = gg.getGg_group_count() - gg.getGg_def_count();
					price = CommUtil.null2Double(gg.getGg_price());
				}
			}
		} else {
			count = goods.getGoods_inventory();
			//如果没有规格 取促销价 没有促销价再取商店价格
			price = CommUtil.null2Double(goods.getGoods_current_price()==null?goods.getStore_price():goods.getGoods_current_price());
			if (goods.getInventory_type().equals("spec")) {
				List<HashMap> list = Json.fromJson(ArrayList.class,
						goods.getGoods_inventory_detail());
				String[] gsp_ids = gsp.split(",");
				for (Map temp : list) {
					String[] temp_ids = CommUtil.null2String(temp.get("id"))
							.split("_");
					Arrays.sort(gsp_ids);
					Arrays.sort(temp_ids);
					if (Arrays.equals(gsp_ids, temp_ids)) {
						count = CommUtil.null2Int(temp.get("count"));
						price = CommUtil.null2Double(temp.get("price"));
					}
				}
			}
		}
		map.put("count", count);
		map.put("price", price);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/trans_fee.htm")
	public void trans_fee(HttpServletRequest request,
			HttpServletResponse response, String city_name, String goods_id) {
		Map map = new HashMap();
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		float mail_fee = 0;
		float express_fee = 0;
		float ems_fee = 0;
		if (goods.getTransport() != null) {
			mail_fee = this.transportTools.cal_goods_trans_fee(
					CommUtil.null2String(goods.getTransport().getId()), "mail",
					CommUtil.null2String(goods.getGoods_weight()),
					CommUtil.null2String(goods.getGoods_volume()), city_name);
			express_fee = this.transportTools.cal_goods_trans_fee(
					CommUtil.null2String(goods.getTransport().getId()),
					"express", CommUtil.null2String(goods.getGoods_weight()),
					CommUtil.null2String(goods.getGoods_volume()), city_name);
			ems_fee = this.transportTools.cal_goods_trans_fee(
					CommUtil.null2String(goods.getTransport().getId()), "ems",
					CommUtil.null2String(goods.getGoods_weight()),
					CommUtil.null2String(goods.getGoods_volume()), city_name);
		}
		map.put("mail_fee", mail_fee);
		map.put("express_fee", express_fee);
		map.put("ems_fee", ems_fee);
		map.put("current_city_info", CommUtil.substring(city_name, 5));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/goods_share.htm")
	public ModelAndView goods_share(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new JModelAndView("goods_share.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		mv.addObject("obj", goods);
		return mv;
	}

	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = new HashSet<Long>();
		if (gc != null) {
			ids.add(gc.getId());
			for (GoodsClass child : gc.getChilds()) {
				Set<Long> cids = genericIds(child);
				for (Long cid : cids) {
					ids.add(cid);
				}
				ids.add(child.getId());
			}
		}
		return ids;
	}

	private void generic_evaluate(Store store, ModelAndView mv) {
		double description_result = 0;
		double service_result = 0;
		double ship_result = 0;
		if (store.getSc() != null) {
			StoreClass sc = this.storeClassService.getObjById(store.getSc()
					.getId());
			float description_evaluate = CommUtil.null2Float(sc
					.getDescription_evaluate());
			float service_evaluate = CommUtil.null2Float(sc
					.getService_evaluate());
			float ship_evaluate = CommUtil.null2Float(sc.getShip_evaluate());
			if (store.getPoint() != null) {
				float store_description_evaluate = CommUtil.null2Float(store
						.getPoint().getDescription_evaluate());
				float store_service_evaluate = CommUtil.null2Float(store
						.getPoint().getService_evaluate());
				float store_ship_evaluate = CommUtil.null2Float(store
						.getPoint().getShip_evaluate());
				// 计算和同行比较结果
				description_result = CommUtil.div(store_description_evaluate
						- description_evaluate, description_evaluate);
				service_result = CommUtil.div(store_service_evaluate
						- service_evaluate, service_evaluate);
				ship_result = CommUtil.div(store_ship_evaluate - ship_evaluate,
						ship_evaluate);
			}
		}
		if (description_result > 0) {
			mv.addObject("description_css", "better");
			mv.addObject("description_type", "高于");
			mv.addObject("description_result",
					CommUtil.null2String(CommUtil.mul(description_result, 100))
							+ "%");
		}
		if (description_result == 0) {
			mv.addObject("description_css", "better");
			mv.addObject("description_type", "持平");
			mv.addObject("description_result", "-----");
		}
		if (description_result < 0) {
			mv.addObject("description_css", "lower");
			mv.addObject("description_type", "低于");
			mv.addObject(
					"description_result",
					CommUtil.null2String(CommUtil.mul(-description_result, 100))
							+ "%");
		}
		if (service_result > 0) {
			mv.addObject("service_css", "better");
			mv.addObject("service_type", "高于");
			mv.addObject("service_result",
					CommUtil.null2String(CommUtil.mul(service_result, 100))
							+ "%");
		}
		if (service_result == 0) {
			mv.addObject("service_css", "better");
			mv.addObject("service_type", "持平");
			mv.addObject("service_result", "-----");
		}
		if (service_result < 0) {
			mv.addObject("service_css", "lower");
			mv.addObject("service_type", "低于");
			mv.addObject("service_result",
					CommUtil.null2String(CommUtil.mul(-service_result, 100))
							+ "%");
		}
		if (ship_result > 0) {
			mv.addObject("ship_css", "better");
			mv.addObject("ship_type", "高于");
			mv.addObject("ship_result",
					CommUtil.null2String(CommUtil.mul(ship_result, 100)) + "%");
		}
		if (ship_result == 0) {
			mv.addObject("ship_css", "better");
			mv.addObject("ship_type", "持平");
			mv.addObject("ship_result", "-----");
		}
		if (ship_result < 0) {
			mv.addObject("ship_css", "lower");
			mv.addObject("ship_type", "低于");
			mv.addObject("ship_result",
					CommUtil.null2String(CommUtil.mul(-ship_result, 100)) + "%");
		}
	}
}
