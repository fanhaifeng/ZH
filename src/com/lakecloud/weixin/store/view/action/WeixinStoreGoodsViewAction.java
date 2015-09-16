package com.lakecloud.weixin.store.view.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsDC;
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.GoodsSpecification;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.UserGoodsClass;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGoodsServiceDC;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserGoodsClassService;
import com.lakecloud.manage.admin.tools.StoreTools;
import com.lakecloud.view.web.tools.GoodsViewTools;
import com.lakecloud.view.web.tools.StoreViewTools;

/**
 * @info 微信店铺客户端商品管理控制器，用来查看商品列表页、详细页
 * @since V1.3
 * @version 1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang hz
 * 
 */
@Controller
@SuppressWarnings("unchecked")
public class WeixinStoreGoodsViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private IGoodsServiceDC goodsServiceDC;
	@Autowired
	private IOrderFormService orderFormService;

	@RequestMapping("/weixin/goods.htm")
	public ModelAndView weixin_goods(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new JModelAndView("weixin/goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		Store store = obj.getGoods_store();
		request.getSession(false).setAttribute("iskyshop_view_type", "weixin");
		request.getSession(false).setAttribute("store_id", store.getId());
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("store_id", store.getId());
		params.put("goods_status", 0);
		params.put("goods_recommend", true);
		params.put("goods_id", CommUtil.null2Long(goods_id));
		List<Goods> recommend_goods_list = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend and obj.id!=:goods_id order by obj.addTime desc",
						params, 0, 4);
		mv.addObject("obj", obj);
		mv.addObject("store", obj.getGoods_store());
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("recommend_goods_list", recommend_goods_list);
		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;
	}

	@RequestMapping("/weixin/goods_recommend_list.htm")
	public ModelAndView weixin_goods_recommend_list(HttpServletRequest request,
			HttpServletResponse response, String queryType, String store_id) {
		ModelAndView mv = new JModelAndView("weixin/goods_recommend_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = null;
		if (store_id != null && !store_id.equals("")) {
			request.getSession(false).setAttribute("store_id", store_id);
			store = this.storeService.getObjById(CommUtil.null2Long(store_id));
		} else {
			store = this.storeService.getObjById(CommUtil.null2Long(request
					.getSession(false).getAttribute("store_id")));
		}
		if (store != null) {
			if (store.getWeixin_status() == 1) {
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("store_id", store.getId());
				params.put("goods_status", 0);
				params.put("goods_recommend", true);
				List<Goods> recommend_goods_list = null;
				if (queryType != null && !queryType.equals("")) {
					mv.addObject("queryType", queryType);
					if (queryType.equals("goods_collect")) {
						recommend_goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.goods_collect desc",
										params, 0, 6);
					}
					if (queryType.equals("goods_salenum")) {
						recommend_goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.goods_salenum desc",
										params, 0, 6);
					}
					if (queryType.equals("store_price")) {
						recommend_goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.store_price asc",
										params, 0, 6);
					}
					if (queryType.equals("store_credit")) {
						recommend_goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.goods_store.store_credit desc",
										params, 0, 6);
					}
				} else {
					recommend_goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.addTime desc",
									params, 0, 6);
				}
				mv.addObject("objs", recommend_goods_list);
			} else {
				mv = new JModelAndView("weixin/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "未开通微信商城");
			}
		}
		return mv;
	}

	@RequestMapping("/weixin/goods_recommend_list_ajax.htm")
	public ModelAndView weixin_goods_recommend_list_ajax(
			HttpServletRequest request, HttpServletResponse response,
			String begin_count, String queryType) {
		ModelAndView mv = new JModelAndView("weixin/goods_list_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Long store_id = CommUtil.null2Long(request.getSession(false)
				.getAttribute("store_id"));
		Map params = new HashMap();
		params.put("store_id", store_id);
		params.put("goods_status", 0);
		params.put("goods_recommend", true);
		List<Goods> recommend_goods_list = null;
		if (queryType != null && !queryType.equals("")) {
			mv.addObject("queryType", queryType);
			if (queryType.equals("goods_collect")) {
				recommend_goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.goods_collect desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("goods_salenum")) {
				recommend_goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.goods_salenum desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("store_price")) {
				recommend_goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.store_price asc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("store_credit")) {
				recommend_goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.goods_store.store_credit desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
		} else {
			recommend_goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.addTime desc",
							params, CommUtil.null2Int(begin_count), 6);
		}
		mv.addObject("objs", recommend_goods_list);
		return mv;
	}

	@RequestMapping("/weixin/goods_list.htm")
	public ModelAndView weixin_goods_list(HttpServletRequest request,
			HttpServletResponse response, String queryType, String store_id) {
		ModelAndView mv = new JModelAndView("weixin/seller_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = null;
		if (store_id != null && !store_id.equals("")) {
			request.getSession(false).setAttribute("store_id", store_id);
			store = this.storeService.getObjById(CommUtil.null2Long(store_id));
		} else {
			store = this.storeService.getObjById(CommUtil.null2Long(request
					.getSession(false).getAttribute("store_id")));
		}
		if (store != null) {
			if (store.getWeixin_status() == 1) {
				if (queryType != null && !queryType.equals("")) {
					mv.addObject("queryType", queryType);
				}
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("store_id", store.getId());
				params.put("goods_status", 0);
				List<Goods> goods_list = null;
				if (queryType != null && !queryType.equals("")) {
					if (queryType.equals("goods_seller_time")) {
						goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_seller_time desc",
										params, 0, 6);
					}
					if (queryType.equals("goods_collect")) {
						goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_collect desc",
										params, 0, 6);
					}
					if (queryType.equals("goods_salenum")) {
						goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
										params, 0, 6);
					}
					if (queryType.equals("store_price")) {
						goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.store_price asc",
										params, 0, 6);
					}
					if (queryType.equals("store_credit")) {
						goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_store.store_credit desc",
										params, 0, 6);
					}
					if (queryType.equals("addTime")) {
						goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.addTime desc",
										params, 0, 6);
					}
				}
				mv.addObject("objs", goods_list);
			} else {
				mv = new JModelAndView("weixin/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "未开通微信商城");
			}
		}
		return mv;
	}

	/**
	 * @param request
	 * @param response
	 * @param id
	 * @return 卖家中心 查看商品详情
	 */
	@RequestMapping("/weixin/seller_goods_detail.htm")
	public ModelAndView goods_detail(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		ModelAndView mv = new JModelAndView("weixin/seller_goods_detail.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods obj = this.goodsService.getObjById(Long.parseLong(goods_id));
		mv.addObject("obj", obj);
		// 商品分类
		mv.addObject("goods_class_info",
				this.storeTools.generic_goods_class_info(obj.getGc()));
		// 商品规格
		GoodsDC goodsDC = this.goodsServiceDC.getObjById(obj.getGoodsDc_id());
		 List<Map<String, List<String>>> htmlSpec = getGoodsdcSpec(obj);
		 mv.addObject("specHTML", htmlSpec);
		// 商品类型属性
		 try{
		JSONArray array = (JSONArray) JSONValue.parse(goodsDC
				.getGoods_property());
		StringBuffer html = new StringBuffer();
		for (Object object : array) {
			HashMap<String, String> map = (HashMap<String, String>) object;
			html.append("<tr class='loadRemove'><td align='right' valign='top'>"
					+ map.get("name")
					+ "：</td><td class='sptable'><span class='tabtxt1 size2'><input value='"
					+ map.get("val")
					+ "' readonly='readonly' /> </span></td></tr>");
		}
		mv.addObject("goods_property", array);}
		 catch (Exception e) {
		}
		return mv;
	}
	// 生成商品规格
		private List<Map<String,List<String>>> getGoodsdcSpec(Goods goods) {
			List<Map<String,List<String>>> listret=new ArrayList<Map<String,List<String>>>();
			Map<Long,Map<String,List<String>>> map=new HashMap<Long, Map<String,List<String>>>();
			if (goods != null) {
				List<GoodsSpecProperty> specs = goods.getGoods_specs();
				for (GoodsSpecProperty goodsSpecProperty : specs) {
					GoodsSpecification gsf = goodsSpecProperty.getSpec();
					if(map.containsKey(gsf.getId())){
						map.get(gsf.getId()).get(gsf.getName()).add(goodsSpecProperty.getValue());
					}else{
						List<String> list=new ArrayList<String>();
						list.add(goodsSpecProperty.getValue());
						Map<String,List<String>> map2=new HashMap<String, List<String>>();
						map2.put(gsf.getName(), list);
						map.put(gsf.getId(), map2);
					}
				}
			}
			for (Long value : map.keySet()) {
				listret.add(map.get(value));
			}
						return listret;
		}
	/**
	 * @param request
	 * @param response
	 * @param goods_id
	 * @return 更新商品
	 */
	@RequestMapping("/weixin/seller_goods_update.htm")
	public ModelAndView goods_update(HttpServletRequest request,
			HttpServletResponse response) {
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		Goods goods = this.goodsService.getObjById(Integer.valueOf(
				request.getParameter("id")).longValue());
		WebForm wf = new WebForm();
		goods = (Goods) wf.toPo(request, goods);
		//维护店铺促销价，商品原价和商品当前价格
		goods.setGoods_price(goods.getStore_price());
		goods.setGoods_current_price(goods.getGoods_promotion()==null?goods.getStore_price():goods.getGoods_promotion());
		this.goodsService.update(goods);
		ModelAndView mv = new JModelAndView("weixin/seller_goods_detail.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// Goods obj = this.goodsService.getObjById(Long.parseLong(goods_id));
		// mv.addObject("obj",obj);
		// //商品分类
		// mv.addObject("goods_class_info",
		// this.storeTools.generic_goods_class_info(obj.getGc()));
		// //商品规格
		// GoodsDC goodsDC =
		// this.goodsServiceDC.getObjById(obj.getGoodsDc_id());
		// String htmlSpec = getGoodsdcSpec(goodsDC, obj, url);
		// mv.addObject("specHTML", htmlSpec);
		// //商品类型属性
		// JSONArray array = (JSONArray)
		// JSONValue.parse(goodsDC.getGoods_property());
		// StringBuffer html = new StringBuffer();
		// for (Object object : array) {
		// HashMap<String, String> map = (HashMap<String, String>) object;
		// html.append("<tr class='loadRemove'><td align='right' valign='top'>"
		// + map.get("name")
		// +
		// "：</td><td class='sptable'><span class='tabtxt1 size2'><input value='"
		// + map.get("val")
		// + "' readonly='readonly' /> </span></td></tr>");
		// }
		// mv.addObject("goods_property", html.toString());
		return weixin_goods_list(request, response, "goods_seller_time",
				SecurityUserHolder.getCurrentUser().getStore().getId()
						.toString());
	}
	/**
	 * 订单列表 
	 * @param request
	 * @param response
	 * @param order_status
	 *            ：订单状态，根据订单状态不同查询相应的订单
	 * @return
	 */
	@RequestMapping("/weixin/seller_order_list.htm")
	public ModelAndView order_list(HttpServletRequest request,
			HttpServletResponse response,String order_status) {
		ModelAndView mv = new JModelAndView("weixin/seller_order.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<OrderForm> orders = null;
		Map map = new HashMap();
		map.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
		if (!CommUtil.null2String(order_status).equals("")) {
			map.put("order_status", CommUtil.null2Int(order_status));
			orders = this.orderFormService
					.query("select obj from OrderForm obj where obj.order_status=:order_status and obj.store.id=:store_id order by addTime desc",
							map, 0, 6);
			mv.addObject("order_status", order_status);
		} else {
			orders = this.orderFormService
					.query("select obj from OrderForm obj where obj.store.id=:store_id order by addTime desc",
							map, 0, 6);
		}
		mv.addObject("objs", orders);
		return mv;
	}
	/**
	 * 卖家中心订单列表ajax加载
	 * 
	 * @param request
	 * @param response
	 * @param order_status
	 *            ：订单状态
	 * @param beginCount
	 *            ：订单查询开始值，每次查询6条数据
	 * @return
	 */
	@RequestMapping("/weixin/seller_order_ajax.htm")
	public ModelAndView weixin_seller_order_data(HttpServletRequest request,
			HttpServletResponse response, String order_status, String beginCount) {
		ModelAndView mv = new JModelAndView("weixin/seller_order_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<OrderForm> orders = null;
		Map map = new HashMap();
		map.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
		if (!CommUtil.null2String(order_status).equals("")) {
			map.put("order_status", CommUtil.null2Int(order_status));
			orders = this.orderFormService
					.query("select obj from OrderForm obj where obj.order_status=:order_status and obj.store.id=:store_id order by addTime desc",
							map, CommUtil.null2Int(beginCount), 6);
			mv.addObject("order_status", order_status);
		} else {
			orders = this.orderFormService
					.query("select obj from OrderForm obj where obj.store.id=:store_id order by addTime desc",
							map, CommUtil.null2Int(beginCount), 6);
		}
		mv.addObject("objs",orders);
		mv.addObject("beginCount", beginCount);
		return mv;
	}
	
	/**
	 * 订单详情
	 * 
	 * @param request
	 * @param response
	 * @param id
	 *            :订单id
	 * @return
	 */
	@RequestMapping("/weixin/seller_order_view.htm")
	public ModelAndView weixin_order_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("weixin/seller_order_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getStore()!= null
				&& obj.getStore().getUser().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
			try {
				mv.addObject("payment", obj.getTotalPrice().subtract(obj.getCharge_Num()));
			} catch (Exception e) {
				mv.addObject("payment", 0);
			}
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}
	@RequestMapping("/weixin/goods_list_ajax.htm")
	public ModelAndView weixin_goods_list_ajax(HttpServletRequest request,
			HttpServletResponse response, String begin_count, String queryType) {
		ModelAndView mv = new JModelAndView("weixin/goods_list_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Long store_id = CommUtil.null2Long(request.getSession(false)
				.getAttribute("store_id"));
		Map params = new HashMap();
		params.put("store_id", store_id);
		params.put("goods_status", 0);
		List<Goods> goods_list = null;
		if (queryType != null && !queryType.equals("")) {
			mv.addObject("queryType", queryType);
			if (queryType.equals("goods_collect")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_collect desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("goods_salenum")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("store_price")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.store_price asc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("store_credit")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_store.store_credit desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("addTime")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.addTime desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
		}
		mv.addObject("objs", goods_list);
		return mv;
	}

	// 店铺商品分类一级
	@RequestMapping("/weixin/classes_first_list.htm")
	public ModelAndView weixin_classes_first_list(HttpServletRequest request,
			HttpServletResponse response, String store_id) {
		ModelAndView mv = new JModelAndView("weixin/classes_first_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = null;
		if (store_id != null && !store_id.equals("")) {
			request.getSession(false).setAttribute("store_id", store_id);
			store = this.storeService.getObjById(CommUtil.null2Long(store_id));
		} else {
			store = this.storeService.getObjById(CommUtil.null2Long(request
					.getSession(false).getAttribute("store_id")));
		}
		if (store != null) {
			if (store.getWeixin_status() == 1) {
				Map map = new HashMap();
				map.put("uid", store.getUser().getId());
				List<UserGoodsClass> usergoodsClasses = this.userGoodsClassService
						.query("select obj from UserGoodsClass obj where obj.user.id=:uid and obj.parent is null",
								map, -1, -1);
				mv.addObject("usergoodsClasses", usergoodsClasses);
			} else {
				mv = new JModelAndView("weixin/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "未开通微信商城");
			}
		}
		return mv;
	}

	// 店铺商品分类二级
	@RequestMapping("/weixin/classes_second_list.htm")
	public ModelAndView weixin_classes_second_list(HttpServletRequest request,
			HttpServletResponse response, String class_id) {
		ModelAndView mv = new JModelAndView("weixin/classes_second_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		UserGoodsClass parent = this.userGoodsClassService.getObjById(CommUtil
				.null2Long(class_id));
		if (parent.getUser().getStore().getWeixin_status() == 1) {
			request.getSession(false).setAttribute("store_id",
					parent.getUser().getStore().getId());
			Map map = new HashMap();
			map.put("uid", parent.getUser().getId());
			map.put("parent_id", parent.getId());
			List<UserGoodsClass> usergoodsClasses = this.userGoodsClassService
					.query("select obj from UserGoodsClass obj where obj.user.id=:uid and obj.parent.id=:parent_id",
							map, -1, -1);
			mv.addObject("usergoodsClasses", usergoodsClasses);
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "未开通微信商城");
		}
		return mv;
	}

	// 分类商品列表
	@RequestMapping("/weixin/classes_goods_list.htm")
	public ModelAndView weixin_classes_goods_list(HttpServletRequest request,
			HttpServletResponse response, String queryType, String ugc_id) {
		ModelAndView mv = new JModelAndView("weixin/classes_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		UserGoodsClass parent = this.userGoodsClassService.getObjById(CommUtil
				.null2Long(ugc_id));
		if (parent.getUser().getStore().getWeixin_status() == 1) {
			request.getSession(false).setAttribute("store_id",
					parent.getUser().getStore().getId());
			Map params = new HashMap();
			params.put("store_id", parent.getUser().getStore().getId());
			params.put("goods_status", 0);
			params.put("ugc", parent);
			List<Goods> goods_list = null;
			if (queryType != null && !queryType.equals("")) {
				mv.addObject("queryType", queryType);
				if (queryType.equals("goods_collect")) {
					goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.goods_collect desc",
									params, 0, 6);
				}
				if (queryType.equals("goods_salenum")) {
					goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.goods_salenum desc",
									params, 0, 6);
				}
				if (queryType.equals("store_price")) {
					goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.store_price asc",
									params, 0, 6);
				}
				if (queryType.equals("store_credit")) {
					goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.goods_store.store_credit desc",
									params, 0, 6);
				}
			} else {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.addTime desc",
								params, 0, 6);
			}
			mv.addObject("ugc_id", ugc_id);
			mv.addObject("objs", goods_list);
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "未开通微信商城");
		}
		return mv;
	}

	@RequestMapping("/weixin/classes_goods_ajax.htm")
	public ModelAndView weixin_classes_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String begin_count, String queryType,
			String ugc_id) {
		ModelAndView mv = new JModelAndView("weixin/classes_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Long store_id = CommUtil.null2Long(request.getSession(false)
				.getAttribute("store_id"));
		UserGoodsClass ugc = this.userGoodsClassService.getObjById(CommUtil
				.null2Long(ugc_id));
		Map params = new HashMap();
		params.put("store_id", store_id);
		params.put("goods_status", 0);
		params.put("ugc", ugc);
		List<Goods> goods_list = null;
		if (queryType != null && !queryType.equals("")) {
			mv.addObject("queryType", queryType);
			if (queryType.equals("goods_collect")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.goods_collect desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("goods_salenum")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.goods_salenum desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("store_price")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.store_price asc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("store_credit")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.goods_store.store_credit desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
		} else {
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.addTime desc",
							params, CommUtil.null2Int(begin_count), 6);
		}
		mv.addObject("objs", goods_list);
		return mv;
	}

	@RequestMapping("/weixin/search_goods_list.htm")
	public ModelAndView weixin_search_goods_list(HttpServletRequest request,
			HttpServletResponse response, String queryType, String keyword,String storeId,String sort) {
		ModelAndView mv;
		Long store_id;
		if(storeId!=null&&!storeId.equals("")){
		 mv = new JModelAndView("weixin/search_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		 store_id=CommUtil.null2Long(storeId);
		}else{
		 mv = new JModelAndView("weixin/seller_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		 store_id=SecurityUserHolder.getCurrentUser().getStore().getId();
		}
		queryType=(queryType != null && !queryType.equals(""))?queryType:"goods_seller_time";
		sort=(sort != null && !sort.equals(""))?sort:" desc";
		mv.addObject("queryType", queryType);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("goods_status", 0);
		keyword=(keyword==null)?"":keyword;
		params.put("goods_name", "%" + keyword.trim() + "%");
		List<Goods> goods_list = null;
		String hql="select obj from Goods obj where obj.goods_status=:goods_status ";
		params.put("store_id",store_id);
		hql+=" and obj.goods_store.id=:store_id ";
		hql+=" and obj.goods_name like:goods_name order by obj."+queryType+" "+sort;
	    goods_list = this.goodsService.query(hql,params, 0, 6);
		mv.addObject("keyword", keyword);
		mv.addObject("storeId", storeId);
		mv.addObject("sort", sort);
		mv.addObject("objs", goods_list);
		return mv;
	}

	@RequestMapping("/weixin/search_goods_ajax.htm")
	public ModelAndView weixin_search_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String begin_count, String queryType,
			String keyword,String sort,String storeId) {
		ModelAndView mv=null;
		Long store_id;
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		if(storeId!=null&&!storeId.equals("")){
		 mv = new JModelAndView("weixin/search_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		 store_id=CommUtil.null2Long(storeId);
		}else{
		 mv = new JModelAndView("weixin/seller_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		 try {
			 store_id=SecurityUserHolder.getCurrentUser().getStore().getId();
		     } catch (Exception e) {			
			return new ModelAndView("redirect:platform/index.htm");
		     }
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("store_id", store_id);
		params.put("goods_status", 0);
		params.put("goods_name", "%" + keyword.trim() + "%");
		queryType=(queryType != null && !queryType.equals(""))?queryType:"goods_seller_time";
		sort=(sort != null && !sort.equals(""))?sort:" desc";
		List<Goods> goods_list = null;
		String hql="select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_name like:goods_name  order by obj."+queryType+" "+sort;
		goods_list = this.goodsService.query(hql,params, CommUtil.null2Int(begin_count), 6);
		//判断是否有数据
		if(goods_list!=null&&goods_list.size()>0){
			mv.addObject("queryType", queryType);
		    mv.addObject("sort", sort);
		    mv.addObject("objs", goods_list);
		    return mv;
		}
		return null;
	}

	// 生成商品规格
	private String getGoodsdcSpec(GoodsDC gdc, Goods goods, String url) {
		Map<Long, Object> goodsSpec = new HashMap<Long, Object>();
		if (goods != null) {
			List<GoodsSpecProperty> spec = goods.getGoods_specs();
			for (GoodsSpecProperty goodsSpecProperty : spec) {
				goodsSpec.put(goodsSpecProperty.getId(), goodsSpecProperty);
			}
		}
		StringBuffer htmlSpec = new StringBuffer();
		List<GoodsSpecProperty> specs = gdc.getGoodsDC_specs();
		Map<Long, List<GoodsSpecProperty>> map = new HashMap<Long, List<GoodsSpecProperty>>();
		for (GoodsSpecProperty goodsSpecProperty : specs) {
			Long specId = goodsSpecProperty.getSpec().getId();
			if (!map.containsKey(specId)) {
				List<GoodsSpecProperty> list = new ArrayList<GoodsSpecProperty>();
				list.add(goodsSpecProperty);
				map.put(specId, list);
			} else {
				map.get(specId).add(goodsSpecProperty);
			}
		}
		for (Long long1 : map.keySet()) {
			List<GoodsSpecProperty> list = map.get(long1);
			htmlSpec.append("<tr class='loadRemove' id='gs_" + long1
					+ "' gs_name='" + list.get(0).getSpec().getName()
					+ "'><td align='right' valign='top'>"
					+ list.get(0).getSpec().getName()
					+ ":</td><td class='sptable' ><ul class='color_chose'>");
			for (GoodsSpecProperty goodsSpecProperty : list) {
				String ck = goodsSpec.containsKey(goodsSpecProperty.getId()) ? "checked='checked'"
						: "";
				htmlSpec.append("<li><span class='cc_sp1'><input disabled='disabled' name='spec_"
						+ goodsSpecProperty.getId()
						+ "' type='checkbox' id='spec_"
						+ goodsSpecProperty.getId()
						+ "' gs_id='"
						+ long1
						+ "' gsp_val='"
						+ goodsSpecProperty.getValue()
						+ "' value='"
						+ goodsSpecProperty.getId()
						+ "'"
						+ ck
						+ "/></span> <label for='spec_"
						+ goodsSpecProperty.getId() + "'>");
				if (goodsSpecProperty.getSpec().getType().equals("img")) {
					htmlSpec.append("<span class='cc_sp2'><img src='" + url
							+ "/" + goodsSpecProperty.getSpecImage().getPath()
							+ "/" + goodsSpecProperty.getSpecImage().getName()
							+ "' width='16' height='16' /> </span>");
				}
				htmlSpec.append("<span class='cc_sp3'>"
						+ goodsSpecProperty.getValue() + "</span></label></li>");
			}
			htmlSpec.append("</ul></td></tr>");
		}
		return htmlSpec.toString();
	}
}
