package com.lakecloud.foundation.service;

import com.lakecloud.foundation.domain.SysConfig;

public interface ISysConfigService {
	/**
	 * 
	 * @param shopConfig
	 * @return
	 */
	boolean save(SysConfig shopConfig);

	/**
	 * 
	 * @param shopConfig
	 * @return
	 */
	boolean delete(SysConfig shopConfig);

	/**
	 * 
	 * @param shopConfig
	 * @return
	 */
	boolean update(SysConfig shopConfig);

	/**
	 * 
	 * @return
	 */
	SysConfig getSysConfig();
}
