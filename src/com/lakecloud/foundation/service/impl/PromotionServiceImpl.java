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
import com.lakecloud.foundation.domain.Promotion;
import com.lakecloud.foundation.service.IPromotionService;

@Service
@Transactional
public class PromotionServiceImpl implements IPromotionService {
	@Resource(name = "promotionDAO")
	private IGenericDAO<Promotion> promotionDao;

	public boolean save(Promotion promotion) {
		/**
		 * init other field here
		 */
		try {
			this.promotionDao.save(promotion);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Promotion getObjById(Long id) {
		Promotion promotion = this.promotionDao.get(id);
		if (promotion != null) {
			return promotion;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.promotionDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> promotionIds) {
		// TODO Auto-generated method stub
		for (Serializable id : promotionIds) {
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
		GenericPageList pList = new GenericPageList(Promotion.class, query,
				params, this.promotionDao);
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

	public boolean update(Promotion promotion) {
		try {
			this.promotionDao.update(promotion);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Promotion> query(String query, Map params, int begin, int max) {
		return this.promotionDao.query(query, params, begin, max);
	}
}
