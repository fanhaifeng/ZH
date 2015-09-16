package com.lakecloud.weixin.store.view.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ibm.db2.jcc.sqlj.m;
import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.Md5Encrypt;
import com.lakecloud.core.tools.SendMessageUtil;
import com.lakecloud.foundation.domain.Album;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Audit;
import com.lakecloud.foundation.domain.Branch;
import com.lakecloud.foundation.domain.Crop;
import com.lakecloud.foundation.domain.Document;
import com.lakecloud.foundation.domain.IntegralLog;
import com.lakecloud.foundation.domain.MobileVerifyCode;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Role;
import com.lakecloud.foundation.domain.StoreClass;
import com.lakecloud.foundation.domain.Template;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IAlbumService;
import com.lakecloud.foundation.service.IAreaService;
import com.lakecloud.foundation.service.IAuditService;
import com.lakecloud.foundation.service.IBranchService;
import com.lakecloud.foundation.service.ICropService;
import com.lakecloud.foundation.service.IDocumentService;
import com.lakecloud.foundation.service.IIntegralLogService;
import com.lakecloud.foundation.service.IMobileVerifyCodeService;
import com.lakecloud.foundation.service.IRoleService;
import com.lakecloud.foundation.service.IStoreClassService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.ITemplateService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.foundation.service.impl.UserServiceImpl;
import com.lakecloud.manage.admin.tools.MsgTools;
import com.lakecloud.view.web.tools.ImageViewTools;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * @info 微信店铺客户端注册、登录管理控制器
 * @since V1.3
 * @version 1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang hz
 * 
 */
@Controller
public class WeixinStoreLoginViewAction {
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
	private ImageViewTools imageViewTools;
	@Autowired
	private IDocumentService documentService;
	@Autowired
	private IAuditService auditService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IStoreClassService storeClassService;
	@Autowired
	private IMobileVerifyCodeService mobileverifycodeService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private ICropService cropService;
	@Autowired
	private IBranchService branchService;

