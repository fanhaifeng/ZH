package com.lakecloud.foundation.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
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
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Integration;
import com.lakecloud.foundation.domain.Integration_Child;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.XConf;
import com.lakecloud.foundation.service.IIntegrationService;
import com.lakecloud.foundation.service.IIntegration_ChildService;
import com.lakecloud.foundation.service.IXConfService;
import com.lakecloud.weixin.utils.ConstantUtils;

@Service
@Transactional
public class Integration_ChildServiceImpl implements IIntegration_ChildService {
	@Resource(name = "integration_ChildDAO")
	private IGenericDAO<Integration_Child> integration_ChildDao;
	@Autowired
	private IIntegrationService integrationService;
	@Autowired
	private IXConfService xconfService;

	public boolean save(Integration_Child integration_Child) {
		/**
		 * init other field here
		 */
		try {
			this.integration_ChildDao.save(integration_Child);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Integration_Child getObjById(Long id) {
		Integration_Child integration_Child = this.integration_ChildDao.get(id);
		if (integration_Child != null) {
			return integration_Child;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.integration_ChildDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> integration_ChildIds) {
		for (Serializable id : integration_ChildIds) {
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
		GenericPageList pList = new GenericPageList(Integration_Child.class,
				query, params, this.integration_ChildDao);
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

	public boolean update(Integration_Child integration_Child) {
		try {
			this.integration_ChildDao.update(integration_Child);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Integration_Child> query(String query, Map params, int begin,
			int max) {
		return this.integration_ChildDao.query(query, params, begin, max);
	}

	public List<Integration_Child> queryByTypeStoreOrUser(int type, User user,
			Store store) {
		String hql = "";
		Map params = new HashMap();
		params.put("type", type);
		if (null != user) {
			params.put("user", user);
			hql = " and obj.user=:user";
		}
		if (null != store) {
			params.put("store", store);
			hql += " and obj.store=:store";
		}
		List<Integration_Child> list = query(
				"select obj from Integration_Child obj where obj.type=:type"
						+ hql, params, -1, -1);
		return list;
	}

	/**
	 * 该订单使用农豆，减少
	 */
	public boolean subtractByOrderForm(OrderForm of) {
		boolean result = false;
		User user = of.getUser();
		Store store = of.getStore();
		List<Integration_Child> integrationStoreList = queryByTypeStoreOrUser(
				ConstantUtils._INTEGRATION_TYPE[1], user, store);
		if (null != integrationStoreList && integrationStoreList.size() > 0) {
			result = updateByOrderForm(of, integrationStoreList,
					ConstantUtils._INTEGRATION_TYPE[1]);
		} else {
			result = true;
			// result = saveByOrderForm(of, ConstantUtils._INTEGRATION_TYPE[1]);
		}
		if (result) {
			List<Integration_Child> integrationPlatformList = queryByTypeStoreOrUser(
					ConstantUtils._INTEGRATION_TYPE[0], user, null);
			if (null != integrationPlatformList
					&& integrationPlatformList.size() > 0) {
				result = updateByOrderForm(of, integrationPlatformList,
						ConstantUtils._INTEGRATION_TYPE[0]);
			}
		}
		return result;
	}

	/**
	 * 用于平台农豆扣减，店铺农豆和过期农豆扣减
	 */
	private boolean updateByOrderForm(OrderForm of,
			List<Integration_Child> integrationList, int type) {
		Integration_Child integrationChild = integrationList.get(0);
		try {
			if (ConstantUtils._INTEGRATION_TYPE[1] == type) {
				Integer integrationStore_new = integrationChild.getIntegrals()
						- of.getIntegrationStore();
				if (integrationStore_new >= 0) {
					integrationChild.setIntegrals(integrationStore_new);
				} else {
					System.out.println(ConstantUtils
							._getIntegrationServiceImplFunctions(2, 0)
							+ "店铺农豆<0");
				}
				if (CommUtil.isNotNull(integrationChild.getOverdue_integrals())) {// 是否含过期的店铺农豆
					Integer overdue_integrals_new = integrationChild
							.getOverdue_integrals()
							- of.getIntegrationStore();
					if (overdue_integrals_new >= 0) {// 判断店铺农豆是否够
						integrationChild
								.setOverdue_integrals(overdue_integrals_new);
					} else {
						integrationChild.setOverdue_integrals(0);
					}
				}
			} else if (ConstantUtils._INTEGRATION_TYPE[0] == type) {
				Integer integrationPlatform_new = integrationChild
						.getIntegrals()
						- of.getIntegrationPlatform();
				if (integrationPlatform_new >= 0) {
					integrationChild.setIntegrals(integrationPlatform_new);
				} else {
					System.out.println(ConstantUtils
							._getIntegrationServiceImplFunctions(2, 0)
							+ "智慧豆<0");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return update(integrationChild);
	}

	private boolean saveByOrderForm(OrderForm of, int type) {
		Integration_Child integrationChild = new Integration_Child();
		integrationChild.setIntegrals(0);
		integrationChild.setType(type);
		List<Integration> integrationList = integrationService.queryByUser(of
				.getUser());
		if (null != integrationList && integrationList.size() > 0) {
			integrationChild.setInteg(integrationList.get(0));
		}
		integrationChild.setStore(of.getStore());
		integrationChild.setUser(of.getUser());
		integrationChild.setAddTime(new Date());
		return save(integrationChild);
	}

	/**
	 * 该订单获取农豆
	 */
	public boolean saveOrUpdateByXConfAndOrderForm(XConf xconf, OrderForm of) {
		boolean result;
		int type = ConstantUtils._INTEGRATION_TYPE[1];
		List<Integration_Child> integrationChildList = queryByTypeStoreOrUser(
				type, of.getUser(), of.getStore());
		if (null != integrationChildList && integrationChildList.size() > 0) {
			result = updateByTypeAndMoney(xconf, integrationChildList.get(0),
					of);
		} else {
			result = saveByTypeAndMoney(type, xconf, of);
		}
		return result;
	}

	private boolean saveByTypeAndMoney(int type, XConf xconf, OrderForm of) {
		Integration_Child integrationChild = new Integration_Child();
		try {
			if (null != of) {
				integrationChild.setIntegrals(count_integration_by_real_price(
						xconf, of));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		integrationChild.setType(type);
		List<Integration> integrationList = integrationService.queryByUser(of
				.getUser());
		if (null != integrationList && integrationList.size() > 0) {
			integrationChild.setInteg(integrationList.get(0));
		}
		integrationChild.setStore(of.getStore());
		integrationChild.setUser(of.getUser());
		integrationChild.setAddTime(new Date());
		return save(integrationChild);
	}

	private boolean updateByTypeAndMoney(XConf xconf,
			Integration_Child integrationChild, OrderForm of) {
		try {
			integrationChild.setIntegrals(integrationChild.getIntegrals()
					+ count_integration_by_real_price(xconf, of));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return update(integrationChild);
	}

	/**
	 * 通过实际所付金额，计算返还的农豆
	 */
	public Integer count_integration_by_real_price(XConf xconf, OrderForm of) {
		// XConf xconf_integration_rate_for_money = xconfService
		// .queryByXconfkey(ConstantUtils._INTEGRATION_RATE_FOR_MONEY);
		BigDecimal bigDecimal = of.getTotalPrice();
		// if (null != of.getIntegrationStore()) {
		// bigDecimal = bigDecimal.subtract(new BigDecimal(of
		// .getIntegrationStore()
		// * Double.valueOf(xconf_integration_rate_for_money
		// .getXconfvalue())));
		// }
		// if (null != of.getIntegrationPlatform()) {
		// bigDecimal = bigDecimal.subtract(new BigDecimal(of
		// .getIntegrationPlatform()
		// * Double.valueOf(xconf_integration_rate_for_money
		// .getXconfvalue())));
		// }
		if (null != of.getIntegration_price()) {
			bigDecimal = bigDecimal.subtract(of.getIntegration_price());// 订单总金额-农豆抵用总金额
		}
		bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN);// 取整
		Integer integer = null;
		try {
			integer = bigDecimal.intValue()
					* Integer.valueOf(xconf.getXconfvalue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return integer;
	}
}
