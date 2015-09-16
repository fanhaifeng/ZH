package com.lakecloud.manage.admin.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.json.simple.JSONObject;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.SendMessageUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.core.tools.database.DatabaseTools;
import com.lakecloud.foundation.dao.AreaDAO;
import com.lakecloud.foundation.domain.Accessory;
import com.lakecloud.foundation.domain.Album;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Audit;
import com.lakecloud.foundation.domain.Branch;
import com.lakecloud.foundation.domain.Crop;
import com.lakecloud.foundation.domain.Evaluate;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.Message;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Payment;
import com.lakecloud.foundation.domain.Role;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreCart;
import com.lakecloud.foundation.domain.StoreClass;
import com.lakecloud.foundation.domain.StoreGrade;
import com.lakecloud.foundation.domain.StoreGradeLog;
import com.lakecloud.foundation.domain.StorePoint;
import com.lakecloud.foundation.domain.SysConfig;
import com.lakecloud.foundation.domain.Template;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.query.AuditQueryObject;
import com.lakecloud.foundation.domain.query.StoreGradeLogQueryObject;
import com.lakecloud.foundation.domain.query.StoreQueryObject;
import com.lakecloud.foundation.domain.query.UserQueryObject;
import com.lakecloud.foundation.service.IAccessoryService;
import com.lakecloud.foundation.service.IAlbumService;
import com.lakecloud.foundation.service.IAreaService;
import com.lakecloud.foundation.service.IAuditService;
import com.lakecloud.foundation.service.IBranchService;
import com.lakecloud.foundation.service.IConsultService;
import com.lakecloud.foundation.service.ICropService;
import com.lakecloud.foundation.service.IEvaluateService;
import com.lakecloud.foundation.service.IGoodsCartService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IMessageService;
import com.lakecloud.foundation.service.IOrderFormLogService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IPaymentService;
import com.lakecloud.foundation.service.IRoleService;
import com.lakecloud.foundation.service.IStoreCartService;
import com.lakecloud.foundation.service.IStoreClassService;
import com.lakecloud.foundation.service.IStoreGradeLogService;
import com.lakecloud.foundation.service.IStoreGradeService;
import com.lakecloud.foundation.service.IStorePointService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.ITemplateService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.lucene.LuceneUtil;
import com.lakecloud.manage.admin.tools.AreaManageTools;
import com.lakecloud.manage.admin.tools.StoreTools;
import com.sun.org.apache.bcel.internal.generic.NEW;
/**
 * 
* <p>Title: StoreManageAction.java</p>

* <p>Description:平台店铺管理控制器，用来列表显示店铺，添加店铺、修改店铺信息等操作 </p>

* <p>Copyright: Copyright (c) 2011-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-5-8

* @version LakeCloud_C2C 1.4
 */
