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
import com.lakecloud.foundation.domain.Refund;
import com.lakecloud.foundation.service.IRefundService;

@Service
@Transactional
public class RefundServiceImpl implements IRefundService{
	@Resource(name = "refundDAO")
	private IGenericDAO<Refund> refundDao;
	
	public boolean save(Refund refund) {
		/**
		 * init other field here
		 */
		try {
			this.refundDao.save(refund);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Refund getObjById(Long id) {
		Refund refund = this.refundDao.get(id);
		if (refund != null) {
			return refund;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.refundDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> refundIds) {
		// TODO Auto-generated method stub
		for (Serializable id : refundIds) {
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
		GenericPageList pList = new GenericPageList(Refund.class, query,
				params, this.refundDao);
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
	
	public boolean update(Refund refund) {
		try {
			this.refundDao.update( refund);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<Refund> query(String query, Map params, int begin, int max){
		return this.refundDao.query(query, params, begin, max);
		
	}
}
