package com.lakecloud.manage.admin.action;

import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
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
import com.lakecloud.foundation.domain.Accessory;
import com.lakecloud.foundation.domain.Album;
import com.lakecloud.foundation.domain.Audit;
import com.lakecloud.foundation.domain.Branch;
import com.lakecloud.foundation.domain.Brpr;
import com.lakecloud.foundation.domain.Evaluate;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsBrand;
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.GoodsClass;
import com.lakecloud.foundation.domain.GoodsClassStaple;
import com.lakecloud.foundation.domain.GoodsDC;
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.Message;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Payment;
import com.lakecloud.foundation.domain.ReferPrice;
import com.lakecloud.foundation.domain.StoreGrade;
import com.lakecloud.foundation.domain.Template;
import com.lakecloud.foundation.domain.Transport;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.UserGoodsClass;
import com.lakecloud.foundation.domain.WaterMark;
import com.lakecloud.foundation.domain.query.AccessoryQueryObject;
import com.lakecloud.foundation.domain.query.GoodsQueryObject;
import com.lakecloud.foundation.service.IAccessoryService;
import com.lakecloud.foundation.service.IAlbumService;
import com.lakecloud.foundation.service.IBranchService;
import com.lakecloud.foundation.service.IEvaluateService;
import com.lakecloud.foundation.service.IGoodsBrandService;
import com.lakecloud.foundation.service.IGoodsCartService;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.IGoodsClassStapleService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGoodsServiceDC;
import com.lakecloud.foundation.service.IGoodsSpecPropertyService;
import com.lakecloud.foundation.service.IGoodsTypePropertyService;
import com.lakecloud.foundation.service.IMessageService;
import com.lakecloud.foundation.service.IOrderFormLogService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IPaymentService;
import com.lakecloud.foundation.service.IReferPriceService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.ITemplateService;
import com.lakecloud.foundation.service.ITransportService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserGoodsClassService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.foundation.service.IWaterMarkService;
import com.lakecloud.lucene.LuceneUtil;
import com.lakecloud.lucene.LuceneVo;
import com.lakecloud.manage.admin.tools.MsgTools;
import com.lakecloud.manage.admin.tools.StoreTools;
import com.lakecloud.view.web.tools.StoreViewTools;
import com.sun.org.apache.bcel.internal.generic.NEW;

@Controller
public class GoodsManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsServiceDC goodsServiceDC;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IGoodsClassStapleService goodsclassstapleService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoodsSpecPropertyService specPropertyService;
	@Autowired
	private IGoodsTypePropertyService goodsTypePropertyService;
	@Autowired
	private ITransportService transportService;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private IBranchService branchService;
	@Autowired
	private IReferPriceService referPriceService;
	@Autowired
	private IWaterMarkService waterMarkService;
	@Autowired
	private IAlbumService albumService;
	/**
	 * GoodsDC列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "平台商品列表", value = "/admin/goodsDC_list.htm*", rtype = "admin", rname = "平台商品管理", rcode = "admin_goods_dc", rgroup = "商品")
	@RequestMapping("/admin/goodsDC_list.htm")
	public ModelAndView goodsDC_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType,String goodsnamet,String goodsbrandt,String goodsclasst,String goodsserialt) {
		ModelAndView mv = new JModelAndView("admin/blue/goodsDC_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
//		WebForm wf = new WebForm();
//		wf.toQueryPo(request, qo, GoodsDC.class, mv);
		if(goodsnamet!=null && !goodsnamet.equals("")){
			qo.addQuery(" obj.goods_name", new SysMap("goods_name","%"+goodsnamet.trim()+"%" ), "like");
			mv.addObject("goodsnamet", goodsnamet);
		}
		if(goodsbrandt!=null && !goodsbrandt.equals("")){
			qo.addQuery(" obj.goods_brand.id", new SysMap("goods_brand",CommUtil.null2Long(goodsbrandt)), "=");
			mv.addObject("goodsbrandt", goodsbrandt);
		}
		if(goodsclasst!=null && !goodsclasst.equals("")){
			qo.addQuery(" obj.gc.id", new SysMap("goods_class",CommUtil.null2Long(goodsclasst)), "=");
			mv.addObject("goodsclasst", goodsclasst);
		}
		if(goodsserialt!=null && !goodsserialt.equals("")){
			qo.addQuery(" obj.goods_serial", new SysMap("goods_serial","%"+goodsserialt.trim()+"%" ), "like");
			mv.addObject("goodsserialt", goodsserialt);
		}
		qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), ">");
		IPageList pList = this.goodsServiceDC.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/goodsDC_list.htm", "",
				params, pList, mv);
		List<GoodsBrand> gbs = this.goodsBrandService.query(
				"select obj from GoodsBrand obj order by obj.sequence asc",
				null, -1, -1);
		List<GoodsClass> gcs = this.goodsClassService
				.query(
						"select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		mv.addObject("goodsbrandt", goodsbrandt);
		mv.addObject("goodsclasst", goodsclasst);
		return mv;
	}
	
		/**
	 * Goods列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "店铺商品列表", value = "/admin/goods_list.htm*", rtype = "admin", rname = "店铺商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_list.htm")
	public ModelAndView goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType,String goodsnamet,String uesernamet ,String goodsbrandt,String goodsclasst,String recommendt) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
//		WebForm wf = new WebForm();
//	wf.toQueryPo(request, qo, Goods.class, mv);
		if(goodsnamet!=null && !goodsnamet.equals("")){
			qo.addQuery(" obj.goods_name", new SysMap("goods_name","%"+goodsnamet.trim()+"%" ), "like");
			mv.addObject("goodsnamet", goodsnamet);
		}
		if(uesernamet!=null && !uesernamet.equals("")){
			qo.addQuery(" obj.goods_store.user.userName", new SysMap("userName","%"+uesernamet.trim()+"%" ), "like");
			mv.addObject("uesernamet", uesernamet);
		}
		if(goodsbrandt!=null && !goodsbrandt.equals("")){
			qo.addQuery(" obj.goods_brand.id", new SysMap("goods_brand",CommUtil.null2Long(goodsbrandt)), "=");
			mv.addObject("goodsbrandt", goodsbrandt);
		}
		if(goodsclasst!=null && !goodsclasst.equals("")){
			qo.addQuery(" obj.gc.id", new SysMap("goods_class",CommUtil.null2Long(goodsclasst)), "=");
			mv.addObject("goodsclasst", goodsclasst);
		}
		if(recommendt!=null && !recommendt.equals("")){
			qo.addQuery(" obj.store_recommend", new SysMap("goods_recommend",Boolean.parseBoolean(recommendt)), "=");
			mv.addObject("recommendt", recommendt);
		}
		Branch branch=SecurityUserHolder.getCurrentUser().getBranch();
		if( branch != null ){
			if(!branch.getCode().equals("1400"))
			qo.addQuery("obj.branch_code", new SysMap("branch_code", branch.getCode()), "=");
		}
		qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), ">");
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/goods_list.htm", "",
				params, pList, mv);
		List<GoodsBrand> gbs = this.goodsBrandService.query(
				"select obj from GoodsBrand obj order by obj.sequence asc",
				null, -1, -1);
		List<GoodsClass> gcs = this.goodsClassService
				.query(
						"select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		mv.addObject("goodsnamet", goodsnamet);
		mv.addObject("uesernamet", uesernamet);
		mv.addObject("goodsbrandt", goodsbrandt);
		mv.addObject("goodsclasst", goodsclasst);
		mv.addObject("recommendt", recommendt);
	
		return mv;
	}
	
	/**
	 * GoodsDC违规商品列表页
	 */
	@SecurityMapping(title = "违规商品列表", value = "/admin/goodsDC_outline.htm*", rtype = "admin", rname = "平台商品管理", rcode = "admin_goods_dc", rgroup = "商品")
	@RequestMapping("/admin/goodsDC_outline.htm")
	public ModelAndView goodsDC_outline(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/goodsDC_outline.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, GoodsDC.class, mv);
		qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), "=");
		IPageList pList = this.goodsServiceDC.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/goodsDC_list.htm", "",
				params, pList, mv);
		List<GoodsBrand> gbs = this.goodsBrandService.query(
				"select obj from GoodsBrand obj order by obj.sequence asc",
				null, -1, -1);
		List<GoodsClass> gcs = this.goodsClassService
				.query(
						"select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		return mv;
	}

	/**
	 * Goods违规商品列表页
	 */
		@SecurityMapping(title = "违规商品列表", value = "/admin/goods_outline.htm*", rtype = "admin", rname = "店铺商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_outline.htm")
	public ModelAndView goods_outline(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType,String goodsnamex,String uesernamex ,String goodsbrandx,String goodsclassx) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_outline.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
