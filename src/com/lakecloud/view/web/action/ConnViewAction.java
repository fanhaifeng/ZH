package com.lakecloud.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.Md5Encrypt;
import com.lakecloud.core.tools.SendMessageUtil;
import com.lakecloud.foundation.domain.Album;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Audit;
import com.lakecloud.foundation.domain.Crop;
import com.lakecloud.foundation.domain.IntegralLog;
import com.lakecloud.foundation.domain.Integration;
import com.lakecloud.foundation.domain.Integration_Child;
import com.lakecloud.foundation.domain.Integration_Log;
import com.lakecloud.foundation.domain.Role;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreClass;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IAlbumService;
import com.lakecloud.foundation.service.IAreaService;
import com.lakecloud.foundation.service.IAuditService;
import com.lakecloud.foundation.service.IBranchService;
import com.lakecloud.foundation.service.ICropService;
import com.lakecloud.foundation.service.IIntegralLogService;
import com.lakecloud.foundation.service.IIntegrationService;
import com.lakecloud.foundation.service.IIntegration_ChildService;
import com.lakecloud.foundation.service.IIntegration_LogService;
import com.lakecloud.foundation.service.IMobileVerifyCodeService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IRoleService;
import com.lakecloud.foundation.service.IStoreClassService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.ITemplateService;
import com.lakecloud.foundation.service.IUserService;

/**
 * 该类用于给农化app提供接口
 * @author FAN
 *
 */
@Controller
public class ConnViewAction {
	@Autowired
	private IUserService userService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IBranchService branchService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IAuditService auditService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IStoreClassService storeClassService;
	@Autowired
	private ICropService cropService;
	@Autowired
	private IIntegrationService integrationService;
	@Autowired
	private IIntegration_ChildService integration_ChildService;
	@Autowired
	private IIntegration_LogService integration_LogService;

