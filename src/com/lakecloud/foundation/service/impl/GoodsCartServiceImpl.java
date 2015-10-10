package com.lakecloud.foundation.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakecloud.core.dao.IGenericDAO;
import com.lakecloud.core.query.GenericPageList;
import com.lakecloud.core.query.PageObject;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.StoreCart;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IGoodsCartService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGoodsSpecPropertyService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IUserService;

@Service
@Transactional
public class GoodsCartServiceImpl implements IGoodsCartService {
	@Resource(name = "goodsCartDAO")
	private IGenericDAO<GoodsCart> goodsCartDao;
	@Resource(name = "storeCartDAO")
	private IGenericDAO<StoreCart> storeCartDao;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;

	public boolean save(GoodsCart goodsCart) {
		/**
		 * init other field here
		 */
		try {
			this.goodsCartDao.save(goodsCart);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public GoodsCart getObjById(Long id) {
		GoodsCart goodsCart = this.goodsCartDao.get(id);
		if (goodsCart != null) {
			return goodsCart;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.goodsCartDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> goodsCartIds) {
		for (Serializable id : goodsCartIds) {
			delete((Long) id);
		}
		return true;
	}

	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(GoodsCart.class, query,
				params, this.goodsCartDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
						.getCurrentPage(), pageObj.getPageSize() == null ? 0
						: pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}

	public boolean update(GoodsCart goodsCart) {
		try {
			this.goodsCartDao.update(goodsCart);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<GoodsCart> query(String query, Map params, int begin, int max) {
		return this.goodsCartDao.query(query, params, begin, max);
	}

	public List<GoodsCart> queryByStoreCart(StoreCart sc) {
		Map params = new HashMap();
		params.put("sc", sc);
		params.put("deleteStatus", false);
		List<GoodsCart> list = query(
				"select obj from GoodsCart obj where obj.sc=:sc and obj.deleteStatus=:deleteStatus",
				params, -1, -1);
		return list;
	}

	/**
	 * 用于商品当前价修改时，购物车模块中，修改goodsCart的price，storeCart的total_price
	 */
	public List<GoodsCart> queryByGoods(Goods goods) {
		Map params = new HashMap();
		params.put("goods", goods);
		// params.put("of", null);
		params.put("deleteStatus", false);
		List<GoodsCart> list = query(
				"select obj from GoodsCart obj where obj.goods=:goods and obj.of is null and obj.deleteStatus=:deleteStatus",
				params, -1, -1);
		return list;
	}

	@Override
	public String remove_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String store_id) {
		GoodsCart gc = getObjById(CommUtil.null2Long(id));
		StoreCart the_sc = gc.getSc();
		gc.getGsps().clear();
		// the_sc.getGcs().remove(gc);
		delete(CommUtil.null2Long(id));
		if (the_sc.getGcs().size() == 0) {
			delete(the_sc.getId());
		}
		List<StoreCart> cart = this.orderFormService.cart_calc(request);
		double total_price = 0;
		double sc_total_price = 0;
		double count = 0;
		for (StoreCart sc2 : cart) {
			List<GoodsCart> goodsCartList = new ArrayList<GoodsCart>();
			for (GoodsCart gc1 : sc2.getGcs()) {
				GoodsCart temp_gc =getObjById(gc1
						.getId());
				if (temp_gc != null) {
					goodsCartList.add(temp_gc);
					total_price = CommUtil.null2Double(gc1.getPrice())
							* gc1.getCount() + total_price;
					count++;
					if (store_id != null && !store_id.equals("")
							&& sc2.getStore().getId().toString().equals(
									store_id)) {
						sc_total_price = sc_total_price
								+ CommUtil.null2Double(gc1.getPrice())
								* gc1.getCount();
						sc2.setTotal_price(BigDecimal.valueOf(sc_total_price));
					}
				}
			}
			sc2.setGcs(goodsCartList);
			this.storeCartDao.update(sc2);
		}
		request.getSession(false).setAttribute("cart", cart);
		Map map = new HashMap();
		map.put("count", count);
		map.put("total_price", total_price);
		map.put("sc_total_price", sc_total_price);
		String ret = Json.toJson(map, JsonFormat.compact());
		return ret;
	}

	@Override
	public String add_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String count,
			String price, String gsp, String buy_type) {
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		price=goods.getGoods_current_price().toString();
		Map<String,Object> map = new HashMap<String,Object>();
		int inventory = goods.getGoods_inventory();
		if(inventory>=CommUtil.null2Int(count)){		
		String cart_session_id = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(cookie.getValue());
				}
			}
		}

		if (cart_session_id.equals("")) {
			cart_session_id = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("cart_session_id", cart_session_id);
			cookie.setDomain(CommUtil.generic_domain(request));
			response.addCookie(cookie);
		}
		List<StoreCart> cart = new ArrayList<StoreCart>();// 整体店铺购物车
		List<StoreCart> user_cart = new ArrayList<StoreCart>();// 当前用户未提交订单的店铺购物车
		List<StoreCart> cookie_cart = new ArrayList<StoreCart>();// 当前cookie指向的店铺购物车
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
		}
		Map params = new HashMap();
		if (user != null) {
			if (!cart_session_id.equals("")) {
				// 如果用户拥有自己的店铺，删除购物车中自己店铺中的商品信息
				if (user.getStore() != null) {
					params.clear();
					params.put("cart_session_id", cart_session_id);
					params.put("user_id", user.getId());
					params.put("sc_status", 0);
					params.put("store_id", user.getStore().getId());
					List<StoreCart> store_cookie_cart = this.storeCartDao
							.query("select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
									params, -1, -1);
					for (StoreCart sc : store_cookie_cart) {
						for (GoodsCart gc : sc.getGcs()) {
							gc.getGsps().clear();
							delete(gc.getId());
						}
						this.storeCartDao.remove(sc.getId());
					}
				}
				// 查询出cookie中的商品信息
				params.clear();
				params.put("cart_session_id", cart_session_id);
				params.put("sc_status", 0);
				cookie_cart = this.storeCartDao
						.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
								params, -1, -1);
				// 查询用户未提交订单的购物车信息
				params.clear();
				params.put("user_id", user.getId());
				params.put("sc_status", 0);
				user_cart = this.storeCartDao
						.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
								params, -1, -1);
			} else {
				// 查询用户未提交订单的购物车信息
				params.clear();
				params.put("user_id", user.getId());
				params.put("sc_status", 0);
				user_cart = this.storeCartDao
						.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
								params, -1, -1);

			}
		} else {
			if (!cart_session_id.equals("")) {
				params.clear();
				params.put("cart_session_id", cart_session_id);
				params.put("sc_status", 0);
				cookie_cart = this.storeCartDao
						.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
								params, -1, -1);
			}
		}
		// 合并当前用户未提交订单的店铺购物车和当前cookie指向的店铺购物车
		for (StoreCart sc : user_cart) {
			boolean sc_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc.getStore().getId())) {
					sc_add = false;
				}
			}
			if (sc_add) {
				cart.add(sc);
			}
		}
		for (StoreCart sc : cookie_cart) {
			boolean sc_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc.getStore().getId())) {
					sc_add = false;
					for (GoodsCart gc : sc.getGcs()) {
						gc.setSc(sc1);
						update(gc);
					}
					this.storeCartDao.remove(sc.getId());
				}
			}
			if (sc_add) {
				cart.add(sc);
			}
		}
		// 购物车查询合并完毕
		String[] gsp_ids = gsp.split(",");
		Arrays.sort(gsp_ids);
		boolean add = true;
		double total_price = 0;
		int total_count = 0;
		for (StoreCart sc : cart) {
			for (GoodsCart gc : sc.getGcs()) {
				if (gsp_ids != null && gsp_ids.length > 0
						&& gc.getGsps() != null && gc.getGsps().size() > 0) {
					String[] gsp_ids1 = new String[gc.getGsps().size()];
					for (int i = 0; i < gc.getGsps().size(); i++) {
						gsp_ids1[i] = gc.getGsps().get(i) != null ? gc
								.getGsps().get(i).getId().toString() : "";
					}
					Arrays.sort(gsp_ids1);
					if (gc.getGoods().getId().toString().equals(id)
							&& Arrays.equals(gsp_ids, gsp_ids1)) {
						add = false;
					}
				} else {
					if (gc.getGoods().getId().toString().equals(id)) {
						add = false;
					}
				}
			}
		}
		if (add) {// 排除购物车中没有重复商品后添加该商品到购物车
			String type = "save";
			StoreCart sc = new StoreCart();
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId()
						.equals(goods.getGoods_store().getId())) {
					sc = sc1;
					type = "update";
					break;
				}
			}
			sc.setStore(goods.getGoods_store());
			if (type.equals("save")) {
				sc.setAddTime(new Date());
				this.storeCartDao.save(sc);// 保存该StoreCart
			} else {
				this.storeCartDao.update(sc);// 如果是已经存在的则更新storeCart
			}

			GoodsCart obj = new GoodsCart();
			obj.setAddTime(new Date());
			if (CommUtil.null2String(buy_type).equals("")) {
				obj.setCount(CommUtil.null2Int(count));
				obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
			}
			if (CommUtil.null2String(buy_type).equals("combin")) {// 组合销售只添加一件商品
				obj.setCount(1);// 设置组合销售套数
				obj.setCart_type("combin");// 设置组合销售标识
				obj.setPrice(goods.getCombin_price());// 设置为组合销售价格
			}
			obj.setGoods(goods);
			String spec_info = "";
			for (String gsp_id : gsp_ids) {
				GoodsSpecProperty spec_property = this.goodsSpecPropertyService
						.getObjById(CommUtil.null2Long(gsp_id));
				obj.getGsps().add(spec_property);
				if (spec_property != null) {
					spec_info = spec_property.getSpec().getName() + ":"
							+ spec_property.getValue() + " " + spec_info;
				}
			}
			obj.setSc(sc);
			obj.setSpec_info(spec_info);
			save(obj);
			sc.getGcs().add(obj);
			double cart_total_price = 0;
			for (GoodsCart gc1 : sc.getGcs()) {
				if (CommUtil.null2String(gc1.getCart_type()).equals("")) {
					cart_total_price = cart_total_price
							+ CommUtil.null2Double(gc1.getGoods()
									.getGoods_current_price()) * gc1.getCount();
				}
				if (CommUtil.null2String(gc1.getCart_type()).equals("combin")) { // 如果是组合销售购买，则设置组合价格
					cart_total_price = cart_total_price
							+ CommUtil.null2Double(gc1.getGoods()
									.getCombin_price()) * gc1.getCount();
				}
			}
			sc.setTotal_price(BigDecimal.valueOf(CommUtil
					.formatMoney(cart_total_price)));
			if (user == null) {
				sc.setCart_session_id(cart_session_id);
			} else {
				sc.setUser(user);
			}
			if (type.equals("save")) {
				sc.setAddTime(new Date());
				this.storeCartDao.save(sc);// 保存该StoreCart
			} else {
				this.storeCartDao.update(sc);
			}
			boolean cart_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc.getStore().getId())) {
					cart_add = false;
				}
			}
			if (cart_add) {
				cart.add(sc);// 将新增的StoreCart添加到整体购物车中，计算总体价格和数量
			}
		}
		for (StoreCart sc1 : cart) {
			// System.out.println(sc1.getGcs().size());
			total_count = total_count + sc1.getGcs().size();
			for (GoodsCart gc1 : sc1.getGcs()) {
				total_price = total_price
						+ CommUtil.mul(gc1.getPrice(), gc1.getCount());
			}
		}
		map.put("count", total_count);
		map.put("total_price", total_price);
		map.put("inventory", -1);
		}else
		map.put("inventory", inventory);
		String ret = Json.toJson(map, JsonFormat.compact());
		return ret;
	}
}
