package com.lakecloud.core.ehcache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.constructs.blocking.LockTimeoutException;
import net.sf.ehcache.constructs.web.AlreadyCommittedException;
import net.sf.ehcache.constructs.web.AlreadyGzippedException;
import net.sf.ehcache.constructs.web.filter.FilterNonReentrantException;
import net.sf.ehcache.constructs.web.filter.SimplePageFragmentCachingFilter;

import org.apache.commons.lang.StringUtils;

import com.lakecloud.core.tools.CommUtil;

/**
 * 
* <p>Title: PageCacheFiler.java</p>

* <p>Description: Eacache文件缓存处理过滤器，系统相对固定页面及资源文件，纳入缓存管理，避免每次都重复加载资源文件</p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
 */
public class PageCacheFiler extends SimplePageFragmentCachingFilter {
	private final static String FILTER_URL_PATTERNS = "patterns";
	private static List<String> cacheURLs=new ArrayList<String>();

	private void init() throws CacheException {
		String patterns = filterConfig.getInitParameter(FILTER_URL_PATTERNS);
		patterns = "/store_goods_head.htm,/goods_recommend_list.htm,/index_goods_stores.htm,/footer.htm,/city.htm,/floor.htm,/advert_invoke.htm,error.css,groupbuy.css,integral.css,seller.css,sparegoods.css,user_phptp.css,jcarousellite_1.0.1.min.js,jquery.ad-gallery.js,jquery.bigcolorpicker.js,jquery.cookie.js,jquery.fancybox-1.3.4.pack.js,jquery.form.js,jquery.jqzoom-core.js,jquery.KinSlideshow.min.js,jquery.lazyload.js,jquery.metadata.js,jquery.poshytip.min.js,jquery.rating.pack.js,jquery.shop.base.js,jquery.shop.common.js,jquery.shop.edit.js,jquery.validate.min.js,jquery.zh.cn.js,jquery-1.6.2.js,jquery-ui-1.8.21.js,swfobject.js,swfupload.js,swfupload.queue.js";
		cacheURLs.addAll(Arrays.asList(StringUtils.split(patterns, ",")));
	}

	@Override
	protected void doFilter(final HttpServletRequest request,

	final HttpServletResponse response, final FilterChain chain)

	throws AlreadyGzippedException, AlreadyCommittedException,

	FilterNonReentrantException, LockTimeoutException, Exception {
		if (cacheURLs == null||cacheURLs.size()<1) {
			init();
		}
		String url = request.getRequestURI();
		
		String include_url = CommUtil.null2String(request
				.getAttribute("javax.servlet.include.request_uri"));
		boolean flag = false;
	/*	if(url.contains("floor.htm")){
			StringBuffer url_param = request.getRequestURL();
			 if (request.getQueryString() != null) {
				 url_param.append("?");
				 url_param.append(request.getQueryString());
			 }
			System.out.println(url_param.toString());
			 // 获取cookies
			String pc_city_id = CommonUtils.getCityIdFromCookies(request);
			url=url+"?city_id="+pc_city_id;
			System.out.println(pc_city_id);
			if(!cacheURLs.contains(url)){//不包含
				cacheURLs.add(url);
			}
		}*/
		if (cacheURLs != null && cacheURLs.size() > 0) {
			for (String cacheURL : cacheURLs) {
				if (include_url.trim().equals("")) {
					if (url.contains(cacheURL.trim())) {
						flag = true;
						break;
					}
				} else {
					if (include_url.contains(cacheURL.trim())) {
						flag = true;
						break;
					}
				}

			}
		}
		// 如果包含我们要缓存的url 就缓存该页面，否则执行正常的页面转向
		if (flag) {
			super.doFilter(request, response, chain);
		} else {
			chain.doFilter(request, response);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean headerContains(final HttpServletRequest request,
			final String header, final String value) {
		logRequestHeaders(request);
		final Enumeration accepted = request.getHeaders(header);
		while (accepted.hasMoreElements()) {
			final String headerValue = (String) accepted.nextElement();
			if (headerValue.indexOf(value) != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @see net.sf.ehcache.constructs.web.filter.Filter#acceptsGzipEncoding(javax.servlet.http.HttpServletRequest)
	 * 
	 *      <b>function:</b> 兼容ie6/7 gzip压缩
	 * 
	 * @author erikchang
	 * 
	 * @createDate 2012-7-4 上午11:07:11
	 * 
	 */

	@Override
	protected boolean acceptsGzipEncoding(HttpServletRequest request) {
		boolean ie6 = headerContains(request, "User-Agent", "MSIE 6.0");
		boolean ie7 = headerContains(request, "User-Agent", "MSIE 7.0");
		return acceptsEncoding(request, "gzip") || ie6 || ie7;
	}

	/**
	 * 这个方法非常重要，重写计算缓存key的方法，没有该方法include的url值会出错
	 */
	@Override
	protected String calculateKey(HttpServletRequest httpRequest) {
		// TODO Auto-generated method stub
		String url = httpRequest.getRequestURI();
		String include_url = CommUtil.null2String(httpRequest
				.getAttribute("javax.servlet.include.request_uri"));
		StringBuffer stringBuffer = new StringBuffer();
		if (include_url.equals("")) {
			stringBuffer.append(httpRequest.getRequestURI()).append(
					httpRequest.getQueryString());
			String key = stringBuffer.toString();
			return key;
		} else {
			stringBuffer
					.append(CommUtil.null2String(httpRequest
							.getAttribute("javax.servlet.include.request_uri")))
					.append(CommUtil.null2String(httpRequest
							.getAttribute("javax.serlvet.include.query_string")));
			String key = stringBuffer.toString();
			return key;
		}
	}
}
