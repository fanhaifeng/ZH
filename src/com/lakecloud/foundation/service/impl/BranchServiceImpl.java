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
import com.lakecloud.foundation.domain.Branch;
import com.lakecloud.foundation.service.IBranchService;

@Service
@Transactional
public class BranchServiceImpl implements IBranchService{
	@Resource(name = "branchDAO")
	private IGenericDAO<Branch> branchDAO;

	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		try {
			this.branchDAO.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Branch getObjById(Long id) {
		// TODO Auto-generated method stub
		return this.branchDAO.get(id);
	}

	public boolean save(Branch branch) {
		// TODO Auto-generated method stub
		try {
			this.branchDAO.save(branch);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean update(Branch branch) {
		// TODO Auto-generated method stub
		try {
			this.branchDAO.update(branch);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public List<Branch> query(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.branchDAO.query(query, params, begin, max);

	}

	public IPageList list(IQueryObject properties) {
		// TODO Auto-generated method stub
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(Branch.class, query, params,
				this.branchDAO);
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
	public Branch getObjByProperty(String propertyName, String value) {
		// TODO Auto-generated method stub
		return this.branchDAO.getBy(propertyName, value);
	}
	
	public List getBranch(String query, Map params, int begin, int max){
		return this.branchDAO.executeNativeQuery(query, params, begin, max);
		
	}

}
