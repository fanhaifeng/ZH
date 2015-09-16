package com.lakecloud.foundation.service.impl;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.lakecloud.core.query.PageObject;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.dao.IGenericDAO;
import com.lakecloud.core.query.GenericPageList;
import com.lakecloud.foundation.domain.Charge;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Refund;
import com.lakecloud.foundation.service.IChargeService;

@Service
@Transactional
public class ChargeServiceImpl implements IChargeService{
	@Resource(name = "chargeDAO")
	private IGenericDAO<Charge> chargeDao;
	@Resource(name = "orderFormDAO")
	private IGenericDAO<OrderForm> orderFormDao;
	@Resource(name = "refundDAO")
	private IGenericDAO<Refund> refundDao;
	
	
	public void weixin_order_pay(String payType, String order_id,
			String chargeNum){
		
	}
	
	
	/**
	 * 赊销金额
	 * @param order_id
	 * @param needpay
	 * @return
	 */
	public OrderForm seller_confirm_refund(String order_id,String needpay) {
		OrderForm obj = this.orderFormDao.get(Long.parseLong(order_id));
		if(obj!=null){
			String query ="select obj from Charge obj where obj.store.id=:store_id and obj.user.id=:user_id";
			Map<String,Long> params = new HashMap<String,Long>();
			params.put("store_id", obj.getStore().getId());
			params.put("user_id", obj.getUser().getId());
			List<Charge>  chargeList = this.chargeDao.query(query, params, -1, -1);
			
			//该用户有赊销记录
			if( chargeList!=null && chargeList.size() > 0 ){
				//修改订单已经还款金额
				//已还金额 要小于等于赊销金额 才记录
				if(obj.getAlreadyPay().add(new BigDecimal(needpay)).compareTo(obj.getCharge_Num()) <= 0 ){
					obj.setAlreadyPay(obj.getAlreadyPay().add(new BigDecimal(needpay)));
					//修改charge可用赊销金额
					Charge charge = chargeList.get(0);
					charge.setUsedPayNum(charge.getUsedPayNum().subtract(new BigDecimal(needpay)));
					
					//记录还款记录
					Refund refund = new Refund();
					refund.setAddTime(new Date());
					refund.setNeedPay(new BigDecimal(needpay));
					refund.setOf(obj);
					this.orderFormDao.update(obj);
					this.chargeDao.update(charge);
					this.refundDao.save(refund);
				}	
			}
		}
		return obj;
	}
	
	
	public boolean save(Charge charge) {
		/**
		 * init other field here
		 */
		try {
			
			this.chargeDao.save(charge);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Charge getObjById(Long id) {
		Charge charge = this.chargeDao.get(id);
		if (charge != null) {
			return charge;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.chargeDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> chargeIds) {
		// TODO Auto-generated method stub
		for (Serializable id : chargeIds) {
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
		GenericPageList pList = new GenericPageList(Charge.class, query,
				params, this.chargeDao);
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
	
	public boolean update(Charge charge) {
		try {
			this.chargeDao.update( charge);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<Charge> query(String query, Map params, int begin, int max){
		return this.chargeDao.query(query, params, begin, max);
		
	}
}