//		WebForm wf = new WebForm();
//		wf.toQueryPo(request, qo, Goods.class, mv);
			if(goodsnamex!=null && !goodsnamex.equals("")){
				qo.addQuery(" obj.goods_name", new SysMap("goods_name","%"+goodsnamex.trim()+"%" ), "like");
				mv.addObject("goodsnamex", goodsnamex);
			}
			if(uesernamex!=null && !uesernamex.equals("")){
				qo.addQuery(" obj.goods_store.user.userName", new SysMap("userName","%"+uesernamex.trim()+"%" ), "like");
				mv.addObject("uesernamex", uesernamex);
			}
			if(goodsbrandx!=null && !goodsbrandx.equals("")){
				qo.addQuery(" obj.goods_brand.id", new SysMap("goods_brand",CommUtil.null2Long(goodsbrandx)), "=");
				mv.addObject("goodsbrandx", goodsbrandx);
			}
			if(goodsclassx!=null && !goodsclassx.equals("")){
				qo.addQuery(" obj.gc.id", new SysMap("goods_class",CommUtil.null2Long(goodsclassx)), "=");
				mv.addObject("goodsclasst", goodsclassx);
			}
		qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), "=");
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/goods_outline.htm", "",
				params, pList, mv);
		System.out.println(url+"/admin/goods_outline.htm");
		List<GoodsBrand> gbs = this.goodsBrandService.query(
				"select obj from GoodsBrand obj order by obj.sequence asc",
				null, -1, -1);
		List<GoodsClass> gcs = this.goodsClassService
				.query(
						"select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		mv.addObject("webPath",url);
		return mv;
	}

	/**
	 * goods添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "平台商品添加", value = "/admin/goods_add.htm*", rtype = "admin", rname = "平台商品管理", rcode = "admin_goods_dc", rgroup = "商品")
	@RequestMapping("/admin/goods_add.htm")
	public ModelAndView goods_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * goodsDC编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "平台商品编辑", value = "/admin/goodsDC_edit.htm*", rtype = "admin", rname = "平台商品管理", rcode = "admin_goods_dc", rgroup = "商品")
	@RequestMapping("/admin/goodsDC_edit.htm")
	public ModelAndView goodsDC_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			GoodsDC goodsDC = this.goodsServiceDC.getObjById(Long.parseLong(id));
			mv.addObject("obj", goodsDC);
			mv.addObject("currentPage", currentPage);
		}
		return mv;
	}
	
		/**
	 * goods编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "店铺商品编辑", value = "/admin/goods_edit.htm*", rtype = "admin", rname = "店铺商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_edit.htm")
	public ModelAndView goods_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Goods goods = this.goodsService.getObjById(Long.parseLong(id));
			mv.addObject("obj", goods);
			mv.addObject("currentPage", currentPage);
		}
		return mv;
	}




	/**
	 * goodsDC保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "平台商品保存", value = "/admin/goodsDC_save.htm*", rtype = "admin", rname = "平台商品管理", rcode = "admin_goods_dc", rgroup = "商品")
	@RequestMapping("/admin/goodsDC_save.htm")
	public ModelAndView goodsDC_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		GoodsDC goodsDC = null;
		if (id.equals("")) {
			goodsDC = wf.toPo(request, GoodsDC.class);
			goodsDC.setAddTime(new Date());
		} else {
			GoodsDC obj = this.goodsServiceDC.getObjById(Long.parseLong(id));
			goodsDC = (GoodsDC) wf.toPo(request, obj);
		}
		if (id.equals("")) {
			this.goodsServiceDC.save(goodsDC);
		} else
			this.goodsServiceDC.update(goodsDC);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存商品成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * goods保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "店铺商品保存", value = "/admin/goods_save.htm*", rtype = "admin", rname = "店铺商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_save.htm")
	public ModelAndView goods_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		Goods goods = null;
		if (id.equals("")) {
			goods = wf.toPo(request, Goods.class);
			goods.setAddTime(new Date());
		} else {
			Goods obj = this.goodsService.getObjById(Long.parseLong(id));
			goods = (Goods) wf.toPo(request, obj);
		}
		if (id.equals("")) {
			this.goodsService.save(goods);
		} else
			this.goodsService.update(goods);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存商品成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}
	
	
//goodsDC删除
	@SecurityMapping(title = "平台商品删除", value = "/admin/goodsDC_del.htm*", rtype = "admin", rname = "平台商品管理", rcode = "admin_goods_dc", rgroup = "商品")
	@RequestMapping("/admin/goodsDC_del.htm")
	public String goodsDC_del(HttpServletRequest request, String mulitId)
			throws Exception {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GoodsDC goodsDC = this.goodsServiceDC.getObjById(CommUtil
						.null2Long(id));
				Map map = new HashMap();
				map.put("gid", goodsDC.getId());
				List<GoodsCart> goodCarts = this.goodsCartService
						.query(
								"select obj from GoodsCart obj where obj.goods.id = :gid",
								map, -1, -1);
				Long ofid = null;
				for (GoodsCart gc : goodCarts) {
					Long of_id = gc.getOf()==null?null:gc.getOf().getId();
					this.goodsCartService.delete(gc.getId());
					OrderForm of = this.orderFormService.getObjById(of_id);
					if (of!=null&&of.getGcs().size() == 0) {
						this.orderFormService.delete(of_id);
					}
				}
				//goodsDC.getGoods_ugcs().clear();
				//goodsDC.getGoods_ugcs().clear();
				goodsDC.getGoodsDC_photos().clear();
				//goodsDC.getGoods_ugcs().clear();
				goodsDC.getGoodsDC_specs().clear();
				this.goodsServiceDC.delete(goodsDC.getId());
				// 删除索引
				LuceneUtil.instance().removeLuceneIndex(id);
				// 发送站内短信提醒卖家
				this.send_site_msgDC(request,
						"msg_toseller_goods_delete_by_admin_notify", goodsDC
								.getGoods_store().getUser(), goodsDC, "商城存在违规");
			}
		}
		return "redirect:goodsDC_list.htm";
	}
//GOOD删除	
	@SecurityMapping(title = "店铺商品删除", value = "/admin/goods_del.htm*", rtype = "admin", rname = "店铺商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_del.htm")
	public String goods_del(HttpServletRequest request, String mulitId)
			throws Exception {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(id));
				Map map = new HashMap();
				map.put("gid", goods.getId());
				List<GoodsCart> goodCarts = this.goodsCartService
						.query(
								"select obj from GoodsCart obj where obj.goods.id = :gid",
								map, -1, -1);
				Long ofid = null;
				for (GoodsCart gc : goodCarts) {
					Long of_id = gc.getOf()==null?null:gc.getOf().getId();
					this.goodsCartService.delete(gc.getId());
					OrderForm of = this.orderFormService.getObjById(of_id);
					if (of!=null&&of.getGcs().size() == 0) {
						this.orderFormService.delete(of_id);
					}
				}

				List<Evaluate> evaluates = goods.getEvaluates();
				for (Evaluate e : evaluates) {
					this.evaluateService.delete(e.getId());
				}
				goods.getGoods_ugcs().clear();
				goods.getGoods_ugcs().clear();
				goods.getGoods_photos().clear();
				goods.getGoods_ugcs().clear();
				goods.getGoods_specs().clear();
				this.goodsService.delete(goods.getId());
				// 删除索引
				LuceneUtil.instance().removeLuceneIndex(id);
				// 发送站内短信提醒卖家
				this.send_site_msg(request,
						"msg_toseller_goods_delete_by_admin_notify", goods
								.getGoods_store().getUser(), goods, "商城存在违规");
			}
		}
		return "redirect:goods_list.htm";
	}

	private void send_site_msgDC(HttpServletRequest request, String mark,
			User user, GoodsDC goodsDC, String reason) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template.isOpen()) {
			String path = request.getSession().getServletContext().getRealPath(
					"/")
					+ "/vm/";
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + "msg.vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, request
					.getRealPath("/")
					+ "vm" + File.separator);
			p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
			Velocity.init(p);
			org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm",
					"UTF-8");
			VelocityContext context = new VelocityContext();
			context.put("reason", reason);
			context.put("user", user);
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			// System.out.println(writer.toString());
			String content = writer.toString();
			User fromUser = this.userService.getObjByProperty("userName",
					"admin");
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setContent(content);
			msg.setFromUser(fromUser);
			msg.setTitle(template.getTitle());
			msg.setToUser(user);
			msg.setType(0);
			this.messageService.save(msg);
			CommUtil.deleteFile(path + "temp.vm");
			writer.flush();
			writer.close();
		}
	}
	
	private void send_site_msg(HttpServletRequest request, String mark,
			User user, Goods goods, String reason) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template.isOpen()) {
			String path = request.getSession().getServletContext().getRealPath(
					"/")
					+ "/vm/";
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + "msg.vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, request
					.getRealPath("/")
					+ "vm" + File.separator);
			p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
			Velocity.init(p);
			org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm",
					"UTF-8");
			VelocityContext context = new VelocityContext();
			context.put("reason", reason);
			context.put("user", user);
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			// System.out.println(writer.toString());
			String content = writer.toString();
			User fromUser = this.userService.getObjByProperty("userName",
					"admin");
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setContent(content);
			msg.setFromUser(fromUser);
			msg.setTitle(template.getTitle());
			msg.setToUser(user);
			msg.setType(0);
			this.messageService.save(msg);
			CommUtil.deleteFile(path + "temp.vm");
			writer.flush();
			writer.close();
		}
	}
	
//更新GOODS表
	@SecurityMapping(title = "店铺商品AJAX更新", value = "/admin/goods_ajax.htm*", rtype = "admin", rname = "店铺商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws Exception {
		Goods obj = this.goodsService.getObjById(Long.parseLong(id));
		int old_status = obj.getGoods_status();
		Field[] fields = Goods.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
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
			} else
				obj.setStore_recommend_time(null);
		}
		this.goodsService.update(obj);
		//违规下架操作发送短信
		if(!value.equals("") && value.equals("-2")){
			if (this.configService.getSysConfig().isSmsEnbale()) {
				//给经销商发送短信
				this.send_undercarriage_sms(request, obj,"sms_toseller_undercarriage_notify");
			}
		}
		if (obj.getGoods_status() == 1&&old_status==-2) {
			
		} else if(obj.getGoods_status() == -2){
			//删除lucene索引
			LuceneUtil.instance().removeLuceneIndex(id);
		}else{
			// 更新lucene索引
			LuceneUtil.instance().ruLuceneIndex(obj,"u");
		}
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

@SecurityMapping(title = "发布商品第一步", value = "/admin/add_goods_first.htm*", rtype = "admin", rname = "商品发布", rcode = "admin_goods_add", rgroup = "商品")
	@RequestMapping("/admin/add_goods_first.htm")
	public ModelAndView add_goods_first(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
//		User user = this.userService.getObjById(SecurityUserHolder
//				.getCurrentUser().getId());
		Map params = new HashMap();

		request.getSession(false).removeAttribute("goods_class_info");

		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv = new JModelAndView(
				"admin/blue/add_goods_first.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request,
				response);
		params.clear();
		//params.put("store_id", user.getStore().getId());
		List<GoodsClassStaple> staples = this.goodsclassstapleService
				.query("select obj from GoodsClassStaple obj where 1=1 order by obj.addTime desc",
						params, -1, -1);
		mv.addObject("staples", staples);
		mv.addObject("gcs", gcs);
		mv.addObject("id", CommUtil.null2String(id));

		return mv;
	}
	
	@SecurityMapping(title = "发布商品第二步", value = "/admin/add_goods_second.htm*", rtype = "admin", rname = "商品发布", rcode = "admin_goods_add", rgroup = "商品")
	@RequestMapping("/admin/add_goods_second.htm")
	public ModelAndView add_goods_second(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		/*int store_status = this.storeService.getObjByProperty("user.id",
				user.getId()).getStore_status();*/
		
			mv = new JModelAndView(
					"admin/blue/add_goods_second.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			GoodsClass gc =null;
			if (request.getSession(false).getAttribute("goods_class_info") != null) {
				gc = (GoodsClass) request.getSession(false)
						.getAttribute("goods_class_info");
				System.out.println(gc.getId());
				gc = this.goodsClassService.getObjById(gc.getId());
				String goods_class_info = this.generic_goods_class_info(gc);
				mv.addObject("goods_class",
						this.goodsClassService.getObjById(gc.getId()));
				mv.addObject("goods_class_info", goods_class_info.substring(0,
						goods_class_info.length() - 1));
				request.getSession(false).removeAttribute("goods_class_info");
			}
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ File.separator
					+ "upload"
					+ File.separator
					+ "goods"
					//+ File.separator + user.getStore().getId();
					+ File.separator +"images";
			double img_remain_size = 0;//剩余图片大小
			//根据店铺等级分配的磁盘空间大小
			/*if (user.getStore().getGrade().getSpaceSize() > 0) {
				img_remain_size = user.getStore().getGrade().getSpaceSize()
						- CommUtil.div(CommUtil.fileSize(new File(path)), 1024);
			}*/
			Map params = new HashMap();
			params.put("user_id", user.getId());
			params.put("display", true);
			List<UserGoodsClass> ugcs = this.userGoodsClassService
					.query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
							params, -1, -1);
