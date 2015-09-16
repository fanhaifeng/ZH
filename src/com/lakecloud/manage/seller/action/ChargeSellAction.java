package com.lakecloud.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Charge;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.query.ChargeQueryObject;
import com.lakecloud.foundation.domain.query.OrderFormQueryObject;
import com.lakecloud.foundation.domain.query.UserQueryObject;
import com.lakecloud.foundation.service.IChargeService;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserGoodsClassService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.manage.admin.tools.StoreTools;
import com.lakecloud.view.web.tools.GoodsViewTools;
import com.sun.org.apache.bcel.internal.generic.NEW;


/**
 * 本类用于处理赊销
 * @author Administrator
 *
 */
@Controller
public class ChargeSellAction {
	
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IChargeService chargeService;
	@Autowired
	private IOrderFormService orderFormService;
	
	
	/**
	 * 赊销还款，列出所有的赊销订单
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "赊销订单列表", value = "/seller/seller_refund_list.htm*", rtype = "seller", rname = "赊销还款", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/seller_refund_list.htm")
	public ModelAndView seller_refund_list(HttpServletRequest request,HttpServletResponse response,String currentPage, String orderBy,
			String orderType,String buyer_userName,String beginTime,String endTime,String order_id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/seller_refund_list.html", configService
				.getSysConfig(),this.userConfigService.getUserConfig(), 0, request,response);
		
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		
		String params = "";
		OrderFormQueryObject qo = new OrderFormQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.addQuery("obj.store.id", new SysMap("id",  SecurityUserHolder.getCurrentUser().getStore().getId()), "=");
		
		//带了订单用户id则查询该用户否则查询整个经销商的
		if (!CommUtil.null2String(buyer_userName).equals("")) {	
			qo.addQuery("obj.user.userName", new SysMap("username", buyer_userName), "=");	
		}
		if (!CommUtil.null2String(order_id).equals("")) {
			qo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id
					+ "%"), "like");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
		}
		qo.addQuery("obj.charge_Num", new SysMap("charge_Num", BigDecimal.ZERO), ">");
		
		IPageList pList = this.orderFormService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/seller/seller_refund_list.htm","",
				params, pList, mv);
		mv.addObject("order_id", order_id);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("buyer_userName", buyer_userName);
		return mv;
	}
	
	
	/**
	 * 该经销商所有赊销用户列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "赊销订单列表", value = "/seller/charge_seller_list.htm*", rtype = "seller", rname = "赊销", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/charge_seller_list.htm")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response,String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/charge_seller_list.html", configService
				.getSysConfig(),this.userConfigService.getUserConfig(), 0, request,response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		
		String params = "";
		ChargeQueryObject qo = new ChargeQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.addQuery("obj.store.id", new SysMap("id",  SecurityUserHolder.getCurrentUser().getStore().getId()), "=");
		
		// WebForm wf = new WebForm();
		// wf.toQueryPo(request, qo,Charge.class,mv);
		IPageList pList = this.chargeService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/seller/charge_seller_list.htm","",
				params, pList, mv);
		return mv;
	}
	
	/**
	 * 订单订单列表
	 * @param request
	 * @param response
	 * @param order_status
	 *            ：订单状态，根据订单状态不同查询相应的订单
	 * @return
	 */
	@SecurityMapping(title = "赊销订单列表", value = "/seller/charge_sell_manage.htm*", rtype = "seller", rname = "赊销", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/charge_sell_manage.htm")
	public ModelAndView charge_sell_manage(HttpServletRequest request,
			HttpServletResponse response, String order_status) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/charge_sell_manage.htm",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		List<OrderForm> orders = null;
		Map map = new HashMap();
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		
		if (!CommUtil.null2String(order_status).equals("")) {
			map.put("order_status", CommUtil.null2Int(order_status));
			orders = this.orderFormService
					.query("select obj from OrderForm obj where obj.order_status=:order_status and obj.user.id=:user_id order by addTime desc",
							map, 0, 6);
			mv.addObject("order_status", order_status);
		} else {
			orders = this.orderFormService
					.query("select obj from OrderForm obj where obj.user.id=:user_id order by addTime desc",
							map, 0, 6);
		}
		mv.addObject("objs", orders);
		return mv;
	}
	
	
	/**
	 * 赊销列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param goods_name
	 * @param user_class_id
	 * @return
	 */
	@SecurityMapping(title = "赊销", value = "/seller/charge_sell.htm*", rtype = "seller", rname = "赊销", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/charge_sell.htm")
	public ModelAndView goods_out(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String user_class_id,String status) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/charge_sell.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if(status==null){
			status="";
		}
		String params="";
		if(!status.equals("")){
			params = "&status="+status;
		}
		
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		
		//获取当前登录用户
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("store_id", user.getStore().getId());
		mv.addObject("status",status);
		if(CommUtil.null2String(status).equals("1")){
			//已经授信
			ChargeQueryObject chargeqo = new ChargeQueryObject(currentPage, mv, orderBy,
					orderType);
			chargeqo.addQuery("obj.store.id", new SysMap("store_id",user.getStore().getId()), "=");
			chargeqo.setOrderType("desc");
			IPageList pList = this.chargeService.list(chargeqo);
			
			List<Charge> chargeList = pList.getResult();
			if(chargeList!=null && chargeList.size()>0){
				List<User> userList = new ArrayList<User>(); 
				for(Charge ch : chargeList){
					User tmpUser = ch.getUser();
					for(Charge tt : tmpUser.getCharges() ){
						if(tt.getStore().getId() == user.getStore().getId()){
							List<Charge> chList = new ArrayList<Charge>();
							chList.add(tt);
							tmpUser.setCharges(chList);
							break;
						}
					}
					userList.add(tmpUser);
				}
				CommUtil.saveIPageList2ModelAndView(url + "/seller/charge_sell.htm", "",
						params,pList, mv);
				mv.addObject("objs",userList);
			}				
		}else if(CommUtil.null2String(status).equals("0")){
			//未授信
			String querString ="select obj from Charge obj where obj.store.id=:store_id";
			Map<String,Long> paMap = new HashMap<String, Long>();
			paMap.put("store_id",user.getStore().getId());
			
			List<Charge>  chargeList = this.chargeService.query(querString, paMap, -1, -1);
			List<Long> idList = new ArrayList<Long>();
			if(chargeList!=null && chargeList.size()>0){
				for(Charge ch:chargeList){
					idList.add(ch.getUser().getId());
				}
			}
			
			UserQueryObject userqo = new UserQueryObject(currentPage, mv, orderBy,
					orderType);
			userqo.addQuery("obj.id", new SysMap("id", SecurityUserHolder
					.getCurrentUser().getId()), "!=");
			userqo.addQuery("obj.userRole", new SysMap("userrole", "ADMIN"), "!=");
			for(Long id : idList){
				userqo.addQuery("obj.id", new SysMap("user_id"+id,id), "!=");
			}
			userqo.setOrderType("desc");
			IPageList pList = this.userService.list(userqo);
			List<User> userList1 = pList.getResult();
			List<User> tmpUsers = new ArrayList<User>();
			for(User uu1 : userList1){
				uu1.setCharges(new ArrayList<Charge>());
				tmpUsers.add(uu1);
			}
			
			CommUtil.saveIPageList2ModelAndView(url + "/seller/charge_sell.htm", "",
					params,pList, mv);
			mv.addObject("objs",tmpUsers);
		}else{
			//所有用户
			UserQueryObject qo = new UserQueryObject(currentPage, mv, orderBy,
					orderType);
			qo.addQuery("obj.id", new SysMap("id", SecurityUserHolder
					.getCurrentUser().getId()), "!=");
			qo.addQuery("obj.userRole", new SysMap("userrole", "ADMIN"), "!=");
			
			qo.setOrderType("desc");
			IPageList pList = this.userService.list(qo);
			Map<Long, Map<Long,User>> userMap = new HashMap<Long, Map<Long,User>>();
			List<User> userList =(List<User>)pList.getResult();	
			for(User u:userList){
				List<Charge> chargeList = u.getCharges();
				if(chargeList.size()>0){	
					Map<Long,User> userTmp = new HashMap<Long,User>();
					for(Charge ch : chargeList){
						userTmp.put(ch.getStore().getId(),u);
					}	
					userMap.put(u.getId(),userTmp);
				}		
			}	
		
			List<User> userListTmp = new ArrayList<User>();
			User userTmp = null;
			for(User uu:userList){
				//买家在此经销商有赊销记录	
				if( userMap.get(uu.getId()) !=null
						&& userMap.get(uu.getId()).get(user.getStore().getId()) != null){			
						User tmpUser  = userMap.get(uu.getId()).get(user.getStore().getId());
						for(Charge tt : tmpUser.getCharges()){
							if(tt.getStore().getId() == user.getStore().getId()){
								List<Charge> chList = new ArrayList<Charge>();
								chList.add(tt);
								tmpUser.setCharges(chList);
								
								break;
							}
						}
						userTmp = userMap.get(uu.getId()).get(user.getStore().getId());
						userListTmp.add(userTmp);		
				}else{
					uu.setCharges(new ArrayList<Charge>());	
					userListTmp.add(uu);
				}	
			}
			
			CommUtil.saveIPageList2ModelAndView(url + "/seller/charge_sell.htm", "",
					params,pList, mv);
			mv.addObject("objs",userListTmp);
		}
		return mv;
	}
		
