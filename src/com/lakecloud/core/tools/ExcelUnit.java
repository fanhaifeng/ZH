package com.lakecloud.core.tools;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.record.formula.functions.T;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.lakecloud.foundation.domain.ExportAddressInfo;
import com.lakecloud.pay.alipay.util.httpClient.HttpResponse;
import com.sun.mail.iap.Response;
import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * 
* <p>Title: ExcelUnit.java</p>

* <p>Description: 该工具类用来操作excel、word、ppt等,目前仅支持excel的导入导出</p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
 */
public class ExcelUnit {
	private static String arg0;


	public static void exportExcel(String title, String[] headers,
			Collection<T> dataset, OutputStream out, String pattern) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(title);
		// 设置表格默认宽度为15个字节
		sheet.setDefaultColumnWidth(15);
		// 生成一个样式
		HSSFCellStyle style = workbook.createCellStyle();
		// 设置这些样式
		style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);

		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		// 生成一个字体
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.VIOLET.index);
		font.setFontHeightInPoints((short) 12);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		// 把字体应用到当前的样式
		style.setFont(font);

		// 生成并设置另一个样式
		HSSFCellStyle style_ = workbook.createCellStyle();
		style_.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		style_.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style_.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style_.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style_.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style_.setBorderTop(HSSFCellStyle.BORDER_THIN);

		style_.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style_.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

		// 生成另一个字体
		HSSFFont font_ = workbook.createFont();
		// font.setColor(color);
		// font.setFontHeightInPoints(height);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		style_.setFont(font_);

		// 生成一个画图的顶级管理器
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		// 定义注释的大小和位置
		HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0,
				0, 0, 0, (short) 4, 2, (short) 6, 5));
		// 设置注释内容
		comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
		// 设置注释作者，当鼠标移到单元格上可以在状态栏中看到该内容
		comment.setAuthor("erikchang");

		// 产生表格标题行
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {

			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(new HSSFRichTextString(headers[i]));
		}
		// 遍历集合数据，产生数据行
		Iterator<T> it = dataset.iterator();
		int index = 0;
		while (it.hasNext()) {
			index++;
			row = sheet.createRow(index);
			T t = (T) it.next();
			// 利用反射，根据javabean属性的先后顺序，动态调用getXxx（）方法得到属性值
			Field[] fields = t.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				HSSFCell cell = row.createCell(i);
				cell.setCellStyle(style_);
				Field field = fields[i];
				String fieldName = field.getName();
				String getMethodName = "get"
						+ fieldName.substring(0, 1).toUpperCase()
						+ fieldName.substring(1);
				try {
					Class tCls = t.getClass();
					Method getMethod = tCls.getMethod(getMethodName,
							new Class[] {});
					Object value = getMethod.invoke(t, new Object[] {});
					// 判断值的类型后进行强制类型转换
					String textValue = null;
					if (value instanceof Boolean) {
						boolean bValue = (Boolean) value;
						textValue = "男";
						if (!bValue) {
							textValue = "女";
						}
					} else if (value instanceof Date) {
						Date date = (Date) value;
						SimpleDateFormat sdf = new SimpleDateFormat(pattern);
						textValue = sdf.format(date);

					} else if (value instanceof byte[]) {
						// 有图片时，设置行高为60px
						row.setHeightInPoints(60);
						// 设置图片所在列宽度为80px，注意这里单位的一个
						sheet.setColumnWidth(i, (short) (35.7 * 80));
						byte[] bsValue = (byte[]) value;
						HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,
								1023, 255, (short) 6, index, (short) 6, index);
						anchor.setAnchorType(2);
						patriarch.createPicture(anchor, workbook.addPicture(
								bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));

					} else {
						// 其他数据类型都当作字符串简单处理
						textValue = value.toString();
					}
					// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
					if (textValue != null) {
						Pattern p = Pattern.compile("^\\d+(\\.\\d+)?$");
						Matcher matcher = p.matcher(textValue);
						if (matcher.matches()) {
							// 是数字当作double处理
							cell.setCellValue(Double.parseDouble(textValue));

						} else {
							HSSFRichTextString richString = new HSSFRichTextString(
									textValue);
							HSSFFont _font = workbook.createFont();
							_font.setColor(HSSFColor.BLUE.index);
							richString.applyFont(_font);
							cell.setCellValue(richString);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		try {
			workbook.write(out);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void exportTable2Excel(String title, String[] headers,
			Collection dataset, OutputStream out, String pattern) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(title);
		// 设置表格默认宽度为15个字节
		sheet.setDefaultColumnWidth(20);
		// 生成一个样式
		HSSFCellStyle style = workbook.createCellStyle();
		// 设置这些样式
		style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);

		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		// 生成一个字体
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.VIOLET.index);
		font.setFontHeightInPoints((short) 14);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		// 把字体应用到当前的样式
		style.setFont(font);

		// 生成并设置另一个样式
		HSSFCellStyle style_ = workbook.createCellStyle();
		style_.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		style_.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style_.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style_.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style_.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style_.setBorderTop(HSSFCellStyle.BORDER_THIN);

		style_.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style_.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

		// 生成另一个字体
		HSSFFont font_ = workbook.createFont();
		// font.setColor(color);
		font_.setFontHeightInPoints((short) 14);
		// font_.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		style_.setFont(font_);

		// 产生表格标题行
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {

			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(new HSSFRichTextString(headers[i]));
		}
		// 遍历集合数据，产生数据行
		// Iterator it = dataset.iterator();
		int index = 0;
		for (Object obj : dataset) {
			index++;
			row = sheet.createRow(index);
			Object[] list = (Object[]) obj;
			int rowIndex = 0;
			for (Object o : list) {
				if (o != null) {
					HSSFCell cell = row.createCell(rowIndex);
					cell.setCellStyle(style_);
					cell.setCellValue(o.toString());
					rowIndex++;
				}
			}
		}
		try {
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//导出报表
	public static void exportPerspective( Workbook wb,String[] exportNamesArray,List<ExportAddressInfo> exportModelsArray) 
					throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {	    
		Sheet  sheet = wb.createSheet("订单收货地址信息");
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 16);
        
        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle cellStyle1 = wb.createCellStyle();
        cellStyle1.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));

        CellStyle cellStyle2 = wb.createCellStyle();
        DataFormat format = wb.createDataFormat();
        cellStyle2.setDataFormat(format.getFormat("@"));
        sheet.setColumnWidth(1, 22 * 2 * 256); // 设置列的宽度
     // 产生表格标题行
		Row row = sheet.createRow(0);
		for (int i = 0; i < exportNamesArray.length; i++) {

			Cell cell = row.createCell(i);
			cell.setCellStyle(cellStyle2);
			cell.setCellValue(new HSSFRichTextString(exportNamesArray[i]));
		}
		Cell c = null;
        Row r = null;
        int rowNum;
		ExportAddressInfo no = new ExportAddressInfo();
		//判断是否有数据，无数据 则导出空的excel
		if(exportModelsArray.size()<=1){
			//行
            r = sheet.createRow((short)1);
            //列（单元格）
            for (short cellnum = (short) 0; cellnum < 4; cellnum++)
            {
            	 c = r.createCell(cellnum);
                 c.setCellStyle(cellStyle2);
                 c.setCellValue(String.valueOf(" "));
            }
               
          
		}else{
        for (rowNum = (short) 1; rowNum < exportModelsArray.size()+1; rowNum++)
        {
        	//行
            r = sheet.createRow((short)rowNum);
            no = exportModelsArray.get((short)rowNum-1);
            Field[] fields = no.getClass().getDeclaredFields();
            //列（单元格）
            for (short cellnum = (short) 0; cellnum < 4; cellnum++)
            {
                c = r.createCell(cellnum);
                c.setCellStyle(cellStyle2);  
                Field field = fields[cellnum];
				String fieldName = field.getName();
                String methodNameString = fieldName.substring(0, 1).toUpperCase()+ fieldName.substring(1);
                c.setCellValue(String.valueOf(no.getClass().getMethod("get"+methodNameString).invoke(no, null)));
            }
        }       
	}
	}


}
