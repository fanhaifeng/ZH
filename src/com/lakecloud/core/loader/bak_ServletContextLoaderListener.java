package com.lakecloud.core.loader;

import java.io.File;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.web.context.support.WebApplicationContextUtils;

import payment.api.system.PaymentEnvironment;

import com.lakecloud.core.security.SecurityManager;
import com.lakecloud.weixin.utils.ConstantUtils;

/**
 * 
 * <p>
 * Title: ServletContextLoaderListener.java
 * </p>
 * 
 * <p>
 * Description:系统基础信息加载监听器，目前用来加载系统权限数据，也可以将系统语言包在这里加载进来，该监听器会在系统系统的时候进行一次数据加载
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2014
 * </p>
 * 
 * <p>
 * Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-27
 * 
 * @version LakeCloud_C2C 1.3
 */
public class bak_ServletContextLoaderListener implements ServletContextListener {
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		webappListener();
		ServletContext servletContext = servletContextEvent.getServletContext();
		SecurityManager securityManager = this
				.getSecurityManager(servletContext);
		Map<String, String> urlAuthorities = securityManager
				.loadUrlAuthorities();
		servletContext.setAttribute("urlAuthorities", urlAuthorities);
	}

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		servletContextEvent.getServletContext().removeAttribute(
				"urlAuthorities");
	}

	protected SecurityManager getSecurityManager(ServletContext servletContext) {
		return (SecurityManager) WebApplicationContextUtils
				.getWebApplicationContext(servletContext).getBean(
						"securityManager");
	}

	/**
	 * <pre>
	 * Copyright Notice:
	 *    Copyright (c) 2005-2009 China Financial Certification Authority(CFCA)
	 *    A-1 You An Men Nei Xin An Nan Li, Xuanwu District, Beijing ,100054, China
	 *    All rights reserved.
	 * 
	 *    This software is the confidential and proprietary information of
	 *    China Financial Certification Authority (&quot;Confidential Information&quot;).
	 *    You shall not disclose such Confidential Information and shall use 
	 *    it only in accordance with the terms of the license agreement you 
	 *    entered into with CFCA.
	 * </pre>
	 */
	private void webappListener() {
		head();
		try {
			String systemConfigPath = ConstantUtils._PAY_THIRD_CONFIG_PATH_SYSTEM;
			String paymentConfigPath = ConstantUtils._PAY_THIRD_CONFIG_PATH_PAYMENT;
			// Log4j
			String log4jConfigFile = systemConfigPath + File.separatorChar
					+ "log4j.xml";
			System.out.println(log4jConfigFile);
			DOMConfigurator.configure(log4jConfigFile);
			// 初始化支付环境
			PaymentEnvironment.initialize(paymentConfigPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 中金支付
	 */
	private void head() {
		System.out.println("==========================================");
		System.out.println("China Payment & Clearing Network Co., Ltd.");
		System.out.println("Payment and Settlement System");
		System.out.println("Institution Simulator v1.7.8.6");
		System.out.println("==========================================");
	}
}
