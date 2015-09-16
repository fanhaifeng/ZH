package com.lakecloud.foundation.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * 导入商品库信息
 * 导入的信息默认为不发布状态
 *
 */
public interface IImportService {
	List<Map<String, Object>> importAssort(Workbook wb);
}