	/**
	 * 修改赊销列表
	 * @param request
	 * @param response
	 * @param num
	 * @param day
	 * @param store_id
	 * @param buyer_id
	 */
	@SecurityMapping(title = "赊销记录修改", value = "/seller/charge_update.htm*", rtype = "seller", rname = "赊销", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/charge_update.htm")
	public void charge_update(HttpServletRequest request,HttpServletResponse response
			,String num,String day,String store_id,String buyer_id){
		
		String query ="select obj from Charge obj where obj.store.id=:store_id and obj.user.id=:user_id";
		Map params = new HashMap();
		params.put("store_id", Long.parseLong(store_id));
		params.put("user_id", Long.parseLong(buyer_id));
		List<Charge>  chargeList = this.chargeService.query(query, params, -1, -1);

		JSONObject jsonObj = new JSONObject();
		boolean flag = false;
		if(chargeList.size()>0){
			Charge charge = chargeList.get(0);
			//现在的金额不能低于当前没有还的 金额
			BigDecimal usedPay = charge.getUsedPayNum();
			BigDecimal nowPay  = new BigDecimal(num);
			
			if( nowPay.compareTo(usedPay) < 0 ){
				jsonObj.put("resultData","修改金额不能低于未还赊销金额");
				jsonObj.put("resultCode","ERROR");
				jsonObj.put("timestamp",new Date().getTime());	
			}else{
				charge.setPaymentDays(Integer.parseInt(day));
				charge.setPaymentNum(new BigDecimal(num));
				charge.setAddTime(new Date());
				flag = this.chargeService.update(charge);
				if(flag){
					jsonObj.put("resultData","更新成功");
					jsonObj.put("resultCode","OK");
					jsonObj.put("timestamp",new Date().getTime());	
				}else{
					jsonObj.put("resultData","更新失败");
					jsonObj.put("resultCode","ERR");
				}
			}	
		}else{
			Charge charge = new Charge();
			charge.setPaymentDays(Integer.parseInt(day));
			charge.setPaymentNum(new BigDecimal(num));
			charge.setAddTime(new Date());
			charge.setUsedPayNum(new BigDecimal(0));
			Store store = new Store();
			store.setId(Long.parseLong(store_id));
			charge.setStore(store);
			User user = new User();
			user.setId(Long.parseLong(buyer_id));
			charge.setUser(user);
			flag = this.chargeService.save(charge);
			if(flag){
				jsonObj.put("resultData","保存成功");
				jsonObj.put("resultCode","OK");
				jsonObj.put("timestamp",new Date().getTime());	
			}else{
				jsonObj.put("resultData","保存失败");
				jsonObj.put("resultCode","ERR");
			}
		}
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
