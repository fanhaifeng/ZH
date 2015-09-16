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
import com.lakecloud.foundation.domain.ThirdBinding;
import com.lakecloud.foundation.service.IThirdBindingService;

@Service
@Transactional
public class ThirdBindingServiceImpl implements IThirdBindingService {
	@Resource(name = "thirdBindingDAO")
	private IGenericDAO<ThirdBinding> thirdBindingDao;

	public boolean save(ThirdBinding thirdBinding) {
		/**
		 * init other field here
		 */
		try {
			this.thirdBindingDao.save(thirdBinding);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public ThirdBinding getObjById(Long id) {
		ThirdBinding thirdBinding = this.thirdBindingDao.get(id);
		if (thirdBinding != null) {
			return thirdBinding;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.thirdBindingDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> thirdBindingIds) {
		// TODO Auto-generated method stub
		for (Serializable id : thirdBindingIds) {
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
		GenericPageList pList = new GenericPageList(ThirdBinding.class, query,
				params, this.thirdBindingDao);
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

	public boolean update(ThirdBinding thirdBinding) {
		try {
			this.thirdBindingDao.update(thirdBinding);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<ThirdBinding> query(String query, Map params, int begin, int max) {
		return this.thirdBindingDao.query(query, params, begin, max);
	}

	public List<ThirdBinding> queryByCreateUser() {
		Map params = new HashMap();
		params.put("create_user", SecurityUserHolder.getCurrentUser()
				.getUsername());
		List<ThirdBinding> list = query(
				"select obj from ThirdBinding obj where obj.create_user=:create_user",
				params, -1, -1);
		return list;
	}
}
