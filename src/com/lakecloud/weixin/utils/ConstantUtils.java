package com.lakecloud.weixin.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.weixin.payment.bean.MapBean;

/**
 * 中金支付&农豆常量
 */
@Component
public class ConstantUtils {
	/**
	 * 机构号码
	 */
	public static String _PAY_THIRD_INSTITUTIONID = "pay_third_InstitutionID";
	/**
	 * TXCODE："2531","2532","2502","2503","1375","1376"
	 */
	public static String[] _PAY_THIRD_TXCODES = { "2531", "2532", "2502",
			"2503", "1375", "1376" };
	/**
	 * 打印日志前缀
	 */
	public static String _PAY_THIRD_LOG_FRONT = "[Message_pay_third]";
	/**
	 * 进入
	 */
	public static String _PAY_THIRD_LOG_ENTER = "enter";
	/**
	 * 退出
	 */
	public static String _PAY_THIRD_LOG_EXIT = "exit";
	/**
	 * 分隔线
	 */
	public static String _COMMON_SEPARATOR = "******************************************************************************************************";
	/**
	 * 银行ID
	 */
	public static String[][] _PAY_THIRD_BANKIDS = { { "中国银行", "104" },
			{ "邮储银行", "100" }, { "工商银行", "102" }, { "农业银行", "103" },
			{ "建设银行", "105" }, { "交通银行", "301" }, { "中信银行", "302" },
			{ "光大银行", "303" }, { "华夏银行", "304" }, { "广发银行", "306" },
			{ "平安银行", "307" }, { "招商银行", "308" }, { "兴业银行", "309" },
			{ "浦发银行", "310" }, { "上海银行", "401" }, { "宁波银行", "408" },
			{ "徽商银行", "440" }, { "兰州银行", "447" }, { "青岛银行", "450" },
			{ "北京银行", "403" }, { "河北银行", "422" }, { "天津银行", "434" } };
	/**
	 * 证件类型
	 */
	public static String[][] _PAY_THIRD_IDENTIFICATION_TYPES = {
			{ "0", "身份证" }, { "1", "户口薄" }, { "2", "护照" }, { "3", "军官证" },
			{ "4", "士兵证" }, { "5", "港澳居民来往内地通行证" }, { "6", "台湾同胞来往内地通行证" },
			{ "7", "临时身份证" }, { "8", "外国人居留证" }, { "9", "警官证" },
			{ "X", "其他证件" } };
	/**
	 * 返回成功
	 */
	public static String _PAY_THIRD_RESULT_OK = "OK.";
	/**
	 * deleteStatus=0
	 */
	public static String _DELETE_STATUS_0 = " and obj.deleteStatus=0";
	/**
	 * 0为订单取消，10为已提交待付款，15为线下付款提交申请，20为已付款待发货，30为已发货待收货，40为已收货，45买家申请退货，46退货中(
	 * 卖家同意退货)，47退货成功,48退货申请被拒绝(卖家拒绝退货)，49退货失败（买家没有在系统规定时间内输入退货物流信息）50买家评价完毕，60
	 * 卖家评价完毕订单完成，65订单不可评价
	 */
	public static int[] _ORDERFORM_ORDER_STATUS = { 0, 10, 15, 20, 30, 40 };
	/**
	 * 0"outline",1"online"中金支付,2"payafter"现款自提
	 */
	public static String[] _PAYMENT_MARK = { "outline", "online", "payafter" };
	/**
	 * 0预生产，1测试，2本地
	 */
	public static String[] _PAY_THIRD_CONFIG_PATH_LIST = {
			"/opt/IBM/WebSphere/AppServer/profiles/demo/installedApps/config/",
			"/opt/IBM/WebSphere/AppServer/profiles/Custom01/installedApps/config/",
			"/config/" };
	public static String _PAY_THIRD_CONFIG_PATH = _PAY_THIRD_CONFIG_PATH_LIST[2];
	public static String _PAY_THIRD_CONFIG_PATH_SYSTEM = _PAY_THIRD_CONFIG_PATH
			+ "system";
	public static String _PAY_THIRD_CONFIG_PATH_PAYMENT = _PAY_THIRD_CONFIG_PATH
			+ "payment";
	/**
	 * 注册获得农豆数
	 */
	public static String _INTEGRATION_FROM_REGISTER = "integration_from_register";
	/**
	 * 邀请获得农豆数
	 */
	public static String _INTEGRATION_FROM_INVITE = "integration_from_invite";
	/**
	 * 订单金额可折合的农豆数
	 */
	public static String _INTEGRATION_FROM_ORDER = "integration_from_order";
	/**
	 * 农豆兑换钱的比例
	 */
	public static String _INTEGRATION_RATE_FOR_MONEY = "integration_rate_for_money";
	/**
	 * 订单可使用的农豆比例
	 */
	public static String _INTEGRATION_RATE_FOR_ORDER = "integration_rate_for_order";
	/**
	 * 0"平台农豆",1"店铺农豆"
	 */
	public static int[] _INTEGRATION_TYPE = { 0, 1 };
	/**
	 * 加减类型：1:增加农豆 -1：减农豆
	 */
	public static int[] _INTEGRATION_LOG_TYPE = { 1, -1 };
	/**
	 * 消费路径：0:订单 1:注册 2：推荐用户 -1：农技服务
	 */
	public static int[] _INTEGRATION_LOG_GETTYPE = { 0, 1, 2, -1 };
	/**
	 * 农豆相关接口实现类：0"OrderFormServiceImpl", 1"IntegrationServiceImpl",
	 * 2"Integration_ChildServiceImpl", 3"Integration_LogServiceImpl"
	 */
	public static String[] _INTEGRATION_SERVICE_IMPLS = {
			"OrderFormServiceImpl", "IntegrationServiceImpl",
			"Integration_ChildServiceImpl", "Integration_LogServiceImpl" };
	/**
	 * 农豆相关接口实现类中的方法
	 */
	public static String[] _INTEGRATION_SERVICE_IMPL_FUNCTIONS = {
			"updateByOrderForm", "setIntegrationPlatformAndIntegrationStore",
			"subtract_operations_for_integration" };
	/**
	 * 日志生产者：0"log_by_bm"
	 */
	public static String[] _LOG_AUTHORS = { "logs_by_bm" };

