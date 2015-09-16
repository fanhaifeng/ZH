package com.lakecloud.manage.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

/**
 * @info PC端商城方法类
 * @since V1.0
 * @author lihao
 * 
 */
@Component
public class CommonUtils {
	/**
	 * PC分页大小
	 */
	public static int _PAGESIZE = 20;

	/**
	 * PC从cookies中获取城市ID
	 * 
	 * @param request
	 * @return
	 */
	public static String getCityIdFromCookies(HttpServletRequest request) {
		/**
		 * 读取cookies
		 * */
		Cookie[] cookies = request.getCookies();
		String pc_city_id = "all";// 城市id
		if(cookies!=null){
			for (Cookie cookie : cookies) {
				if ("pc_city_id".equals(cookie.getName())) {
					pc_city_id = cookie.getValue();
					return pc_city_id;
				}
			}
		}
		return pc_city_id;
	}
}
