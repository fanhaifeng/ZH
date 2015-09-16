package com.lakecloud.manage.admin.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.adaptor.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.security.SecurityManager;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.Md5Encrypt;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.core.tools.database.DatabaseTools;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Branch;
import com.lakecloud.foundation.domain.Res;
import com.lakecloud.foundation.domain.Role;
import com.lakecloud.foundation.domain.RoleGroup;
import com.lakecloud.foundation.domain.SysConfig;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.XConf;
import com.lakecloud.foundation.domain.query.UserQueryObject;
import com.lakecloud.foundation.service.IBranchService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IResService;
import com.lakecloud.foundation.service.IRoleGroupService;
import com.lakecloud.foundation.service.IRoleService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.foundation.service.IXConfService;
import com.lakecloud.manage.buyer.action.AccountBuyerAction;
import com.lakecloud.manage.buyer.action.AddressBuyerAction;
import com.lakecloud.manage.buyer.action.BaseBuyerAction;
import com.lakecloud.manage.buyer.action.ComplaintBuyerAction;
import com.lakecloud.manage.buyer.action.ConsultBuyerAction;
import com.lakecloud.manage.buyer.action.CouponBuyerAction;
import com.lakecloud.manage.buyer.action.FavoriteBuyerAction;
import com.lakecloud.manage.buyer.action.HomePageBuyerAction;
import com.lakecloud.manage.buyer.action.IntegralOrderBuyerAction;
import com.lakecloud.manage.buyer.action.MessageBuyerAction;
import com.lakecloud.manage.buyer.action.OrderBuyerAction;
import com.lakecloud.manage.buyer.action.PredepositBuyerAction;
import com.lakecloud.manage.buyer.action.PredepositCashBuyerAction;
import com.lakecloud.manage.buyer.action.ReportBuyerAction;
import com.lakecloud.manage.seller.action.ActivitySellerAction;
import com.lakecloud.manage.seller.action.AdvertSellerAction;
import com.lakecloud.manage.seller.action.AlbumSellerAction;
import com.lakecloud.manage.seller.action.BargainSellerAction;
import com.lakecloud.manage.seller.action.BaseSellerAction;
import com.lakecloud.manage.seller.action.ChargeSellAction;
import com.lakecloud.manage.seller.action.CombinSellerAction;
import com.lakecloud.manage.seller.action.ComplaintSellerAction;
import com.lakecloud.manage.seller.action.ConsultSellerAction;
import com.lakecloud.manage.seller.action.DeliverySellerAction;
import com.lakecloud.manage.seller.action.GoldSellerAction;
import com.lakecloud.manage.seller.action.GoodsBrandSellerAction;
import com.lakecloud.manage.seller.action.GoodsClassSellerAction;
import com.lakecloud.manage.seller.action.GoodsReturnSellerAction;
import com.lakecloud.manage.seller.action.GoodsSellerAction;
import com.lakecloud.manage.seller.action.GroupSellerAction;
import com.lakecloud.manage.seller.action.OrderSellerAction;
import com.lakecloud.manage.seller.action.PaymentSellerAction;
import com.lakecloud.manage.seller.action.PromotionSellerAction;
import com.lakecloud.manage.seller.action.RefundSellerAction;
import com.lakecloud.manage.seller.action.SpareGoodsSellerAction;
import com.lakecloud.manage.seller.action.StoreNavSellerAction;
import com.lakecloud.manage.seller.action.StorePartnerManageAction;
import com.lakecloud.manage.seller.action.StoreSellerAction;
import com.lakecloud.manage.seller.action.SubAccountSellerAction;
import com.lakecloud.manage.seller.action.TaobaoSellerAction;
import com.lakecloud.manage.seller.action.TransportSellerAction;
import com.lakecloud.manage.seller.action.WaterMarkSellerAction;
import com.lakecloud.manage.seller.action.WeiXinSellerAction;
import com.lakecloud.manage.seller.action.ZtcSellerAction;
import com.lakecloud.view.web.action.CartViewAction;
import com.lakecloud.view.web.action.IntegralViewAction;
import com.lakecloud.weixin.manage.buyer.action.WeixinAccountBuyerAction;
import com.lakecloud.weixin.manage.buyer.action.WeixinAddressBuyerAction;
import com.lakecloud.weixin.manage.buyer.action.WeixinOrderBuyerAction;
import com.lakecloud.weixin.store.view.action.WeixinStoreCartViewAction;

