package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.StoreCart;

public interface IGoodsCartService {
	/**
	 * 保存一个GoodsCart，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsCart instance);

	/**
	 * 根据一个ID得到GoodsCart
	 * 
	 * @param id
	 * @return
	 */
	GoodsCart getObjById(Long id);

	/**
	 * 删除一个GoodsCart
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除GoodsCart
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到GoodsCart
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个GoodsCart
	 * 
	 * @param id
	 *            需要更新的GoodsCart的id
	 * @param dir
	 *            需要更新的GoodsCart
	 */
	boolean update(GoodsCart instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsCart> query(String query, Map params, int begin, int max);

	List<GoodsCart> queryByStoreCart(StoreCart sc);

	List<GoodsCart> queryByGoods(Goods goods);
	/**
	 * 移除商品购物车
	 * @param request
	 * @param response
	 * @param id
	 * @param store_id
	 * @return
	 */
	String remove_goods_cart(HttpServletRequest request,HttpServletResponse response, String id, String store_id);
	/**
	 * 添加购物车
	 * @param request
	 * @param response
	 * @param id
	 * @param count
	 * @param price
	 * @param gsp
	 * @param buy_type
	 * @return
	 */
	String add_goods_cart(HttpServletRequest request,HttpServletResponse response, String id, String count,String price, String gsp, String buy_type);
}
