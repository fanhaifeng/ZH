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
	public static String softwareSerialNo = "9SDK-EMY-0229-JCXOK";// 软件序列号,请通过亿美销售人员获取
	public static String key = "9461198073F3F9537B44128B7B38382C";// 序列号首次激活时自己设定
	public static String password = "";// 密码,请通过亿美销售人员获取
	public static String _HTTP_URL = "http://sdk229ws.eucp.b2m.cn:8080/sdkproxy/sendtimesms.action";

	/**
	 * @param phone
	 *            电话号码
	 * @param message
	 *            短信内容
	 * @throws IOException
	 */
	public static void sendHttpPost(String phone, String message)
			throws IOException {
		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset("UTF-8");
		Map<String, String> params = new HashMap<String, String>();
		params.put("cdkey", softwareSerialNo);
		params.put("password", key);
		params.put("phone", phone);
		params.put("message", message);
		request.setParameters(generatNameValuePair(params));
		request.setUrl(_HTTP_URL);
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
		// sendHttpPost("14795501247", "您的订单已发货,短信发送成功");
		// testSendSMS("", "test您的订单已发货");
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
	// /**
	// * 发送短信、可以发送定时和即时短信 sendSMS(String[] mobiles,String smsContent, String
	// * addSerial, int smsPriority) 1、mobiles 手机数组长度不能超过1000 2、smsContent
	// * 最多500个汉字或1000个纯英文
	// * 、请客户不要自行拆分短信内容以免造成混乱、亿美短信平台会根据实际通道自动拆分、计费以实际拆分条数为准、亿美推荐短信长度70字以内
	// * 3、addSerial 附加码(长度小于15的字符串) 用户可通过附加码自定义短信类别,或添加自定义主叫号码( 联系亿美索取主叫号码列表)
	// * 4、优先级范围1~5，数值越高优先级越高(相对于同一序列号) 5、其它短信发送请参考使用手册自己尝试使用
	// */
	// public static void testSendSMS(String phone, String message) {
	// try {
	// int i = EmaySDKClient.getInstance(softwareSerialNo, key).sendSMS(
	// new String[] { phone }, message, "", 5);// 带扩展码
	// System.out.println("testSendSMS=====" + i);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
}
