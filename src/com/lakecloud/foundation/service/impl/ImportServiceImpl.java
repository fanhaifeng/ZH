package com.lakecloud.foundation.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakecloud.core.tools.Formatter;
import com.lakecloud.foundation.dao.GoodsDCImportDAO;
import com.lakecloud.foundation.domain.GoodsBrand;
import com.lakecloud.foundation.domain.GoodsClass;
import com.lakecloud.foundation.domain.GoodsDC;
import com.lakecloud.foundation.domain.GoodsDCImport;
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.GoodsSpecification;
import com.lakecloud.foundation.domain.GoodsTypeProperty;
import com.lakecloud.foundation.service.IGoodsBrandService;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.IGoodsServiceDC;
import com.lakecloud.foundation.service.IImportService;

/**
 * 导入商品库信息 导入的信息默认为不发布状态
 * 
 */
@Service
@Transactional
public class ImportServiceImpl implements IImportService {
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsServiceDC goodsServiceDC;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private GoodsDCImportDAO goodsDCImportDAO;

	public List<Map<String, Object>> importAssort(Workbook wb) {
		org.apache.poi.ss.usermodel.Sheet sheet = null;
		for (int j = 0; j < wb.getNumberOfSheets(); j++) { // 获取每个Sheet表
			List<GoodsDCImport> list = new ArrayList<GoodsDCImport>();
			if (j > 0)// 测试:只读excel文件第一个表格
				break;
			sheet = wb.getSheetAt(j);
			int rowsize = sheet.getPhysicalNumberOfRows();
			Row hssfrow = null;
			Set<String> catentryTempSql = new HashSet<String>();
			for (int i = 2; i < rowsize; i++) {
				GoodsDCImport gdci = new GoodsDCImport();
				hssfrow = sheet.getRow(i); // 获取行
				if (Formatter.isBlankRow(hssfrow)) {
					break;
				}
				if(checkRow(hssfrow)){
					String gcfirst = hssfrow.getCell(0) == null ? "" : hssfrow
							.getCell(0).toString();
					String gcsecond = hssfrow.getCell(1) == null ? "" : hssfrow
							.getCell(1).toString();
					String gcthird = hssfrow.getCell(2) == null ? "" : hssfrow
							.getCell(2).toString();
					String goods_name = hssfrow.getCell(3) == null ? "" : hssfrow
							.getCell(3).toString();
					String goods_serial = hssfrow.getCell(4) == null ? "" : hssfrow
							.getCell(4).toString();
					String goods_weight = hssfrow.getCell(5) == null ? "" : hssfrow
							.getCell(5).toString();
					String goods_brand = hssfrow.getCell(6) == null ? "" : hssfrow
							.getCell(6).toString();
					String seo_keywords = hssfrow.getCell(7) == null ? "" : hssfrow
							.getCell(7).toString();
					String seo_description = hssfrow.getCell(8) == null ? ""
							: hssfrow.getCell(8).toString();
					String goods_clour = hssfrow.getCell(9) == null ? "" : hssfrow
							.getCell(9).toString();
					String goods_formula = hssfrow.getCell(10) == null ? ""
							: hssfrow.getCell(10).toString();
					String goods_bag_weight = hssfrow.getCell(11) == null ? ""
							: hssfrow.getCell(11).toString();
					StringBuffer sql = new StringBuffer();
					sql.append("insert into LAKECLOUD_GOODS_DC_IMPORT(Gcfirst,Gcsecond,Gcthird,Goods_name,Goods_serial,Goods_weight,Goods_brand,Seo_keywords,Seo_description,Goods_clour,Goods_formula,Goods_bag_weight,DELETESTATUS)  values('");
					sql.append(gcfirst + "','" + gcsecond + "','" + gcthird + "','"
							+ goods_name + "','" + goods_serial + "','"
							+ goods_weight + "','" + goods_brand + "','"
							+ seo_keywords + "','" + seo_description + "','"
							+ goods_clour + "','" + goods_formula + "','"
							+ goods_bag_weight + "','" + 0 + "')");
					catentryTempSql.add(sql.toString());
				}
				/*
				 * gdci.setGcfirst(hssfrow.getCell(0)==null?"":hssfrow.getCell(0)
				 * .toString());
				 * gdci.setGcsecond(hssfrow.getCell(1)==null?"":hssfrow
				 * .getCell(1).toString());
				 * gdci.setGcthird(hssfrow.getCell(2)==null
				 * ?"":hssfrow.getCell(2).toString());
				 * gdci.setGoods_name(hssfrow
				 * .getCell(3)==null?"":hssfrow.getCell(3).toString());
				 * gdci.setGoods_serial
				 * (hssfrow.getCell(4)==null?"":hssfrow.getCell(4).toString());
				 * gdci
				 * .setGoods_weight(hssfrow.getCell(5)==null?"":hssfrow.getCell
				 * (5).toString());
				 * gdci.setGoods_brand(hssfrow.getCell(6)==null?
				 * "":hssfrow.getCell(6).toString());
				 * gdci.setSeo_keywords(hssfrow
				 * .getCell(7)==null?"":hssfrow.getCell(7).toString());
				 * gdci.setSeo_description
				 * (hssfrow.getCell(8)==null?"":hssfrow.getCell(8).toString());
				 * gdci
				 * .setGoods_clour(hssfrow.getCell(9)==null?"":hssfrow.getCell
				 * (9).toString());
				 * gdci.setGoods_formula(hssfrow.getCell(10)==null
				 * ?"":hssfrow.getCell(10).toString());
				 * gdci.setGoods_bag_weight(
				 * hssfrow.getCell(11)==null?"":hssfrow.getCell(11).toString());
				 * list.add(gdci);
				 */
			}
			this.batckInsertTemp(catentryTempSql);

		}
		return null;
	}

