package com.lakecloud.weixin.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

/**
 * @info 微信开发App方法类
 * @since V1.3
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net lihao
 * 
 */
@Component
public class WeixinUtils {
	/**
	 * 微信分页数
	 */
	public static int _WEIXIN_PAGESIZE = 12;

	/**
	 * 微信从cookies中获取城市ID
	 * 
	 * @param request
	 * @return
	 */
	public static String getCityIdFromCookies(HttpServletRequest request) {
		/**
		 * 读取cookies
		 * */
		Cookie[] cookies = request.getCookies();
		String app_city_id = "all";// 城市id
		if(cookies!=null){
			for (Cookie cookie : cookies) {
				if ("app_city_id".equals(cookie.getName())) {
					app_city_id = cookie.getValue();
					return app_city_id;
				}
			}
		}
		return app_city_id;
	}
}
