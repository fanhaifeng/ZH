package com.lakecloud.view.web.action;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.json.simple.JSONObject;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.Formatter;
import com.lakecloud.core.tools.LoginFlag;
import com.lakecloud.core.tools.Md5Encrypt;
import com.lakecloud.core.tools.SendMessageUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.Accessory;
import com.lakecloud.foundation.domain.Album;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Audit;
import com.lakecloud.foundation.domain.Branch;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.IntegralLog;
import com.lakecloud.foundation.domain.Integration;
import com.lakecloud.foundation.domain.MobileVerifyCode;
import com.lakecloud.foundation.domain.Role;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreClass;
import com.lakecloud.foundation.domain.Template;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.WaterMark;
import com.lakecloud.foundation.service.IAccessoryService;
import com.lakecloud.foundation.service.IAlbumService;
import com.lakecloud.foundation.service.IAreaService;
import com.lakecloud.foundation.service.IAuditService;
import com.lakecloud.foundation.service.IBranchService;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IIntegralLogService;
import com.lakecloud.foundation.service.IIntegrationService;
import com.lakecloud.foundation.service.IMobileVerifyCodeService;
import com.lakecloud.foundation.service.IRoleService;
import com.lakecloud.foundation.service.IStoreClassService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.ITemplateService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.foundation.service.impl.UserServiceImpl;
import com.lakecloud.manage.admin.tools.StoreTools;
import com.lakecloud.uc.api.UCClient;
import com.lakecloud.uc.api.UCTools;
import com.lakecloud.view.web.tools.ImageViewTools;

/**
 * 
 * <p>
 * Title: LoginViewAction.java
 * </p>
 * 
 * <p>
 * Description:
 * 用户登录、注册管理控制器，用来管理用户登录、注册、UC统一登录等功能,V1.3版本开始独立出来，为了方便外部系统（如UCenter等）的使用
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-5-9
 * 
 * @version LakeCloud_C2C 1.4 集群版
 */