@Controller
public class StoreManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IStoreGradeService storeGradeService;
	@Autowired
	private IStoreClassService storeClassService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IConsultService consultService;
	@Autowired
	private AreaManageTools areaManageTools;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IStoreGradeLogService storeGradeLogService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IStoreCartService storeCartService;
	@Autowired
	private IStorePointService storepointService;
	@Autowired
	private IAuditService auditService;
	@Autowired
	private IBranchService branchService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private ICropService cropService;
	
	
	@SecurityMapping(title = "卖家审核列表", value = "/admin/store_audit.htm*", rtype = "admin", rname = "卖家审核", rcode = "admin_store_check", rgroup = "店铺")
	@RequestMapping("/admin/store_audit.htm")
	public ModelAndView store_audit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String condition, String value,String status) {
		ModelAndView mv = new JModelAndView("admin/blue/store_audit.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		AuditQueryObject qo = new AuditQueryObject(currentPage, mv, orderBy,
				orderType);
		if(null!=status && !"".equals(status)){
			qo.addQuery("obj.status", new SysMap("status", status), "=");
            mv.addObject("status", status);
		}
		
		//超级管理员admin
		Branch branch=SecurityUserHolder.getCurrentUser().getBranch();
		if( branch != null ){
			//总部管理员不做筛选
			if(!branch.getCode().equals("1400"))
			qo.addQuery("obj.branch_code", new SysMap("branch_code", branch.getCode()), "=");
		}
		
		IPageList pList = this.auditService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/store_audit.htm", "",
				params, pList, mv);
		
		return mv;
	}
	
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "卖家审核", value = "/admin/store_audit_pass.htm*", rtype = "admin", rname = "卖家审核", rcode = "admin_store_check", rgroup = "店铺")
	@RequestMapping("/admin/store_audit_pass.htm")
	public void store_audit_pass(HttpServletRequest request,HttpServletResponse response,
			String mulitId,String flag,String option)
			throws Exception {
		
		String[] ids = mulitId.split(",");
		JSONObject jsonObj = this.storeService.save_store_user(mulitId, flag, option);
		for (String id : ids) {
			if (!id.equals("")) {
				Audit audit = this.auditService.getObjById(Long.parseLong(id));
				if (this.configService.getSysConfig().isSmsEnbale()) {
					//给经销商发送短信
					this.send_register_sms(request, audit,"sms_toseller_result_open_notify");
				}	
			}
		}
		jsonObj.put("resultCode","OK");
		jsonObj.put("timestamp",new Date().getTime());		
		
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(jsonObj.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
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
				pwrite.flush();
				pwrite.close();
				// 生成模板
				Properties p = new Properties();
				p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
						path);
				p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
				p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
				Velocity.init(p);
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
	 * Store列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "店铺列表", value = "/admin/store_list.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_list.htm")
	public ModelAndView store_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/store_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		StoreQueryObject qo = new StoreQueryObject(currentPage, mv, orderBy,
				orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, Store.class, mv);
		Branch branch=SecurityUserHolder.getCurrentUser().getBranch();		
		if( branch != null ){
			//总部管理员不做筛选
			if(!branch.getCode().equals("1400"))
			qo.addQuery("obj.branch_code", new SysMap("branch_code", branch.getCode()), "=");
		}
		IPageList pList = this.storeService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/store_list.htm", "",
				params, pList, mv);
		return mv;
	}

	/**
	 * store添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "店铺添加1", value = "/admin/store_add.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_add.htm")
	public ModelAndView store_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/store_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "店铺添加2", value = "/admin/store_new.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_new.htm")
	public ModelAndView store_new(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String userName,
			String list_url, String add_url) {
		ModelAndView mv = new JModelAndView("admin/blue/store_new.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjByProperty("userName", userName);
		Store store = null;
		if (user != null)
			store = this.storeService.getObjByProperty("user.id", user.getId());
		if (user == null) {
			mv = new JModelAndView("admin/blue/tip.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_tip", "不存在该用户");
			mv.addObject("list_url", list_url);
		} else {
			if (store == null) {
				List<StoreClass> scs = this.storeClassService
						.query("select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc",
								null, -1, -1);
				List<Area> areas = this.areaService.query(
						"select obj from Area obj where obj.parent.id is null",
						null, -1, -1);
				List<StoreGrade> grades = this.storeGradeService
						.query("select obj from StoreGrade obj order by obj.sequence asc",
								null, -1, -1);
				mv.addObject("grades", grades);
				mv.addObject("areas", areas);
				mv.addObject("scs", scs);
				mv.addObject("currentPage", currentPage);
				mv.addObject("user", user);
			} else {
				mv = new JModelAndView("admin/blue/tip.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_tip", "该用户已经开通店铺");
				mv.addObject("list_url", add_url);
			}
		}
		return mv;
	}

	/**
	 * store编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "店铺编辑", value = "/admin/store_edit.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_edit.htm")
	public ModelAndView store_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/store_edit.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Store store = this.storeService.getObjById(Long.parseLong(id));
			List<StoreClass> scs = this.storeClassService
					.query("select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc",
							null, -1, -1);
			List<Area> areas = this.areaService.query(
					"select obj from Area obj where obj.parent.id is null",
					null, -1, -1);
			mv.addObject("areas", areas);
			mv.addObject("scs", scs);
			mv.addObject("obj", store);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
			if (store.getArea() != null) {
				mv.addObject("area_info",
						this.areaManageTools.generic_area_info(store.getArea()));
			}
		}
		return mv;
	}

	/**
	 * store保存管理
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "店铺保存", value = "/admin/store_save.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_save.htm")
	public ModelAndView store_save(HttpServletRequest request,
			HttpServletResponse response, String id, String area_id,
			String sc_id, String grade_id, String user_id, String store_status,
			String currentPage, String cmd, String list_url, String add_url)
			throws Exception {
		WebForm wf = new WebForm();
		Store store = null;
		if (id.equals("")) {
			store = wf.toPo(request, Store.class);
			store.setAddTime(new Date());
		} else {
			Store obj = this.storeService.getObjById(Long.parseLong(id));
			store = (Store) wf.toPo(request, obj);
		}
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		
		if( area.getLevel() == 2 && area.getParent()!=null){
			Area parentArea = this.areaService.getObjById(area.getParent().getId()); 
			//根据area获取分公司
			if(parentArea.getBranch()!=null){
				System.out.println(parentArea.getBranch().getId());
				Branch branch = this.branchService.getObjById(parentArea.getBranch().getId());
				if(branch!=null){
					Branch b = new Branch();
					b.setId(parentArea.getBranch().getId());
					store.setBranch_code(branch.getCode());
				}
			}
		}
		
		store.setArea(area);
		StoreClass sc = this.storeClassService
				.getObjById(Long.parseLong(sc_id));
		store.setSc(sc);
		store.setWeixin_status(1);
		store.setTemplate("default");
		
		store.setGrade(this.storeGradeService.getObjById(Long
					.parseLong("1")));
			
		if (store.isStore_recommend()) {
			store.setStore_recommend_time(new Date());
		} else {
			store.setStore_recommend_time(null);
		}
		//更新店铺区域以及店铺商品分公司代码
		store=this.goodsService.branchUpdate(store,CommUtil.null2Long(area_id));	
		if (id.equals("")) {
			this.storeService.save(store);
		} else {
			this.storeService.update(store);
		}
		
		if (store_status != null && !store_status.equals("")) {
			store.setStore_status(CommUtil.null2Int(store_status));
			if (store_status.equals("2")) {// 通过后直接生成storepoint，防止定时器执行时产生多个对应一个store的storepoint
				StorePoint point = store.getPoint();
				if (point == null) {
					point = new StorePoint();
					point.setStore(store);
					this.storepointService.save(point);
				}
			}
			//如果是违规关闭操作 ，下架该商店所有商品
			if(store_status.equals("3")){
				if(!id.equals("")){
				String query ="select obj from Goods obj where obj.goods_store.id=:storeId";
				Map params = new HashMap();
				params.put("storeId", Long.parseLong(id));
				List<Goods> goodsList = this.goodsService.query(query, params, -1, -1);
				for(int i=0;i<goodsList.size();i++){
					Goods goods=goodsList.get(i);
					if(goods != null){
						if(goods.getGoods_status()==0){
							goods.setGoods_status(1);
							goodsService.update(goods);
							//更新索引
							LuceneUtil.instance().removeLuceneIndex(goods.getId().toString());
					   }
					}
				}
				}
			}
		} else {
			store.setStore_status(2);
		}
		
		if (user_id != null && !user_id.equals("")) {
			User user = this.userService.getObjById(Long.parseLong(user_id));
			user.setStore(store);
			user.setUserRole("BUYER_SELLER");
			// 给用户赋予卖家权限
			Map params = new HashMap();
			params.put("type", "SELLER");
			List<Role> roles = this.roleService.query(
					"select obj from Role obj where obj.type=:type", params,
					-1, -1);
			user.getRoles().addAll(roles);
			this.userService.update(user);
		}
		// 关闭违规店铺发送站内信提醒
		if (!id.equals("") && store.getStore_status() == 3) {
			this.send_site_msg(request, "msg_to_seller_store_closed_notify",
					store);
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存店铺成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	private void send_site_msg(HttpServletRequest request, String mark,
			Store store) throws Exception {
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
				pwrite.flush();
				pwrite.close();
				// 生成模板
				Properties p = new Properties();
				p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,path);
				p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
				p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
				Velocity.init(p);
				org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm",
						"UTF-8");
				VelocityContext context = new VelocityContext();
				context.put("store",store);
				StringWriter writer = new StringWriter();
				blank.merge(context, writer);
				System.out.println(writer.toString());
				String content = writer.toString();
				try{
					sendmessage.sendHttpPost(store.getUser().getTelephone(), content);
				}catch (Exception e) {
					// TODO: handle exception
				}
			} 
		
		}
	}

	@SecurityMapping(title = "店铺删除", value = "/admin/store_del.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_del.htm")
	public String store_del(HttpServletRequest request, String mulitId)
			throws Exception {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Store store = this.storeService.getObjById(Long.parseLong(id));
				if (store.getUser() != null)
					store.getUser().setStore(null);
				for (Goods goods : store.getGoods_list()) {
					Map map = new HashMap();
					map.put("gid", goods.getId());
					List<GoodsCart> goodCarts = this.goodsCartService
							.query("select obj from GoodsCart obj where obj.goods.id = :gid",
									map, -1, -1);
					Long ofid = null;
					for (GoodsCart gc : goodCarts) {
						gc.getGsps().clear();
						this.goodsCartService.delete(gc.getId());
						if (gc.getOf() != null) {
							Map map2 = new HashMap();
							ofid = gc.getOf().getId();
							map2.put("ofid", ofid);
							List<GoodsCart> goodCarts2 = this.goodsCartService
									.query("select obj from GoodsCart obj where obj.of.id = :ofid",
											map2, -1, -1);
							if (goodCarts2.size() == 0) {
								this.orderFormService.delete(ofid);
							}
						}
					}

					List<Evaluate> evaluates = goods.getEvaluates();
					for (Evaluate e : evaluates) {
						this.evaluateService.delete(e.getId());
					}
					goods.getGoods_ugcs().clear();
					Accessory acc = goods.getGoods_main_photo();
					if (acc != null) {
						acc.setAlbum(null);
						Album album = acc.getCover_album();
						if (album != null) {
							album.setAlbum_cover(null);
							this.albumService.update(album);
						}
						this.accessoryService.update(acc);
					}
					for (Accessory acc1 : goods.getGoods_photos()) {
						if (acc1 != null) {
							acc1.setAlbum(null);
							Album album = acc1.getCover_album();
							if (album != null) {
								album.setAlbum_cover(null);
								this.albumService.update(album);
							}
							acc1.setCover_album(null);
							this.accessoryService.update(acc1);
						}
					}
					goods.getCombin_goods().clear();
					this.goodsService.delete(goods.getId());
				}
				for (OrderForm of : store.getOfs()) {
					for (GoodsCart gc : of.getGcs()) {
						gc.getGsps().clear();
						this.goodsCartService.delete(gc.getId());
					}
					this.orderFormService.delete(of.getId());
				}
				for (StoreCart sc : store.getCarts()) {
					for (GoodsCart gc : sc.getGcs()) {
						gc.getGsps().clear();
						this.goodsCartService.delete(gc.getId());
					}
					this.storeCartService.delete(sc.getId());
				}
				this.storeService.delete(CommUtil.null2Long(id));
				this.send_site_msg(request,
						"msg_toseller_goods_delete_by_admin_notify", store);
			}
		}
		return "redirect:store_list.htm";
	}

	@SecurityMapping(title = "店铺AJAX更新", value = "/admin/store_ajax.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_ajax.htm")
	public void store_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Store obj = this.storeService.getObjById(Long.parseLong(id));
		Field[] fields = Store.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			// System.out.println(field.getName());
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if (field.getType().getName().equals("int")) {
					clz = Class.forName("java.lang.Integer");
				}
				if (field.getType().getName().equals("boolean")) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!value.equals("")) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper
							.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		if (fieldName.equals("store_recommend")) {
			if (obj.isStore_recommend()) {
				obj.setStore_recommend_time(new Date());
			} else {
				obj.setStore_recommend_time(null);
			}
		}
		this.storeService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "卖家信用", value = "/admin/store_base.htm*", rtype = "admin", rname = "基本设置", rcode = "admin_store_base", rgroup = "店铺")
	@RequestMapping("/admin/store_base.htm")
	public ModelAndView store_base(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/store_base_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "卖家信用保存", value = "/admin/store_set_save.htm*", rtype = "admin", rname = "基本设置", rcode = "admin_store_base", rgroup = "店铺")
	@RequestMapping("/admin/store_set_save.htm")
	public ModelAndView store_set_save(HttpServletRequest request,
			HttpServletResponse response, String id, String list_url,
			String store_allow) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig sc = this.configService.getSysConfig();
		sc.setStore_allow(CommUtil.null2Boolean(store_allow));
		Map map = new HashMap();
		for (int i = 0; i <= 29; i++) {
			map.put("creditrule" + i,
					CommUtil.null2Int(request.getParameter("creditrule" + i)));
		}
		String creditrule = Json.toJson(map, JsonFormat.compact());
		sc.setCreditrule(creditrule);
		if (id.equals("")) {
			this.configService.save(sc);
		} else
			this.configService.update(sc);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存店铺设置成功");
		return mv;
	}

	// 店铺模板管理
	@SecurityMapping(title = "店铺模板", value = "/admin/store_template.htm*", rtype = "admin", rname = "店铺模板", rcode = "admin_store_template", rgroup = "店铺")
	@RequestMapping("/admin/store_template.htm")
	public ModelAndView store_template(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/store_template.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("path", request.getRealPath("/"));
		mv.addObject("separator", File.separator);
		return mv;
	}

	// 店铺模板管理
	@SecurityMapping(title = "店铺模板增加", value = "/admin/store_template_add.htm*", rtype = "admin", rname = "店铺模板", rcode = "admin_store_template", rgroup = "店铺")
	@RequestMapping("/admin/store_template_add.htm")
	public ModelAndView store_template_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/store_template_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "店铺模板保存", value = "/admin/store_template_save.htm*", rtype = "admin", rname = "店铺模板", rcode = "admin_store_template", rgroup = "店铺")
	@RequestMapping("/admin/store_template_save.htm")
	public ModelAndView store_template_save(HttpServletRequest request,
			HttpServletResponse response, String id, String list_url,
			String templates) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig sc = this.configService.getSysConfig();
		sc.setTemplates(templates);
		if (id.equals("")) {
			this.configService.save(sc);
		} else
			this.configService.update(sc);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "店铺模板设置成功");
		return mv;
	}

	@SecurityMapping(title = "店铺升级列表", value = "/admin/store_gradelog_list.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_gradelog_list.htm")
	public ModelAndView store_gradelog_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String store_name, String grade_id,
			String store_grade_status) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/store_gradelog_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		StoreGradeLogQueryObject qo = new StoreGradeLogQueryObject(currentPage,
				mv, orderBy, orderType);
		if (!CommUtil.null2String(store_name).equals("")) {
			qo.addQuery("obj.store.store_name", new SysMap("store_name", "%"
					+ store_name + "%"), "like");
			mv.addObject("store_name", store_name);
		}
		if (CommUtil.null2Long(grade_id) != -1) {
			qo.addQuery("obj.store.update_grade.id", new SysMap("grade_id",
					CommUtil.null2Long(grade_id)), "=");
			mv.addObject("grade_id", grade_id);
		}
		if (!CommUtil.null2String(store_grade_status).equals("")) {
			qo.addQuery(
					"obj.store_grade_status",
					new SysMap("store_grade_status", CommUtil
							.null2Int(store_grade_status)), "=");
			mv.addObject("store_grade_status", store_grade_status);
		}
		IPageList pList = this.storeGradeLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/store_list.htm", "",
				params, pList, mv);
		List<StoreGrade> grades = this.storeGradeService.query(
				"select obj from StoreGrade obj order by obj.sequence asc",
				null, -1, -1);
		mv.addObject("grades", grades);
		return mv;
	}

	@SecurityMapping(title = "店铺升级编辑", value = "/admin/store_gradelog_edit.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_gradelog_edit.htm")
	public ModelAndView store_gradelog_edit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/store_gradelog_edit.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		StoreGradeLog obj = this.storeGradeLogService.getObjById(CommUtil
				.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "店铺升级保存", value = "/admin/store_gradelog_save.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_gradelog_save.htm")
	public ModelAndView store_gradelog_save(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String cmd, String list_url) throws Exception {
		WebForm wf = new WebForm();
		StoreGradeLog obj = this.storeGradeLogService.getObjById(CommUtil
				.null2Long(id));
		StoreGradeLog log = (StoreGradeLog) wf.toPo(request, obj);
		log.setLog_edit(true);
		log.setAddTime(new Date());
		boolean ret = this.storeGradeLogService.update(log);
		if (ret) {
			Store store = log.getStore();
			if (log.getStore_grade_status() == 1) {
				store.setGrade(store.getUpdate_grade());
			}
			store.setUpdate_grade(null);
			this.storeService.update(store);
		}
		// 店铺升级审核发送站内信提醒
		if (log.getStore_grade_status() == 1) {
			this.send_site_msg(request,
					"msg_toseller_store_update_allow_notify", log.getStore());
		}
		if (log.getStore_grade_status() == -1) {
			this.send_site_msg(request,
					"msg_toseller_store_update_refuse_notify", log.getStore());
		}
		this.send_site_msg(request, "msg_toseller_store_update_allow_notify",
				log.getStore());
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存店铺成功");
		return mv;
	}

	@SecurityMapping(title = "店铺升级日志查看", value = "/admin/store_gradelog_view.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_gradelog_view.htm")
	public ModelAndView store_gradelog_view(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/store_gradelog_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		StoreGradeLog obj = this.storeGradeLogService.getObjById(CommUtil
				.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		return mv;
	}


}