package com.lakecloud.foundation.service.impl;
import java.io.Serializable;
import java.util.ArrayList;
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
import com.lakecloud.foundation.domain.UserGoodsClass;
import com.lakecloud.foundation.service.IUserGoodsClassService;

@Service
@Transactional
public class UserGoodsClassServiceImpl implements IUserGoodsClassService{
	@Resource(name = "userGoodsClassDAO")
	private IGenericDAO<UserGoodsClass> userGoodsClassDao;
	
	public boolean save(UserGoodsClass userGoodsClass) {
		/**
		 * init other field here
		 */
		try {
			this.userGoodsClassDao.save(userGoodsClass);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public UserGoodsClass getObjById(Long id) {
		UserGoodsClass userGoodsClass = this.userGoodsClassDao.get(id);
		if (userGoodsClass != null) {
			return userGoodsClass;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.userGoodsClassDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> userGoodsClassIds) {
		// TODO Auto-generated method stub
		for (Serializable id : userGoodsClassIds) {
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
		GenericPageList pList = new GenericPageList(UserGoodsClass.class, query,
				params, this.userGoodsClassDao);
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
	
	public boolean update(UserGoodsClass userGoodsClass) {
		try {
			this.userGoodsClassDao.update( userGoodsClass);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<UserGoodsClass> query(String query, Map params, int begin, int max){
		return this.userGoodsClassDao.query(query, params, begin, max);
		
	}

	@Override
	public boolean usergoodsclass_del(String mulitId) {
		String[] ids = mulitId.split(",");
		StringBuffer idList=new StringBuffer();
		idList.append("(");
		for (String id : ids) {
			if (!id.equals("")) {
				idList.append(id);
			}
		}
		mulitId=idList.append(")").toString();
		try {
			String sql="delete from LAKECLOUD_GOODS_UGC ugc where ugc.CLASS_ID in "+mulitId;
			this.userGoodsClassDao.executeNativeSQL(sql);
			sql="delete from LAKECLOUD_USERGOODSCLASS ugc where ugc.id in "+mulitId;
			this.userGoodsClassDao.executeNativeSQL(sql);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
