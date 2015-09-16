package com.lakecloud.foundation.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Integration;
import com.lakecloud.foundation.domain.SysConfig;
import com.lakecloud.foundation.domain.User;

public interface IUserService {
	/**
	 * 
	 * @param user
	 * @return
	 */
	boolean save(User user);

	/**
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 
	 * @param user
	 * @return
	 */
	boolean update(User user);

	/**
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 
	 * @param id
	 * @return
	 */
	User getObjById(Long id);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	User getObjByProperty(String propertyName, String value);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<User> query(String query, Map params, int begin, int max);
	
	public void register_finish(SysConfig sysConfig,String userName,String password,
			String telephone,Area a,Long branchId,String mobile_verify_code,
			String care,String plant,String cul_area,String promote_tel);
	
	void createIntegration(User user,String integral,int gettype);
	
	void updateIntegration(User user,Integration integration,String integral,int gettype);
	
	void integFunction(User user,String promote_tel);
}
