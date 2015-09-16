package com.lakecloud.core.tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;

import com.lakecloud.pay.alipay.util.httpClient.HttpProtocolHandler;
import com.lakecloud.pay.alipay.util.httpClient.HttpRequest;
import com.lakecloud.pay.alipay.util.httpClient.HttpResponse;
import com.lakecloud.pay.alipay.util.httpClient.HttpResultType;

/**
 * @author lihao
 * @description 调用中化接口发送短信方法
 * 
 */
public class SendMessageUtil {

	/**
	 * @param phone 电话号码
	 * @param message 短信内容
	 * @throws IOException
	 */
	public static void sendHttpPost(String phone, String message)
			throws IOException {
		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset("UTF-8");
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		params.put("message", message);
		request.setParameters(generatNameValuePair(params));
		String url = "http://oa.sinofert.com/C6/JHSoft.Web.SinofertMessage/SendMessage.aspx";
		request.setUrl(url);
		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();
		HttpResponse response = httpProtocolHandler.execute(request);
		if (response == null) {
			System.out.println("send message failed");
		}
		String strResult = response.getStringResult();
		System.out.println(strResult);
	}

	public static void main(String[] args) throws IOException {
		sendHttpPost("14795501247", "您的订单已发货,短信发送成功");
	}

	/**
	 * MAP类型数组转换成NameValuePair类型
	 * 
	 * @param properties
	 *            MAP类型数组
	 * @return NameValuePair类型数组
	 */
	private static NameValuePair[] generatNameValuePair(
			Map<String, String> properties) {
		NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
		int i = 0;
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			nameValuePair[i++] = new NameValuePair(entry.getKey(), entry
					.getValue());
		}

		return nameValuePair;
	}
}
