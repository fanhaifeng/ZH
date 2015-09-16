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
import com.lakecloud.foundation.domain.GoodsDCImport;
import com.lakecloud.foundation.service.IGoodsDCImportService;

@Service
@Transactional
public class GoodsDCImportServiceImpl implements IGoodsDCImportService{
	@Resource(name = "goodsDCImportDAO")
	private IGenericDAO<GoodsDCImport> goodsDCImportDao;
	
	public boolean save(GoodsDCImport goodsDCImport) {
		/**
		 * init other field here
		 */
		try {
			this.goodsDCImportDao.save(goodsDCImport);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public GoodsDCImport getObjById(Long id) {
		GoodsDCImport goodsDCImport = this.goodsDCImportDao.get(id);
		if (goodsDCImport != null) {
			return goodsDCImport;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.goodsDCImportDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> goodsDCImports) {
		for (Serializable id : goodsDCImports) {
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
		GenericPageList pList = new GenericPageList(GoodsDCImport.class, query,
				params, this.goodsDCImportDao);
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
	
	public boolean update(GoodsDCImport goodsDCImport) {
		try {
			this.goodsDCImportDao.update(goodsDCImport);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<GoodsDCImport> query(String query, Map params, int begin, int max){
		return this.goodsDCImportDao.query(query, params, begin, max);
		
	}
}
