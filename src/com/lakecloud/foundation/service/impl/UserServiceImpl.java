package com.lakecloud.foundation.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.lakecloud.core.tools.Md5Encrypt;
import com.lakecloud.foundation.domain.Album;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Audit;
import com.lakecloud.foundation.domain.Branch;
import com.lakecloud.foundation.domain.Crop;
import com.lakecloud.foundation.domain.IntegralLog;
import com.lakecloud.foundation.domain.Integration;
import com.lakecloud.foundation.domain.Integration_Child;
import com.lakecloud.foundation.domain.Integration_Log;
import com.lakecloud.foundation.domain.MobileVerifyCode;
import com.lakecloud.foundation.domain.Role;
import com.lakecloud.foundation.domain.SysConfig;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.XConf;
import com.lakecloud.foundation.service.IIntegrationService;
import com.lakecloud.foundation.service.IIntegration_ChildService;
import com.lakecloud.foundation.service.IIntegration_LogService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.foundation.service.IXConfService;

@Service
@Transactional
public class UserServiceImpl implements IUserService {
	@Resource(name = "userDAO")
	private IGenericDAO<User> userDAO;
	@Resource(name = "albumDAO")
	private IGenericDAO<Album> albumDAO;
	@Resource(name = "cropDAO")
	private IGenericDAO<Crop> cropDAO;
	@Resource(name = "areaDAO")
	private IGenericDAO<Area> areaDAO;
	@Resource(name = "branchDAO")
	private IGenericDAO<Branch> branchDAO;
	@Resource(name = "roleDAO")
	private IGenericDAO<Role> roleDAO;
	@Resource(name = "sysConfigDAO")
	private IGenericDAO<SysConfig> sysConfigDAO;
	@Resource(name = "integralLogDAO")
	private IGenericDAO<IntegralLog> integralLogDAO;
	@Autowired
	private IIntegrationService integrationService;
	@Autowired
	private IIntegration_ChildService integration_ChildService;
	@Autowired
	private IIntegration_LogService integration_LogService;
	@Autowired
	private IXConfService xConfService;
	
	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		try {
			this.userDAO.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public User getObjById(Long id) {
		// TODO Auto-generated method stub
		return this.userDAO.get(id);
	}

	public boolean save(User user) {
		// TODO Auto-generated method stub
		try {
			this.userDAO.save(user);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean update(User user) {
		// TODO Auto-generated method stub
		try {
			this.userDAO.update(user);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public List<User> query(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.userDAO.query(query, params, begin, max);

	}

	public IPageList list(IQueryObject properties) {
		// TODO Auto-generated method stub
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(User.class, query, params,
				this.userDAO);
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
	public User getObjByProperty(String propertyName, String value) {
		// TODO Auto-generated method stub
		return this.userDAO.getBy(propertyName, value);
	}
	
	
	public void register_finish(SysConfig sysConfig,String userName,String password,
			String telephone,Area a,Long branchId,String mobile_verify_code,
			String care,String plant,String cul_area,String promote_tel){
		
			User user = new User();
			user.setUserName(userName);
			user.setUserRole("BUYER");
			user.setBbc_Role("BUYER");
			user.setAddTime(new Date());
			user.setTelephone(telephone);
			
			Crop crop = new Crop();
			crop.setAddTime(new Date());
	
			crop.setCare(CommUtil.null2String(care));
			crop.setPlant(CommUtil.null2String(plant));
			crop.setCul_area(CommUtil.null2String(cul_area));
			this.cropDAO.save(crop);
			user.setCrop(crop);
			
			if(a!=null){
				user.setArea(a);
			}
			if(branchId!=Long.MIN_VALUE){
				Branch branch = this.branchDAO.get(branchId);
				System.out.println(branch.getName());
				
				if(branch!=null){
					Branch b = new Branch();
					b.setId(branchId);
					user.setBranch(b);
					user.setBranch_code(branch.getCode());
				}
			}
			
				
			user.setPassword(Md5Encrypt.md5(password).toLowerCase());
			Map params = new HashMap();
			params.put("type", "BUYER");
			List<Role> roles = this.roleDAO.query("select obj from Role obj where obj.type=:type", params,-1, -1);
			user.getRoles().addAll(roles);
			if (sysConfig.isIntegral()) {
				user.setIntegral(sysConfig.getMemberRegister());
				this.userDAO.save(user);
				IntegralLog log = new IntegralLog();
				log.setAddTime(new Date());
				log.setContent("用户注册增加"+ sysConfig.getMemberRegister()+ "分");
				log.setIntegral(sysConfig.getMemberRegister());
				log.setIntegral_user(user);
				log.setType("reg");
				this.integralLogDAO.save(log);
			} else {		
				this.userDAO.save(user);
			}
			// 创建用户默认相册
			Album album = new Album();
			album.setAddTime(new Date());
			album.setAlbum_default(true);
			album.setAlbum_name("默认相册");
			album.setAlbum_sequence(-10000);
			album.setUser(user);
			this.albumDAO.save(album);	
			//农豆
			integFunction(user,promote_tel);
	}
	
	public void integFunction(User user,String promote_tel){
		//新建用户 创建农豆主表
		
		//获取注册新用户新增的农豆
		Map param = new HashMap();
		param.put("confkey", "integration_from_register");
		//List<XConf> xConfList = this.xConfService.query(
		//		"select obj from xConf obj where obj.xconfkey=:confkey",
		//		param, -1, -1);
		XConf xConfList = this.xConfService.queryByXconfkey( "integration_from_register");
		String integral  =null;
		if(xConfList!=null){
			integral  = xConfList.getXconfvalue();
		}else{
			integral="0";
		}
		createIntegration(user,integral,1);
		
		//给推荐的用户增加农豆
		if(promote_tel!=null && isPhone(promote_tel)==true){
			//去用户表查是否存在该用户
			Map paramu = new HashMap();
			paramu.put("telephone", promote_tel);
			List<User> userList = this.query(
					"select obj from User obj where obj.telephone=:telephone",
					paramu, -1, -1);
			if(userList!=null && userList.size()==1){
				User puser = userList.get(0);
				//获得推荐用户可增加的农豆数
				XConf xcon = this.xConfService.queryByXconfkey( "integration_from_invite");
				String integralp  =null;
				if(xcon!=null){
					integralp  = xcon.getXconfvalue();
				}else{
					integralp="0";
				}
				//判断主表是否有该用户
				String queryString = "select obj from Integration obj where obj.user.id="+puser.getId();
				List<Integration> intList = this.integrationService.query(queryString, null, -1, -1);
				if( intList != null && intList.size() ==1 ){
					
					Integration getInte = intList.get(0);
					updateIntegration(puser,getInte,integralp,2);
					
				}else if(intList != null && intList.size() ==0){
					createIntegration(puser,integralp,2);
				}
			}
		}
	}
	
	//验证是否为手机号
	public static boolean isPhone(String phone) {

		Pattern phonePattern = Pattern.compile("^1\\d{10}$");

		Matcher matcher = phonePattern.matcher(phone);

		if(matcher.find()){

		return true;

		}

		return false;

		} 
	
	//创建新的农豆对象
	public void createIntegration(User user,String integral,int gettype){
		Integration intep = new Integration();//主表
		intep.setAddTime(new Date());
		intep.setUser(user);
		intep.setIntegrals(Integer.parseInt(integral));
		this.integrationService.save(intep);
		Integration_Child intechp = new Integration_Child();//子表
		intechp.setAddTime(new Date());
		intechp.setUser(user);
		intechp.setInteg(intep);
		intechp.setIntegrals(Integer.parseInt(integral));
		intechp.setType(0);
		this.integration_ChildService.save(intechp);
		Integration_Log intelogp = new Integration_Log();//log
		intelogp.setAddTime(new Date());
		intelogp.setUser(user);
		intelogp.setGettype(gettype);
		intelogp.setType(1);
		intelogp.setBe_integrals(0);
		intelogp.setAf_integrals(Integer.parseInt(integral));
		intelogp.setIntegrals(Integer.parseInt(integral));
		intelogp.setInteg(intep);
		this.integration_LogService.save(intelogp);
		
	}
	//推荐用户更新农豆
	public void updateIntegration(User user,Integration integration,String integral,int gettype){

		//去子表查看是否有这个用户
		String query = "select obj from Integration_Child obj where obj.integ.id="+integration.getId()+" and obj.type = 0";
		List<Integration_Child> intchList = this.integration_ChildService.query(query, null, -1, -1);
		
		if( intchList != null && intchList.size()==1 ){
			Integration_Child intech = intchList.get(0);
			
			//日志表
			
			Integration_Log intelog = new Integration_Log();
			intelog.setInteg(integration);
			intelog.setAddTime(new Date());
			intelog.setUser(user);//用户
			intelog.setBe_integrals(intech.getIntegrals()==null?0:intech.getIntegrals());//消费前的平台农豆
			intelog.setIntegrals(Integer.parseInt(integral));//农豆
			intelog.setGettype(gettype);//获取路径
			intelog.setType(1);//消费方式
			intelog.setAf_integrals((intech.getIntegrals()==null?0:intech.getIntegrals())+Integer.parseInt(integral));//消费后的平台农豆
			
			this.integration_LogService.save(intelog);
			
	
			//主表
			integration.setIntegrals(integration.getIntegrals()==null?0:(integration.getIntegrals()+Integer.parseInt(integral)));
			//子表
			intech.setIntegrals(intech.getIntegrals()==null?0:(intech.getIntegrals()+Integer.parseInt(integral)));
	
		this.integrationService.save(integration);
		this.integration_ChildService.save(intech);
	
		}
		
		
	}

}

