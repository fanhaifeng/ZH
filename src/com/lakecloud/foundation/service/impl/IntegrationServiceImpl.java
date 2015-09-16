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
import com.lakecloud.foundation.domain.Integration;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IIntegrationService;

@Service
@Transactional
public class IntegrationServiceImpl implements IIntegrationService {
	@Resource(name = "integrationDAO")
	private IGenericDAO<Integration> integrationDao;

	public boolean save(Integration integration) {
		/**
		 * init other field here
		 */
		try {
			this.integrationDao.save(integration);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Integration getObjById(Long id) {
		Integration integration = this.integrationDao.get(id);
		if (integration != null) {
			return integration;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.integrationDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> integrationIds) {
		for (Serializable id : integrationIds) {
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
		GenericPageList pList = new GenericPageList(Integration.class, query,
				params, this.integrationDao);
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

	public boolean update(Integration integration) {
		try {
			this.integrationDao.update(integration);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Integration> query(String query, Map params, int begin, int max) {
		return this.integrationDao.query(query, params, begin, max);
	}

	public List<Integration> queryByUser(User user) {
		String hql = "";
		Map params = new HashMap();
		params.put("user", user);
		List<Integration> list = query(
				"select obj from Integration obj where obj.user=:user", params,
				-1, -1);
		return list;
	}

	public boolean updateByOrderForm(OrderForm of, String money_overdue,
			Integration integration) {
		if (null != of.getIntegrationStore()) {
			integration.setIntegrals(integration.getIntegrals()
					- of.getIntegrationStore());
		}
		if (null != of.getIntegrationPlatform()) {
			integration.setIntegrals(integration.getIntegrals()
					- of.getIntegrationPlatform());
		}
		return update(integration);
	}

	@Override
	public Integration getObjByProperty(String propertyName, Object value) {
		return this.integrationDao.getBy(propertyName, value);
	}
}
