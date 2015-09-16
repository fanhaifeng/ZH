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
import com.lakecloud.foundation.domain.PromotionLevel;
import com.lakecloud.foundation.service.IPromotionLevelService;

@Service
@Transactional
public class PromotionLevelServiceImpl implements IPromotionLevelService {
	@Resource(name = "promotionLevelDAO")
	private IGenericDAO<PromotionLevel> promotionLevelDao;

	public boolean save(PromotionLevel promotionLevel) {
		/**
		 * init other field here
		 */
		try {
			this.promotionLevelDao.save(promotionLevel);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public PromotionLevel getObjById(Long id) {
		PromotionLevel promotionLevel = this.promotionLevelDao.get(id);
		if (promotionLevel != null) {
			return promotionLevel;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.promotionLevelDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> promotionLevelIds) {
		// TODO Auto-generated method stub
		for (Serializable id : promotionLevelIds) {
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
		GenericPageList pList = new GenericPageList(PromotionLevel.class,
				query, params, this.promotionLevelDao);
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

	public boolean update(PromotionLevel promotionLevel) {
		try {
			this.promotionLevelDao.update(promotionLevel);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<PromotionLevel> query(String query, Map params, int begin,
			int max) {
		return this.promotionLevelDao.query(query, params, begin, max);
	}
}