	/**
	 * 修改用户信息
	 * @param request
	 * @param response
	 * @param userName
	 * @param telephone
	 * @param area_id
	 */
	@RequestMapping("/changeUserInfo.htm")
	public  void changeUserInfo(HttpServletRequest request,HttpServletResponse response,
			String userName,String telephone,String area_id,
			String care,String plant,String cul_area){		
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("timestamp",new Date().getTime());
		
		PrintWriter writer;	
		if(userName == null || "".equals(userName)){	
			jsonObj.put("resultCode","ERROR");
			jsonObj.put("resultData","用户名不能为空。");	
		}else{
			if(telephone == null || "".equals(telephone)){
				jsonObj.put("resultCode","ERROR");
				jsonObj.put("resultData","手机号不能为空。");	
			}else{
				Map<String, String> params = new HashMap<String, String>();
				params.put("telephone", telephone);
				params.put("userName", userName);
				String query = "select obj from User obj where obj.telephone=:telephone and obj.userName!=:userName";
				List<User> userList = this.userService.query(query, params, -1, -1);
				if( userList != null && userList.size() > 0  ){
					//手机号已经存在，不能修改为该手机号	
					jsonObj.put("resultCode","ERROR");
					jsonObj.put("resultData","手机号已经存在,无法修改。");	
				}else{
					Map<String, String> params1 = new HashMap<String, String>();
					params1.put("userName", userName);
					String query1 = "select obj from User obj where obj.userName=:userName";
					List<User> userList1 = this.userService.query(query1, params1, -1, -1);
					if( userList1 != null && userList1.size() ==1  ){
						User user = userList1.get(0);
						user.setTelephone(telephone);
						Area area = this.areaService.getObjById(Long.parseLong(area_id));
						if(area!=null){
							user.setArea(area);
						}
						
						
						Crop crop = user.getCrop();
						if(crop!=null){
							crop.setAddTime(new Date());
							crop.setCare(CommUtil.null2String(care));
							crop.setPlant(CommUtil.null2String(plant));
							crop.setCul_area(CommUtil.null2String(cul_area));
						
							this.cropService.update(crop);
							user.setCrop(crop);
						}else{
							Crop crop1 = new Crop();
							crop1.setAddTime(new Date());
							crop1.setCare(CommUtil.null2String(care));
							crop1.setPlant(CommUtil.null2String(plant));
							crop.setCul_area(CommUtil.null2String(cul_area));
						
							this.cropService.save(crop1);
							user.setCrop(crop1);
						}
						this.userService.update(user);
						jsonObj.put("resultCode","OK");
						jsonObj.put("resultData","用户信息修改成功。");	
					}else{
						jsonObj.put("resultCode","ERROR");
						jsonObj.put("resultData","该用户不存在。");	
					}
				}
			}		
		}
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 修改用户信息     根据用户名去改就行电话和区域
	 * @param request
	 * @param response
	 * @param userName
	 * @param telephone
	 * @param area_id
	 */
	@RequestMapping("/changeTelAndArea.htm")
	public  void changeUserInfo(HttpServletRequest request,HttpServletResponse response,
			String userName,String telephone,String area_id){		
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("timestamp",new Date().getTime());
		
		PrintWriter writer;	
		int flag=0;
		if(userName == null || "".equals(userName)){	
			jsonObj.put("resultCode","ERROR");
			jsonObj.put("resultData","用户名不能为空。");	
		}else{
			Map<String, String> params1 = new HashMap<String, String>();
			params1.put("userName", userName);
			String query1 = "select obj from User obj where obj.userName=:userName";
			List<User> userList1 = this.userService.query(query1, params1, -1, -1);
			if( userList1 != null && userList1.size() ==1  ){
				User user = userList1.get(0);
				//电话不为空时则修改电话
				if(telephone != null && !"".equals(telephone)){
					Map<String, String> params = new HashMap<String, String>();
					params.put("telephone", telephone);
					params.put("userName", userName);
					String query = "select obj from User obj where obj.telephone=:telephone and obj.userName!=:userName";
					List<User> userList = this.userService.query(query, params, -1, -1);
					if( userList != null && userList.size() > 0  ){
						//手机号已经存在，不能修改为该手机号	
						flag=1;
						
				    }else{
						user.setTelephone(telephone);
				    }
				}
				//区域不为空时则修改区域
				if(area_id != null && !"".equals(area_id)){
					Area area = this.areaService.getObjById(Long.parseLong(area_id));
					if(area!=null){
						user.setArea(area);
					}	
				}
				if(flag==0){
					this.userService.update(user);
					jsonObj.put("resultCode","OK");
					jsonObj.put("resultData","用户信息修改成功。");
				}else if(flag==1){
					jsonObj.put("resultCode","ERROR");
					jsonObj.put("resultData","手机号已经存在,无法修改。");	
				}
			}else{
				jsonObj.put("resultCode","ERROR");
				jsonObj.put("resultData","该用户不存在！");
			}
						
		}
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 修改作物信息
	 * @param request
	 * @param response
	 * @param userName
	 * @param care
	 * @param plant
	 * @param cul_area
	 */
	@RequestMapping("/changePlant.htm")
	public  void changePlant(HttpServletRequest request,HttpServletResponse response,
			String userName,String care,String plant,String cul_area){		
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("timestamp",new Date().getTime());
		
		PrintWriter writer;	
		if(userName == null || "".equals(userName)){	
			jsonObj.put("resultCode","ERROR");
			jsonObj.put("resultData","用户名不能为空。");	
		}else{	
			Map<String, String> params1 = new HashMap<String, String>();
			params1.put("userName", userName);
			String query1 = "select obj from User obj where obj.userName=:userName";
			List<User> userList1 = this.userService.query(query1, params1, -1, -1);
			if( userList1 != null && userList1.size() ==1  ){
				User user = userList1.get(0);
				
				Crop crop = user.getCrop();
				crop.setAddTime(new Date());
				crop.setCare(CommUtil.null2String(care));
				crop.setPlant(CommUtil.null2String(plant));
				crop.setCul_area(CommUtil.null2String(cul_area));
				if(crop!=null){	
					this.cropService.update(crop);	
				}else{
					this.cropService.save(crop);
				}
				user.setCrop(crop);
				this.userService.update(user);
				jsonObj.put("resultCode","OK");
				jsonObj.put("resultData","用户信息修改成功。");	
			}else{
				jsonObj.put("resultCode","ERROR");
				jsonObj.put("resultData","该用户不存在。");	
			}
		}
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 忘记密码找回   ====根据手机号，调用短信接口获取验证码后，可以将重置为新密码
	 * @param request
	 * @param response
	 * @param telephone
	 */
	@RequestMapping("/forgetPassword.htm")
	public void forgetPassword(HttpServletRequest request,HttpServletResponse response,
			String telephone,String password){
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("timestamp",new Date().getTime());
		
		PrintWriter writer;	
		if(telephone == null || "".equals(telephone)){	
			jsonObj.put("resultCode","ERROR");
			jsonObj.put("resultData","手机号不能为空。");	
		}else{
			//根据手机号查询
			User user = this.userService.getObjByProperty("telephone", telephone);
			SendMessageUtil sendmessage = new SendMessageUtil();
			try {
				if(user!=null){
					String content = "尊敬的用户，您的密码已经被重置为"+password+",请尽快登录修改。";	
					sendmessage.sendHttpPost(telephone, content);
					user.setPassword(Md5Encrypt.md5(password));
					this.userService.update(user);
					jsonObj.put("resultCode","OK");
					jsonObj.put("resultData","密码修改成功。");	
				}else{
					jsonObj.put("resultCode","ERROR");
					jsonObj.put("resultData","无此用户。");	
				}
			
			} catch (IOException e) {
				jsonObj.put("resultCode","ERROR");
				jsonObj.put("resultData","短信未发送成功，密码修改失败。");	
			}	
		}	
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 修改密码 ，手机号码
	 * @param request
	 * @param response
	 * @param password
	 */
	@RequestMapping("/conn/changeInfo.htm")
	public void changeInfo(HttpServletRequest request,HttpServletResponse response,
			String userName,String password,String new_password,String telephone){
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("timestamp",new Date().getTime());
		int flag=0;
		PrintWriter writer;	
		if(userName == null || "".equals(userName)){	
			jsonObj.put("resultCode","ERROR");
			jsonObj.put("resultData","用户名不能为空。");	
		}else{
			Map<String, String> params = new HashMap<String, String>();
			params.put("userName", userName);
			String queryString = "select obj from User obj where obj.userName=:userName";
			List<User> userList = this.userService.query(queryString, params, -1, -1);
			if( userList != null && userList.size()==1 ){		
					User user = userList.get(0);
					if(telephone != null && !telephone.equals("")){
						params.put("telephone", telephone);
						String query = "select obj from User obj where obj.telephone=:telephone and obj.userName!=:userName";
						List<User> userList_tel = this.userService.query(query, params, -1, -1);
						if( userList_tel != null && userList_tel.size() > 0  ){
							//手机号已经存在，不能修改为该手机号	
							flag=1;
						}else{
							user.setTelephone(telephone);
						}
					}
					if(new_password != null && !new_password.equals("")){
						if( userList.get(0).getPassword().equals(Md5Encrypt.md5(password)) ){	
							user.setPassword(Md5Encrypt.md5(new_password));
						}else{
							flag=2;
						}
					}
					if(flag==0){
						this.userService.update(user);
						jsonObj.put("resultCode","OK");
						jsonObj.put("resultData","修改成功。");
					}else if(flag==1){
						jsonObj.put("resultCode","ERROR");
						jsonObj.put("resultData","手机号已经存在,无法修改。");	
					}else if(flag==2){
						jsonObj.put("resultCode","ERROR");
						jsonObj.put("resultData","密码错误。");	
					}
			}else{
				jsonObj.put("resultCode","ERROR");
				jsonObj.put("resultData","用户名不存在。");	
			}
		}
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 用户登录
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 */
	@RequestMapping("/conn/login.htm")
	public void LoginCheckAction(HttpServletRequest request,HttpServletResponse response,
			String userName,String password){
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("timestamp",new Date().getTime());
		
		PrintWriter writer;	
		if(userName == null || "".equals(userName) || password==null || "".equals(password)){	
			jsonObj.put("resultCode","ERROR");
			jsonObj.put("resultData","用户名或密码不能为空。");	
		}else{
			Map<String, String> params = new HashMap<String, String>();
			params.put("userName", userName);
			String queryString = "select obj from User obj where obj.userName=:userName";
			List<User> userList = this.userService.query(queryString, params, -1, -1);
			if( userList != null && userList.size()==1 ){
				if( userList.get(0).getPassword().equals(Md5Encrypt.md5(password)) ){
					jsonObj.put("resultCode","OK");
					jsonObj.put("resultData","登录成功。");	
				}else{
					jsonObj.put("resultCode","ERROR");
					jsonObj.put("resultData","密码错误。");	
				}
			}else{
				jsonObj.put("resultCode","ERROR");
				jsonObj.put("resultData","用户名不存在。");	
			}
		}
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 注册买家
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param telephone
	 * @param area
	 * @param mobile_verify_code
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	@RequestMapping("/conn/register_buyer.htm")
	public void register_buyer(HttpServletRequest request,
			HttpServletResponse response,
			String userName,String password,
			String telephone,String area_id) throws HttpException, IOException {
		boolean reg = true;// 防止机器注册，如后台开启验证码则强行验证验证码
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("timestamp",new Date().getTime());		
		List<String> errorInfo = new ArrayList<String>();
		
		//用户名 
		if(userName!=null && !"".equals(userName)){
			Map params = new HashMap();
			params.put("userName", userName);
			List<User> users = this.userService.query(
					"select obj from User obj where obj.userName=:userName",
					params, -1, -1);
			if (users != null && users.size() > 0) {
				reg = false;
				errorInfo.add("用户名重复。");
			}
		}else{
			errorInfo.add("用户名不能为空。");
			reg=false;
		}
	
		//区域
		if(area_id!=null && !"".equals(area_id)){
			Area a1 = this.areaService.getObjById(Long.parseLong(area_id));
			if(a1 == null){
				reg = false;
				errorInfo.add("区域不正确。");
			}
		}else{
			reg=false;
			errorInfo.add("区域不能为空。");
		}
		
		//密码不能为空
		if(password==null || "".equals(password)){
			reg=false;
			errorInfo.add("密码不能为空。");
		}
			
		//验证码是否有误
		if(telephone!=null && !"".equals(telephone)){
			Map params = new HashMap();
			params.put("telephone", telephone);
			//手机号码不能重复
			List<User> users = this.userService.query(
					"select obj from User obj where obj.telephone=:telephone",
					params, -1, -1);
			if (users != null && users.size() > 0) {
				reg = false;
				errorInfo.add("手机号不能重复。");
			}
		}else{
			reg=false;
			errorInfo.add("手机号不能为空。");
		}
			
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter writer;	
		if(reg){
			User user = new User();
			user.setUserName(userName);
			user.setUserRole("BUYER");
			user.setBbc_Role("BUYER");
			user.setAddTime(new Date());
			user.setTelephone(telephone);
			
			Area a = new Area();
			a.setId(Long.parseLong(area_id));
			user.setArea(a);
			user.setPassword(Md5Encrypt.md5(password).toLowerCase());
			Map<String,String> params = new HashMap<String, String>();
			params.clear();
			params.put("type", "BUYER");
			List<Role> roles = this.roleService.query(
					"select obj from Role obj where obj.type=:type", params,
					-1, -1);
			user.getRoles().addAll(roles);
			if (this.configService.getSysConfig().isIntegral()) {
				user.setIntegral(this.configService.getSysConfig()
						.getMemberRegister());
				this.userService.save(user);
				IntegralLog log = new IntegralLog();
				log.setAddTime(new Date());
				log.setContent("用户注册增加"
						+ this.configService.getSysConfig().getMemberRegister()
						+ "分");
				log.setIntegral(this.configService.getSysConfig()
						.getMemberRegister());
				log.setIntegral_user(user);
				log.setType("reg");
				this.integralLogService.save(log);
			} else {
				this.userService.save(user);
			}
			// 创建用户默认相册
			Album album = new Album();
			album.setAddTime(new Date());
			album.setAlbum_default(true);
			album.setAlbum_name("默认相册");
			album.setAlbum_sequence(-10000);
			album.setUser(user);
			this.albumService.save(album);	
			
			jsonObj.put("resultCode","OK");
			jsonObj.put("resultData","注册成功");	
		}else{
			jsonObj.put("resultCode","ERROR");
			jsonObj.put("resultData",errorInfo.get(0));		
		}
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 注册卖家
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param telephone
	 * @param area_id  区域id	
	 * @param true_name  真实姓名
	 * @param store_name  店铺名称
	 * @param sc_id 店铺分类
	 * @param store_area 店铺区域
	 * @param store_address 店铺详细地址
	 * @param store_ower_card 身份证信息
	 * @throws HttpException
	 * @throws IOException
	 */
	@RequestMapping("/conn/register_seller.htm")
	public void register_seller(HttpServletRequest request,
			HttpServletResponse response,
			String userName,String password,
			String telephone,String area_id, 
			String true_name,String store_name,String sc_id,
			String store_area,String store_address,
			String store_ower_card) throws HttpException, IOException {
		boolean reg = true;// 防止机器注册，如后台开启验证码则强行验证验证码
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("timestamp",new Date().getTime());		
		List<String> errorInfo = new ArrayList<String>();
		
		//用户名 
		if(userName!=null && !"".equals(userName)){
			Map params = new HashMap();
			params.put("userName", userName);
			List<User> users = this.userService.query(
					"select obj from User obj where obj.userName=:userName",
					params, -1, -1);
			if (users != null && users.size() > 0) {
				reg = false;
				errorInfo.add("用户名重复。");
			}
			List<Audit> audits = this.auditService.query(
					"select obj from Audit obj where obj.userName=:userName",
					params, -1, -1);
			//审核表是否用户名有重复
			if (audits != null && audits.size() > 0) {
				reg = false;
				errorInfo.add("用户名重复。");
			}		
		}else{
			errorInfo.add("用户名不能为空。");
			reg=false;
		}

		//密码不能为空
		if(password==null || "".equals(password)){
			reg=false;
			errorInfo.add("密码不能为空。");
		}
		
		//区域
		if(area_id!=null && !"".equals(area_id)){
			Area a1 = this.areaService.getObjById(Long.parseLong(area_id));
			if(a1 == null){
				reg = false;
				errorInfo.add("区域不正确。");
			}
		}else{
			reg=false;
			errorInfo.add("区域不能为空。");
		}
			
		if(telephone!=null && !"".equals(telephone)){
			Map params = new HashMap();
			params.put("telephone", telephone);
			//手机号码不能重复
			List<User> users = this.userService.query(
					"select obj from User obj where obj.telephone=:telephone",
					params, -1, -1);
			if (users != null && users.size() > 0) {
				reg = false;
				errorInfo.add("手机号不能重复。");
			}
			List<Audit> audits = this.auditService.query(
					"select obj from Audit obj where obj.telephone=:telephone",
					params, -1, -1);
			if (audits != null && audits.size() > 0) {
				reg = false;
				errorInfo.add("手机号不能重复。");
			}
		}else{
			reg=false;
			errorInfo.add("手机号不能为空。");
		}
			
		//店铺名
		if(store_name!=null && !"".equals(store_name)){
			Map params = new HashMap();
			params.put("storeName", store_name);
			List<Audit> storeList = this.auditService.query(
					"select obj from Audit obj where obj.store_name=:storeName",
					params, -1, -1);
			if (storeList != null && storeList.size() > 0) {
				reg = false;
				errorInfo.add("店铺名不能重复。");
			}
			
			List<Store> storeList1 = this.storeService.query(
					"select obj from Store obj where obj.store_name=:storeName",
					params, -1, -1);
			if (storeList1 != null && storeList1.size() > 0) {
				reg = false;
				errorInfo.add("店铺名不能重复。");
			}
		}else{
			reg=false;
			errorInfo.add("店铺名不能为空。");
		}
		
		if(sc_id==null || "".equals(sc_id)){
			reg=false;
			errorInfo.add("店铺分类不能为空。");
		}
		if(store_area==null || "".equals(store_area)){
			reg=false;
			errorInfo.add("店铺地区不能为空。");
		}
	
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter writer;	
		if(reg){
			Audit audit = new Audit();
			audit.setStatus("0");
			audit.setDeleteStatus(false);
			audit.setUserName(userName);
			audit.setPassword(Md5Encrypt.md5(password).toLowerCase());
			audit.setTelephone(telephone);
			audit.setAddTime(new Date());			
			audit.setTrueName(true_name);
			audit.setStore_name(store_name);
			audit.setStore_addr(store_address);
			audit.setId_card(store_ower_card);
			
			Area a1 = this.areaService.getObjById(Long.parseLong(area_id));
			if(a1!=null){
				audit.setArea(a1);
			}
		
			StoreClass storeCls = this.storeClassService.getObjById(Long.parseLong(sc_id));
			if(storeCls!=null){
				audit.setStore_class(storeCls);
			}
			
			Area a2 = this.areaService.getObjById(Long.parseLong(store_area));
			if(store_area!=null){
				audit.setStore_area(a2);
			}
			
			this.auditService.save(audit);
			jsonObj.put("resultCode","OK");
			jsonObj.put("resultData","信息已经提交，请等待审核结果");
		}else{
			jsonObj.put("resultCode","ERROR");
			jsonObj.put("resultData",errorInfo.get(0));			
		}
		
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
			
	/**
	 * APP根据用户名查找用户信息
	 * @param username
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getUserConfigByName.htm")
	public void getUserConfigByName(HttpServletRequest request,HttpServletResponse response,String userName){	
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");

		PrintWriter writer;	
		User user = null;
		user = this.userService.getObjByProperty("userName", userName);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("timestamp",new Date().getTime());
		if(user!=null){
			jsonObj.put("resultCode","OK");
			jsonObj.put("resultData",user.getInfo());
		}else{
			jsonObj.put("resultCode","ERROR");
			jsonObj.put("resultData","不存在该用户");
		}		
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
		
	/**
	 * APP查询所有区域的详细列表
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getArea.htm")
	public void getArea(HttpServletRequest request , HttpServletResponse response) throws UnsupportedEncodingException{	
		String query = "SELECT ID,DELETESTATUS,AREANAME,COMMON,LEVEL,SEQUENCE,PARENT_ID from LAKECLOUD_AREA WHERE DELETESTATUS=0";
		List<Object[]> area = this.areaService.getArea(query,null,-1,-1);
		JSONObject jsonObj = new JSONObject();
		JSONArray arr =new JSONArray();
		if(area==null){
			jsonObj.put("resultCode","ERROR");
			jsonObj.put("resultData","区域不存在。");	
		}else{		
			for(int j=0;j<area.size();j++){
				List<Object[]> li = new ArrayList();
				li.add(area.get(j));
				for(Object[] o : li){
					JSONObject obj = new JSONObject();	
					obj.put("ID", o[0]);  		
					obj.put("DELETESTATUS", o[1]);
					obj.put("AREANAME",o[2]);
					obj.put("COMMON", o[3]);
					obj.put("LEVEL", o[4]);
					obj.put("SEQUENCE", o[5]);
					obj.put("PARENT_ID", o[6]);
					arr.add(obj);
				}	
			}
			jsonObj.put("resultCode","OK");
			jsonObj.put("resultData",arr);
		}
		
		jsonObj.put("timestamp",new Date().getTime());		
		System.out.println(jsonObj.toJSONString());
		
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * 获取分公司
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getBranch.htm")
	public void getBranch(HttpServletRequest request , HttpServletResponse response) throws UnsupportedEncodingException{	
		String query = "SELECT ID,DELETESTATUS,NAME,CODE from LAKECLOUD_BRANCH WHERE DELETESTATUS=0";
		List<Object[]> branchs = this.branchService.getBranch(query, null, -1, -1);
		JSONObject jsonObj = new JSONObject();
		JSONArray arr =new JSONArray();
		if(branchs==null){
			jsonObj.put("resultCode","ERROR");
			jsonObj.put("resultData","分公司不存在。");	
		}else{
			for(int j=0;j<branchs.size();j++){
				List<Object[]> li = new ArrayList();
				li.add(branchs.get(j));
				for(Object[] o : li){
					JSONObject obj = new JSONObject();	
					obj.put("ID", o[0]);  		
					obj.put("DELETESTATUS", o[1]);
					obj.put("NAME",o[2]);
					obj.put("CODE",o[3]);
					arr.add(obj);
				}	
			}
			jsonObj.put("resultCode","OK");
			jsonObj.put("resultData",arr);
		}
		
		jsonObj.put("timestamp",new Date().getTime());		
		System.out.println(jsonObj.toJSONString());
		
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getAreaById.htm")
	public void getAreaById(HttpServletRequest request,HttpServletResponse response,String id){
		JSONObject jsonObj = new JSONObject();
		JSONArray arr =new JSONArray();
		Area area = this.areaService.getObjById(Long.parseLong(id));
		
		if(area==null){
			jsonObj.put("resultCode","ERROR");
			jsonObj.put("resultData","区域不存在。");	
		}else{		
			JSONObject tmp = new JSONObject();	
			tmp.put("ID",area.getId());
			tmp.put("DELETESTATUS",area.getAreaName());
			tmp.put("LEVEL", area.getLevel());
			if(area.getParent() !=null){
				tmp.put("PARENT_ID", area.getParent().getId());
			}
			arr.add(tmp);
			jsonObj.put("resultCode","OK");
			jsonObj.put("resultData",arr);
		}
	
		jsonObj.put("timestamp",new Date().getTime());		
		System.out.println(jsonObj.toJSONString());	
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
	/**
	 * APP根据输入的区域，查询下级区域信息。
	 * @param request
	 * @param response
	 * @param parentId 父类
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getSomeArea.htm")
	public void getSomeArea(HttpServletRequest request,HttpServletResponse response,String parentId){
		String query = "SELECT ID,DELETESTATUS,AREANAME,COMMON,LEVEL,SEQUENCE,PARENT_ID from LAKECLOUD_AREA  WHERE DELETESTATUS=0 AND PARENT_ID";
		List<Object[]> area = null;
		if(parentId == null || parentId.equals("")){
			query += " is null";		
		}else{
			query = query + "=" + parentId;
		}
		area = this.areaService.getArea(query,null,-1,-1);
		JSONObject jsonObj = new JSONObject();
		JSONArray arr =new JSONArray();
		if(area==null){
			jsonObj.put("resultCode","ERROR");
			jsonObj.put("resultData","区域不存在。");	
		}else{		
			for(int j=0;j<area.size();j++){
				List<Object[]> li = new ArrayList();
				li.add(area.get(j));
				for(Object[] o : li){
					JSONObject obj = new JSONObject();	
					obj.put("ID", o[0]);  		
					obj.put("DELETESTATUS", o[1]);
					obj.put("AREANAME",o[2]);
					obj.put("COMMON", o[3]);
					obj.put("LEVEL", o[4]);
					obj.put("SEQUENCE", o[5]);
					obj.put("PARENT_ID", o[6]);
					arr.add(obj);
				}	
			}
			jsonObj.put("resultCode","OK");
			jsonObj.put("resultData",arr);
		}
		jsonObj.put("timestamp",new Date().getTime());		
		System.out.println(jsonObj.toJSONString());
		
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	/**
	 * 农豆
	 * @param request
	 * @param response
	 * @param userid 用户ID
	 * @param integral 农豆
	 * @param type 类型（加(add)还是减(min)）
	 */
/*	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getUserIntegrals.htm")
	public void getUserIntegrals(HttpServletRequest request,HttpServletResponse response,String userName,String integral,String type){
		JSONObject jsonObj = new JSONObject();
		if(userName == null || "".equals(userName)){
			jsonObj.put("resultCode","ERROR");
			jsonObj.put("resultData","用户ID不能为空。");
		}else{
			//判断是否有这个用户
			Map<String, String> paramsuser = new HashMap<String, String>();
			paramsuser.put("userName", userName);
			String queryuser = "select obj from User obj where obj.userName=:userName";
			List<User> getUserList = this.userService.query(queryuser, paramsuser, -1, -1);
		if( getUserList != null && getUserList.size()==1){
			User user = getUserList.get(0);
			if(integral == null || "".equals(integral) || type == null || "".equals(type)){
				jsonObj.put("resultCode","ERROR");
				jsonObj.put("resultData","未明确定义农豆数或者加减类型。");
			}else{
				boolean flag = true;
				
				//判断主表是否有该用户
				String queryString = "select obj from Integration obj where obj.user.id="+user.getId();
				List<Integration> intList = this.integrationService.query(queryString, null, -1, -1);
				if( intList != null && intList.size() ==1 ){
				
				//加减农豆
				Integration inte = intList.get(0);
				if(!"add".equals(type)){
				if((inte.getIntegrals()==null?0:inte.getIntegrals())<Integer.parseInt(integral)){
							flag = false;
				      }
				}
				if(flag){
				//去子表查看是否有这个用户
				String query = "select obj from Integration_Child obj where obj.integ.id="+inte.getId()+" and obj.type = 0";
				List<Integration_Child> intchList = this.integration_ChildService.query(query, null, -1, -1);
				
				if( intchList != null && intchList.size()==1 ){
					Integration_Child intech = intchList.get(0);
					
					//日志表
					
					Integration_Log intelog = new Integration_Log();
					intelog.setInteg(inte);
					intelog.setAddTime(new Date());
					intelog.setUser(user);//用户
					intelog.setBe_integrals(intech.getIntegrals()==null?0:intech.getIntegrals());//消费前的平台农豆
					intelog.setIntegrals(Integer.parseInt(integral));//农豆
					intelog.setGettype(-1);//获取路径
					if("add".equals(type)){
						intelog.setType(1);//消费方式
						intelog.setAf_integrals((intech.getIntegrals()==null?0:intech.getIntegrals())+Integer.parseInt(integral));//消费后的平台农豆
					}else{
						intelog.setType(-1);
						intelog.setAf_integrals(((intech.getIntegrals()==null?0:intech.getIntegrals())-Integer.parseInt(integral))<0?0:((intech.getIntegrals()==null?0:intech.getIntegrals())-Integer.parseInt(integral)));
						//即将过期的平台农豆
						intelog.setOverdue_integrals((intech.getOverdue_integrals()==null?0:intech.getOverdue_integrals())>Integer.parseInt(integral)?(intech.getOverdue_integrals()-Integer.parseInt(integral)):0);
					}
					this.integration_LogService.save(intelog);
					
				if("add".equals(type)){
					//主表
					inte.setIntegrals(inte.getIntegrals()==null?0:(inte.getIntegrals()+Integer.parseInt(integral)));
					//子表
					intech.setIntegrals(intech.getIntegrals()==null?0:(intech.getIntegrals()+Integer.parseInt(integral)));
				}else{
					//主表
					inte.setIntegrals(inte.getIntegrals()==null?0:(inte.getIntegrals()-Integer.parseInt(integral)));
					//扣减农豆需判断即将过期农豆数量，扣到0就不再扣减
					inte.setOverdue_integrals((inte.getOverdue_integrals()==null?0:inte.getOverdue_integrals())>Integer.parseInt(integral)?(inte.getOverdue_integrals()-Integer.parseInt(integral)):0);
					//子表
					intech.setIntegrals(intech.getIntegrals()==null?0:(intech.getIntegrals()-Integer.parseInt(integral)));
					intech.setOverdue_integrals((intech.getOverdue_integrals()==null?0:intech.getOverdue_integrals())>Integer.parseInt(integral)?(intech.getOverdue_integrals()-Integer.parseInt(integral)):0);
				}
				this.integrationService.save(inte);
				this.integration_ChildService.save(intech);
				
				
				jsonObj.put("resultCode","OK");
				jsonObj.put("resultData","操作农豆成功。");
				}
				}else{
					jsonObj.put("resultCode","ERROR");
					jsonObj.put("resultData","用户农豆不足，不可扣减。");	
				}
			}else if(intList != null && intList.size()<=0){
				if("add".equals(type)){
				//该用户没有开通农豆账户，则开通
				Integration inte_add = new Integration();//主表
				inte_add.setAddTime(new Date());
				inte_add.setUser(user);
				inte_add.setIntegrals(Integer.parseInt(integral));
				this.integrationService.save(inte_add);
				Integration_Child intech_add = new Integration_Child();//子表
				intech_add.setAddTime(new Date());
				intech_add.setUser(user);
				intech_add.setInteg(inte_add);
				intech_add.setIntegrals(Integer.parseInt(integral));
				intech_add.setType(0);
				this.integration_ChildService.save(intech_add);
				Integration_Log intelog = new Integration_Log();//log
				intelog.setAddTime(new Date());
				intelog.setUser(user);
				intelog.setGettype(-1);
				intelog.setType(1);
				intelog.setBe_integrals(0);
				intelog.setAf_integrals(Integer.parseInt(integral));
				intelog.setIntegrals(Integer.parseInt(integral));
				intelog.setInteg(inte_add);
				this.integration_LogService.save(intelog);
				jsonObj.put("resultCode","OK");
				jsonObj.put("resultData","操作农豆成功。");
				}else{
					jsonObj.put("resultCode","ERROR");
					jsonObj.put("resultData","该用户没有农豆不可执行扣除操作。");	
				}
				
			}else{
				jsonObj.put("resultCode","ERROR");
				jsonObj.put("resultData","农豆主表违反唯一用户规则。");	
			}
				}
		}else{
			jsonObj.put("resultCode","ERROR");
			jsonObj.put("resultData","用户不存在。");	
		}
			
		}
		jsonObj.put("timestamp",new Date().getTime());		
		System.out.println(jsonObj.toJSONString());	
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
*/	
	
	 @RequestMapping({"/getUserIntegral.htm"})
	  public void getUserIntegral(HttpServletRequest request, HttpServletResponse response, String userName, String integral, String type)
	  {
	    JSONObject jsonObj = new JSONObject();
	    JSONArray arr = new JSONArray();
	    if ((userName == null) || ("".equals(userName))) {
	      jsonObj.put("resultCode", "ERROR");
	      jsonObj.put("resultData", "用户名不能为空。");
	    }
	    else {
	      Map params = new HashMap();
	      params.put("userName", userName);
	      String queryString = "select obj from User obj where obj.userName=:userName";
	      List userList = this.userService.query(queryString, params, -1, -1);
	      if ((userList != null) && (userList.size() == 1)) {
	        if ((integral == null) || ("".equals(integral)) || (type == null) || ("".equals(type))) {
	          jsonObj.put("resultCode", "ERROR");
	          jsonObj.put("resultData", "未明确定义智慧豆的加减类型。");
	        }
	        else {
	          User user = (User)userList.get(0);
	          if ("add".equals(type))
	            user.setIntegral(user.getIntegral() + Integer.parseInt(integral));
	          else {
	            user.setIntegral((user.getIntegral() - Integer.parseInt(integral))<0?0:(user.getIntegral() - Integer.parseInt(integral)));
	          }
	          this.userService.update(user);
	          jsonObj.put("resultCode", "OK");
	          jsonObj.put("resultData", "智慧豆操作成功。");
	        }
	      } else {
	        jsonObj.put("resultCode", "ERROR");
	        jsonObj.put("resultData", "用户名不存在。");
	      }
	    }

	    jsonObj.put("timestamp", Long.valueOf(new Date().getTime()));
	    System.out.println(jsonObj.toJSONString());
	    response.setContentType("application/json");
	    response.setHeader("Cache-Control", "no-cache");
	    response.setCharacterEncoding("UTF-8");
	    try
	    {
	      PrintWriter writer = response.getWriter();
	      writer.print(jsonObj.toJSONString());
	      writer.flush();
	      writer.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	  }
	
	
	/**
	 * 初始化农豆主表数据
	 * */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/setInitialData.htm")
	public void setInitialData(HttpServletRequest request,HttpServletResponse response){
		JSONObject jsonObj = new JSONObject();
		List<User> userList = this.userService.query(
				"select obj from User obj where obj.id is not null",
				null, -1, -1);
		for(int i=0;i<userList.size();i++){
			Long userId =userList.get(i).getId();
			User user = this.userService.getObjById(userId);
			//判断主表是否有该用户
			String queryString = "select obj from Integration obj where obj.user.id="+userId;
			List<Integration> intList = this.integrationService.query(queryString, null, -1, -1);
			if( intList != null && intList.size() <=0 ){
				Integration intep = new Integration();//主表
				intep.setAddTime(new Date());
				intep.setUser(user);
				intep.setIntegrals(0);
				intep.setOverdue_integrals(0);
				this.integrationService.save(intep);
				//去子表查看是否有这个用户
				String query = "select obj from Integration_Child obj where obj.user.id="+userId+" and obj.type = 0";
				List<Integration_Child> intchList = this.integration_ChildService.query(query, null, -1, -1);
				if(intchList !=null && intchList.size()<=0){
					Integration_Child intechp = new Integration_Child();//子表
					intechp.setAddTime(new Date());
					intechp.setUser(user);
					intechp.setInteg(intep);
					intechp.setIntegrals(0);
					intechp.setOverdue_integrals(0);
					intechp.setType(0);
					this.integration_ChildService.save(intechp);
				}
			}
		}
		jsonObj.put("resultCode", "OK");
		jsonObj.put("resultData", "操作成功。");
		jsonObj.put("timestamp",new Date().getTime());		
		System.out.println(jsonObj.toJSONString());	
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
}

