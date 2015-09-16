package com.lakecloud.core.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具箱
 * 处理字符串、分隔符等相关方法
 * 
 * @author CPF
 */
public class Formatter
{
    /**
     * 定义日志输出器
     */
    private static final Logger log = LoggerFactory.getLogger(Formatter.class);

    /**
     * 转换XML中的<与>号为代码标记
     * 
     * @param desc
     * @return
     */
    public static String removeXMLKeywords(String desc)
    {
        String xml = "";
        if (desc != null)
        {
            xml = desc.replace("<", "&lt;").replace(">", "&gt;");
        }

        return xml;
    }

    /**
     * 将参数全部转换为小写
     * 
     * @param arg 要转换的参数
     * @return 小写字符
     */
    public static String toLowerCase(String arg)
    {
        String str = "";
        if (arg != null)
        {
            log.debug("未传入转换为小写的字符！");
            str = arg.toLowerCase();
        }

        return str;
    }

    /**
     * 将参数全部转换为小写
     * 
     * @param arg 要转换的参数
     * @return 小写字符
     */
    public static String toUpperCase(String arg)
    {
        String str = "";
        if (arg != null)
        {
            log.debug("未传入转换为大写的字符！");
            str = arg.toLowerCase();
        }

        return str;
    }

    /**
     * 将对象转换为字符串
     * 
     * @param obj 需要转换的对象
     * @return 字符串
     */
    public static String toString(Object obj)
    {
        return obj == null ? "" : obj.toString();
    }

