package com.lakecloud.foundation.service.impl;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import com.lakecloud.core.query.PageObject;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lakecloud.core.dao.IGenericDAO;
import com.lakecloud.core.query.GenericPageList;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsDC;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGoodsServiceDC;

@Service
@Transactional
public class GoodsServiceDCImpl implements IGoodsServiceDC{
	@Resource(name = "goodsDCDAO")
	private IGenericDAO<GoodsDC> goodsDao;
	
	public boolean save(GoodsDC goodsDC) {
		/**
		 * init other field here
		 */
		try {
			this.goodsDao.save(goodsDC);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public GoodsDC getObjById(Long id) {
		GoodsDC goodsDC = this.goodsDao.get(id);
		if (goodsDC != null) {
			return goodsDC;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.goodsDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> goodsIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goodsIds) {
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
		GenericPageList pList = new GenericPageList(GoodsDC.class, query,
				params, this.goodsDao);
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
	
	public boolean update(GoodsDC goodsDC) {
		try {
			this.goodsDao.update( goodsDC);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<GoodsDC> query(String query, Map params, int begin, int max){
		return this.goodsDao.query(query, params, begin, max);
		
	}

	@Override
	public GoodsDC getObjByProperty(String propertyName, Object value) {
		// TODO Auto-generated method stub
		return this.goodsDao.getBy(propertyName, value);
	}

	@Override
	public List getPrice(String query, Map params, int begin, int max) {
		return this.goodsDao.executeNativeQuery(query, params, begin, max);
	}
	
	public int nativeupdate(String query, Object params[]){
		return this.goodsDao.batchUpdate(query, params);
	}
}
