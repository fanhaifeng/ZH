package com.lakecloud.foundation.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakecloud.core.dao.IGenericDAO;
import com.lakecloud.core.query.GenericPageList;
import com.lakecloud.core.query.PageObject;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.GoodsFloorGoods;
import com.lakecloud.foundation.service.IGoodsFloorGoodsService;

@Service
@Transactional
public class GoodsFloorGoodsServiceImpl implements IGoodsFloorGoodsService {
	@Resource(name = "goodsFloorGoodsDAO")
	private IGenericDAO<GoodsFloorGoods> goodsFloorGoodsDao;

	public boolean save(GoodsFloorGoods goodsFloorGoods) {
		/**
		 * init other field here
		 */
		try {
			this.goodsFloorGoodsDao.save(goodsFloorGoods);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public GoodsFloorGoods getObjById(Long id) {
		GoodsFloorGoods goodsFloorGoods = this.goodsFloorGoodsDao.get(id);
		if (goodsFloorGoods != null) {
			return goodsFloorGoods;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.goodsFloorGoodsDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> goodsFloorGoodsIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goodsFloorGoodsIds) {
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
		GenericPageList pList = new GenericPageList(GoodsFloorGoods.class,
				query, params, this.goodsFloorGoodsDao);
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

	public boolean update(GoodsFloorGoods goodsFloorGoods) {
		try {
			this.goodsFloorGoodsDao.update(goodsFloorGoods);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<GoodsFloorGoods> query(String query, Map params, int begin,
			int max) {
		return this.goodsFloorGoodsDao.query(query, params, begin, max);
	}
}
