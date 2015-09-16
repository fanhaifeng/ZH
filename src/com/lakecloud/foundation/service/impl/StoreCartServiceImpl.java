package com.lakecloud.foundation.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lakecloud.core.query.PageObject;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakecloud.core.dao.IGenericDAO;
import com.lakecloud.core.query.GenericPageList;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.GroupGoods;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreCart;
import com.lakecloud.foundation.service.IChargeService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IStoreCartService;

@Service
@Transactional
public class StoreCartServiceImpl implements IStoreCartService {
	@Resource(name = "storeCartDAO")
	private IGenericDAO<StoreCart> storeCartDao;
	@Resource(name = "goodsCartDAO")
	private IGenericDAO<GoodsCart> goodsCartDao;
	@Autowired
	private IOrderFormService orderFormService;

	public boolean save(StoreCart storeCart) {
		/**
		 * init other field here
		 */
		try {
			this.storeCartDao.save(storeCart);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public StoreCart getObjById(Long id) {
		StoreCart storeCart = this.storeCartDao.get(id);
		if (storeCart != null) {
			return storeCart;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.storeCartDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> storeCartIds) {
		// TODO Auto-generated method stub
		for (Serializable id : storeCartIds) {
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
		GenericPageList pList = new GenericPageList(StoreCart.class, query,
				params, this.storeCartDao);
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

	public boolean update(StoreCart storeCart) {
		try {
			this.storeCartDao.update(storeCart);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<StoreCart> query(String query, Map params, int begin, int max) {
		return this.storeCartDao.query(query, params, begin, max);
	}

	public void weixin_goods_cart1(Store store, List<StoreCart> cart) {
		if (store != null) {
			for (StoreCart sc : cart) {
				if (sc.getStore().getId().equals(store.getId())) {
					for (GoodsCart gc : sc.getGcs()) {
						gc.getGsps().clear();
						this.goodsCartDao.remove(gc.getId());
					}
					sc.getGcs().clear();
					this.storeCartDao.remove(sc.getId());
				}
			}
		}
	}

	public void cart_calc(Map params) {
		List<StoreCart> store_cookie_cart = this.storeCartDao
				.query(
						"select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
						params, -1, -1);
		for (StoreCart sc : store_cookie_cart) {
			for (GoodsCart gc : sc.getGcs()) {
				gc.getGsps().clear();
				this.goodsCartDao.remove(gc.getId());
			}
			this.storeCartDao.remove(sc.getId());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public int query_goods_type(Long user_id) {		
		String sql="select count(*) from(select count(GOODS_SERIAL) from LAKECLOUD_GOODS where id in (select GOODS_ID from LAKECLOUD_GOODSCART where SC_ID in (select id from LAKECLOUD_STORECART where SC_STATUS=0 and USER_ID="+user_id+")) group by GOODS_SERIAL)";
		List<Integer> list=this.storeCartDao.executeNativeQuery(sql, new HashMap(), -1, -1);		
		return list.get(0);
	}

	@Override
	public String goods_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String cart_id, String store_id,
			String count) {
		List<StoreCart> cart = this.orderFormService.cart_calc(request);
		//
		double goods_total_price = 0;
		String error = "100";// 100表示修改成功，200表示库存不足,300表示团购库存不足
		Goods goods = null;
		String cart_type = "";// 判断是否为组合销售
		for (StoreCart sc : cart) {
			for (GoodsCart gc : sc.getGcs()) {
				if (gc.getId().toString().equals(cart_id)) {
					goods = gc.getGoods();
					cart_type = CommUtil.null2String(gc.getCart_type());
				}
			}
		}
		if (cart_type.equals("")) {// 普通商品的处理			
			if (goods.getGroup_buy() == 2) {
				GroupGoods gg = new	GroupGoods(); 
				for (GroupGoods gg1 : goods.getGroup_goods_list()){ 
					if (gg1.getGg_goods().equals(goods.getId())) {
						gg = gg1;
					} 
				} 
				if(gg.getGg_count() >= CommUtil.null2Int(count)) {
					for (StoreCart sc : cart) { 
						for (int i = 0; i < sc.getGcs().size(); i++) {
							GoodsCart gc = sc.getGcs().get(i);
							GoodsCart gc1 = gc;
							if(gc.getId().toString().equals(cart_id)) {
								sc.setTotal_price(BigDecimal.valueOf(CommUtil.add(sc.getTotal_price(),(CommUtil.null2Int(count) - gc.getCount())*CommUtil.null2Double(gc .getPrice()))));
								gc.setCount(CommUtil.null2Int(count));
								gc1 = gc;
								sc.getGcs().remove(gc);
                                sc.getGcs().add(gc1); goods_total_price =CommUtil.null2Double(gc1 .getPrice())*gc1.getCount();
								this.storeCartDao.update(sc); 
							} 
						} 
					} 
				} else {
					error = "300";
				}
			} else {			
				if (goods.getGoods_inventory() >= CommUtil.null2Int(count)) {
					for (StoreCart sc : cart) {
						for (int i = 0; i < sc.getGcs().size(); i++) {
							GoodsCart gc = sc.getGcs().get(i);
							GoodsCart gc1 = gc;
							if (gc.getId().toString().equals(cart_id)) {
								sc.setTotal_price(BigDecimal.valueOf(CommUtil.add(sc.getTotal_price(), (CommUtil.null2Int(count) - gc.getCount())* Double.parseDouble(gc.getPrice().toString()))));
								gc.setCount(CommUtil.null2Int(count));
								gc1 = gc;
								sc.getGcs().remove(gc);
								sc.getGcs().add(gc1);
								goods_total_price = Double.parseDouble(gc1.getPrice().toString())*gc1.getCount();
								this.storeCartDao.update(sc);
							}
						}
					}
				} else {
					count = CommUtil.null2String(goods.getGoods_inventory());
					error = "200";
				}
			}
		}		
		if (cart_type.equals("combin")) {// 组合销售的处理 
			if(goods.getGoods_inventory() >= CommUtil.null2Int(count)) {
				for(StoreCart sc : cart) {
					for (int i = 0; i < sc.getGcs().size(); i++){
					GoodsCart gc = sc.getGcs().get(i); 
					GoodsCart gc1 = gc; 
					if(gc.getId().toString().equals(cart_id)) {
						sc.setTotal_price(BigDecimal.valueOf(CommUtil.add(sc.getTotal_price(), (CommUtil.null2Int(count) - gc.getCount())*CommUtil.null2Float(gc.getGoods() .getCombin_price()))));
						gc.setCount(CommUtil.null2Int(count)); 
						gc1 = gc;
						sc.getGcs().remove(gc);
						sc.getGcs().add(gc1); goods_total_price =Double.parseDouble(gc1 .getPrice().toString()) * gc1.getCount();
						this.storeCartDao.update(sc);
					} 
					} 
				} 
			} else {
				error = "200";
			} 
		}		 
		DecimalFormat df = new DecimalFormat("0.00");
		Map map = new HashMap();
		map.put("count", count);
		for (StoreCart sc : cart) {
			System.out.println("=============:" + sc.getTotal_price());
			if (sc.getStore().getId().equals(CommUtil.null2Long(store_id))) {
				map.put("sc_total_price", df.format(CommUtil.null2Float(sc
						.getTotal_price())));
			}
		}
		map.put("goods_total_price",df.format(goods_total_price));
		map.put("error", error);
		return Json.toJson(map, JsonFormat.compact());
	
	}
}
