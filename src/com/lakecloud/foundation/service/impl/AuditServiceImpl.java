package com.lakecloud.foundation.service.impl;

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
import com.lakecloud.foundation.domain.Audit;
import com.lakecloud.foundation.service.IAuditService;

@Service
@Transactional
public class AuditServiceImpl implements IAuditService{
	@Resource(name = "auditDAO")
	private IGenericDAO<Audit> auditDAO;

	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		try {
			this.auditDAO.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Audit getObjById(Long id) {
		// TODO Auto-generated method stub
		return this.auditDAO.get(id);
	}

	public boolean save(Audit audit) {
		// TODO Auto-generated method stub
		try {
			this.auditDAO.save(audit);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean update(Audit audit) {
		// TODO Auto-generated method stub
		try {
			this.auditDAO.update(audit);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public List<Audit> query(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.auditDAO.query(query, params, begin, max);

	}

	public IPageList list(IQueryObject properties) {
		// TODO Auto-generated method stub
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(Audit.class, query, params,
				this.auditDAO);
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

	@Override
	public Audit getObjByProperty(String propertyName, String value) {
		// TODO Auto-generated method stub
		return this.auditDAO.getBy(propertyName, value);
	}

}
