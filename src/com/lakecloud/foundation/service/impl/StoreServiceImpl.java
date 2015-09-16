package com.lakecloud.foundation.service.impl;
import java.io.IOException;
import java.io.PrintWriter;
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

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakecloud.core.dao.IGenericDAO;
import com.lakecloud.core.query.GenericPageList;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Audit;
import com.lakecloud.foundation.domain.Branch;
import com.lakecloud.foundation.domain.Crop;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Refund;
import com.lakecloud.foundation.domain.Role;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreGrade;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.IUserService;

@Service
@Transactional
public class StoreServiceImpl implements IStoreService{
	@Resource(name = "storeDAO")
	private IGenericDAO<Store> storeDao;
	@Resource(name = "orderFormDAO")
	private IGenericDAO<OrderForm> orderFormDao;
	@Resource(name = "auditDAO")
	private IGenericDAO<Audit> auditDao;
	@Resource(name = "userDAO")
	private IGenericDAO<User> userDao;
	@Resource(name = "cropDAO")
	private IGenericDAO<Crop> cropDao;
	@Resource(name = "areaDAO")
	private IGenericDAO<Area> areaDao;
	@Resource(name = "branchDAO")
	private IGenericDAO<Branch> branchDao;
	@Resource(name = "roleDAO")
	private IGenericDAO<Role> roleDao;
	@Autowired
	private IUserService userService;
	
	
	public boolean save(Store store) {
		/**
		 * init other field here
		 */
		try {
			this.storeDao.save(store);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Store getObjById(Long id) {
		Store store = this.storeDao.get(id);
		if (store != null) {
			return store;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.storeDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> storeIds) {
		// TODO Auto-generated method stub
		for (Serializable id : storeIds) {
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
		GenericPageList pList = new GenericPageList(Store.class, query,
				params, this.storeDao);
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
	
	public boolean update(Store store) {
		try {
			this.storeDao.update( store);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<Store> query(String query, Map params, int begin, int max){
		return this.storeDao.query(query, params, begin, max);
		
	}

	@Override
	public Store getObjByProperty(String propertyName, Object value) {
		// TODO Auto-generated method stub
		return this.storeDao.getBy(propertyName, value);
	}
	
	
	public JSONObject save_store_user(String mulitId,String flag,String option){
		String[] ids = mulitId.split(",");
		JSONObject jsonObj = new JSONObject();
		for (String id : ids) {
			if (!id.equals("")) {
				Audit audit = this.auditDao.get(Long.parseLong(id));
				audit.setOption(option);
				if(flag.equals("1")){
					boolean reg = true;
					Map params1 = new HashMap();
					params1.put("userName", audit.getUserName());
					List<User> users = this.userDao.query(
							"select obj from User obj where obj.userName=:userName",
							params1, -1, -1);
					if (users != null && users.size() > 0) {
						reg = false;
					}					
					if(reg){
						User user = new User();
						user.setAddTime(new Date());
						user.setUserName(audit.getUserName());
						user.setPassword(audit.getPassword());
						user.setTelephone(audit.getTelephone());
						user.setArea(audit.getArea());
						user.setTrueName(audit.getTrueName());
						
						Crop crop = new Crop();					
						crop.setCare(audit.getCare()==null ? "" : CommUtil.null2String(audit.getCare()));
						crop.setPlant(audit.getPlant() == null ? "" : CommUtil.null2String(audit.getPlant()));
						crop.setCul_area(audit.getCul_area() == null ? "" : audit.getCul_area());
						
						crop.setAddTime(new Date());
						this.cropDao.save(crop);
						user.setCrop(crop);
						Store store = new Store();
						if (store.isStore_recommend()) {
							store.setStore_recommend_time(new Date());
						}else{
							store.setStore_recommend_time(null);
						}
						//店铺等级
						StoreGrade grade = new StoreGrade();
						grade.setId(new Long(1));
						store.setGrade(grade);
						
						store.setAddTime(new Date());
						store.setTemplate("default");
						store.setStore_status(2);// 直接通过,通过后直接生成storepoint，防止定时器执行时产生多个对应一个store的storepoint
						store.setStore_ower(audit.getTrueName());
						store.setStore_name(audit.getStore_name());
						store.setSc(audit.getStore_class());	
						store.setWeixin_status(1);
						store.setArea(audit.getStore_area());
						store.setStore_address(audit.getStore_addr());
						store.setStore_ower_card(audit.getId_card());
						store.setStore_second_domain("shop"+id
										.toString());
						store.setBranch_code(audit.getBranch_code());
						user.setStore(store);	
						user.setUserRole("BUYER_SELLER");
						user.setBbc_Role("BUYER_SELLER");
						
						//用户分公司
						Area area1 = this.areaDao.get(audit.getArea().getId());
						if(area1!=null){
							if(area1.getBranch() != null){
								Branch branch = this.branchDao.get(area1.getBranch().getId());
								if(branch !=null){
									user.setBranch(branch);
									user.setBranch_code(branch.getCode());
								}
							}
						}

						// 给用户赋予卖家权限
						Map paramsList = new HashMap();
						paramsList.put("type","SELLER");
						paramsList.put("type2","BUYER");
						List<Role> roles = this.roleDao.query(
								"select obj from Role obj where obj.type=:type or obj.type=:type2",
								paramsList, -1, -1);
						
						user.getRoles().addAll(roles);
						
						// 重新加载用户权限
						/*Authentication authentication = new UsernamePasswordAuthenticationToken(
								SecurityContextHolder.getContext().getAuthentication()
										.getPrincipal(), SecurityContextHolder
										.getContext().getAuthentication()
										.getCredentials(),
								user.get_common_Authorities());
						SecurityContextHolder.getContext().setAuthentication(
								authentication);*/
						
						this.storeDao.save(store);
						this.userDao.save(user);
						//农豆
						this.userService.integFunction(user, audit.getMobile());
					}
					
					
					
					audit.setStatus("1");//审核通过
					this.auditDao.save(audit);
					jsonObj.put("resultData","该用户审核通过");		
				}else{
					audit.setStatus("2");//审核失败
					jsonObj.put("resultData","该用户经审核不通过");
					this.auditDao.save(audit);
				}
				
				
			}
		}
		return jsonObj;
	}
}
