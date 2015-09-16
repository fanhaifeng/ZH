package com.lakecloud.foundation.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakecloud.core.dao.IGenericDAO;
import com.lakecloud.core.query.GenericPageList;
import com.lakecloud.core.query.PageObject;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.foundation.domain.Integration;
import com.lakecloud.foundation.domain.Integration_Child;
import com.lakecloud.foundation.domain.Integration_Log;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IIntegration_ChildService;
import com.lakecloud.foundation.service.IIntegration_LogService;
import com.lakecloud.weixin.utils.ConstantUtils;

@Service
@Transactional
public class Integration_LogServiceImpl implements IIntegration_LogService {
	@Resource(name = "integration_LogDAO")
	private IGenericDAO<Integration_Log> integration_LogDao;
	@Autowired
	private IIntegration_ChildService integrationChildService;
	@Resource(name = "integrationDAO")
	private IGenericDAO<Integration> integrationDao;
	@Resource(name = "integration_ChildDAO")
	private IGenericDAO<Integration_Child> integration_ChildDao;

	public boolean save(Integration_Log integration_Log) {
		/**
		 * init other field here
		 */
		try {
			this.integration_LogDao.save(integration_Log);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Integration_Log getObjById(Long id) {
		Integration_Log integration_Log = this.integration_LogDao.get(id);
		if (integration_Log != null) {
			return integration_Log;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.integration_LogDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> integration_LogIds) {
		for (Serializable id : integration_LogIds) {
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
		GenericPageList pList = new GenericPageList(Integration_Log.class, query,
		    params, this.integration_LogDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(
				    pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage(),
				    pageObj.getPageSize() == null ? 0 : pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}

	public boolean update(Integration_Log integration_Log) {
		try {
			this.integration_LogDao.update(integration_Log);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Integration_Log> query(String query, Map params, int begin,
	    int max) {
		return this.integration_LogDao.query(query, params, begin, max);
	}

	@Override
	public Integration_Log getObjByProperty(String propertyName, Object value) {
		return this.integration_LogDao.getBy(propertyName, value);
	}

	public boolean saveByIntegrationLogConstructor(Integration_Log integrationLog) {
		User user = SecurityUserHolder.getCurrentUser();
		if (null != integrationLog.getOrderForm()) {
			Store store = integrationLog.getOrderForm().getStore();
			int integrationStore = 0;
			List<Integration_Child> integrationStoreList = integrationChildService
			    .queryByTypeStoreOrUser(ConstantUtils._INTEGRATION_TYPE[1], user,
			        store);
			if (null != integrationStoreList && integrationStoreList.size() > 0) {
				integrationStore = integrationStoreList.get(0).getIntegrals() != null ? integrationStoreList
				    .get(0).getIntegrals() : 0;
			}
			integrationLog.setBe_integrals_store(integrationStore
			    - integrationLog.getIntegrals_store() * integrationLog.getType());
			integrationLog.setAf_integrals_store(integrationStore);
			integrationLog.setStore(store);
		}
		int integrationPlatform = 0;
		List<Integration_Child> integrationPlatformList = integrationChildService
		    .queryByTypeStoreOrUser(ConstantUtils._INTEGRATION_TYPE[0], user, null);
		if (null != integrationPlatformList && integrationPlatformList.size() > 0) {
			integrationPlatform = integrationPlatformList.get(0).getIntegrals() != null ? integrationPlatformList
			    .get(0).getIntegrals() : 0;
		}
		integrationLog.setBe_integrals(integrationPlatform
		    - integrationLog.getIntegrals() * integrationLog.getType());
		integrationLog.setAf_integrals(integrationPlatform);
		integrationLog.setUser(user);
		integrationLog.setAddTime(new Date());
		return save(integrationLog);
	}

	@Override
	public void return_Integration_Log_By_OrderForm(OrderForm order) {
		System.out.println("*******fanhuannongdou*********");
		// 返回农豆
		User user = order.getUser();
		Store store = order.getStore();
		// 查询子表用户农豆
		String query = "from Integration_Child obj where obj.user.id=:uid and (obj.store.id=:sid or obj.type=0)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", user.getId());
		params.put("sid", store.getId());
		List<Integration_Child> listics = this.integration_ChildDao.query(query,
		    params, -1, -1);
		Integration_Child integration_Childs;
		Integration_Child integration_Childp;
		if (listics.size() < 2) {
			//如果不存在子表店铺农豆生产一条临时空数据用于计算
			System.out
			    .println("ERRO:Integration_LogServiceImpl.java  Line:170 ****************");
			integration_Childs = new Integration_Child();
			integration_Childs.setIntegrals(0);
			integration_Childs.setOverdue_integrals(0);
			integration_Childp=listics.get(0);
		} else {
			System.out
	    .println("ERRO:Integration_LogServiceImpl.java  Line:177  has two item****************");
			integration_Childs = listics.get(0).getType() == 1 ? listics.get(0)
			    : listics.get(1);
			integration_Childp = listics.get(0).getType() == 0 ? listics
			    .get(0) : listics.get(1);
		}
		// 查询主表用户农豆
		query = "from Integration obj where obj.user.id=:uid";
		params.clear();
		params.put("uid", user.getId());
		List<Integration> listi = this.integrationDao.query(query, params, -1, -1);
		if (listi.size() != 1) {
			System.out
			    .println("ERRO:Integration_LogServiceImpl.java  Line:180 ****************");
		}
		Integration integration = listi.get(0);
		// 查询用户订单对应农豆日志
		query = "from Integration_Log obj where obj.orderForm.id=:oid";
		params.clear();
		params.put("oid", order.getId());
		List<Integration_Log> listil = this.integration_LogDao.query(query, params,
		    -1, -1);
		for (Integration_Log integration_Log1 : listil) {
			Integer type, pt = 0, dp = 0, pto = 0, dpo = 0;
			type = integration_Log1.getType() == 1 ? -1 : 1;// 消费类型
			pt = integration_Log1.getIntegrals() * type;// 平台农豆
			dp=integration_Log1.getIntegrals_store();
			dp = dp==null?0:dp * type;// 店铺农豆
			pto = integration_Log1.getOverdue_integrals();
			pto = pto == null ? 0 : pto * type;// 即将过期的平台农豆
			dpo = integration_Log1.getOverdue_integrals_order();
			dpo = dpo == null ? 0 : dpo * type;// 即将过期的店铺农豆
		Integer main_change = pt + dp;
		if (main_change == 0){
			System.out.println("INFO:Integration_LogServiceImpl.java  Line:209  NO CHANGES****************");
			return;
		}
		// 生成返回农豆日志
		Integration_Log integration_Log = new Integration_Log();
		integration_Log.setAddTime(new Date());
		integration_Log.setIntegrals(pt > 0 ? pt : pt * -1);
		integration_Log.setIntegrals_store(dp > 0 ? dp : dp * -1);
		integration_Log.setBe_integrals_store(integration_Childs.getIntegrals());
		integration_Log.setBe_integrals(integration_Childp.getIntegrals());
		integration_Log.setGettype(0);
		integration_Log.setOverdue_integrals(pto > 0 ? pto : pto * -1);
		integration_Log.setOverdue_integrals_order(dpo > 0 ? dpo : dpo * -1);
		integration_Log.setUser(user);
		integration_Log.setStore(store);
		integration_Log.setInteg(integration);
		integration_Log.setOrderForm(order);
		// 返回子表农豆
		Integer p_now = integration_Childp.getIntegrals();
		integration_Childp.setIntegrals(p_now == null ? 0 : p_now + pt);// 子表平台农豆
		Integer p_over = integration_Childp.getOverdue_integrals();
		integration_Childp.setOverdue_integrals(p_over == null ? 0 : p_over + pto);// 子表平台将要过期农豆
		Integer s_now = integration_Childs.getIntegrals();
		integration_Childs.setIntegrals(s_now == null ? 0 : s_now + dp);// 子表店铺农豆
		Integer s_over = integration_Childs.getOverdue_integrals();
		integration_Childs.setOverdue_integrals(s_over == null ? 0 : s_over + dpo);// 子表将要过期店铺农豆
		// 返回主表农豆
		Integer main_now = integration.getIntegrals();
		integration.setIntegrals(main_now == null ? 0 : main_now + pt + dp);// 主表农豆
		Integer main_over = integration.getOverdue_integrals();
		integration.setOverdue_integrals(main_over == null ? 0 : main_over + pto
		    + dpo);// 主表将要过期农豆
		// 保存农豆修改
		if (integration_Childs.getId() != null)//判断是否存在子表
			this.integration_ChildDao.update(integration_Childs);
		this.integration_ChildDao.update(integration_Childp);
		this.integrationDao.update(integration);
		// 保存返回农豆日志
		integration_Log.setAf_integrals_store(integration_Childs.getIntegrals());
		integration_Log.setAf_integrals(integration_Childp.getIntegrals());
		integration_Log.setType(main_change >= 0 ? 1 : -1);
		this.integration_LogDao.save(integration_Log);
		}
	}
	/**
	 * unused用于确认收货时，订单获取店铺农豆
	 */
	// public Integration_Log queryByOrderFormAndType(int type, OrderForm of) {
	// Integration_Log integrationLog = null;
	// Map params = new HashMap();
	// params.put("type", type);
	// params.put("of", of);
	// List<Integration_Log> list = query(
	// "select obj from Integration_Log obj where obj.type=:type and obj.orderForm=:of",
	// params, -1, -1);
	// if (null != list && list.size() > 0) {
	// integrationLog = list.get(0);
	// }
	// return integrationLog;
	// }
}