@Controller
public class LoginViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private ImageViewTools imageViewTools;
	@Autowired
	private UCTools ucTools;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IStoreClassService storeClassService;
	@Autowired
	private IAuditService auditServier;
	@Autowired
	private IMobileVerifyCodeService mobileverifycodeService;
	@Autowired
	private IAuditService auditService;
	@Autowired
	private IBranchService branchService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IIntegrationService iIntegrationService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private StoreTools storeTools;
	
	
	@RequestMapping("/register_next.htm")
	public ModelAndView register_next(HttpServletRequest request,
			HttpServletResponse response,String userName,String password,
			String telephone,String area,String mobile_verify_code,String code,String promote_tel){
		ModelAndView mv = new JModelAndView("register_next.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("userName",userName);
		mv.addObject("code",code);
		mv.addObject("password",password);
		mv.addObject("telephone",telephone);
		mv.addObject("promote_tel",promote_tel);
		mv.addObject("area",area);
		mv.addObject("mobile_verify_code",mobile_verify_code);
		return mv;
	}
	@RequestMapping("/is_login.htm")
	public void is_login(HttpServletRequest request,
			HttpServletResponse response){
		int ret=0;
		User user = SecurityUserHolder.getCurrentUser();
		if(user!=null)
			ret=1;
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 用户登录页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/user/login.htm")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response, String url) {
		ModelAndView mv = new JModelAndView("login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		boolean domain_error = CommUtil.null2Boolean(request.getSession(false)
				.getAttribute("domain_error"));
		if (url != null && !url.equals("")) {
			request.getSession(false).setAttribute("refererUrl", url);
		}else
			request.getSession(false).setAttribute("refererUrl", CommUtil.getURL(request)+ "/index.htm?");
		if (domain_error) {
			mv.addObject("op_title", "域名绑定错误，请与http://www.chinacloud.net联系");
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		} else {
			mv.addObject("imageViewTools", imageViewTools);
		}
		mv.addObject("uc_logout_js",
				request.getSession(false).getAttribute("uc_logout_js"));
		String iskyshop_view_type = CommUtil.null2String(request.getSession(
				false).getAttribute("iskyshop_view_type"));
		if (iskyshop_view_type != null && !iskyshop_view_type.equals("")) {
			if (iskyshop_view_type.equals("weixin")) {
				String store_id = CommUtil.null2String(request
						.getSession(false).getAttribute("store_id"));
				mv = new JModelAndView("weixin/success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "退出成功！");
				mv.addObject("url",url);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/index.htm?store_id=" + store_id);
			}
		}
		return mv;
	}

	/**
	 * 买家用户注册页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/register.htm")
	public ModelAndView register(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("register.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		
		List<Area> areas = this.areaService
				.query("select obj from Area obj where obj.parent.id is null",
						null, -1, -1);
		
		List<StoreClass> scs = this.storeClassService
				.query("select obj from StoreClass obj where obj.parent.id is null",
						null, -1, -1);
		mv.addObject("areas", areas);
		mv.addObject("scs", scs);
		return mv;
	}
	
	/**
	 * 卖家用户注册页面
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/register_seller.htm")
	public ModelAndView register_seller(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("register_seller.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		List<Area> areas = this.areaService
				.query("select obj from Area obj where obj.parent.id is null",
						null, -1, -1);
		
		List<StoreClass> scs = this.storeClassService
				.query("select obj from StoreClass obj where obj.parent.id is null",
						null, -1, -1);
		mv.addObject("areas", areas);
		mv.addObject("scs", scs);
		request.getSession(false).removeAttribute("verify_code");
		return mv;
	}
	
	@RequestMapping("/register_finish.htm")
	public String register_finish(HttpServletRequest request,
			HttpServletResponse response, String userName,String password,
			String telephone,String mobile_verify_code,String area, String code,
			String care,String plant,String cul_area,String promote_tel) throws HttpException, IOException {
		boolean reg = true;// 防止机器注册，如后台开启验证码则强行验证验证码
		if (code != null && !code.equals("")) {
			code = CommUtil.filterHTML(code);// 过滤验证码
		}
		
		if (this.configService.getSysConfig().isSecurityCodeRegister()) {
			if (!request.getSession(false).getAttribute("verify_code")
					.equals(code)) {
				reg = false;
			}
		}
		
		// 进一步控制用户名不能重复，防止在未开启注册码的情况下注册机恶意注册
		Map params = new HashMap();
		params.put("userName", userName);
		List<User> users = this.userService.query(
				"select obj from User obj where obj.userName=:userName",
				params, -1, -1);
		if (users != null && users.size() > 0) {
			reg = false;
		}
		
		//验证码是否有误
		MobileVerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile", telephone);
		if( mvc==null || !mobile_verify_code.equals(mvc.getCode()) ){
			reg = false;
		}
		
		//根据地区获取分公司
		Area a = this.areaService.getObjById(Long.parseLong(area));
		Branch branch = a.getBranch();
		Long branchId = Long.MIN_VALUE;
		if(branch!=null){
			 branchId = branch.getId();
		}
		
		if (reg) {
			this.userService.register_finish(this.configService.getSysConfig(),userName, 
					password, telephone, a,branchId, mobile_verify_code, care, plant, cul_area,promote_tel);
			request.getSession(false).removeAttribute("verify_code");
			return "redirect:/iskyshop_login.htm?username="
					+ CommUtil.encode(userName) + "&password=" + password
					+ "&encode=true";
		} else {
			return "redirect:register.htm";
		}
	}

	
	@RequestMapping("/register_seller_finish.htm")
	public ModelAndView register_seller_finish(HttpServletRequest request,
			HttpServletResponse response,
			String userName,String password,
			String telephone,String area, 
			String true_name,String store_name,String sc_id,
			String store_area,String store_address,
			String store_ower_card,String mobile_verify_code,String code,
			String care,String plant,String cul_area,String promote_tel,
			String file1,String file2,String file3) throws Exception{
	
		boolean reg = true;// 防止机器注册，如后台开启验证码则强行验证验证码
		ModelAndView mv = null;
		
		if(userName=="" || password=="" || telephone=="" ||
		   area=="" || mobile_verify_code=="" ||true_name=="" || 
		   store_name=="" || sc_id=="" || store_area=="" || 
		   store_address=="" || store_ower_card=="" || mobile_verify_code=="" ||
		   file1== "" || file2=="" || file3 == ""){
			reg=false;
		}
			
		if (code != null && !code.equals("")) {
			code = CommUtil.filterHTML(code);// 过滤验证码
		}
		
		if (this.configService.getSysConfig().isSecurityCodeRegister()) {
			if (!request.getSession(false).getAttribute("verify_code")
					.equals(code)) {
				reg = false;
			}
		}
		// 进一步控制用户名不能重复，防止在未开启注册码的情况下注册机恶意注册
		Map params = new HashMap();
		params.put("userName", userName);
		List<Audit> audits = this.auditService.query(
				"select obj from Audit obj where obj.userName=:userName",
				params, -1, -1);
		//审核表是否用户名有重复
		if (audits != null && audits.size() > 0) {
			reg = false;
		}
		
		//用户表是否有重复
		List<User> users = this.userService.query(
				"select obj from User obj where obj.userName=:userName",
				params, -1, -1);
		if (users != null && users.size() > 0) {
			reg = false;
		}
		
		//验证码是否有误
		MobileVerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile", telephone);
		if( mvc == null || !mobile_verify_code.equals(mvc.getCode())){
			reg=false;
		}
		
		if (reg) {
			Audit audit = new Audit();
			audit.setStatus("0");
			audit.setUserName(userName);
			audit.setPassword(Md5Encrypt.md5(password).toLowerCase());
			audit.setTelephone(telephone);
			
			
			//推荐用户手机号存在mobile字段
			if(promote_tel!=null && !"".equals(promote_tel)){
				if(UserServiceImpl.isPhone(promote_tel)==true){
					audit.setMobile(promote_tel);
				}
			}
			
			Area a1 = new Area();
			a1.setId(Long.parseLong(area));
			audit.setArea(a1);
			
			audit.setAddTime(new Date());			
			audit.setTrueName(true_name);
			audit.setStore_name(store_name);
			
			StoreClass cls = new StoreClass();
			cls.setId(Long.parseLong(sc_id));
			audit.setStore_class(cls);
			
			Accessory  accessory1 = this.accessoryService.getObjById(CommUtil.null2Long(file1));
			audit.setFile1(accessory1);
			Accessory  accessory2 = this.accessoryService.getObjById(CommUtil.null2Long(file2));
			audit.setFile2(accessory2);
			Accessory  accessory3 = this.accessoryService.getObjById(CommUtil.null2Long(file3));
			audit.setFile3(accessory3);
			
			//区级
			Area store_aArea =  this.areaService.getObjById(Long.parseLong(store_area));
			//区级的父级
			if( store_aArea.getLevel() == 2 && store_aArea.getParent()!=null){
				 Area parentArea = this.areaService.getObjById(store_aArea.getParent().getId()); 
				//根据area获取分公司
				if(parentArea.getBranch()!=null){
					System.out.println(parentArea.getBranch().getId());
					Branch branch = this.branchService.getObjById(parentArea.getBranch().getId());
					if(branch!=null){
						Branch b = new Branch();
						b.setId(parentArea.getBranch().getId());
						audit.setBranch(b);
						audit.setBranch_code(branch.getCode());
					}
				}
			}
			
			
			audit.setStore_area(store_aArea);
			audit.setStore_addr(store_address);
			audit.setId_card(store_ower_card);	
			
			audit.setCare(CommUtil.null2String(care));
			audit.setPlant(CommUtil.null2String(plant));
			audit.setCul_area(CommUtil.null2String(cul_area));
			
			Area a = this.areaService.getObjById(Long.parseLong(area));
			if(a!=null){
				audit.setArea(a);
			}
	
			this.auditService.save(audit);	
			mv = new JModelAndView("success.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "注册卖家成功，审核结果将会以短信方式通知您！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			
			if (this.configService.getSysConfig().isSmsEnbale()) {
				//给经销商发送短信
				this.send_register_sms(request, audit,"sms_toseller_open_store_notify");
			}
		} else {
			mv = new ModelAndView("redirect:register_seller.htm");
		}
		return mv;
	}
	
	@RequestMapping("/register_seller_next.htm")
	public ModelAndView register_seller_next(HttpServletRequest request,
			HttpServletResponse response,	
			String userName,String password,
			String telephone,String area, 
			String true_name,String store_name,String sc_id,
			String store_area,String store_address,String code,
			String store_ower_card,String mobile_verify_code,String promote_tel,
			String file1,String file2,String file3){
		ModelAndView mv = new JModelAndView("register_seller_next.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("userName",userName);
		mv.addObject("password",password);
		mv.addObject("telephone",telephone);
		mv.addObject("area",area);
		mv.addObject("true_name",true_name);
		mv.addObject("store_name",store_name);
		mv.addObject("sc_id",sc_id);
		mv.addObject("code",code);
		mv.addObject("store_area",store_area);
		mv.addObject("store_address",store_address);
		mv.addObject("store_ower_card",store_ower_card);
		mv.addObject("mobile_verify_code",mobile_verify_code);
		mv.addObject("promote_tel",promote_tel);	
		mv.addObject("file1",file1);
		mv.addObject("file2",file2);
		mv.addObject("file3",file3);
		return mv;
	}
	
	private void send_register_sms(HttpServletRequest request, Audit audit, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			if(this.configService.getSysConfig().isSmsEnbale()) {
				SendMessageUtil sendmessage = new SendMessageUtil();				
				String path = request.getSession().getServletContext()
						.getRealPath("/")
						+ "/vm/";
				PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(path + "msg.vm", false), "UTF-8"));
				pwrite.print(template.getContent());	
				System.out.println(path+"======="+template.getContent());
				pwrite.flush();
				pwrite.close();
				// 生成模板
				Properties p = new Properties();
				p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,path);
				p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
				p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
				Velocity.init(p);
				System.out.println(path);
				org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm",
						"UTF-8");
				VelocityContext context = new VelocityContext();
				context.put("audit",audit);
				StringWriter writer = new StringWriter();
				blank.merge(context, writer);
				System.out.println(writer.toString());
				String content = writer.toString();
				try{
					sendmessage.sendHttpPost(audit.getTelephone(), content);
				}catch (Exception e) {
					// TODO: handle exception
				}
			}	
		}
	}
	
	/**
	 * springsecurity登录成功后跳转到该页面，如有登录相关处理可以在该方法中完成
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/user_login_success.htm")
	public ModelAndView user_login_success(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String url=(String) request.getSession().getAttribute("refererUrl");
		if(url!=null&&url!=""){			
		}else			
		url = CommUtil.getURL(request) + "/index.htm?login_flag=1";// TODO 与生产不同，是否需要跳转原页面？
		String iskyshop_view_type = CommUtil.null2String(request.getSession(
				false).getAttribute("iskyshop_view_type"));
		if (iskyshop_view_type != null && !iskyshop_view_type.equals("")) {
			if (iskyshop_view_type.equals("weixin")) {
				String store_id = CommUtil.null2String(request
						.getSession(false).getAttribute("store_id"));
				mv = new JModelAndView("weixin/success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				url=CommUtil.getURL(request) + "/weixin/platform/index.htm?login_flag=1";
				/*url = CommUtil.getURL(request) + "/weixin/index.htm?store_id="
						+ store_id;*/
			}
		}
		Area area  = areaService.getObjById(SecurityUserHolder.getCurrentUser()
				.getArea().getId());
		mv.addObject("area_name", area.getAreaName());
		HttpSession session = request.getSession(false);
		if (session.getAttribute("refererUrl") != null
				&& !session.getAttribute("refererUrl").equals("")) {
			url = (String) session.getAttribute("refererUrl");
			session.removeAttribute("refererUrl");
		}
		if (this.configService.getSysConfig().isUc_bbs()) {
			String uc_login_js = CommUtil.null2String(request.getSession(false)
					.getAttribute("uc_login_js"));
			mv.addObject("uc_login_js", uc_login_js);
		}
		String bind = CommUtil.null2String(request.getSession(false)
				.getAttribute("bind"));
		User user = SecurityUserHolder.getCurrentUser();
		/*int user_integ = 0;
		//判断主表是否有该用户
		String queryString = "select obj from Integration obj where obj.user.id="+user.getId();
		List<Integration> intList = iIntegrationService.query(queryString, null, -1, -1);
		if( intList != null && intList.size() ==1 ){
			
			Integration getInte = intList.get(0);
			user_integ = getInte.getIntegrals();
		}*/
		
		JSONObject obj = new JSONObject();
		obj.put("userName", user.getUserName());
		obj.put("password", user.getPassword());
		obj.put("areaId", user.getArea()==null?"":user.getArea().getId());
		obj.put("areaName", user.getArea()==null?"":user.getArea().getAreaName());
		if(user.getArea() != null){
			Area areaParent=areaService.getObjById(user.getArea().getId());
				obj.put("parent_areaId", areaParent.getParent()==null?"":areaParent.getParent().getId());
				obj.put("parent_areaName", areaParent.getParent()==null?"":areaParent.getParent().getAreaName());
		}else{
			obj.put("parent_areaId", "");
			obj.put("parent_areaName","");
		}
		obj.put("care", user.getCrop()==null?"":user.getCrop().getCare());
		obj.put("plant", user.getCrop()==null?"":user.getCrop().getPlant());
		obj.put("cul_area", user.getCrop()==null?"":user.getCrop().getCul_area());
		obj.put("telephone", user.getTelephone()==null?"":user.getTelephone() );
		obj.put("user_integ", user.getIntegral());
		
		//密码加密处理
		String login_password_string = LoginFlag.encodeLoginPassword(session.getAttribute("login_password").toString());
		obj.put("sequence", login_password_string);
		
		mv.addObject("user", user);
		mv.addObject("userInfo",obj.toJSONString());
		
		//String decoString = LoginFlag.decodeLogin(login_password_string);
		session.removeAttribute("login_password");
		if (!bind.equals("")) {
			mv = new JModelAndView(bind + "_login_bind.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			request.getSession(false).removeAttribute("bind");
		}
		mv.addObject("op_title", "登录成功");
		mv.addObject("url", url);
		return mv;
	}

	/**
	 * 弹窗登录页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/user_dialog_login.htm")
	public ModelAndView user_dialog_login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user_dialog_login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	class StoreTest implements Runnable {
		private int store_thread_num;

		public StoreTest(int num) {
			this.store_thread_num = num;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("第" + this.store_thread_num + "线程启动");
			for (int i = (store_thread_num - 1) * 2000; i < store_thread_num * 2000; i++) {
				Store store = new Store();
				Random ran = new Random();
				User user = userService.getObjById(CommUtil.null2Long(i));
				store.setAddTime(new Date());
				store.setStore_info("store" + i);
				store.setTemplate("default");
				store.setStore_status(2);
				storeService.save(store);
				Map params = new HashMap();
				params.put("type", "SELLER");
				List<Role> roles = roleService.query(
						"select obj from Role obj where obj.type=:type",
						params, -1, -1);
				user.setStore(store);
				user.getRoles().addAll(roles);
				userService.update(user);
			}
		}

	}

	class UserTest implements Runnable {

		private int user_thread_num;

		public UserTest(int num) {
			this.user_thread_num = num;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("第" + this.user_thread_num + "线程启动");
			for (int i = (user_thread_num - 1) * 2000; i < user_thread_num * 2000; i++) {
				User user = new User();
				user.setUserName("user" + i);
				user.setUserRole("BUYER");
				user.setAddTime(new Date());
				user.setEmail("user" + i + "@163.com");
				user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
				Map params = new HashMap();
				params.clear();
				params.put("type", "BUYER");
				List<Role> roles = roleService.query(
						"select obj from Role obj where obj.type=:type",
						params, -1, -1);
				user.getRoles().addAll(roles);
				if (configService.getSysConfig().isIntegral()) {
					user.setIntegral(configService.getSysConfig()
							.getMemberRegister());
					userService.save(user);
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("用户注册增加"
							+ configService.getSysConfig().getMemberRegister()
							+ "分");
					log.setIntegral(configService.getSysConfig()
							.getMemberRegister());
					log.setIntegral_user(user);
					log.setType("reg");
					integralLogService.save(log);
				} else {
					userService.save(user);
				}
				// 创建用户默认相册
				Album album = new Album();
				album.setAddTime(new Date());
				album.setAlbum_default(true);
				album.setAlbum_name("默认相册");
				album.setAlbum_sequence(-10000);
				album.setUser(user);
				albumService.save(album);
			}
			user_thread_num = user_thread_num + 1;
		}

	}

	class GoodsTest implements Runnable {
		private int goods_thread_num;

		public GoodsTest(int num) {
			this.goods_thread_num = num;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("第" + this.goods_thread_num + "线程启动");
			for (int i = (goods_thread_num - 1) * 200; i < goods_thread_num * 200; i++) {
				Goods goods = new Goods();
				goods.setAddTime(new Date());
				goods.setGc(goodsClassService.getObjById(3l));
				goods.setGoods_name("测试商品" + i);
				double price = CommUtil.null2Double(CommUtil.randomInt(3));
				goods.setStore_price(BigDecimal.valueOf(price));
				goods.setGoods_price(BigDecimal.valueOf(price + 80));
				Random ran = new Random();
				Store goods_store = storeService.getObjById(CommUtil
						.null2Long(ran.nextInt(20000)));
				goods.setGoods_store(goods_store);
				goodsService.save(goods);
			}
			System.out.println("第" + this.goods_thread_num + "线程运行完毕");
			if (this.goods_thread_num == 5000) {
				System.out.println("所有线程运行完毕，生成100万件商品");
			}
		}

	}

	
	@RequestMapping("/register/swf_upload.htm")
	public void swf_upload(HttpServletRequest request,HttpServletResponse response) {
		String path = this.storeTools.createRegisterFolder(request,this.configService.getSysConfig());
		String url = this.storeTools.createRegisterFolderURL(
				this.configService.getSysConfig());		
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("imgFile");
		Map json_map = new HashMap();
		try {
			Map map = CommUtil.saveFileToServer(request, "imgFile", path,
					null, null);
			Accessory image = new Accessory();
			image.setAddTime(new Date());
			image.setExt((String) map.get("mime"));
			image.setPath(url);
			image.setWidth(CommUtil.null2Int(map.get("width")));
			image.setHeight(CommUtil.null2Int(map.get("height")));
			image.setName(CommUtil.null2String(map.get("fileName")));
			this.accessoryService.save(image);
			System.out.println(CommUtil.getURL(request) + "/" + url + "/"+ image.getName());
			json_map.put("url", CommUtil.getURL(request) + "/" + url + "/"
					+ image.getName());
			json_map.put("id", image.getId());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	@RequestMapping("/init_test_data.htm")
	public ModelAndView init_test_data(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// 初始化一百万商品
		System.out.println("开始初始化100万商品信息....");
		for (int i = 1; i <= 5000; i++) {
			GoodsTest goodsTest1 = new GoodsTest(i);
			Thread thread1 = new Thread(goodsTest1);
			thread1.start();
		}
		mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		return mv;
	}
}
