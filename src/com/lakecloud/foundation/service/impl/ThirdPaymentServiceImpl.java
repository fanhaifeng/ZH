package com.lakecloud.foundation.service.impl;

import java.io.Serializable;
import java.util.HashMap;
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
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.foundation.domain.ThirdPayment;
import com.lakecloud.foundation.service.IThirdPaymentService;

@Service
@Transactional
public class ThirdPaymentServiceImpl implements IThirdPaymentService {
	@Resource(name = "thirdPaymentDAO")
	private IGenericDAO<ThirdPayment> thirdPaymentDao;

	public boolean save(ThirdPayment thirdPayment) {
		/**
		 * init other field here
		 */
		try {
			this.thirdPaymentDao.save(thirdPayment);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public ThirdPayment getObjById(Long id) {
		ThirdPayment thirdPayment = this.thirdPaymentDao.get(id);
		if (thirdPayment != null) {
			return thirdPayment;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.thirdPaymentDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> thirdPaymentIds) {
		// TODO Auto-generated method stub
		for (Serializable id : thirdPaymentIds) {
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
		GenericPageList pList = new GenericPageList(ThirdPayment.class, query,
				params, this.thirdPaymentDao);
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

	public boolean update(ThirdPayment thirdPayment) {
		try {
			this.thirdPaymentDao.update(thirdPayment);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<ThirdPayment> query(String query, Map params, int begin, int max) {
		return this.thirdPaymentDao.query(query, params, begin, max);
	}

	public List<ThirdPayment> queryByCreateUser() {
		Map params = new HashMap();
		params.put("create_user", SecurityUserHolder.getCurrentUser()
				.getUsername());
		List<ThirdPayment> list = query(
				"select obj from ThirdPayment obj where obj.create_user=:create_user",
				params, -1, -1);
		return list;
	}

	public List<ThirdPayment> queryByOrderId(String orderId) {
		Map params = new HashMap();
		params.put("create_user", SecurityUserHolder.getCurrentUser()
				.getUsername());
		params.put("order_id", orderId);
		List<ThirdPayment> list = query(
				"select obj from ThirdPayment obj where obj.create_user=:create_user and obj.order_id=:order_id",
				params, -1, -1);
		return list;
	}
}