	/**
	 * @param hssfrow
	 * @return 校验一行数据是否合法
	 */
	private boolean checkRow(Row hssfrow) {
		String cell0 = hssfrow.getCell(0) == null ? "" : hssfrow.getCell(0)
				.toString().trim();
		if (cell0.equals(""))
			return false;
		String cell1 = hssfrow.getCell(1) == null ? "" : hssfrow.getCell(1)
				.toString().trim();
		if (cell1.equals(""))
			return false;
		String cell2 = hssfrow.getCell(2) == null ? "" : hssfrow.getCell(2)
				.toString().trim();
		if (cell2.equals(""))
			return false;
		// 商品名称
		String cell3 = hssfrow.getCell(3) == null ? "" : hssfrow.getCell(3)
				.toString().trim();
		if (cell3.equals(""))
			return false;
		// 商品货号处理
		String cell4 = hssfrow.getCell(4) == null ? "" : hssfrow.getCell(4)
				.toString().trim();
		if (cell4.equals(""))
			return false;
		// 商品重量处理
		String cell5 = hssfrow.getCell(5) == null ? "" : hssfrow.getCell(5)
				.toString().trim();
		if (cell5 != null && !cell5.equals("")) {
			try {
				@SuppressWarnings("unused")
				Double weigth = Double.valueOf(cell5);
				if (cell5.indexOf("-") == 0)
					return false;
			} catch (Exception e) {
				return false;
			}
		}	
		return true;
	}

	/**
	 * 批处理商品原始数据
	 */
	public void batckInsertTemp(Set catentryTempSql) {
		List skuTableSqlnew = new ArrayList(catentryTempSql);
		String[] sqlMainArray = new String[skuTableSqlnew.size()];
		for (int i = 0, j = skuTableSqlnew.size(); i < j; i++) {
			sqlMainArray[i] = (String) skuTableSqlnew.get(i);
		}
		System.out.println("batchstart:");
		goodsDCImportDAO.batchInsert(sqlMainArray);
		System.out.println("batchend:");
	}

