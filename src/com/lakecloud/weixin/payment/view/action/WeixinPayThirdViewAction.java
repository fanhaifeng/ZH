package com.lakecloud.weixin.payment.view.action;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import payment.api.system.TxMessenger;
import payment.api.tx.marketorder.Tx1375Request;
import payment.api.tx.marketorder.Tx1375Response;
import payment.api.tx.marketorder.Tx1376Request;
import payment.api.tx.paymentbinding.Tx2531Request;
import payment.api.tx.paymentbinding.Tx2531Response;
import payment.api.tx.paymentbinding.Tx2532Request;
import payment.api.tx.paymentbinding.Tx2532Response;
import payment.tools.util.GUID;

import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Payment;
import com.lakecloud.foundation.domain.ThirdBinding;
import com.lakecloud.foundation.domain.ThirdPayment;
import com.lakecloud.foundation.domain.XConf;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IPaymentService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IThirdBindingService;
import com.lakecloud.foundation.service.IThirdPaymentService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IXConfService;
import com.lakecloud.weixin.utils.ConstantUtils;

@Controller
public class WeixinPayThirdViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IXConfService xconfService;
	@Autowired
	private IThirdBindingService thirdBindingService;
	@Autowired
	private IThirdPaymentService thirdPaymentService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IPaymentService paymentService;

	/**
	 * 绑卡
	 */
	@RequestMapping("/weixin/third_binding.htm")
	public ModelAndView binding(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String chargeNum, String totalPrice) {
		ModelAndView mv = new JModelAndView("weixin/third_binding.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("xconfkey", ConstantUtils._PAY_THIRD_INSTITUTIONID);
		List<XConf> xconfList = this.xconfService.query(
				"select obj from XConf obj where (obj.xconfkey=:xconfkey)",
				params, -1, -1);
		String txSNBinding = GUID.getTxNo();
		mv.addObject("InstitutionID", xconfList.get(0).getXconfvalue());// 机构号：001639
		mv.addObject("TxSNBinding", txSNBinding);
		mv.addObject("order_id", order_id);
		mv.addObject("totalPrice", totalPrice);
		ThirdBinding thirdBinding = null;
		List<ThirdBinding> list = this.thirdBindingService.queryByCreateUser();
		if (null != list && list.size() > 0) {
			thirdBinding = list.get(0);
		}
		mv.addObject("thirdBinding", thirdBinding);
		mv.addObject("bankBeanList", ConstantUtils._getBankIDList());
		mv.addObject("identificationTypeList", ConstantUtils
				._getIdentificationTypeList());
		return mv;
	}

	/**
	 * 建立绑定关系（发送短信）
	 */
	@RequestMapping("/weixin/tx2531.htm")
	public void tx2531(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String chargeNum, String totalPrice) {
		System.out.println(ConstantUtils
				._getLogEnter(ConstantUtils._PAY_THIRD_TXCODES[0]));
		String result = "";
		String logFront = ConstantUtils
				._getLogFront(ConstantUtils._PAY_THIRD_TXCODES[0]);
		try {
			// 1.获取参数
			String institutionID = request.getParameter("InstitutionID");
			String txCode = ConstantUtils._PAY_THIRD_TXCODES[0];
			String txSNBinding = request.getParameter("TxSNBinding");
			String bankID = request.getParameter("BankID");
			String accountName = request.getParameter("AccountName");
			String accountNumber = request.getParameter("AccountNumber");
			String identificationType = request
					.getParameter("IdentificationType");
			String identificationNumber = request
					.getParameter("IdentificationNumber");
			String phoneNumber = request.getParameter("PhoneNumber");
			String cardType = request.getParameter("CardType");
			// String validDate = !""
			// .equals(request.getParameter("ValidDate")) ? request
			// .getParameter("ValidDate").trim() : null;
			// String cvn2 = !"".equals(request.getParameter("CVN2")) ?
			// request
			// .getParameter("CVN2").trim()
			// : null;
			// 创建交易请求对象
			Tx2531Request txRequest = new Tx2531Request();
			txRequest.setInstitutionID(institutionID);
			txRequest.setTxCode(txCode);
			txRequest.setTxSNBinding(txSNBinding);
			txRequest.setBankID(bankID);
			txRequest.setAccountName(accountName);
			txRequest.setAccountNumber(accountNumber);
			txRequest.setIdentificationNumber(identificationNumber);
			txRequest.setIdentificationType(identificationType);
			txRequest.setPhoneNumber(phoneNumber);
			txRequest.setCardType(cardType);
			// txRequest.setValidDate(validDate);
			// txRequest.setCvn2(cvn2);
			System.out.println(logFront + "institutionID=" + institutionID
					+ ";txCode=" + txCode + ";txSNBinding=" + txSNBinding
					+ ";bankID=" + bankID + ";accountName=" + accountName
					+ ";accountNumber" + accountNumber + ";identificationType="
					+ identificationType + ";identificationNumber="
					+ identificationNumber + ";phoneNumber=" + phoneNumber
					+ ";cardType=" + cardType);
			save_third_binding(txRequest);// TODO 是否合理？
			// 3.执行报文处理
			txRequest.process();
			// 4.将参数放置到request对象
			// request.setAttribute("plainText", txRequest
			// .getRequestPlainText());
			// request.setAttribute("message",
			// txRequest.getRequestMessage());
			// request.setAttribute("signature", txRequest
			// .getRequestSignature());
			// request.setAttribute("txCode", txRequest.getTxCode());
			// request.setAttribute("txName", "建立绑定关系（发送验证短信）");
			// request.setAttribute("action", request.getContextPath()
			// + "/SendMessage");
			TxMessenger txMessenger = new TxMessenger();
			String[] respMsg = txMessenger.send(txRequest.getRequestMessage(),
					txRequest.getRequestSignature());
			Tx2531Response txResponse = new Tx2531Response(respMsg[0],
					respMsg[1]);
			// if ("2000".equals(txResponse.getCode())) {
			result = txResponse.getMessage();
			System.out.println(logFront + result);
			// }
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			writer = response.getWriter();
			writer.print(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(ConstantUtils
				._getLogExit(ConstantUtils._PAY_THIRD_TXCODES[0]));
	}

	/**
	 * 建立绑定关系（验证并绑定）
	 */
	@RequestMapping("/weixin/tx2532.htm")
	public void tx2532(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String chargeNum, String totalPrice) {
		System.out.println(ConstantUtils
				._getLogEnter(ConstantUtils._PAY_THIRD_TXCODES[1]));
		String result = "";
		String logFront = ConstantUtils
				._getLogFront(ConstantUtils._PAY_THIRD_TXCODES[1]);
		try {
			// 1.获取参数
			String institutionID = request.getParameter("InstitutionID");
			String txCode = ConstantUtils._PAY_THIRD_TXCODES[1];
			String txSNBinding = request.getParameter("TxSNBinding");
			String SMSValidationCode = request
					.getParameter("SMSValidationCode");
			// 创建交易请求对象
			Tx2532Request txRequest = new Tx2532Request();
			txRequest.setInstitutionID(institutionID);
			txRequest.setTxCode(txCode);
			txRequest.setTxSNBinding(txSNBinding);
			txRequest.setSMSValidationCode(SMSValidationCode);
			System.out.println(logFront + "institutionID=" + institutionID
					+ ";txCode=" + txCode + ";txSNBinding=" + txSNBinding
					+ ";SMSValidationCode=" + SMSValidationCode);
			// 3.执行报文处理
			txRequest.process();
			TxMessenger txMessenger = new TxMessenger();
			String[] respMsg = txMessenger.send(txRequest.getRequestMessage(),
					txRequest.getRequestSignature());
			Tx2532Response txResponse = new Tx2532Response(respMsg[0],
					respMsg[1]);
			// if ("2000".equals(txResponse.getCode())) {
			result = txResponse.getMessage();
			System.out.println(logFront + result);
			// }
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			writer = response.getWriter();
			writer.print(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(ConstantUtils
				._getLogExit(ConstantUtils._PAY_THIRD_TXCODES[1]));
	}

	private boolean save_third_binding(Tx2531Request txRequest) {
		ThirdBinding thirdBinding;
		List<ThirdBinding> list = this.thirdBindingService.queryByCreateUser();
		if (null != list && list.size() > 0) {
			thirdBinding = list.get(0);
		} else {
			thirdBinding = new ThirdBinding();
		}
		thirdBinding.setInstitutionID(txRequest.getInstitutionID());
		thirdBinding.setTxCode(txRequest.getTxCode());
		thirdBinding.setTxSNBinding(txRequest.getTxSNBinding());
		thirdBinding.setBankID(txRequest.getBankID());
		thirdBinding.setAccountName(txRequest.getAccountName());
		thirdBinding.setAccountNumber(txRequest.getAccountNumber());
		thirdBinding.setIdentificationNumber(txRequest
				.getIdentificationNumber());
		thirdBinding.setIdentificationType(txRequest.getIdentificationType());
		thirdBinding.setPhoneNumber(txRequest.getPhoneNumber());
		thirdBinding.setCardType(txRequest.getCardType());
		thirdBinding.setCreate_user(SecurityUserHolder.getCurrentUser()
				.getUsername());
		thirdBinding.setAddTime(new Date());
		return this.thirdBindingService.update(thirdBinding);
		// update_third_payment(txRequest.getTxSNBinding());
	}

	/**
	 * 支付
	 */
	@RequestMapping("/weixin/third_payment.htm")
	public ModelAndView payment(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String chargeNum, String totalPrice) {
		ModelAndView mv = new JModelAndView("weixin/third_payment.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		XConf xconf = this.xconfService
				.queryByXconfkey(ConstantUtils._PAY_THIRD_INSTITUTIONID);
		String paymentNo = GUID.getTxNo();
		mv.addObject("InstitutionID", xconf.getXconfvalue());// 机构号：001639
		mv.addObject("PaymentNo", paymentNo);
		mv.addObject("order_id", order_id);
		//mv.addObject("totalPrice", totalPrice);
		try {
			OrderForm of = orderFormService.getObjById(CommUtil
					.null2Long(order_id));
			mv.addObject("totalPrice", ConstantUtils
					._getRealPriceByOrderForm(of));
		} catch (Exception e) {
			e.printStackTrace();
		}
		ThirdBinding thirdBinding = null;
		List<ThirdBinding> list = this.thirdBindingService.queryByCreateUser();
		if (null != list && list.size() > 0) {
			thirdBinding = list.get(0);
		}
		mv.addObject("thirdBinding", thirdBinding);
		mv.addObject("bankBeanList", ConstantUtils._getBankIDList());
		mv.addObject("identificationTypeList", ConstantUtils
				._getIdentificationTypeList());
		String orderNo = "" + System.currentTimeMillis();
		mv.addObject("OrderNo", orderNo);
		return mv;
	}

	/**
	 * 市场订单快捷支付(发送短信)
	 */
	@RequestMapping("/weixin/tx1375.htm")
	public void tx1375(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String chargeNum, String totalPrice) {
		System.out.println(ConstantUtils
				._getLogEnter(ConstantUtils._PAY_THIRD_TXCODES[4]));
		String result = "";
		String logFront = ConstantUtils
				._getLogFront(ConstantUtils._PAY_THIRD_TXCODES[4]);
		try {
			// 1.取得参数
			String institutionID = request.getParameter("InstitutionID");
			String orderNo = request.getParameter("OrderNo");
			String paymentNo = request.getParameter("PaymentNo");
			OrderForm of = this.orderFormService.queryByOrderId(order_id);
			long amount = Long.valueOf(
					(of.getTotalPrice().subtract(of.getCharge_Num())
							.subtract(of.getIntegration_price())).multiply(
							new BigDecimal(100)).setScale(0, 4).toString())
					.longValue();
			String txSNBinding = request.getParameter("TxSNBinding");
			String remark = request.getParameter("Remark");
			// 2.创建交易请求对象
			Tx1375Request txRequest = new Tx1375Request();
			txRequest.setInstitutionID(institutionID);
			txRequest.setOrderNo(orderNo);
			txRequest.setPaymentNo(paymentNo);
			txRequest.setAmount(amount);
			txRequest.setTxSNBinding(txSNBinding);
			txRequest.setRemark(remark);
			System.out.println(logFront + "order_id=" + order_id
					+ ";institutionID=" + institutionID + ";orderNo=" + orderNo
					+ ";paymentNo=" + paymentNo + ";amount=" + amount
					+ ";txSNBinding=" + txSNBinding + ";remark=" + remark);
			save_third_payment(txRequest, order_id);// TODO 扣库存
			// 3.执行报文处理
			txRequest.process();
			TxMessenger txMessenger = new TxMessenger();
			String[] respMsg = txMessenger.send(txRequest.getRequestMessage(),
					txRequest.getRequestSignature());
			Tx1375Response txResponse = new Tx1375Response(respMsg[0],
					respMsg[1]);
			// if ("2000".equals(txResponse.getCode())) {
			result = txResponse.getMessage();
			System.out.println(logFront + result);
			// }
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			writer = response.getWriter();
			writer.print(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(ConstantUtils
				._getLogExit(ConstantUtils._PAY_THIRD_TXCODES[4]));
	}

	/**
	 * 市场订单快捷支付(验证并支付)
	 */
	@RequestMapping("/weixin/tx1376.htm")
	public void tx1376(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String chargeNum, String totalPrice) {
		System.out.println(ConstantUtils
				._getLogEnter(ConstantUtils._PAY_THIRD_TXCODES[5]));
		String result = "";
		String logFront = ConstantUtils
				._getLogFront(ConstantUtils._PAY_THIRD_TXCODES[5]);
		try {
			// 1.取得参数
			String institutionID = request.getParameter("InstitutionID");
			String orderNo = request.getParameter("OrderNo");
			String paymentNo = request.getParameter("PaymentNo");
			String smsValidationCode = request
					.getParameter("SMSValidationCode");
			// 2.创建交易请求对象
			Tx1376Request txRequest = new Tx1376Request();
			txRequest.setInstitutionID(institutionID);
			txRequest.setOrderNo(orderNo);
			txRequest.setPaymentNo(paymentNo);
			txRequest.setSmsValidationCode(smsValidationCode);
			System.out.println(logFront + "institutionID=" + institutionID
					+ ";orderNo=" + orderNo + ";paymentNo=" + paymentNo
					+ ";smsValidationCode=" + smsValidationCode);
			// 3.执行报文处理
			txRequest.process();
			TxMessenger txMessenger = new TxMessenger();
			String[] respMsg = txMessenger.send(txRequest.getRequestMessage(),
					txRequest.getRequestSignature());
			Tx1375Response txResponse = new Tx1375Response(respMsg[0],
					respMsg[1]);
			// if ("2000".equals(txResponse.getCode())) {
			result = txResponse.getMessage();
			System.out.println(logFront + result);
			// }
			if (ConstantUtils._PAY_THIRD_RESULT_OK.equals(result)) {
				update_orderForm(order_id);
			}
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			writer = response.getWriter();
			writer.print(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(ConstantUtils
				._getLogExit(ConstantUtils._PAY_THIRD_TXCODES[5]));
	}

	private boolean save_third_payment(Tx1375Request txRequest, String orderId) {
		ThirdPayment thirdPayment;
		List<ThirdPayment> list = this.thirdPaymentService
				.queryByOrderId(orderId);
		if (null != list && list.size() > 0) {
			thirdPayment = list.get(0);
		} else {
			thirdPayment = new ThirdPayment();
		}
		thirdPayment.setInstitutionID(txRequest.getInstitutionID());
		thirdPayment.setOrderNo(txRequest.getOrderNo());
		thirdPayment.setPaymentNo(txRequest.getPaymentNo());
		thirdPayment.setAmount(txRequest.getAmount());
		thirdPayment.setTxSNBinding(txRequest.getTxSNBinding());
		thirdPayment.setRemark(txRequest.getRemark());
		thirdPayment.setCreate_user(SecurityUserHolder.getCurrentUser()
				.getUsername());
		thirdPayment.setAddTime(new Date());
		thirdPayment.setTxCode(ConstantUtils._PAY_THIRD_TXCODES[4]);
		thirdPayment.setOrder_id(orderId);
		return this.thirdPaymentService.update(thirdPayment);
	}

	// unused
	private void update_third_payment(String txSNBinding) {
		List<ThirdPayment> list = this.thirdPaymentService.queryByCreateUser();
		for (ThirdPayment thirdPayment : list) {
			thirdPayment.setTxSNBinding(txSNBinding);
			this.thirdPaymentService.update(thirdPayment);
		}
	}

	private boolean update_orderForm(String order_id) {
		OrderForm orderForm = this.orderFormService.queryByOrderId(order_id);
		Map params = new HashMap();
		params.put("mark", ConstantUtils._PAYMENT_MARK[1]);
		params.put("store_id", orderForm.getStore().getId());
		List<Payment> paymentList = this.paymentService
				.query(
						"select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
						params, -1, -1);
		if (paymentList.size() > 0) {
			orderForm.setPayment(paymentList.get(0));
			orderForm.setPayTime(new Date());
		}
		orderForm.setOrder_status(ConstantUtils._ORDERFORM_ORDER_STATUS[3]);
		return this.orderFormService.update(orderForm);
	}
}
