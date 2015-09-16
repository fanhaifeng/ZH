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
import com.lakecloud.foundation.domain.XConf;
import com.lakecloud.foundation.service.IXConfService;
import com.lakecloud.weixin.utils.ConstantUtils;

@Service
@Transactional
public class XConfServiceImpl implements IXConfService {
	@Resource(name = "xConfDAO")
	private IGenericDAO<XConf> xConfDao;

	public boolean save(XConf xConf) {
		/**
		 * init other field here
		 */
		try {
			this.xConfDao.save(xConf);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public XConf getObjById(Long id) {
		XConf xConf = this.xConfDao.get(id);
		if (xConf != null) {
			return xConf;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.xConfDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> xConfIds) {
		// TODO Auto-generated method stub
		for (Serializable id : xConfIds) {
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
		GenericPageList pList = new GenericPageList(XConf.class, query, params,
				this.xConfDao);
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

	public boolean update(XConf xConf) {
		try {
			this.xConfDao.update(xConf);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<XConf> query(String query, Map params, int begin, int max) {
		return this.xConfDao.query(query, params, begin, max);
	}

	public XConf queryByXconfkey(String xconfkey) {
		Map params = new HashMap();
		params.put("xconfkey", xconfkey);
		List<XConf> list = query(
				"select obj from XConf obj where obj.xconfkey=:xconfkey",
				params, -1, -1);
		XConf xConf;
		if (null != list && list.size() > 0) {
			xConf = list.get(0);
		} else {
			xConf = null;
			System.out.println(ConstantUtils._getLogEnter(" XConfServiceImpl"));
			System.out.println("not found xConf queryByXconfkey");
			System.out.println(ConstantUtils._getLogExit(" XConfServiceImpl"));
		}
		return xConf;
	}
}
