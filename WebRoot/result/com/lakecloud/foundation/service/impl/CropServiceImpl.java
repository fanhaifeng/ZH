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
import com.lakecloud.foundation.domain.Crop;
import com.lakecloud.foundation.service.ICropService;

@Service
@Transactional
public class CropServiceImpl implements ICropService{
	@Resource(name = "cropDAO")
	private IGenericDAO<Crop> cropDao;
	
	public boolean save(Crop crop) {
		/**
		 * init other field here
		 */
		try {
			this.cropDao.save(crop);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Crop getObjById(Long id) {
		Crop crop = this.cropDao.get(id);
		if (crop != null) {
			return crop;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.cropDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> cropIds) {
		// TODO Auto-generated method stub
		for (Serializable id : cropIds) {
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
		GenericPageList pList = new GenericPageList(Crop.class, query,
				params, this.cropDao);
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
	
	public boolean update(Crop crop) {
		try {
			this.cropDao.update( crop);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<Crop> query(String query, Map params, int begin, int max){
		return this.cropDao.query(query, params, begin, max);
		
	}
}