//			List<GoodsBrand> gbs = this.goodsBrandService.query(
//					"select obj from GoodsBrand obj order by obj.sequence asc",
//					null, -1, -1);
			List<GoodsBrand> gbs = gc.getGoodsType()==null?null:gc.getGoodsType().getGbs();
			mv.addObject("gbs", gbs);
			mv.addObject("ugcs", ugcs);
			mv.addObject("img_remain_size", img_remain_size);
			mv.addObject("imageSuffix", this.storeViewTools
					.genericImageSuffix(this.configService.getSysConfig()
							.getImageSuffix()));
			String goods_session = CommUtil.randomString(32);
			mv.addObject("goods_session", goods_session);
			request.getSession(false).setAttribute("goods_session",
					goods_session);
		return mv;
	}
	private String generic_goods_class_info(GoodsClass gc) {
		String goods_class_info = gc.getClassName() + ">";
		if (gc.getParent() != null) {
			String class_info = generic_goods_class_info(gc.getParent());
			goods_class_info = class_info + goods_class_info;
		}
		return goods_class_info;
	}
	
	
	@SecurityMapping(title = "发布商品第三步", value = "/admin/add_goods_finish.htm*", rtype = "admin", rname = "商品发布", rcode = "admin_goods_add", rgroup = "商品")
	@RequestMapping("/admin/add_goods_finish.htm")
	public ModelAndView add_goods_finish(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_class_id,
			String image_ids, String goods_main_img_id, String user_class_ids,
			String goods_brand_id, String goods_spec_ids,
			String goods_properties,
			String goods_session, String transport_type, String transport_id) {
		ModelAndView mv = null;
		String goods_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("goods_session"));
		if (goods_session1.equals("")) {
			mv = new JModelAndView("admin/blue/goods_referprice_finish.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(),0, request,
					response);
			mv.addObject("op_title", "禁止重复提交表单");
		//	mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		} else {
			if (goods_session1.equals(goods_session)) {
				if (id == null || id.equals("")) {
					mv = new JModelAndView(
							"admin/blue/success_goods_publish.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
					mv.addObject("op_title", "商品发布成功");
					mv.addObject("add_url","/admin/add_goods_first.htm");//跳转到商品发布页面
					mv.addObject("list_url","/admin/add_goods_first.htm");
				} else {
					mv = new JModelAndView("admin/blue/success_goods_publish.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			        mv.addObject("op_title", "商品编辑成功");
					mv.addObject("list_url","/admin/goodsDC_list.htm");//跳转到平台商品管理页面
				}
				WebForm wf = new WebForm();
				GoodsDC goodsDC = null;
				//物料号
				String partnum = "";
				int goodsdc_status = 0;
				if (id.equals("")) {
					goodsDC = wf.toPo(request, GoodsDC.class);
					goodsDC.setAddTime(new Date());
					User user = this.userService.getObjById(SecurityUserHolder
							.getCurrentUser().getId());
					goodsDC.setGoods_store(user.getStore());
				} else {
					GoodsDC obj = this.goodsServiceDC
							.getObjById(Long.parseLong(id));
					partnum = obj.getGoods_serial();//获取原来的物料号
					goodsdc_status = obj.getGoodsdc_status();//获取原来的商品库中商品状态
					goodsDC = (GoodsDC) wf.toPo(request, obj);
				}
				goodsDC.setGoods_name(clearContent(goodsDC.getGoods_name()));
				GoodsClass gc = this.goodsClassService.getObjById(Long
						.parseLong(goods_class_id));
				goodsDC.setGc(gc);
				Accessory main_img = null;
				if (goods_main_img_id != null && !goods_main_img_id.equals("")) {
					main_img = this.accessoryService.getObjById(Long
							.parseLong(goods_main_img_id));
				}
				goodsDC.setGoods_main_photo(main_img);
//				goodsDC.getGoods_ugcs().clear();
//				String[] ugc_ids = user_class_ids.split(",");
//				for (String ugc_id : ugc_ids) {
//					if (!ugc_id.equals("")) {
//						UserGoodsClass ugc = this.userGoodsClassService
//								.getObjById(Long.parseLong(ugc_id));
//						goodsDC.getGoods_ugcs().add(ugc);
//					}
//				}
				String[] img_ids = image_ids.split(",");
				goodsDC.getGoodsDC_photos().clear();
				for (String img_id : img_ids) {
					if (!img_id.equals("")) {
						Accessory img = this.accessoryService.getObjById(Long
								.parseLong(img_id));
						goodsDC.getGoodsDC_photos().add(img);
					}
				}
				if (goods_brand_id != null && !goods_brand_id.equals("")) {
					GoodsBrand goods_brand = this.goodsBrandService
							.getObjById(Long.parseLong(goods_brand_id));
					goodsDC.setGoods_brand(goods_brand);
				}
				goodsDC.getGoodsDC_specs().clear();
				String[] spec_ids = goods_spec_ids.split(",");
				for (String spec_id : spec_ids) {
					if (!spec_id.equals("")) {
						GoodsSpecProperty gsp = this.specPropertyService
								.getObjById(Long.parseLong(spec_id));
						goodsDC.getGoodsDC_specs().add(gsp);
					}
				}
				List<Map> maps = new ArrayList<Map>();
				String[] properties = goods_properties.split(";");
				for (String property : properties) {
					if (!property.equals("")) {
						String[] list = property.split(",");
						Map map = new HashMap();
						map.put("id", list[0]);
						map.put("val", list[1]);
						map.put("name", this.goodsTypePropertyService
								.getObjById(Long.parseLong(list[0])).getName());
						maps.add(map);
					}
				}
				goodsDC.setGoods_property(Json.toJson(maps, JsonFormat.compact()));
				maps.clear();
				if (CommUtil.null2Int(transport_type) == 0) {// 使用运费模板
					Transport trans = this.transportService.getObjById(CommUtil
							.null2Long(transport_id));
					goodsDC.setTransport(trans);
				}
				if (CommUtil.null2Int(transport_type) == 1) {// 使用固定运费
					goodsDC.setTransport(null);
				}
			
				if (id.equals("")) {
					this.goodsServiceDC.save(goodsDC);
					// 新增lucene索引无需创建索引
				} else {
					this.goodsServiceDC.update(goodsDC);//更新goodsdc商品库表
					goodsDC = this.goodsServiceDC.getObjById(Long.parseLong(id));
					//根据货号查goods表的对象
					Map<Object,Object> params=new HashMap<Object,Object>();
					params.put("goods_serial",partnum);
					List<Goods> goodsList=this.goodsService.query("select obj from Goods obj where obj.goods_serial=:goods_serial", params, -1,-1);
					for(int i=0;i<goodsList.size();i++){
						Goods goods=goodsList.get(i);
						if(goods != null){
						if(goodsDC.getGoodsdc_status()==1){//若是不可售状态，将商品状态置为不可售
						goods.setGoods_status(-4);//商品状态
						}else{
							//if(goodsdc_status==1){若当前商品状态为可售，判断之前的商品状态，若为不可售，则将商品状态统一改为1，否不做改变
								goods.setGoods_status(1);	
							//}
						}
						goods.setMail_trans_fee(goodsDC.getMail_trans_fee());// 平邮费用
						goods.setExpress_trans_fee(goodsDC.getExpress_trans_fee());//// 快递费用
						goods.setEms_trans_fee(goodsDC.getEms_trans_fee());//ems费用
						goods.setGoods_fee(goodsDC.getGoods_fee());//运费
						goods.setGoods_serial(goodsDC.getGoods_serial());//商品货号
						goods.setGoods_weight(goodsDC.getGoods_weight());//商品重量
						goods.setGoods_volume(goodsDC.getGoods_volume());//商品体积
						goods.setGoods_details(goodsDC.getGoods_details());//详细说明
						goods.setGoods_choice_type(goodsDC.getGoods_choice_type());// 0实体商品，1为虚拟商品
						goods.setTransport(goodsDC.getTransport());// 调用的运费模板信息
						goods.setGoods_name(goodsDC.getGoods_name());//名称
						goods.setAddTime(goodsDC.getAddTime());//更新时间
						goods.setGc(goodsDC.getGc()==null?null:goodsDC.getGc());// 商品对应的大分类
						goods.setGoods_main_photo(goodsDC.getGoods_main_photo()==null?null:goodsDC.getGoods_main_photo());//商品主图片
	
						List<Accessory> photosList = goodsDC.getGoodsDC_photos(); 
						if(null!=photosList && !photosList.isEmpty()){  
							List<Accessory> newphotosList = new ArrayList<Accessory>(photosList.size());  
							for(Accessory bs : photosList){  
							  newphotosList.add(bs);  
							} 
							goods.setGoods_photos(newphotosList);//商品图片
						}
						List<GoodsSpecProperty> specsList = goodsDC.getGoodsDC_specs(); 
						if(null!=specsList && !specsList.isEmpty()){  
							List<GoodsSpecProperty> newspecsList = new ArrayList<GoodsSpecProperty>(specsList.size());  
							for(GoodsSpecProperty specs : specsList){  
								newspecsList.add(specs);  
							} 
							goods.setGoods_specs(newspecsList);//商品对应的规格值
						}
						goods.setGoods_brand(goodsDC.getGoods_brand()==null?null:goodsDC.getGoods_brand());// 商品品牌
						goods.setGoods_property(goodsDC.getGoods_property()==null?null:goodsDC.getGoods_property());//商品属性
						goods.setTransport(goodsDC.getTransport()==null?null:goodsDC.getTransport());//运费
						}
						if(goods != null){
							goodsService.update(goods);//更新goods商品表
							// 更新lucene索引
							LuceneUtil.instance().ruLuceneIndex(goods,"u");
						}
					}
				}
				mv.addObject("obj", goodsDC);
				request.getSession(false).removeAttribute("goods_session");
			} else {
				mv = new JModelAndView("admin/blue/goods_referprice_finish.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(),0, request,
						response);
				mv.addObject("op_title", "参数错误");
				//mv.addObject("url", CommUtil.getURL(request)
						//+ "/admin/add_goods_first.htm");
			}
		}
		return mv;
	}
	
	//GOODSDC商品编辑
	@SecurityMapping(title = "平台商品编辑", value = "/admin/goods_DC_edit.htm*", rtype = "admin", rname = "平台商品管理", rcode = "admin_goods_dc", rgroup = "商品")
	@RequestMapping("/admin/goods_DC_edit.htm")
	public ModelAndView goodstype_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/add_goods_second.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsDC obj = this.goodsServiceDC.getObjById(Long.parseLong(id));
		if (obj !=null && !id.equals("")) {
//			User user = this.userService.getObjById(SecurityUserHolder
//					.getCurrentUser().getId());
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ File.separator
					+ "upload"
					+ File.separator
					+ "store"
					+ File.separator +"goods"+File.separator+"images";
			double img_remain_size =0;// user.getStore().getGrade().getSpaceSize()
					//- CommUtil.div(CommUtil.fileSize(new File(path)), 1024);
//			Map params = new HashMap();
//			params.put("user_id", user.getId());
//			params.put("display", true);
//			List<UserGoodsClass> ugcs = this.userGoodsClassService
//					.query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
//							params, -1, -1);
			AccessoryQueryObject aqo = new AccessoryQueryObject();
			aqo.setPageSize(8);
//			aqo.addQuery("obj.user.id", new SysMap("user_id", user.getId()),
//					"=");
			aqo.setOrderBy("addTime");
			aqo.setOrderType("desc");
			IPageList pList = this.accessoryService.list(aqo);
			String photo_url = CommUtil.getURL(request)
					+ "/seller/load_photo.htm";
//			List<GoodsBrand> gbs = this.goodsBrandService.query(
//					"select obj from GoodsBrand obj order by obj.sequence asc",
//					null, -1, -1);
//			mv.addObject("gbs", gbs);
			mv.addObject("photos", pList.getResult());
			mv.addObject(
					"gotoPageAjaxHTML",
					CommUtil.showPageAjaxHtml(photo_url, "",
							pList.getCurrentPage(), pList.getPages()));
//			mv.addObject("ugcs", ugcs);
			mv.addObject("img_remain_size", img_remain_size);
			mv.addObject("obj", obj);
			mv.addObject("type",request.getParameter("type"));//从url上获取type判断是查看还是编辑
			GoodsClass gc =null;
			if (request.getSession(false).getAttribute("goods_class_info") != null) {
				GoodsClass session_gc = (GoodsClass) request.getSession(false)
						.getAttribute("goods_class_info");
				gc = this.goodsClassService.getObjById(session_gc
						.getId());
				mv.addObject("goods_class_info",
						this.storeTools.generic_goods_class_info(gc));
				mv.addObject("goods_class", gc);
				request.getSession(false).removeAttribute("goods_class_info");
			} else {
				if (obj.getGc() != null) {
					gc = obj.getGc();
					mv.addObject("goods_class_info", this.storeTools
							.generic_goods_class_info(gc));
					mv.addObject("goods_class", gc);			
				}
			}
			List<GoodsBrand> gbs = gc.getGoodsType()==null?null:gc.getGoodsType().getGbs();
			mv.addObject("gbs", gbs);
			String goods_session = CommUtil.randomString(32);
			mv.addObject("goods_session", goods_session);
			request.getSession(false).setAttribute("goods_session",
					goods_session);
			mv.addObject("imageSuffix", this.storeViewTools
					.genericImageSuffix(this.configService.getSysConfig()
							.getImageSuffix()));
		} else {
			mv = new JModelAndView("admin/blue/goods_referprice_finish.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(),0, request,
					response);
			mv.addObject("op_title", "您没有该商品信息！");
			//mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}
	
	
	
	/**
	 * 过滤商品名称中的html代码
	 * 
	 * @param inputString
	 * @return
	 */
	private String clearContent(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		java.util.regex.Pattern p_html1;
		java.util.regex.Matcher m_html1;

		try {
			String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script>
			String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[//s//S]*?<///style>
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			String regEx_html1 = "<[^>]+";
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll(""); // 过滤html标签

			textStr = htmlStr;
		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}
		return textStr;// 返回文本字符串
	}
	
	
	/**
	 * 指导价格页面
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "指导价格", value = "/admin/goods_referprice.htm*", rtype = "admin", rname = "平台商品管理", rcode = "admin_goods_dc", rgroup = "商品")
	@RequestMapping("/admin/goods_referprice.htm")
	public ModelAndView goods_referprice(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_referprice.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		List<Branch> branch = this.branchService.query(
				"select obj from Branch obj",
				null, -1, -1);
		String query ="select obj from ReferPrice obj where obj.goods_id=:goods_id and obj.branch_id=:branch_id";
		Map params = new HashMap();
		params.put("goods_id", Integer.parseInt(id));
		List<ReferPrice> referList =null;
		List<Brpr> brprList = new ArrayList<Brpr>();
		for(int i= 0;i<branch.size();i++){
			Brpr brpr = new Brpr();//将分公司名称 分公司id 指导价 经销商最低价放入java类
			params.put("branch_id",(branch.get(i).getId()).intValue());
			referList = this.referPriceService.query(query, params, -1, -1);
			brpr.setBrId(branch.get(i).getId().toString()==null?"":branch.get(i).getId().toString());//分公司ID
			brpr.setCompname(branch.get(i).getName().toString()==null?"":branch.get(i).getName().toString());//分公司名称
			brpr.setLowprice(getSomeArea(branch.get(i).getId().toString(),id)==null?"":getSomeArea(branch.get(i).getId().toString(),id));//经销商最低价
			if(referList.size()>0){
				System.out.println("-----"+referList.get(0).getReferprice());
				brpr.setReprice(referList.get(0).getReferprice()==null?"":referList.get(0).getReferprice().toString());//指导价	
			}else{
				brpr.setReprice("");//指导价
			}
			brprList.add(brpr);  
		}
		mv.addObject("branch",branch);
		mv.addObject("id",id);
//		ReferencePrice rePrice = this.referPriceService.getObjById(Long.parseLong(id));
		mv.addObject("brprList",brprList);
		return mv;
	}
	
	
	/**
	 * 获取经销商最低价
	 * @param request	  
	 */
	@SuppressWarnings("unchecked")
	//@RequestMapping(value="/admin/goods_getprice.htm*")
	private String getSomeArea(String goods_company_id,String id){
		String query = "select GOODS_PRICE from LAKECLOUD_GOODS where GOODS_STORE_ID in (SELECT store_id FROM LAKECLOUD_USER where BRANCH_ID="+goods_company_id+") and GOODSDC_ID="+id+" order by GOODS_PRICE asc";
		List<BigDecimal> priceList = this.goodsServiceDC.getPrice(query,null,-1,-1);	
		BigDecimal price  =null;
		if(priceList.size()>0){
		for(int i =0;i<priceList.size();i++){
			price  = priceList.get(0);
		}
		return price.toString();	
		}else{
		return null;
		}
	}
	
	//保存指导价
	@SuppressWarnings("unchecked")
	//@SecurityMapping(title = "商品列表", value = "/admin/goods_referprice_finish.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_referprice_finish.htm")
	public void goods_referprice_finish(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_company,
			String goods_referprice_low){

//		ModelAndView mv = new JModelAndView("admin/blue/goods_referprice_finish.html",
//				configService.getSysConfig(),
//				this.userConfigService.getUserConfig(),0, request,
//				response);
		//获取当前操作的对象
		JSONObject obj = new JSONObject();	
		if(!"".equals(id) || !"".equals(goods_company)){
		String query ="select obj from ReferPrice obj where obj.goods_id=:goods_id and obj.branch_id=:branch_id";
		Map params = new HashMap();
		params.put("goods_id", Integer.parseInt(id));
		params.put("branch_id", Integer.parseInt(goods_company));
		List<ReferPrice> referList = this.referPriceService.query(query, params, -1, -1);
		if(referList.size()>0){
			//有，update
			ReferPrice rp = referList.get(0);
			rp.setReferprice(goods_referprice_low==""?BigDecimal.ZERO:new BigDecimal(goods_referprice_low));
			rp.setAddTime(new Date());
			this.referPriceService.update(rp);
			//mv.addObject("op_title", "更新成功！");
			obj.put("message","更新成功");  	
		}else{
			//无，insert
			ReferPrice rp = new ReferPrice();
			rp.setReferprice(goods_referprice_low==""?BigDecimal.ZERO:new BigDecimal(goods_referprice_low));
			rp.setAddTime(new Date());
			rp.setBranch_id(Integer.valueOf(goods_company).intValue());
			rp.setGoods_id(Integer.valueOf(id).intValue());
			this.referPriceService.save(rp);
			//mv.addObject("op_title", "保存成功！");
			obj.put("message","保存成功");  
			}
		}else{
			obj.put("message","操作失败");
		}
		//return mv;
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(obj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	//类目
	/**
	 * AJAX加载商品分类数据
	 * 
	 * @param request
	 * @param response
	 * @param pid
	 *            上级分类Id
	 * @param session
	 *            是否加载到session中
	 */
	@SecurityMapping(title = "加载商品分类", value = "/admin/load_goods_class.htm*", rtype = "admin", rname = "商品发布", rcode = "admin_goods_add", rgroup = "商品")
	@RequestMapping("/admin/load_goods_class.htm")
	public void load_goods_class(HttpServletRequest request,
			HttpServletResponse response, String pid, String session) {
		GoodsClass obj = this.goodsClassService.getObjById(CommUtil
				.null2Long(pid));
		Map<String, Object> mapreturn = new HashMap<String, Object>();
		List<Map> list = new ArrayList<Map>();
		String isLast = "true";
		String isThird = "false";
		if (obj != null) {
			List<GoodsClass> a = obj.getChilds();
			if (obj.getChilds() == null || obj.getChilds().size() == 0)
				isLast = "false";
			if (obj.getLevel()==2)
				isThird = "true";
			for (GoodsClass gc : obj.getChilds()) {
				Map map = new HashMap();
				map.put("id", gc.getId());
				map.put("className", gc.getClassName());
				list.add(map);
			}
			if (CommUtil.null2Boolean(session)) {
				request.getSession(false).setAttribute("goods_class_info", obj);
			}
		}
		mapreturn.put("list", list);
		mapreturn.put("isLast", isLast);
		mapreturn.put("isThird", isThird);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(mapreturn));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SecurityMapping(title = "添加用户常用商品分类", value = "/admin/load_goods_class.htm*", rtype = "admin", rname = "商品发布", rcode = "admin_goods_add", rgroup = "商品")
	@RequestMapping("/admin/add_goods_class_staple.htm")
	public void add_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response) {
		String ret = "error";
		if (request.getSession(false).getAttribute("goods_class_info") != null) {
			GoodsClass gc = (GoodsClass) request.getSession(false)
					.getAttribute("goods_class_info");
			gc = goodsClassService.getObjById(gc.getId());
			Map<String, Object> params = new HashMap<String, Object>();
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			params.put("store_id", user.getStore().getId());
			params.put("gc_id", gc.getId());
			List<GoodsClassStaple> gcs = this.goodsclassstapleService
					.query("select obj from GoodsClassStaple obj where obj.store.id=:store_id and obj.gc.id=:gc_id",
							params, -1, -1);
			if (gcs.size() == 0) {
				GoodsClassStaple staple = new GoodsClassStaple();
				staple.setAddTime(new Date());
				staple.setGc(gc);
				String name = this.generic_goods_class_info(gc);
				staple.setName(name.substring(0, name.length() - 1));
				staple.setStore(user.getStore());
				boolean flag = this.goodsclassstapleService.save(staple);
				if (flag) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("name", staple.getName());
					map.put("id", staple.getId());
					ret = Json.toJson(map);
				}
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
			e.printStackTrace();
		}
	}
	

	@SecurityMapping(title = "删除用户常用商品分类", value = "/admin/del_goods_class_staple.htm*", rtype = "admin", rname = "商品发布", rcode = "admin_goods_add", rgroup = "商品")
	@RequestMapping("/admin/del_goods_class_staple.htm")
	public void del_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = this.goodsclassstapleService.delete(Long.parseLong(id));
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
	
	@SecurityMapping(title = "根据用户常用商品分类加载分类信息", value = "/admin/del_goods_class_staple.htm*", rtype = "admin", rname = "商品发布", rcode = "admin_goods_add", rgroup = "商品")
	@RequestMapping("/admin/load_goods_class_staple.htm")
	public void load_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response, String id, String name) throws UnsupportedEncodingException {
		name=URLDecoder.decode(name,"utf-8");
		GoodsClass obj = null;
		if (id != null && !id.equals(""))
			obj = this.goodsclassstapleService.getObjById(Long.parseLong(id))
					.getGc();
		if (name != null && !name.equals(""))
			obj = this.goodsClassService.getObjByProperty("className", name);
		List<List<Map<String, Object>>> list = new ArrayList<List<Map<String, Object>>>();
		if (obj != null) {
			// 该版本要求三级分类才能添加到常用分类
			request.getSession(false).setAttribute("goods_class_info", obj);
			Map<String, Object> params = new HashMap<String, Object>();
			List<Map<String, Object>> second_list = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> third_list = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> other_list = new ArrayList<Map<String, Object>>();
			if (obj.getLevel() == 2) {
				params.put("pid", obj.getParent().getParent().getId());
				List<GoodsClass> second_gcs = this.goodsClassService
						.query("select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc",
								params, -1, -1);
				for (GoodsClass gc : second_gcs) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", gc.getId());
					map.put("className", gc.getClassName());
					second_list.add(map);
				}
				params.clear();
				params.put("pid", obj.getParent().getId());
				List<GoodsClass> third_gcs = this.goodsClassService
						.query("select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc",
								params, -1, -1);
				for (GoodsClass gc : third_gcs) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", gc.getId());
					map.put("className", gc.getClassName());
					third_list.add(map);
				}
			}

			if (obj.getLevel() == 1) {
				params.clear();
				params.put("pid", obj.getParent().getId());
				List<GoodsClass> third_gcs = this.goodsClassService
						.query("select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc",
								params, -1, -1);
				for (GoodsClass gc : third_gcs) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", gc.getId());
					map.put("className", gc.getClassName());
					second_list.add(map);
				}
			}

			Map<String, Object> map = new HashMap<String, Object>();
			String staple_info = this.generic_goods_class_info(obj);
			map.put("staple_info",
					staple_info.substring(0, staple_info.length() - 1));
			other_list.add(map);

			list.add(second_list);
			list.add(third_list);
			list.add(other_list);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(list, JsonFormat.compact()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 中化后台管理员上传图片
	 * **/
	@RequestMapping("/admin/swf_upload.htm")
	public void swf_upload(HttpServletRequest request,
			HttpServletResponse response, String user_id, String album_id) {
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		String path = this.storeTools.createUserFolder(request,

		this.configService.getSysConfig(), user.getStore());
		String url = this.storeTools.createUserFolderURL(
				this.configService.getSysConfig(), user.getStore());
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest
				.getFile("imgFile");
		double fileSize = Double.valueOf(file.getSize());// 返回文件大小，单位为byte
		fileSize = fileSize / 1048576;
		double csize = CommUtil.fileSize(new File(path));
		double remainSpace = 0;
		if (user.getStore()!=null&&user.getStore().getGrade().getSpaceSize() != 0) {
			remainSpace = (user.getStore().getGrade().getSpaceSize() * 1024 - csize) * 1024;
		} else {
			remainSpace = 10000000;
		}
		Map json_map = new HashMap();
		if (remainSpace > fileSize) {
			try {
				Map map = CommUtil.saveFileToServer(request, "imgFile", path,
						null, null);
				Map params = new HashMap();
				String hql="select obj from WaterMark obj ";
				if(user.getStore()!=null){
					params.put("store_id", user.getStore().getId());
					hql+=" where obj.store.id=:store_id";
					List<WaterMark> wms = this.waterMarkService.query(hql,params, -1, -1);//店铺水印处理
					if (wms.size() > 0) {
						WaterMark mark = wms.get(0);
						if (mark.isWm_image_open()) {
							String pressImg = request.getSession()
									.getServletContext().getRealPath("")
									+ File.separator
									+ mark.getWm_image().getPath()
									+ File.separator + mark.getWm_image().getName();
							String targetImg = path + File.separator
									+ map.get("fileName");
							int pos = mark.getWm_image_pos();
							float alpha = mark.getWm_image_alpha();
							CommUtil.waterMarkWithImage(pressImg, targetImg, pos,
									alpha);
						}
						if (mark.isWm_text_open()) {
							String targetImg = path + File.separator
									+ map.get("fileName");
							int pos = mark.getWm_text_pos();
							String text = mark.getWm_text();
							String markContentColor = mark.getWm_text_color();
							CommUtil.waterMarkWithText(targetImg, targetImg, text,
									markContentColor,
									new Font(mark.getWm_text_font(), Font.BOLD,
											mark.getWm_text_font_size()), pos, 100f);
						}
					}
				}
				
				Accessory image = new Accessory();
				image.setAddTime(new Date());
				image.setExt((String) map.get("mime"));
				image.setPath(url);
				image.setWidth(CommUtil.null2Int(map.get("width")));
				image.setHeight(CommUtil.null2Int(map.get("height")));
				image.setName(CommUtil.null2String(map.get("fileName")));
				image.setUser(user);
				Album album = null;
				if (album_id != null && !album_id.equals("")) {
					album = this.albumService.getObjById(CommUtil
							.null2Long(album_id));
				} else {
					album = this.albumService.getDefaultAlbum(CommUtil
							.null2Long(user_id));
					if (album == null) {
						album = new Album();
						album.setAddTime(new Date());
						album.setAlbum_name("默认相册");
						album.setAlbum_sequence(-10000);
						album.setAlbum_default(true);
						this.albumService.save(album);
					}
				}
				image.setAlbum(album);
				this.accessoryService.save(image);
				System.out.println(CommUtil.getURL(request) + "/" + url + "/"+ image.getName());
				json_map.put("url", CommUtil.getURL(request) + "/" + url + "/"
						+ image.getName());
				json_map.put("id", image.getId());
				json_map.put("remainSpace", remainSpace == 10000 ? 0
						: remainSpace);
				// 同步生成小图片
				String ext = image.getExt().indexOf(".") < 0 ? "."
						+ image.getExt() : image.getExt();
				String source = request.getSession().getServletContext()
						.getRealPath("/")+ File.separator
						+ image.getPath() + File.separator + image.getName();
				String target = source + "_small" + ext;
				CommUtil.createSmall(source, target, this.configService
						.getSysConfig().getSmallWidth(), this.configService
						.getSysConfig().getSmallHeight());
				// 同步生成app中等图片
				String midext = image.getExt().indexOf(".") < 0 ? "."
						+ image.getExt() : image.getExt();
				String midtarget = source + "_middle" + ext;
				CommUtil.createSmall(source, midtarget, this.configService
						.getSysConfig().getMiddleWidth(), this.configService
						.getSysConfig().getMiddleHeight());
				// 同步pc中图片
				String pcmidext = image.getExt().indexOf(".") < 0 ? "."
						+ image.getExt() : image.getExt();
				String pcmidtarget = source + "_pcmiddle" + ext;
				CommUtil.createSmall(source, pcmidtarget, this.configService
						.getSysConfig().getPcmiddleWidth(), this.configService
						.getSysConfig().getPcmiddleHeight());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			json_map.put("url", "");
			json_map.put("id", "");
			json_map.put("remainSpace", 0);

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
	
	//商品货号唯一性验证
	@SuppressWarnings("unchecked")
	@RequestMapping("/admin/goods_check_serial.htm")
	public void goods_check_serial(HttpServletRequest request,
			HttpServletResponse response,String goodsNum,String op_type,String id){
		JSONObject obj = new JSONObject();	
		String query ="select obj from GoodsDC obj where obj.goods_serial=:goodsNum";
		Map params = new HashMap();
		params.put("goodsNum", goodsNum);
		if(id != null && !"".equals(id)){
				query+=" and obj.id != :id";
				params.put("id",Long.parseLong(id));
		}
		List<GoodsDC> goodsDcList = this.goodsServiceDC.query(query,params, -1, -1);
		if(op_type.equals("show")){
			obj.put("message","0"); 
		}else {
			if(goodsDcList.size()>0){
				obj.put("message","1");  	
			}else{
				
				obj.put("message","0");  	
			}
		}
	
	
		//return mv;
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(obj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	//查询放入仓库操作是否可做
	@SuppressWarnings("unchecked")
	@RequestMapping("/admin/goodsdc_status_change.htm")
	public void goodsdc_status_change(HttpServletRequest request,
			HttpServletResponse response, String id){

		JSONObject obj = new JSONObject();	
		if(!"".equals(id)){
		String query ="select obj from Goods obj where obj.goodsDc_id=:gooddc_id and obj.goods_status=:goods_status";
		Map params = new HashMap();
		params.put("gooddc_id", Long.parseLong(id));
		params.put("goods_status",0);
		List<Goods> goodsList = this.goodsService.query(query, params, -1, -1);
		if(goodsList.size()>0){
			
			obj.put("message","0");  	//0则不可直接放入仓库
		}else{
			
			obj.put("message","1"); 
		 }
		}else{
			obj.put("message","1"); //若没有id，则是新增操作，可直接设为不可销售
		}
		//return mv;
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(obj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * 管理员下架商品后发送短信通知
	 * @param request
	 * @param audit
	 * @param mark
	 * @throws Exception
	 */
	private void send_undercarriage_sms(HttpServletRequest request, Goods goods, String mark) throws Exception {
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
				context.put("goods",goods);
				StringWriter writer = new StringWriter();
				blank.merge(context, writer);
				System.out.println(writer.toString());
				String content = writer.toString();
				try{
					sendmessage.sendHttpPost(goods.getGoods_store().getUser().getTelephone(), content);
				}catch (Exception e) {
					// TODO: handle exception
				}
			} 
		
		}
	}
	
	@SecurityMapping(title = "商品图片删除", value = "/admin/goods_image_del.htm*", rtype = "admin", rname = "商品发布", rcode = "admin_goods_add", rgroup = "商品")
	@RequestMapping("/admin/goods_image_del.htm")
	public void goods_image_del(HttpServletRequest request,
			HttpServletResponse response, String image_id) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		String path = this.storeTools.createUserFolder(request,
				this.configService.getSysConfig(), user.getStore());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			Map map = new HashMap();
			Accessory img = this.accessoryService.getObjById(CommUtil
					.null2Long(image_id));
			for (Goods goods : img.getGoods_main_list()) {
				goods.setGoods_main_photo(null);
				this.goodsService.update(goods);
			}
			for (Goods goods1 : img.getGoods_list()) {
				goods1.getGoods_photos().remove(img);
				this.goodsService.update(goods1);
			}
			for (GoodsDC goodsDC : img.getGoodsDC_main_list()) {
				goodsDC.setGoods_main_photo(null);
				this.goodsServiceDC.update(goodsDC);
			}
			for (GoodsDC goodsDC1 : img.getGoodsDC_list()) {
				goodsDC1.getGoodsDC_photos().remove(img);
				this.goodsServiceDC.update(goodsDC1);
			}
			boolean ret = this.accessoryService.delete(img.getId());
			if (ret) {
				CommUtil.del_acc(request, img);
			}
			double csize = CommUtil.fileSize(new File(path));
			double remainSpace = 10000.0;
//			if (user.getStore().getGrade().getSpaceSize() != 0) {
//				remainSpace = CommUtil.div(user.getStore().getGrade()
//						.getSpaceSize()
//						* 1024 - csize, 1024);
//			}
			map.put("result", ret);
			map.put("remainSpace", remainSpace);
			writer = response.getWriter();
			writer.print(Json.toJson(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/admin/goods_img_album.htm")
	public ModelAndView goods_img_album(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String type) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/" + type
				+ ".html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AccessoryQueryObject aqo = new AccessoryQueryObject(currentPage, mv,
				"addTime", "desc");
		aqo.setPageSize(16);
		aqo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		aqo.setOrderBy("addTime");
		aqo.setOrderType("desc");
		IPageList pList = this.accessoryService.list(aqo);
		String photo_url = CommUtil.getURL(request)
				+ "/seller/goods_img_album.htm";
		mv.addObject("photos", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(photo_url,
				"", pList.getCurrentPage(), pList.getPages()));

		return mv;
	}
	
}