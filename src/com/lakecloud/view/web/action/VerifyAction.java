package com.lakecloud.view.web.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.dao.OrderFormDAO;
import com.lakecloud.foundation.domain.Audit;
import com.lakecloud.foundation.domain.MobileVerifyCode;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IAuditService;
import com.lakecloud.foundation.service.IGroupService;
import com.lakecloud.foundation.service.IMobileVerifyCodeService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.foundation.service.impl.MobileVerifyCodeServiceImpl;
import com.lakecloud.weixin.domain.VMenu;
import com.lakecloud.weixin.service.IVMenuService;

/**
 * @info 系统验证控制器
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Controller
public class VerifyAction {
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IVMenuService vmenuService;
	@Autowired
	private IAuditService auditService;
	@Autowired
	private IMobileVerifyCodeService mobileVerifyCodeServiceImpl;
	@Autowired
	private OrderFormDAO orderFormDao;
	
	
	/**
	 * 验证码ajax验证方法
	 * 
	 * @param response
	 */
	@RequestMapping("/verify_code.htm")
	public void validate_code(HttpServletRequest request,
			HttpServletResponse response, String code, String code_name) {
		HttpSession session = request.getSession(false);
		String verify_code = "";
		if (CommUtil.null2String(code_name).equals("")) {
			verify_code = (String) session.getAttribute("verify_code");
		} else {
			verify_code = (String) session.getAttribute(code_name);
		}
		boolean ret = true;
		if (verify_code != null && !verify_code.equals("")) {
			if (!CommUtil.null2String(code.toUpperCase()).equals(verify_code)) {
				ret = false;
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 验证赊销还款是否超出
	 * @param request
	 * @param response
	 * @param order_id
	 */
	@RequestMapping("/verify_chargeNum.htm")
	public void verify_chargeNum(HttpServletRequest request ,HttpServletResponse response,
			String order_id,String needpay){
		boolean ret = false;
		OrderForm obj = this.orderFormDao.get(Long.parseLong(order_id));
		if(obj!=null){
			if(obj.getAlreadyPay().add(new BigDecimal(needpay)).compareTo(obj.getCharge_Num()) <= 0 ){
				ret=true;
			}
		}
		
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	@RequestMapping("/verify_storeName.htm")
	public void verify_storeName(HttpServletRequest request,HttpServletResponse response, String store_name){
		boolean ret = true;
		Map params = new HashMap();
		params.put("storeName", store_name);

		List<Audit> audits = this.auditService
				.query("select obj from Audit obj where obj.store_name=:storeName",
						params, -1, -1);
		if (audits != null && audits.size() > 0) {
			ret = false;
		}
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 用户名ajax验证方法
	 * 
	 * @param response
	 */
	@RequestMapping("/verify_app_username.htm")
	public void verify_app_username(HttpServletRequest request,
			HttpServletResponse response, String userName, String id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("userName", userName);
		params.put("id", CommUtil.null2Long(id));
		List<User> users = this.userService
				.query("select obj from User obj where obj.userName=:userName and obj.id!=:id",
						params, -1, -1);
		if (users != null && users.size() > 0) {
			ret = false;
		}
		List<Audit> audits = this.auditService
				.query("select obj from Audit obj where obj.userName=:userName and obj.id!=:id",
						params, -1, -1);
		if (audits != null && audits.size() > 0) {
			ret = false;
		}
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/verify_seller_telephone.htm")
	public void verify_seller_telephone(HttpServletRequest request,
			HttpServletResponse response, String telephone) {
		boolean ret = true;
		Map<String,String> params = new HashMap<String,String>();
		params.put("telephone", CommUtil.null2String(telephone));
		List<User> users = this.userService
				.query("select obj from User obj where obj.telephone=:telephone",
						params, -1, -1);
		if (users != null && users.size() > 0) {
			ret = false;
		}
		List<Audit> audits = this.auditService
				.query("select obj from Audit obj where obj.telephone=:telephone",
						params, -1, -1);
		if (audits != null && audits.size() > 0) {
			ret = false;
		}
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//推荐手机号验证
	@RequestMapping("/verify_promotion_telephone.htm")
	public void verify_promotion_telephone(HttpServletRequest request,
			HttpServletResponse response, String promote_tel,String telephone) {
		boolean ret = true;
		if(promote_tel!=null && !"".equals(promote_tel)){
		if(promote_tel.equals(telephone)){
			ret = false;
		}
		Map<String,String> params = new HashMap<String,String>();
		params.put("promote_tel", CommUtil.null2String(promote_tel));
		List<User> users = this.userService
				.query("select obj from User obj where obj.telephone=:promote_tel",
						params, -1, -1);
		if (users != null && users.size() != 1) {
			ret = false;
		}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping("/verify_buyer_telephone.htm")
	public void verify_buyer_telephone(HttpServletRequest request,
			HttpServletResponse response, String telephone) {
		boolean ret = true;
		Map<String,String> params = new HashMap<String,String>();
		params.put("telephone", CommUtil.null2String(telephone));
		List<User> users = this.userService
				.query("select obj from User obj where obj.telephone=:telephone",
						params, -1, -1);
		if (users != null && users.size() > 0) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 用户名ajax验证方法
	 * 
	 * @param response
	 */
	@RequestMapping("/verify_username.htm")
	public void verify_username(HttpServletRequest request,
			HttpServletResponse response, String userName, String id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("userName", userName);
		params.put("id", CommUtil.null2Long(id));
		List<User> users = this.userService
				.query("select obj from User obj where obj.userName=:userName and obj.id!=:id",
						params, -1, -1);
		if (users != null && users.size() > 0) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 验证手机验证码
	 * @param request
	 * @param response
	 * @param mobile_verify_code
	 */
	@RequestMapping("/verify_verifycode.htm")
	public void verify_verifycode(HttpServletRequest request,
			HttpServletResponse response, String mobile_verify_code,String telephone) {
		boolean ret = false;
		Map<String,String> params = new HashMap<String,String>();
		params.put("mobile_verify_code", CommUtil.null2String(mobile_verify_code));
		params.put("telephone", CommUtil.null2String(telephone));
		
		List<MobileVerifyCode> mobilecodeList = this.mobileVerifyCodeServiceImpl
				.query("select obj from MobileVerifyCode obj where obj.code=:mobile_verify_code and obj.mobile=:telephone",
						params, -1, -1);
		if (mobilecodeList != null && mobilecodeList.size() > 0) {
			ret = true;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	/**
	 * 验证Email
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 */
	@RequestMapping("/verify_email.htm")
	public void verify_email(HttpServletRequest request,
			HttpServletResponse response, String email, String id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("email", email);
		params.put("id", CommUtil.null2Long(id));
		List<User> users = this.userService
				.query("select obj from User obj where obj.email=:email and obj.id!=:id",
						params, -1, -1);
		if (users != null && users.size() > 0) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 验证店铺名称是否重复
	 * 
	 * @param request
	 * @param response
	 * @param email
	 * @param id
	 */
	@RequestMapping("/verify_storename.htm")
	public void verify_storename(HttpServletRequest request,
			HttpServletResponse response, String store_name, String id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("store_name", store_name);
		params.put("id", CommUtil.null2Long(id));
		List<Store> users = this.storeService
				.query("select obj from Store obj where obj.store_name=:store_name and obj.id!=:id",
						params, -1, -1);
		if (users != null && users.size() > 0) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 验证手机号是否重复
	 * 
	 * @param request
	 * @param response
	 * @param mobile
	 * @param id
	 */
	@RequestMapping("/verify_telephone.htm")
	public void verify_telephone(HttpServletRequest request,
			HttpServletResponse response, String telephone, String id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("telephone", telephone);
		params.put("id", CommUtil.null2Long(id));
		List<User> users = this.userService
				.query("select obj from User obj where obj.telephone=:telephone and obj.id!=:id",
						params, -1, -1);
		if (users != null && users.size() > 0) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/weixin_menukey_verify.htm")
	public void weixin_menukey_verify(HttpServletRequest request,
			HttpServletResponse response, String menu_id, String menu_key,
			String store_id) throws IOException {
		boolean ret = true;
		Map params = new HashMap();
		params.put("menu_key", menu_key);
		params.put("store_id", CommUtil.null2Long(store_id));
		params.put("menu_id", CommUtil.null2Long(menu_id));
		List<VMenu> VMenus = this.vmenuService
				.query("select obj from VMenu obj where obj.menu_key=:menu_key and obj.store.id=:store_id and obj.id!=:menu_id",
						params, -1, -1);
		if (VMenus != null && VMenus.size() > 0) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 验证码生成
	 * 
	 * @param response
	 * @param mobile
	 * @throws IOException
	 */
	@RequestMapping("/verify.htm")
	public void verify(HttpServletRequest request,
			HttpServletResponse response, String name) throws IOException {
		response.setContentType("image/jpeg");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		HttpSession session = request.getSession(false);
		// 在内存中创建图象
		int width = 73, height = 27;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		// 获取图形上下文
		Graphics g = image.getGraphics();

		// 生成随机类
		Random random = new Random();

		// 设定背景色
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);

		// 设定字体
		g.setFont(new Font("Times New Roman", Font.PLAIN, 24));

		// 画边框
		// g.setColor(new Color());
		// g.drawRect(0,0,width-1,height-1);

		// 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}

		// 取随机产生的认证码(4位数字)
		String sRand = "";
		for (int i = 0; i < 4; i++) {
			String rand = CommUtil.randomInt(1).toUpperCase();
			sRand += rand;
			// 将认证码显示到图象中
			g.setColor(new Color(20 + random.nextInt(110), 20 + random
					.nextInt(110), 20 + random.nextInt(110)));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
			g.drawString(rand, 13 * i + 6, 24);
		}

		// 将认证码存入SESSION
		if (CommUtil.null2String(name).equals("")) {
			session.setAttribute("verify_code", sRand);
		} else {
			session.setAttribute(name, sRand);
		}
		// 图象生效
		g.dispose();
		ServletOutputStream responseOutputStream = response.getOutputStream();
		// 输出图象到页面
		ImageIO.write(image, "JPEG", responseOutputStream);

		// 以下关闭输入流！
		responseOutputStream.flush();
		responseOutputStream.close();
	}

	private Color getRandColor(int fc, int bc) {// 给定范围获得随机颜色
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
}
