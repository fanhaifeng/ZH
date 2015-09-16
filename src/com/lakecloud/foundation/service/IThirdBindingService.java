package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.ThirdBinding;

public interface IThirdBindingService {
	/**
	 * 保存一个ThirdBinding，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(ThirdBinding instance);

	/**
	 * 根据一个ID得到ThirdBinding
	 * 
	 * @param id
	 * @return
	 */
	ThirdBinding getObjById(Long id);

	/**
	 * 删除一个ThirdBinding
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除ThirdBinding
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到ThirdBinding
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个ThirdBinding
	 * 
	 * @param id
	 *            需要更新的ThirdBinding的id
	 * @param dir
	 *            需要更新的ThirdBinding
	 */
	boolean update(ThirdBinding instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<ThirdBinding> query(String query, Map params, int begin, int max);

	List<ThirdBinding> queryByCreateUser();
}