	/**
	 * 获取打印日志前缀
	 */
	public static String _getLogFront(String str) {
		return _PAY_THIRD_LOG_FRONT + "[tx" + str + "]";
	}

	/**
	 * 进入打印日志
	 */
	public static String _getLogEnter(String str) {
		_printCommonSeparator(0);
		return _getLogFront(str) + _PAY_THIRD_LOG_ENTER;
	}

	/**
	 * 退出打印日志
	 */
	public static String _getLogExit(String str) {
		_printCommonSeparator(0);
		return _getLogFront(str) + _PAY_THIRD_LOG_EXIT;
	}

	/**
	 * 银行列表
	 */
	public static List<MapBean> _getBankIDList() {
		List<MapBean> list = new ArrayList<MapBean>();
		for (int i = 0; i < ConstantUtils._PAY_THIRD_BANKIDS.length; i++) {
			MapBean bankBean = new MapBean();
			bankBean.setKey(ConstantUtils._PAY_THIRD_BANKIDS[i][1]);
			bankBean.setValue(ConstantUtils._PAY_THIRD_BANKIDS[i][0]);
			list.add(bankBean);
		}
		return list;
	}

	/**
	 * 证件类型列表
	 */
	public static List<MapBean> _getIdentificationTypeList() {
		List<MapBean> list = new ArrayList<MapBean>();
		for (int i = 0; i < ConstantUtils._PAY_THIRD_IDENTIFICATION_TYPES.length; i++) {
			MapBean bankBean = new MapBean();
			bankBean
					.setKey(ConstantUtils._PAY_THIRD_IDENTIFICATION_TYPES[i][0]);
			bankBean
					.setValue(ConstantUtils._PAY_THIRD_IDENTIFICATION_TYPES[i][1]);
			list.add(bankBean);
		}
		return list;
	}

	/**
	 * 通过订单，计算实际付款金额
	 */
	public static String _getRealPriceByOrderForm(OrderForm of) {
		return of.getTotalPrice().subtract(
				new BigDecimal(CommUtil.null2Long(of.getCharge_Num())))
				.subtract(of.getIntegration_price()).toString();
	}

	/**
	 * 返回农豆相关的接口实现类中，对应方法的日志
	 */
	public static String _getIntegrationServiceImplFunctions(int impl,
			int function) {
		_printCommonSeparator(0);
		return "[" + _INTEGRATION_SERVICE_IMPLS[impl] + "]["
				+ _INTEGRATION_SERVICE_IMPL_FUNCTIONS[function] + "]";
	}

	/**
	 * 根据创建者，打印日志分隔线
	 */
	public static void _printCommonSeparator(int author) {
		System.out.println(_LOG_AUTHORS[author] + _COMMON_SEPARATOR);
	}
}