/**
 * @info 超级管理员管理控制器
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Controller
public class AdminManageAction implements ServletContextAware {
	private ServletContext servletContext;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IRoleGroupService roleGroupService;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	SecurityManager securityManager;
	@Autowired
	private IResService resService;
	@Autowired
	private IBranchService branchService;
	@Autowired
	private IXConfService xConfService;
	

	@SecurityMapping(title = "管理员列表", value = "/admin/admin_list.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_list.htm")
	public ModelAndView admin_list(String currentPage, String orderBy,
			String orderType, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		UserQueryObject uqo = new UserQueryObject(currentPage, mv, orderBy,
				orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, uqo, User.class, mv);
		uqo.addQuery("obj.userRole", new SysMap("userRole", "ADMIN"), "=");
		uqo.addQuery("obj.userRole", new SysMap("userRole1",
				"ADMIN_BUYER_SELLER"), "=", "or");
		IPageList pList = this.userService.list(uqo);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		CommUtil.saveIPageList2ModelAndView(url + "/admin/admin_list.htm", "",
				"", pList, mv);
		mv.addObject("userRole", "ADMIN");
		return mv;
	}

	@SecurityMapping(title = "管理员添加", value = "/admin/admin_add.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_add.htm")
	public ModelAndView admin_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("type", "ADMIN");
		List<RoleGroup> rgs = this.roleGroupService
				.query("select obj from RoleGroup obj where obj.type=:type and obj.deleteStatus=0 order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("rgs", rgs);
		mv.addObject("op", "admin_add");
		return mv;
	}

	@SecurityMapping(title = "管理员编辑", value = "/admin/admin_edit.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_edit.htm")
	public ModelAndView admin_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String op) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("type", "ADMIN");
		List<RoleGroup> rgs = this.roleGroupService
				.query("select obj from RoleGroup obj where obj.type=:type and obj.deleteStatus=0 order by obj.sequence asc",
						params, -1, -1);
		if (id != null) {
			if (!id.equals("")) {
				User user = this.userService.getObjById(Long.parseLong(id));
				mv.addObject("obj", user);		
				mv.addObject("branchId",user.getBranch().getId());
				mv.addObject("branchCode",user.getBranch_code());
			}
		}
		
		mv.addObject("rgs", rgs);
		mv.addObject("op", op);
		return mv;
	}

	@SecurityMapping(title = "管理员保存", value = "/admin/admin_save.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_save.htm")
	public ModelAndView admin_save(HttpServletRequest request,
			HttpServletResponse response, String id, String role_ids,
			String list_url, String add_url,String password,String new_password,String area) {
		WebForm wf = new WebForm();
		User user = null;
		if (id.equals("")) {
			user = wf.toPo(request, User.class);
			user.setAddTime(new Date());
			if(password!=null&&!password.equals("")){
				user.setPassword(Md5Encrypt.md5(password.trim()).toLowerCase());
			}	
		} else {
			User u = this.userService.getObjById(Long.parseLong(id));
			user = (User) wf.toPo(request, u);
			if(new_password!=null&&!new_password.equals("")){
				user.setPassword(Md5Encrypt.md5(new_password.trim()).toLowerCase());
			}
		}
		
		if(null != area && !"".equals(area)){
			//修改用户的分公司
			Branch branch = this.branchService.getObjById(CommUtil.null2Long(area));
			if(branch!=null){
				user.setBranch(branch);
				user.setBranch_code(branch.getCode());
			}	
		}
		
		
//		if (user.getPassword() == null || user.getPassword().equals("")) {
//			user.setPassword("123456");
//			user.setPassword(Md5Encrypt.md5(user.getPassword()).toLowerCase());
//		} else {
//			if (id.equals(""))
//				user.setPassword(Md5Encrypt.md5(user.getPassword())
//						.toLowerCase());
//		}
		user.getRoles().clear();
		if (user.getUserRole().equalsIgnoreCase("ADMIN")) {
			Map params = new HashMap();
			params.put("display", false);
			params.put("type", "ADMIN");
			params.put("type1", "BUYER");
			List<Role> roles = this.roleService
					.query("select obj from Role obj where (obj.display=:display and obj.type=:type) or obj.type=:type1",
							params, -1, -1);
			user.getRoles().addAll(roles);
		}
		String[] rids = role_ids.split(",");
		for (String rid : rids) {
			if (!rid.equals("")) {
				Role role = this.roleService.getObjById(Long.parseLong(rid));
				user.getRoles().add(role);
			}
		}
		if (id.equals("")) {
			boolean ret = this.userService.save(user);

		} else {
			this.userService.update(user);
		}

		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存管理员成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url);
		}
		return mv;
	}

	@SecurityMapping(title = "管理员删除", value = "/admin/admin_del.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_del.htm")
	public String admin_del(HttpServletRequest request, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				User user = this.userService.getObjById(Long.parseLong(id));
				if (!user.getUsername().equals("admin")) {
					this.databaseTools.execute("delete from "
							+ Globals.DEFAULT_TABLE_SUFFIX
							+ "syslog where user_id=" + id);
					this.databaseTools.execute("delete from "
							+ Globals.DEFAULT_TABLE_SUFFIX
							+ "user_role where user_id=" + id);
					this.userService.delete(user.getId());
				}
			}
		}
		return "redirect:admin_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "管理员修改密码", value = "/admin/admin_pws.htm*", rtype = "admin", rname = "商城后台管理", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/admin_pws.htm")
	public ModelAndView admin_pws(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_pws.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId()));
		return mv;
	}

	@SecurityMapping(title = "管理员密码保存", value = "/admin/admin_pws_save.htm*", rtype = "admin", rname = "商城后台管理", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/admin_pws_save.htm")
	public ModelAndView admin_pws_save(HttpServletRequest request,
			HttpServletResponse response, String old_password, String password) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		if (Md5Encrypt.md5(old_password).toLowerCase()
				.equals(user.getPassword())) {
			user.setPassword(Md5Encrypt.md5(password).toLowerCase());
			this.userService.update(user);
			mv.addObject("op_title", "修改密码成功");
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "原密码错误");
		}
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/admin_pws.htm");
		return mv;
	}

	@RequestMapping("/admin/init_role.htm")
	public String init_role() {
		// TODO Auto-generated method stub
		User current_user = SecurityUserHolder.getCurrentUser();
		if (current_user != null
				&& current_user.getUserRole().indexOf("ADMIN") >= 0
				&& current_user.getUsername().equals("admin")) {
			this.databaseTools.execute("delete from LAKECLOUD_role_res");
			this.databaseTools.execute("delete from LAKECLOUD_res");
			this.databaseTools.execute("delete from LAKECLOUD_user_role");
			this.databaseTools.execute("delete from LAKECLOUD_role");
			this.databaseTools.execute("delete from LAKECLOUD_rolegroup");
			List<Class> clzs = new ArrayList<Class>();
			// 超级管理权限加载
			clzs.add(ActivityManageAction.class);
			clzs.add(AdminManageAction.class);
			clzs.add(AdvertManageAction.class);
			clzs.add(AreaManageAction.class);
			clzs.add(ArticleClassManageAction.class);
			clzs.add(ArticleManageAction.class);
			clzs.add(BargainManageAction.class);
			clzs.add(BaseManageAction.class);
			clzs.add(CacheManageAction.class);
			clzs.add(CombinManageAction.class);
			clzs.add(ComplaintManageAction.class);
			clzs.add(ComplaintSubjectManageAction.class);
			clzs.add(ConsultManageAction.class);
			clzs.add(CouponManageAction.class);
			clzs.add(DatabaseManageAction.class);
			clzs.add(DeliveryManageAction.class);
			clzs.add(DocumentManageAction.class);
			clzs.add(EvaluateManageAction.class);
			clzs.add(ExpressCompanyManageAction.class);
			clzs.add(GoldRecordManageAction.class);
			clzs.add(GoodsBrandManageAction.class);
			clzs.add(GoodsClassManageAction.class);
			//clzs.add(GoodsFloorGoodsManageAction.class);   此类无需权限控制
			clzs.add(GoodsFloorManageAction.class);
			clzs.add(GoodsManageAction.class);
			clzs.add(GoodsSpecificationManageAction.class);
			clzs.add(GoodsTypeManageAction.class);
			clzs.add(GroupAreaManageAction.class);
			clzs.add(GroupClassManageAction.class);
			clzs.add(GroupManageAction.class);
			clzs.add(GroupPriceRangeManageAction.class);
			clzs.add(ImageManageAction.class);
			clzs.add(IntegralGoodsManageAction.class);
			clzs.add(IntegralLogManageAction.class);
			clzs.add(LuceneManageAction.class);
			clzs.add(NavigationManageAction.class);
			clzs.add(OperationManageAction.class);
			clzs.add(OrderManageAction.class);
			clzs.add(PartnerManageAction.class);
			clzs.add(PaymentManageAction.class);
			clzs.add(PredepositCashManageAction.class);
			clzs.add(PredepositLogManageAction.class);
			clzs.add(PredepositManageAction.class);
			//clzs.add(ReferPriceManageAction.class);   此类无权限控制
			clzs.add(ReportManageAction.class);
			clzs.add(ReportSubjectManageAction.class);
			clzs.add(ReportTypeManageAction.class);
			clzs.add(SnsManageAction.class);
			clzs.add(SpareGoodsFloorManageAction.class);
			clzs.add(SpareGoodsManageAction.class);
			clzs.add(StoreClassManageAction.class);
			clzs.add(StoreDepositLogManageAction.class);
			clzs.add(StoreGradeManageAction.class);
			clzs.add(StoreManageAction.class);
			clzs.add(TemplateManageAction.class);
			clzs.add(TransAreaManageAction.class);
			clzs.add(UcenterManageAction.class);
			clzs.add(UserManageAction.class);
			clzs.add(WeixinManageAction.class);
			//clzs.add(XConfManageAction.class);   此类无权限管理
			clzs.add(ZtcManageAction.class);
			
			// 卖家权限加载
			clzs.add(ActivitySellerAction.class);
			clzs.add(AdvertSellerAction.class);
			clzs.add(AlbumSellerAction.class);
			clzs.add(BargainSellerAction.class);
			clzs.add(BaseSellerAction.class);
			//clzs.add(ChargeManageAction.class);   此类无权限管理
			clzs.add(ChargeSellAction.class);
			clzs.add(CombinSellerAction.class);
			clzs.add(ComplaintSellerAction.class);
			clzs.add(ConsultSellerAction.class);
			clzs.add(DeliverySellerAction.class);
			clzs.add(GoldSellerAction.class);
			clzs.add(GoodsBrandSellerAction.class);
			clzs.add(GoodsClassSellerAction.class);
			clzs.add(GoodsReturnSellerAction.class);
			clzs.add(GoodsSellerAction.class);
			clzs.add(GroupSellerAction.class);
			clzs.add(OrderSellerAction.class);
			clzs.add(PaymentSellerAction.class);
			//clzs.add(PromotionLevelSellerAction.class);   此类无权限管理
			clzs.add(PromotionSellerAction.class);
			clzs.add(RefundSellerAction.class);
			clzs.add(SpareGoodsSellerAction.class);
			clzs.add(StoreNavSellerAction.class);
			clzs.add(StorePartnerManageAction.class);
			clzs.add(StoreSellerAction.class);
			clzs.add(SubAccountSellerAction.class);
			clzs.add(TaobaoSellerAction.class);
			clzs.add(TransportSellerAction.class);
			clzs.add(WaterMarkSellerAction.class);
			clzs.add(WeiXinSellerAction.class);
			clzs.add(ZtcSellerAction.class);
			
			// 买家权限加载
			clzs.add(AccountBuyerAction.class);
			clzs.add(AddressBuyerAction.class);
			clzs.add(BaseBuyerAction.class);
			clzs.add(ComplaintBuyerAction.class);
			clzs.add(ConsultBuyerAction.class);
			clzs.add(CouponBuyerAction.class);
			clzs.add(FavoriteBuyerAction.class);
			clzs.add(HomePageBuyerAction.class);
			clzs.add(IntegralOrderBuyerAction.class);
			clzs.add(MessageBuyerAction.class);
			clzs.add(OrderBuyerAction.class);
			clzs.add(PredepositBuyerAction.class);
			clzs.add(PredepositCashBuyerAction.class);
			clzs.add(ReportBuyerAction.class);
			
			//web端权限控制(买家权限)
			clzs.add(CartViewAction.class);  
			clzs.add(IntegralViewAction.class);
			
			//微信端权限控制
			clzs.add(WeixinAccountBuyerAction.class);   //微信买家用户中心
			clzs.add(WeixinAddressBuyerAction.class);   //微信买家收货地址
			clzs.add(WeixinOrderBuyerAction.class);     //微信买家订单
			clzs.add(WeixinStoreCartViewAction.class);  //微信购物车
			
			int sequence = 0;
			for (Class clz : clzs) {
				try {
					Method[] ms = clz.getMethods();
					for (Method m : ms) {
						Annotation[] annotation = m.getAnnotations();
						for (Annotation tag : annotation) {
							if (SecurityMapping.class.isAssignableFrom(tag
									.annotationType())) {
								String value = ((SecurityMapping) tag).value();
								Map params = new HashMap();
								params.put("value", value);
								List<Res> ress = this.resService
										.query("select obj from Res obj where obj.value=:value",
												params, -1, -1);
								if (ress.size() == 0) {
									Res res = new Res();
									res.setResName(((SecurityMapping) tag)
											.title());
									res.setValue(value);
									res.setType("URL");
									res.setAddTime(new Date());
									this.resService.save(res);
									String rname = ((SecurityMapping) tag)
											.rname();
									String roleCode = ((SecurityMapping) tag)
											.rcode();
									if (roleCode.indexOf("ROLE_") != 0) {
										roleCode = ("ROLE_" + roleCode)
												.toUpperCase();
									}
									params.clear();
									params.put("roleCode", roleCode);
									List<Role> roles = this.roleService
											.query("select obj from Role obj where obj.roleCode=:roleCode",
													params, -1, -1);
									Role role = null;
									if (roles.size() > 0) {
										role = roles.get(0);
									}
									if (role == null) {
										role = new Role();
										role.setRoleName(((SecurityMapping) tag)
												.rname());
										role.setRoleCode(roleCode.toUpperCase());
									}
									role.getReses().add(res);
									res.getRoles().add(role);
									role.setAddTime(new Date());
									role.setDisplay(((SecurityMapping) tag)
											.display());
									role.setType(((SecurityMapping) tag)
											.rtype().toUpperCase());
									// 获取权限分组
									String groupName = ((SecurityMapping) tag)
											.rgroup();
									RoleGroup rg = this.roleGroupService
											.getObjByProperty("name", groupName);
									if (rg == null) {
										rg = new RoleGroup();
										rg.setAddTime(new Date());
										rg.setName(groupName);
										rg.setSequence(sequence);
										rg.setType(role.getType());
										this.roleGroupService.save(rg);
									}
									role.setRg(rg);
									this.roleService.save(role);
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				sequence++;
			}
			// 添加默认超级管理员并赋予所有权限
			User user = this.userService.getObjByProperty("userName", "admin");
			Map params = new HashMap();
			List<Role> roles = this.roleService.query(
					"select obj from Role obj order by obj.addTime desc", null,
					-1, -1);
			if (user == null) {
				user = new User();
				user.setUserName("admin");
				user.setUserRole("ADMIN");
				user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
				for (Role role : roles) {
					if (!role.getType().equalsIgnoreCase("SELLER")) {
						user.getRoles().add(role);
					}
				}
				this.userService.save(user);
			} else {
				for (Role role : roles) {
					if (!role.getType().equals("SELLER")) {
						System.out.println(role.getRoleName() + " "
								+ role.getType() + " " + role.getRoleCode());
						user.getRoles().add(role);
					}
				}
				this.userService.update(user);
			}
			// 给其他管理员添加系统默认的权限及买家权限
			params.clear();
			params.put("display", false);
			params.put("type", "ADMIN");
			List<Role> admin_roles = this.roleService
					.query("select obj from Role obj where obj.display=:display and obj.type=:type",
							params, -1, -1);
			params.clear();
			params.put("type", "BUYER");
			List<Role> buyer_roles = this.roleService.query(
					"select obj from Role obj where obj.type=:type", params,
					-1, -1);
			params.clear();
			params.put("userRole", "ADMIN");
			params.put("userName", "admin");
			List<User> admins = this.userService
					.query("select obj from User obj where obj.userRole=:userRole and obj.userName!=:userName",
							params, -1, -1);
			for (User admin : admins) {
				admin.getRoles().addAll(admin_roles);
				admin.getRoles().addAll(buyer_roles);
				this.userService.update(admin);
			}
			// 给所有用户添加买家权限
			params.clear();
			params.put("userRole", "BUYER");
			List<User> buyers = this.userService.query(
					"select obj from User obj where obj.userRole=:userRole",
					params, -1, -1);
			for (User buyer : buyers) {
				buyer.getRoles().addAll(buyer_roles);
				this.userService.update(buyer);
			}
			// 给所有卖家添加卖家权限
			params.clear();
			params.put("type1", "BUYER");
			params.put("type2", "SELLER");
			List<Role> seller_roles = this.roleService
					.query("select obj from Role obj where (obj.type=:type1 or obj.type=:type2)",
							params, -1, -1);
			params.clear();
			params.put("userRole1", "BUYER_SELLER");
			params.put("userRole2", "ADMIN_BUYER_SELLER");
			params.put("userRole3", "ADMIN");
			params.put("userName", "admin");
			List<User> sellers = this.userService
					.query("select obj from User obj where (obj.userRole=:userRole1 or obj.userRole=:userRole2 or obj.userRole=:userRole3) and obj.userName!=:userName ",
							params, -1, -1);
			for (User seller : sellers) {
				seller.getRoles().addAll(buyer_roles);
				seller.getRoles().addAll(seller_roles);
				this.userService.update(seller);
			}
			// 重新加载系统权限
			Map<String, String> urlAuthorities = this.securityManager
					.loadUrlAuthorities();
			this.servletContext.setAttribute("urlAuthorities", urlAuthorities);
			return "redirect:admin_list.htm";
		} else {
			return "redirect:login.htm";
		}
	}
	
	/*
	 * 农豆配置数据
	 *
	 * */
	@SecurityMapping(title = "农豆配置数据", value = "/admin/admin_integrals_set.htm*", rtype = "admin", rname = "商城后台管理", rcode = "admin_integrals_set", rgroup = "设置")
	@RequestMapping("/admin/admin_integrals_set.htm")
	public ModelAndView admin_integrals_set(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/integrals_set.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("integration_from_register",this.xConfService.queryByXconfkey("integration_from_register").getXconfvalue());
		mv.addObject("integration_from_invite",this.xConfService.queryByXconfkey("integration_from_invite").getXconfvalue());
		mv.addObject("integration_from_order",this.xConfService.queryByXconfkey("integration_from_order").getXconfvalue());
		mv.addObject("integration_rate_for_money",this.xConfService.queryByXconfkey("integration_rate_for_money").getXconfvalue());
		mv.addObject("integration_rate_for_order",this.xConfService.queryByXconfkey("integration_rate_for_order").getXconfvalue());
		return mv;
	}
	
	
	/*
	 * 农豆配置数据保存
	 *
	 * */
	@SecurityMapping(title = "订单设置保存", value = "/admin/integrals_set_save.htm*", rtype = "admin", rname = "商城后台管理", rcode = "integrals_set_save", rgroup = "设置")
	@RequestMapping("/admin/integrals_set_save.htm")
	public ModelAndView set_order_confirm_save(HttpServletRequest request,
			HttpServletResponse response, String integration_from_register, String integration_from_invite,
			String integration_from_order, String integration_rate_for_money,
			String integration_rate_for_order) {
		XConf xConf = null;
		Map params = new HashMap();
		params.put("integration_from_register", "integration_from_register");
		List<XConf> xconfList = this.xConfService
				.query("select obj from XConf obj where obj.xconfkey=:integration_from_register",
						params, -1, -1);
		xConf = xconfList.get(0);
		xConf.setXconfvalue(integration_from_register);
		this.xConfService.update(xConf);
		params.clear();
		xconfList.clear();
		xConf=null;
		params.put("integration_from_invite", "integration_from_invite");
		xconfList = this.xConfService
				.query("select obj from XConf obj where obj.xconfkey=:integration_from_invite",
						params, -1, -1);
		xConf = xconfList.get(0);
		xConf.setXconfvalue(integration_from_invite);
		this.xConfService.update(xConf);
		params.clear();
		xconfList.clear();
		xConf=null;
		params.put("integration_from_order", "integration_from_order");
		xconfList = this.xConfService
				.query("select obj from XConf obj where obj.xconfkey=:integration_from_order",
						params, -1, -1);
		xConf = xconfList.get(0);
		xConf.setXconfvalue(integration_from_order);
		this.xConfService.update(xConf);
		params.clear();
		xconfList.clear();
		xConf=null;
		params.put("integration_rate_for_money", "integration_rate_for_money");
		xconfList = this.xConfService
				.query("select obj from XConf obj where obj.xconfkey=:integration_rate_for_money",
						params, -1, -1);
		xConf = xconfList.get(0);
		xConf.setXconfvalue(integration_rate_for_money);
		this.xConfService.update(xConf);
		params.clear();
		xconfList.clear();
		xConf=null;
		params.put("integration_rate_for_order", "integration_rate_for_order");
		xconfList = this.xConfService
				.query("select obj from XConf obj where obj.xconfkey=:integration_rate_for_order",
						params, -1, -1);
		xConf = xconfList.get(0);
		xConf.setXconfvalue(integration_rate_for_order);
		this.xConfService.update(xConf);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "订单设置成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/admin_integrals_set.htm");
		mv.addObject("integration_from_register",integration_from_register);
		mv.addObject("integration_from_invite",integration_from_invite);
		mv.addObject("integration_from_order",integration_from_order);
		mv.addObject("integration_rate_for_money",integration_rate_for_money);
		mv.addObject("integration_rate_for_order",integration_rate_for_order);
		
		return mv;
	}
	
	
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		this.servletContext = servletContext;
	}
}
