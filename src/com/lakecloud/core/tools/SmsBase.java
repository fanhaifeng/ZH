package com.lakecloud.core.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
/**
 * 
* <p>Title: SmsBase.java</p>

* <p>Description:系统手机短信发送类，结合第三方短信平台进行管理使用 </p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
 */
public class SmsBase {
	private String url;
	private String id;
	private String pwd;

	public SmsBase(String url, String id, String pwd) {
		this.url = url;
		this.id = id;
		this.pwd = pwd;
	}

	public String SendSms(String mobile, String content)
			throws UnsupportedEncodingException {
		Integer x_ac = 10;// 发送信息
		HttpURLConnection httpconn = null;
		String result = "-20";
		String memo = content.length() < 70 ? content.trim() : content.trim()
				.substring(0, 70);
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("?id=").append(id);
		sb.append("&pwd=").append(pwd);
		sb.append("&to=").append(mobile);
		sb.append("&content=").append(URLEncoder.encode(content, "gb2312")); // 注意乱码的话换成gb2312编码
		try {
			URL url = new URL(sb.toString());
			httpconn = (HttpURLConnection) url.openConnection();
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					httpconn.getInputStream()));
			result = rd.readLine();
			rd.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (httpconn != null) {
				httpconn.disconnect();
				httpconn = null;
			}

		}
		return result;
	}
}
