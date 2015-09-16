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
public class LoginFlag
{
    /**
     * 定义登录密码密文
     */
    private static final Logger log = LoggerFactory.getLogger(LoginFlag.class);

    /**
     * 密码加密
     * 
     * @param desc
     * @return
     */
    public static String encodeLoginPassword(String password)
    {
        String encodePasswordString = CommUtil.randomString(4).toUpperCase()
        							+ password.substring(0, 1)
        							+ CommUtil.randomString(5).toUpperCase()
        							+ password.substring(1, 2)
        							+ CommUtil.randomString(5).toUpperCase()
        							+ password.substring(2, 4)
        							+ CommUtil.randomString(2).toUpperCase()
        							+ password.substring(4, password.length())
        							+ CommUtil.randomString(3).toUpperCase();
    	return encodePasswordString;
    }

    /**
     * 密码解密
     * 
     * @param arg 要转换的参数
     * @return 小写字符
     */
    public static String decodeLogin(String login_password)
    {
    	String decodePasswordString = login_password.substring(4,5)
    							    + login_password.substring(10,11)
    							    + login_password.substring(16,18)
    							    + login_password.substring(20,login_password.length()-3);
    	return decodePasswordString;
    }

    
}