	/**
	 * @param wb
	 * @return 导入商品数据代码验证
	 */
	public List<Map<String, Object>> importAssort2(Workbook wb) {
		List<Map<String, Object>> errorList = new ArrayList<Map<String, Object>>();
		Map<String, Object> errorMap = null;
		org.apache.poi.ss.usermodel.Sheet sheet = null;
		for (int j = 0; j < wb.getNumberOfSheets(); j++) { // 获取每个Sheet表
			if (j > 0)// 测试只读excel文件第一个表格
				break;
			sheet = wb.getSheetAt(j);
			int rowsize = sheet.getPhysicalNumberOfRows();
			Row hssfrow = null;
			// 获取表格列名
			Row titlerow = sheet.getRow(1);
			short record = titlerow.getLastCellNum();
			String[] titles = new String[record];
			for (int i = 0; i < record; i++) {
				titles[i] = titlerow.getCell(i).toString();
			}
			// 读取表格数据
			for (int i = 2; i < rowsize; i++) {
				int rowId = i + 1;
				GoodsDC gdc = new GoodsDC();
				errorMap = new HashMap<String, Object>();
				hssfrow = sheet.getRow(i); // 获取行
				if (Formatter.isBlankRow(hssfrow)) {
					break;
				}
				// 商品大分类处理
				List<GoodsClass> gcList0 = null;
				String cell0 = hssfrow.getCell(0) == null ? "" : hssfrow
						.getCell(0).toString();
				if (cell0 != null && !cell0.equals("")) {
					Map<String, Object> parament0 = new HashMap<String, Object>();
					parament0.put("className", cell0);
					parament0.put("level", 0);
					gcList0 = goodsClassService
							.query("from GoodsClass obj where obj.className=:className and obj.level=:level",
									parament0, -1, -1);
					if (gcList0.size() != 1) {
						errorMap.put("error", "第" + rowId + "行" + titles[0]
								+ "{" + cell0 + "}不存在");
						errorList.add(errorMap);
						continue;
					}
				} else {
					errorMap.put("error", "第" + rowId + "行" + titles[0] + "{"
							+ cell0 + "}不能为空");
					errorList.add(errorMap);
					continue;
				}
				// 商品中分类处理
				List<GoodsClass> gcList1 = null;
				String cell1 = hssfrow.getCell(1) == null ? "" : hssfrow
						.getCell(1).toString();
				if (cell1 != null && !cell1.equals("")) {
					Map<String, Object> parament1 = new HashMap<String, Object>();
					parament1.put("className", cell1);
					parament1.put("level", 1);
					parament1.put("pid", gcList0.get(0).getId());
					gcList1 = goodsClassService
							.query("from GoodsClass obj where obj.className=:className and obj.level=:level and obj.parent.id=:pid",
									parament1, -1, -1);
					if (gcList1.size() != 1) {
						errorMap.put("error", "第" + rowId + "行" + titles[1]
								+ "{" + cell1 + "}不存在");
						errorList.add(errorMap);
						continue;
					}
				} else {
					errorMap.put("error", "第" + rowId + "行" + titles[1] + "{"
							+ cell1 + "}不能为空");
					errorList.add(errorMap);
					continue;
				}
				// 商品小分类处理
				String cell2 = hssfrow.getCell(2) == null ? "" : hssfrow
						.getCell(2).toString();
				List<GoodsClass> gcList2 = null;
				if (cell2 != null && !cell2.equals("")) {
					Map<String, Object> parament2 = new HashMap<String, Object>();
					parament2.put("className", cell2);
					parament2.put("level", 2);
					parament2.put("pid", gcList1.get(0).getId());
					gcList2 = goodsClassService
							.query("from GoodsClass obj where obj.className=:className and obj.level=:level and obj.parent.id=:pid",
									parament2, -1, -1);
					if (gcList2.size() != 1) {
						errorMap.put("error", "第" + rowId + "行" + titles[2]
								+ "{" + cell2 + "}不存在");
						errorList.add(errorMap);
						continue;
					}
				} else {
					errorMap.put("error", "第" + rowId + "行" + titles[2] + "{"
							+ cell2 + "}不能为空");
					errorList.add(errorMap);
					continue;
				}
				// 商品名称处理
				String cell3 = hssfrow.getCell(3) == null ? "" : hssfrow
						.getCell(3).toString();
				if (cell3 != null && !cell3.equals("")) {
					if (cell3.length() <= 50 && cell3.length() >= 3)
						gdc.setGoods_name(cell3);
					else {
						errorMap.put("error", "第" + rowId + "行" + titles[3]
								+ "{" + cell3 + "}字符长度必须大于3小于50");
						errorList.add(errorMap);
						continue;
					}
				} else {
					errorMap.put("error", "第" + rowId + "行" + titles[3] + "{"
							+ cell3 + "}不能为空");
					errorList.add(errorMap);
					continue;
				}
				// 商品货号处理
				String cell4 = hssfrow.getCell(4) == null ? "" : hssfrow
						.getCell(4).toString().trim();
				if (cell4 != null && !cell4.equals("")) {
					if (cell4.length() <= 20) {
						GoodsDC dc = goodsServiceDC.getObjByProperty(
								"goods_serial", cell4);
						if (dc != null) {
							errorMap.put("error", "第" + rowId + "行" + titles[4]
									+ "{" + cell4 + "}货号不能重复");
							errorList.add(errorMap);
							continue;
						} else
							gdc.setGoods_serial(cell4);
					} else {
						errorMap.put("error", "第" + rowId + "行" + titles[4]
								+ "{" + cell4 + "}字符长度必须小于20");
						errorList.add(errorMap);
						continue;
					}
				} else {
					errorMap.put("error", "第" + rowId + "行" + titles[4] + "{"
							+ cell4 + "}不能为空");
					errorList.add(errorMap);
					continue;
				}
				// 商品重量处理
				String cell5 = hssfrow.getCell(5) == null ? "" : hssfrow
						.getCell(5).toString().trim();
				if (cell5 != null && !cell5.equals("")) {
					try {
						Double weigth = Double.valueOf(cell5);
						if (cell5.indexOf("-") == 0) {
							errorMap.put("error", "第" + rowId + "行" + titles[5]
									+ "{" + cell5 + "}必须为正书");
							errorList.add(errorMap);
						} else
							gdc.setGoods_weight(new BigDecimal(weigth));
					} catch (Exception e) {
						errorMap.put("error", "第" + rowId + "行" + titles[5]
								+ "{" + cell5 + "}必须为数字");
						errorList.add(errorMap);
					}
				} else {
					errorMap.put("error", "第" + rowId + "行" + titles[5] + "{"
							+ cell5 + "}字符长度必须小于20");
					errorList.add(errorMap);
					continue;
				}
				// 商品品牌处理
				String cell6 = hssfrow.getCell(6) == null ? "" : hssfrow
						.getCell(6).toString();
				if (cell6 != null && !cell6.equals("")) {
					try {
						GoodsBrand brand = goodsBrandService.getBy("name",
								cell6);
						if (brand == null)
							gdc.setGoods_brand(brand);
						else {
							errorMap.put("error", "第" + rowId + "行" + titles[6]
									+ "{" + cell6 + "}品牌不存在");
							errorList.add(errorMap);
						}
					} catch (Exception e) {
						errorMap.put("error", "第" + rowId + "行" + titles[6]
								+ "{" + cell6 + "}存在多个相同品牌");
						errorList.add(errorMap);
					}
				}
				// 商品seo关键字处理
				String cell7 = hssfrow.getCell(7) == null ? "" : hssfrow
						.getCell(7).toString();
				if (cell7 != null && !cell7.equals("")) {
					gdc.setSeo_keywords(cell7);
				}
				// 商品seo描述处理
				String cell8 = hssfrow.getCell(8) == null ? "" : hssfrow
						.getCell(8).toString();
				if (cell8 != null && !cell8.equals("")) {
					gdc.setSeo_description(cell8);
				}
				// 获取规格
				gdc.setGc(gcList2.get(0));
				List<GoodsSpecProperty> goodsDC_specs = new ArrayList<GoodsSpecProperty>();
				List<GoodsSpecification> gss = gcList2.get(0).getGoodsType()
						.getGss();
				Map<String, Map<String, GoodsSpecProperty>> map = new HashMap<String, Map<String, GoodsSpecProperty>>();
				// map.put("颜色", new HashMap<String, GoodsSpecProperty>());
				map.put("袋重", new HashMap<String, GoodsSpecProperty>());
				// map.put("配比", new HashMap<String, GoodsSpecProperty>());
				for (GoodsSpecification goodsSpecification : gss) {
					if (map.containsKey(goodsSpecification.getName())) {
						List<GoodsSpecProperty> gp = goodsSpecification
								.getProperties();
						for (GoodsSpecProperty goodsSpecProperty : gp) {
							map.get(goodsSpecification.getName()).put(
									goodsSpecProperty.getValue(),
									goodsSpecProperty);
						}
					}
				}
				// 获取属性
				List<Map<String, Object>> goodsProperties = new ArrayList<Map<String, Object>>();
				List<GoodsTypeProperty> gp = gcList2.get(0).getGoodsType()
						.getProperties();
				Map<String, Map<String, GoodsTypeProperty>> map2 = new HashMap<String, Map<String, GoodsTypeProperty>>();
				map2.put("颜色", new HashMap<String, GoodsTypeProperty>());
				map2.put("配比", new HashMap<String, GoodsTypeProperty>());
				for (GoodsTypeProperty goodsTypeProperty : gp) {
					if (map2.containsKey(goodsTypeProperty.getName())) {
						String[] value = goodsTypeProperty.getValue()
								.split(",");
						for (String string : value)
							map2.get(goodsTypeProperty.getName()).put(string,
									goodsTypeProperty);
					}
				}
				// 商品属性颜色处理
				String cell9 = hssfrow.getCell(9) == null ? "" : hssfrow
						.getCell(9).toString();
				if (cell9 != null && !cell9.equals("")) {
					if (map2.containsKey("颜色")
							&& map2.get("颜色").containsKey(cell9)) {
						Map<String, Object> property = new HashMap<String, Object>();
						property.put("val", cell9);
						property.put("id", map2.get("颜色").get(cell9).getId());
						property.put("name", "颜色");
						goodsProperties.add(property);
					} else {
						errorMap.put("error", "第" + rowId + "行" + titles[9]
								+ "{" + cell9 + "}不存在该属性或属性值");
						errorList.add(errorMap);
					}
				}
				/**
				 * String cell4 = hssfrow.getCell(4).toString();
				 * if(cell4!=null&&!cell4.equals("")){
				 * if(map.containsKey("颜色")&&map.get("颜色").containsKey(cell4)){
				 * goodsDC_specs.add(map.get("颜色").get(cell4)); }else{
				 * errorMap.put("error", "第" + rowId + "行"+titles[4]+"{" + cell4
				 * + "}不存在该规格或规格值"); errorList.add(errorMap); } }
				 **/
				// 商品属性配比处理
				String cell10 = hssfrow.getCell(10) == null ? "" : hssfrow
						.getCell(10).toString();
				if (cell10 != null && !cell10.equals("")) {
					if (map2.containsKey("配比")
							&& map2.get("配比").containsKey(cell10)) {
						Map<String, Object> property = new HashMap<String, Object>();
						property.put("val", cell10);
						property.put("id", map2.get("配比").get(cell10).getId());
						property.put("name", "配比");
						goodsProperties.add(property);
					} else {
						errorMap.put("error", "第" + rowId + "行" + titles[10]
								+ "{" + cell10 + "}不存在该属性或属性值");
						errorList.add(errorMap);
					}
				}
				/**
				 * String cell6 = hssfrow.getCell(6).toString();
				 * if(cell6!=null&&!cell6.equals("")){
				 * if(map.containsKey("配比")&&map.get("配比").containsKey(cell6)){
				 * goodsDC_specs.add(map.get("配比").get(cell6)); }else{
				 * errorMap.put("error", "第" + rowId + "行"+titles[6]+"{" + cell6
				 * + "}不存在该规格或规格值"); errorList.add(errorMap); } }
				 **/
				// 商品规格袋重处理
				String cell11 = hssfrow.getCell(11) == null ? "" : hssfrow
						.getCell(11).toString();
				if (cell11 != null && !cell11.equals("")) {
					if (map.containsKey("袋重")
							&& map.get("袋重").containsKey(cell11)) {
						goodsDC_specs.add(map.get("袋重").get(cell11));
					} else {
						errorMap.put("error", "第" + rowId + "行" + titles[11]
								+ "{" + cell11 + "}不存在该规格或规格值");
						errorList.add(errorMap);
					}
				}
				gdc.setGoods_property(JSONArray.toJSONString(goodsProperties));
				gdc.setGoodsDC_specs(goodsDC_specs);
				try {
					goodsServiceDC.save(gdc);
				} catch (Exception e) {
					errorMap.put("error", "第" + rowId + "行数据保存异常!请填写合法数据。");
					errorList.add(errorMap);
				}
			}
		}
		return errorList;
	}
}