	/**
	 * 用户登录页面
	 * 
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/weixin/login.htm")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response, String url) {
		ModelAndView mv = new JModelAndView("weixin/login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		request.getSession(false).setAttribute("iskyshop_view_type","weixin");
		return mv;
	}
	/*
	@RequestMapping("/weixin/login.htm")
	public void login(HttpServletRequest request,
			HttpServletResponse response, String url) throws IOException {
		response.sendRedirect(CommUtil.getURL(request) + "/weixin/platform/index.htm");
	}
	*/
	/**
	 * 法律申明
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/weixin/introduce.htm")
	public ModelAndView introduce(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/introduce.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
	
	/**
	 * 找回密码
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/weixin/find_password.htm")
	public ModelAndView findPassd(HttpServletRequest request,
			HttpServletResponse response){
		ModelAndView mv = new JModelAndView("weixin/findPass.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		return mv;
	}
	
	/**
	 * 重置密码
	 * @param request
	 * @param response
	 * @param telephone
	 * @return
	 */
	@RequestMapping("/weixin/resetPassword.htm")
	public ModelAndView resetPassword(HttpServletRequest request,
			HttpServletResponse response,String telephone){
		ModelAndView mv = null;
		request.getSession(false).removeAttribute("verify_code");	
		try{
			User user = this.userService.getObjByProperty("telephone",CommUtil.null2String(telephone));
			if(user!=null){
				String passwordString = CommUtil.randomString(6).toLowerCase();
				SendMessageUtil sendmessage = new SendMessageUtil();
				try{
					sendmessage.sendHttpPost(user.getTelephone(), "您好，您的密码被重置为"+passwordString+"，请登陆后即时修改，以免帐号泄漏。");
					user.setPassword(Md5Encrypt.md5(passwordString).toLowerCase());
					this.userService.update(user);
					
					mv = new JModelAndView("weixin/success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "密码修改成功，新密码将已短信形式发送至您的手机！");		
					mv.addObject("url", CommUtil.getURL(request)
							+ "/weixin/login.htm");
				}catch (Exception e) {
					// TODO: handle exception
				}
			}else{
				mv = new JModelAndView("weixin/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您的手机号尚未开通农易宝账户！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/find_password.htm");
			}
		}catch (Exception e) {
			// TODO: handle exception
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您的手机号绑定的账户异常，请联系管理员进行修改！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/find_password.htm");
		}
		return mv;
	}
	
	/**
	 * 手机短信发送
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/weixin/account_mobile_sms.htm")
	public void account_mobile_sms(HttpServletRequest request,
			HttpServletResponse response, String type, String mobile)
			throws UnsupportedEncodingException {
		String ret = "100";
		if (type.equals("mobile_vetify_code")) {
			String code = CommUtil.randomInt(4).toLowerCase();
			String content = "尊敬的用户，你正在注册成为会员，手机验证码为：" + code + "。["
					+ this.configService.getSysConfig().getTitle() + "]";
			if (this.configService.getSysConfig().isSmsEnbale()) {
				SendMessageUtil sendmessage = new SendMessageUtil();
				try{
					sendmessage.sendHttpPost(mobile, content);
					MobileVerifyCode mvc = this.mobileverifycodeService
							.getObjByProperty("mobile", mobile);
					if (mvc == null) {
						mvc = new MobileVerifyCode();
					}
					mvc.setAddTime(new Date());
					mvc.setCode(code);
					mvc.setMobile(mobile);
					this.mobileverifycodeService.update(mvc);
				}catch (Exception e) {
					// TODO: handle exception
				}
			} else {
				ret = "300";
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
	}

	/**
	 * 用户注册页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/weixin/register.htm")
	public ModelAndView register(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/register.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		Document obj = this.documentService.getObjByProperty("mark", "agree");
		mv.addObject("obj", obj);
		return mv;
	}
	
	@RequestMapping("/weixin/register_seller.htm")
	public ModelAndView register_seller(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/register_seller.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		Document obj = this.documentService.getObjByProperty("mark", "agree");
		mv.addObject("obj", obj);
		
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

	@RequestMapping("/weixin/register_seller_finish.htm")
	public ModelAndView register_seller_finish(HttpServletRequest request,
			HttpServletResponse response,
			String userName,String password,
			String telephone,String area, 
			String true_name,String store_name,String sc_id,
			String store_area,String store_address,
			String store_ower_card,String mobile_verify_code,
			String care,String plant,String cul_area,String promote_tel) throws Exception{
		
		boolean reg = true;// 防止机器注册，如后台开启验证码则强行验证验证码
		ModelAndView mv = null;
		
		if(userName=="" || password=="" || telephone=="" ||
		   area=="" || mobile_verify_code=="" ||true_name=="" || 
		   store_name=="" || sc_id=="" || store_area=="" || 
		   store_address=="" || store_ower_card=="" || mobile_verify_code==""){
			reg=false;
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
			mv = new JModelAndView("weixin/success.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "注册卖家成功，审核结果将会以短信方式通知您！");
			mv.addObject("url", CommUtil.getURL(request) + "/weixin/platform/index.htm");
			
			if (this.configService.getSysConfig().isSmsEnbale()) {
				//给经销商发送短信
				this.send_register_sms(request, audit,"sms_toseller_open_store_notify");
			}
		} else {
			mv = new ModelAndView("redirect:register_seller.htm");
		}
		return mv;
	}
	
	/**
	 * 经销商注册完成后发送短信通知
	 * @param request
	 * @param audit
	 * @param mark
	 * @throws Exception
	 */
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
	
	@RequestMapping("/weixin/register_next.htm")
	public ModelAndView register_next(HttpServletRequest request,
			HttpServletResponse response,String userName,String password,
			String telephone,String area,String mobile_verify_code,String promote_tel){
		ModelAndView mv = new JModelAndView("weixin/register_next.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("userName",userName);
		mv.addObject("password",password);
		mv.addObject("telephone",telephone);
		mv.addObject("promote_tel",promote_tel);
		mv.addObject("area",area);
		mv.addObject("mobile_verify_code",mobile_verify_code);
		return mv;
	}
	
	@RequestMapping("/weixin/register_seller_next.htm")
	public ModelAndView register_seller_next(HttpServletRequest request,
			HttpServletResponse response,	
			String userName,String password,
			String telephone,String area, 
			String true_name,String store_name,String sc_id,
			String store_area,String store_address,
			String store_ower_card,String mobile_verify_code,String promote_tel){
		ModelAndView mv = new JModelAndView("weixin/register_seller_next.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("userName",userName);
		mv.addObject("password",password);
		mv.addObject("telephone",telephone);
		mv.addObject("area",area);
		mv.addObject("true_name",true_name);
		mv.addObject("store_name",store_name);
		mv.addObject("sc_id",sc_id);
		mv.addObject("store_area",store_area);
		mv.addObject("store_address",store_address);
		mv.addObject("store_ower_card",store_ower_card);
		mv.addObject("mobile_verify_code",mobile_verify_code);
		mv.addObject("promote_tel",promote_tel);
		return mv;
	}
	
	/**
	 * 买家注册
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param telephone
	 * @param area
	 * @param mobile_verify_code
	 * @return
	 */
	@RequestMapping("/weixin/register_finish.htm")
	public String register_finish(HttpServletRequest request,
			HttpServletResponse response,String userName,String password,
			String telephone,String area,String mobile_verify_code,
			String care,String plant,String cul_area,String promote_tel) throws HttpException, IOException {
		boolean reg = true;// 防止机器注册，如后台开启验证码则强行验证验证码
		// 进一步控制用户名不能重复，防止在未开启注册码的情况下注册机恶意注册
		if(userName=="" || password=="" || telephone=="" || area=="" || mobile_verify_code==""){
			reg=false;
		}
		
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
	
		Area a = this.areaService.getObjById(Long.parseLong(area));
		Branch branch = a.getBranch();
		Long branchId = Long.MIN_VALUE;
		if(branch!=null){
			 branchId = branch.getId();
		}
		
		if(reg){
			this.userService.register_finish(this.configService.getSysConfig(),userName, 
					password, telephone, a,branchId, mobile_verify_code, care, plant, cul_area,promote_tel);
			request.getSession(false).removeAttribute("verify_code");
			return "redirect:../iskyshop_login.htm?username="
					+ CommUtil.encode(userName) + "&password=" + password
					+ "&encode=true&mark=weixin";
		}else{
			return "redirect:register.htm";
		}
	}
}