    /**
     * 将对象转换为整形
     * 
     * @param obj 需要转换的对象
     * @return 字符串
     */
    public static Integer toInteger(Object obj)
    {
        try
        {
            return Integer.parseInt(obj.toString());
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    /**
     *  字符串拼接for in语句
     * @param values
     * @param type
     * @return
     */
    public static String arrayToString(String[] dataArray, String type)
    {
        String items = "";
        if ("string".equals(type))
        {
            for (int i = 0; i < dataArray.length; i++)
            {
                String item = dataArray[i];
                items = "".equals(items) ? "'"+item+"'" : items+",'"+item+"'";
            }
        }
        else if ("int".equals(type))
        {
            for (int i = 0; i < dataArray.length; i++)
            {
                String item = dataArray[i];
                items = "".equals(items) ? item : items+","+item;
            }
        }
        return items;
    }
    
    public static boolean isBlankRow(Row row)
    {
        if (row == null)
        {
            return true; 
        }
        boolean result = true;
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++)
        {
            Cell cell = row.getCell(i, Row.RETURN_BLANK_AS_NULL);
            String value = "";
            if (cell != null)
            {
                switch (cell.getCellType())
                {
                case Cell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    value = String.valueOf((int) cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    value = String.valueOf(cell.getCellFormula());
                    break;
                // case Cell.CELL_TYPE_BLANK:
                // break;
                default:
                    break;
                }

                if (!value.trim().equals(""))
                {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
    
    public static String isStringTypeForSize(Cell cell)
    {
        String value = "";
        if (cell != null)
        {
            value = cell.toString().trim();
            if (Cell.CELL_TYPE_NUMERIC == cell.getCellType())
            {
                if (HSSFDateUtil.isCellDateFormatted(cell))
                {
                    // value =
                    // Formatter.stringFromDate(cell.getDateCellValue());
                }
                else
                {
                    value = String.valueOf(cell.getNumericCellValue()); // 如果是double型并且小数后只有0
//                    if (value.contains(".0"))
//                    {
//                        value = value.substring(0, value.lastIndexOf("."));
//                    }
                }
            }
        }
        return value;
    }
    public static String isStringType(Cell cell)
    {
        String value = "";
        if (cell != null)
        {
            value = cell.toString().trim();
            if (Cell.CELL_TYPE_NUMERIC == cell.getCellType())
            {
                if (HSSFDateUtil.isCellDateFormatted(cell))
                {
                    // value =
                    // Formatter.stringFromDate(cell.getDateCellValue());
                }
                else
                {
                    value = String.valueOf(cell.getNumericCellValue()); // 如果是double型并且小数后只有0
                    if (value.contains(".0"))
                    {
                        value = value.substring(0, value.lastIndexOf("."));
                    }
                }
            }
        }
        return value;
    }
    
    /**
     * 验证字符串是否为数字
     * @param str
     * @return
     */
    public static boolean isNumberic(String str)
    {
        if (str == null || "".equals(str))
        {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++)
        {
            if (Character.isDigit(str.charAt(i)) == false && !String.valueOf(str.charAt(i)).contains("."))
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 验证字符串不为空时是否为数字
     * @param str
     * @return
     */
    public static int isNumber(String str)
    {
        if (str == null || "".equals(str)||"0".equals(str))
        {
            return 1;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++)
        {
        	if(String.valueOf(str.charAt(i)).contains(".")){
        		return 2;
        	}else if(Character.isDigit(str.charAt(i)) == false || String.valueOf(str.charAt(i)).contains("."))
            {
                return 2;
            }
        }
        return 3;
    }
    
    public static String converDate(String dateString)
    {
//        String dateString = "Tue May 14 09:10:53 CST 2013";
        dateString = dateString.replace("Jan", "01");
        dateString = dateString.replace("Feb", "02");
        dateString = dateString.replace("Mar", "03");
        dateString = dateString.replace("Apr", "04");
        dateString = dateString.replace("May", "05");
        dateString = dateString.replace("Jun", "06");
        dateString = dateString.replace("Jul", "07");
        dateString = dateString.replace("Aug", "08");
        dateString = dateString.replace("Sep", "09");
        dateString = dateString.replace("Oct", "10");
        dateString = dateString.replace("Nov", "11");
        dateString = dateString.replace("Dec", "12");
                     
        Date date;
        if (dateString.contains("."))
        {
            dateString = dateString.substring(0,dateString.indexOf("."));
        }
        return dateString;
    }
     /**
     * 兼容2003,2007(使用)
     * @param file
     * @param is
     * @return
     * @throws IOException
     */
    public static Workbook getExcelType(MultipartFile file, InputStream is) throws IOException
    {
        Workbook wb = null;
        String fileName = file.getOriginalFilename();
        if (fileName.endsWith("xlsx"))
          {
              wb = new XSSFWorkbook(is);
          }
          else
          {
              wb = new HSSFWorkbook(is);
          }
        return wb;
    }
    
    public static boolean checkDate(String date) {
//        String eL = "^((//d{2}(([02468][048])|([13579][26]))[//-/////s]?((((0?[13578])|(1[02]))[//-/////s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[//-/////s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[//-/////s]?((0?[1-9])|([1-2][0-9])))))|(//d{2}(([02468][1235679])|([13579][01345789]))[//-/////s]?((((0?[13578])|(1[02]))[//-/////s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[//-/////s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[//-/////s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(//s(((0?[0-9])|([1][0-9])|([2][0-3]))//:([0-5]?[0-9])((//s)|(//:([0-5]?[0-9])))))?$";
        String eL= "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-9]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";  
        Pattern p = Pattern.compile(eL);
        Matcher m = p.matcher(date);
        boolean b = m.matches();
        if (b)
        {
//            System.out.println("格式正确");
        } 
        else
        {
           System.out.println("格式错误");
        }
        return b;
       }
    
    public static boolean checkChineseName(String charName)
    {
//        charName=new String(charName.getBytes());//用GBK编码
        String eL = "[\u4e00-\u9fa5]";
//        String eL = "/[^u4E00-u9FA5]/g";
        Pattern p = Pattern.compile(eL);
        Matcher m = p.matcher(charName);
        System.out.println(m.find());
        return m.find();
    }
    
    public static String getEncoding(String str) {  
        String encode = "GB2312";  
        try {  
            if (str.equals(new String(str.getBytes(encode), encode))) {  
                String s = encode;  
                return s;  
            }  
        } catch (Exception exception) {  
        }  
        encode = "ISO-8859-1";  
        try {  
            if (str.equals(new String(str.getBytes(encode), encode))) {  
                String s1 = encode;  
                return s1;  
            }  
        } catch (Exception exception1) {  
        }  
        encode = "UTF-8";  
        try {  
            if (str.equals(new String(str.getBytes(encode), encode))) {  
                String s2 = encode;  
                return s2;  
            }  
        } catch (Exception exception2) {  
        }  
        encode = "GBK";  
        try {  
            if (str.equals(new String(str.getBytes(encode), encode))) {  
                String s3 = encode;  
                return s3;  
            }  
        } catch (Exception exception3) {  
        }  
        return "";  
    }  

      
    public static void main(String[] args) throws UnsupportedEncodingException
        {   
    //        checkDate("ffewf");
    //        checkChineseName("aa");
    //        String s = "����";  
    //        System.out.println(Formatter.getEncoding(s));
    //        String sa = new String(s.getBytes("utf-8"),"gbk");//解码:用什么字符集编码就用什么字符集解码  
    //        String sb = new String(sa.getBytes("utf-8"),"utf-8");
    //        String se = new String(sb.getBytes("utf-8"),"utf-8");
    //        String sf = new String(se.getBytes("gbk"),"gbk");
           
            /**
             * "测试字符串"的UTF-8编码数组
             */
    //        byte[] bUTF_8= "锟斤拷锟斤拷".getBytes("UTF-8");
    //        /**
    //         * 假如你不小心把UTF-8编码数组通过GBK解码字符串，那你只有先反向得到字节数组，再用UTF-8获取字符串
    //         */
    //        String sb_wrong =new String(bUTF_8,"GBK");
    //        String sb_right =new String(sb_wrong.getBytes("GBK"),"UTF-8");
    //        System.out.println(sb_wrong);
    //        System.out.println(sb_right);
            String sql = "_更换的商品款号：086207_更换的商品款号：6908741703058";
            String[] list = sql.split("_更换的商品款号：");
            for (int i = 0; i < list.length; i++)
            {
                String string = list[i];
                System.out.println(string);
                
            }
//            if ('' == brand)
//        	{
//        		jAlert('请选择品牌', '提示');
//        		return;
//        	}
            /**  品牌start **/
//            String brand  = String.valueOf(request.getParameter("brand"));
//            String tableBrand = String.valueOf(ConfigurationRegistry.brandCatgrpMap.get(brand));
            /** 品牌end  **/
        }
}
