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
import com.lakecloud.foundation.domain.ReferPrice;
import com.lakecloud.foundation.service.IReferPriceService;

@Service
@Transactional
public class ReferPriceServiceImpl implements IReferPriceService{
	@Resource(name = "referPriceDAO")
	private IGenericDAO<ReferPrice> referPriceDao;
	
	public boolean save(ReferPrice referPrice) {
		/**
		 * init other field here
		 */
		try {
			this.referPriceDao.save(referPrice);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public ReferPrice getObjById(Long id) {
		ReferPrice referPrice = this.referPriceDao.get(id);
		if (referPrice != null) {
			return referPrice;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.referPriceDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> referPriceIds) {
		// TODO Auto-generated method stub
		for (Serializable id : referPriceIds) {
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
		GenericPageList pList = new GenericPageList(ReferPrice.class, query,
				params, this.referPriceDao);
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
	
	public boolean update(ReferPrice referPrice) {
		try {
			this.referPriceDao.update( referPrice);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<ReferPrice> query(String query, Map params, int begin, int max){
		return this.referPriceDao.query(query, params, begin, max);
		
	}
}
