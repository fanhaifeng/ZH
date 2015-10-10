package com.lakecloud.manage.seller.action;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Accessory;
import com.lakecloud.foundation.domain.Album;
import com.lakecloud.foundation.domain.Evaluate;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsBrand;
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.GoodsClass;
import com.lakecloud.foundation.domain.GoodsClassStaple;
import com.lakecloud.foundation.domain.GoodsDC;
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.GoodsSpecification;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Payment;
import com.lakecloud.foundation.domain.ReferPrice;
import com.lakecloud.foundation.domain.Report;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreCart;
import com.lakecloud.foundation.domain.StoreGrade;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.UserGoodsClass;
import com.lakecloud.foundation.domain.WaterMark;
import com.lakecloud.foundation.domain.query.AccessoryQueryObject;
import com.lakecloud.foundation.domain.query.GoodsQueryObject;
import com.lakecloud.foundation.domain.query.ReportQueryObject;
import com.lakecloud.foundation.domain.query.TransportQueryObject;
import com.lakecloud.foundation.service.IAccessoryService;
import com.lakecloud.foundation.service.IAlbumService;
import com.lakecloud.foundation.service.IEvaluateService;
import com.lakecloud.foundation.service.IGoodsBrandService;
import com.lakecloud.foundation.service.IGoodsCartService;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.IGoodsClassStapleService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGoodsServiceDC;
import com.lakecloud.foundation.service.IGoodsSpecPropertyService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IPaymentService;
import com.lakecloud.foundation.service.IReferPriceService;
import com.lakecloud.foundation.service.IReportService;
import com.lakecloud.foundation.service.IStoreCartService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.ITransportService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserGoodsClassService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.foundation.service.IWaterMarkService;
import com.lakecloud.lucene.LuceneUtil;
import com.lakecloud.manage.admin.tools.StoreTools;
import com.lakecloud.manage.seller.Tools.TransportTools;
import com.lakecloud.view.web.tools.GoodsViewTools;
import com.lakecloud.view.web.tools.StoreViewTools;

/**
 * @info 卖家中心商品管理控制器
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Controller
@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
public class GoodsSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsClassStapleService goodsclassstapleService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsSpecPropertyService specPropertyService;
	@Autowired
	private IWaterMarkService waterMarkService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IReportService reportService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private ITransportService transportService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IGoodsServiceDC goodsServiceDC;
	@Autowired
	private IReferPriceService referPriceService;
	@Autowired
	private IStoreCartService storeCartService;

	@SecurityMapping(title = "发布商品第一步", value = "/seller/add_goods_first.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/add_goods_first.htm")
	public ModelAndView add_goods_first(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		List<Payment> payments = new ArrayList<Payment>();
		Map params = new HashMap();
		if (this.configService.getSysConfig().getConfig_payment_type() == 1) {
			params.put("type", "admin");
			params.put("install", true);
			payments = this.paymentService
					.query("select obj from Payment obj where obj.type=:type and obj.install=:install",
							params, -1, -1);
		} else {
			params.put("store_id", user.getStore().getId());
			params.put("install", true);
			payments = this.paymentService
					.query("select obj from Payment obj where obj.store.id=:store_id and obj.install=:install",
							params, -1, -1);
		}
		if (payments.size() == 0) {
			mv.addObject("op_title", "请至少开通一种支付方式");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/payment.htm");
			return mv;
		}
		request.getSession(false).removeAttribute("goods_class_info");
		int store_status = (user.getStore() == null ? 0 : user.getStore()
				.getStore_status());
		if (store_status == 2) {
			StoreGrade grade = user.getStore().getGrade();
			int user_goods_count = user.getStore().getGoods_list().size();
			if (grade.getGoodsCount() == 0
					|| user_goods_count < grade.getGoodsCount()) {
				List<GoodsClass> gcs = this.goodsClassService
						.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
								null, -1, -1);
				mv = new JModelAndView(
						"user/default/usercenter/add_goods_first.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				params.clear();
				params.put("store_id", user.getStore().getId());
				List<GoodsClassStaple> staples = this.goodsclassstapleService
						.query("select obj from GoodsClassStaple obj where obj.store.id=:store_id order by obj.addTime desc",
								params, -1, -1);
				mv.addObject("staples", staples);
				mv.addObject("gcs", gcs);
				mv.addObject("id", CommUtil.null2String(id));
			} else {
				mv.addObject("op_title", "您的店铺等级只允许上传" + grade.getGoodsCount()
						+ "件商品!");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/store_grade.htm");
			}
		}

		if (store_status == 0) {
			mv.addObject("op_title", "您尚未开通店铺，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		if (store_status == 1) {
			mv.addObject("op_title", "您的店铺在审核中，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		if (store_status == 3) {
			mv.addObject("op_title", "您的店铺已被关闭，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "商品运费模板分页显示", value = "/seller/goods_transport.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_transport.htm")
	public ModelAndView goods_transport(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String ajax, String transport_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/goods_transport.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2Boolean(ajax)) {
			mv = new JModelAndView(
					"user/default/usercenter/goods_transport_list.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		TransportQueryObject qo = new TransportQueryObject(currentPage, mv,
				orderBy, orderType);
		if (transport_id != null && !transport_id.equals("")) {
			mv.addObject("showfenye", "false");
			qo.addQuery("obj.id", new SysMap("id", Integer
					.valueOf(transport_id).longValue()), "=");
		} else {
			Store store = this.userService.getObjById(
					SecurityUserHolder.getCurrentUser().getId()).getStore();
			qo.addQuery("obj.store.id", new SysMap("store_id", store.getId()),
					"=");
			mv.addObject("showfenye", "true");
		}
		qo.setPageSize(1);
		IPageList pList = this.transportService.list(qo);
		CommUtil.saveIPageList2ModelAndView(
				url + "/seller/goods_transport.htm", "", params, pList, mv);
		mv.addObject("transportTools", transportTools);
		return mv;
	}

	/**
	 * 加载商品信息
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/seller/goodsdc_load.htm")
	public void goodsdcLoad(HttpServletRequest request,
			HttpServletResponse response) {
		// 获取gdc
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		Long id = Integer.valueOf(request.getParameter("goodsDc_id"))
				.longValue();
		// 默认图片路径（一张不存在的图用以触发展示默认图）
		String default_image_url = url + "/"
				+ this.configService.getSysConfig().getGoodsImage().getPath()
				+ "/"
				+ this.configService.getSysConfig().getGoodsImage().getName()
				+ ".jpg";
		GoodsDC gdc = this.goodsServiceDC.getObjById(id);
		Map<String, Object> goods = new HashMap<String, Object>();
		goods.put("id", gdc.getId());
		goods.put("goods_name", gdc.getGoods_name());
		goods.put("goods_choice_type", gdc.getGoods_choice_type() == 0 ? "实体商品"
				: "虚拟商品");
		goods.put("goods_serial", gdc.getGoods_serial());
		// 商品图片处理
		if (gdc.getGoods_main_photo() != null) {
			goods.put("goods_main_photo", url + "/"
					+ gdc.getGoods_main_photo().getPath() + "/"
					+ gdc.getGoods_main_photo().getName());
			goods.put("goods_image_1", url + "/"
					+ gdc.getGoods_main_photo().getPath() + "/"
					+ gdc.getGoods_main_photo().getName() + "_small."
					+ gdc.getGoods_main_photo().getExt());
		} else {
			goods.put("goods_main_photo", default_image_url);
			goods.put("goods_image_1", default_image_url);
		}
		for (int i = 0; i < 4; i++) {
			try {
				goods.put("goods_image_" + (i + 2), url + "/"
						+ gdc.getGoodsDC_photos().get(i).getPath() + "/"
						+ gdc.getGoodsDC_photos().get(i).getName() + "_small."
						+ gdc.getGoodsDC_photos().get(i).getExt());
			} catch (Exception e) {
				goods.put("goods_image_" + (i + 2), default_image_url);
			}
		}
		goods.put("goods_weight", gdc.getGoods_weight());
		goods.put("goods_volume", gdc.getGoods_volume());
		goods.put("goods_transfee", gdc.getGoods_transfee() == 0 ? "买家承担运费"
				: "卖家承担运费");
		// 运费显示
		String transfeeHtml = "";
		if (gdc.getGoods_transfee() == 0) {
			// 判断是否使用运费模板 0:不使用运费模板1：使用运费模板
			if (gdc.getTransport() == null) {
				transfeeHtml = getTrans_fee(gdc.getMail_trans_fee(),
						gdc.getExpress_trans_fee(), gdc.getEms_trans_fee());
				goods.put("transfeeHtml", transfeeHtml);
				goods.put("transport", "");
			} else {
				goods.put("transport", gdc.getTransport().getId());
			}
		}
		try {
			goods.put("goods_brand", gdc.getGoods_brand().getName());
		} catch (Exception e) {
		}
		// 商品类型属性
		JSONArray array = (JSONArray) JSONValue.parse(gdc.getGoods_property());
		StringBuffer html = new StringBuffer();
		for (Object object : array) {
			HashMap<String, String> map = (HashMap<String, String>) object;
			html.append("<tr class='loadRemove'><td align='right' valign='top'>"
					+ map.get("name")
					+ "：</td><td class='sptable'><span class='tabtxt1 size2'><input value='"
					+ map.get("val")
					+ "' disabled='disabled' /> </span></td></tr>");
		}
		goods.put("goods_property", html.toString());
		goods.put("goods_details", gdc.getGoods_details());
		goods.put("seo_description", gdc.getSeo_description());
		goods.put("seo_keywords", gdc.getSeo_keywords());
		// 规格html
		String htmlSpec = getGoodsdcSpec(gdc, null, url);
		goods.put("specHtml", htmlSpec);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(goods));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 商品价格限定
	@RequestMapping("/seller/goods_promotion_check.htm")
	public void goods_promotion_check(HttpServletRequest request,
			HttpServletResponse response, String value, String goodsdc_id,
			String goods_id) {
		Long goods_dc_id = 0l;
		Object result = "";
		goods_dc_id = goods_id.equals("") ? 0 : this.goodsService.getObjById(
				CommUtil.null2Long(goods_id)).getGoodsDc_id();
		goods_dc_id = goods_dc_id.equals("") ? goods_dc_id
				: this.goodsServiceDC.getObjById(CommUtil.null2Long(goodsdc_id)).getId();
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Long branch_id = user.getBranch().getId();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("goodsid", goods_dc_id.intValue());
		params.put("branchid", branch_id.intValue());
		List<ReferPrice> list = this.referPriceService
				.query("select obj from ReferPrice obj where obj.goods_id=:goodsid and obj.branch_id=:branchid",
						params, 0, 1);
		if (list.size() == 0)
			result = "true";
		else {
			if (list.get(0).getReferprice().compareTo(new BigDecimal(value)) == 1) {
				result = list.get(0).getReferprice().intValue();
			} else {
				result = "true";
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(result));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}/*
	  // 商品申请
		@RequestMapping("/seller/goods_dc_apply.htm")
		public ModelAndView goods_dc_apply(HttpServletRequest request,
				HttpServletResponse response) {
			ModelAndView mv = new JModelAndView("error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			int store_status = (user.getStore() == null ? 0 : user.getStore()
					.getStore_status());
			if (store_status == 2) {
				mv = new JModelAndView(
						"user/default/usercenter/goods_dc_apply.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
			}
			if (store_status == 0) {
				mv.addObject("op_title", "您尚未开通店铺，不能发布商品");
				mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
			}
			if (store_status == 1) {
				mv.addObject("op_title", "您的店铺在审核中，不能发布商品");
				mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
			}
			if (store_status == 3) {
				mv.addObject("op_title", "您的店铺已被关闭，不能发布商品");
				mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
			}
			return mv;
		}
*/
	private String getTrans_fee(BigDecimal mail, BigDecimal express,
			BigDecimal ems) {
		StringBuffer sb = new StringBuffer();
		sb.append("<td class='user_table'><div id='buyer_transport_info' style='width:80%; background:#F8F8F8; border:1px solid #CCC; margin-top:10px;padding-left:20px;'>"
				+ "<ul style='line-height:30px;'><li><label><input type='radio'name='transport_type' id='transport_type' value='1' /> 固定运费:</label>平邮 <input name='mail_trans_fee' type='text'"
				+ "id='mail_trans_fee'value='"
				+ mail
				+ "'size='10'/>元,快递 <input name='express_trans_fee' type='text'id='express_trans_fee' "
				+ "value='"
				+ express
				+ "'size='10'/> 元,EMS <input name='ems_trans_fee' type='text'id='ems_trans_fee'value='"
				+ ems + "'" + "size='10'/>元</li></ul></div></td>");

		return sb.toString();
	}

	private String getGoodsdcSpec(GoodsDC gdc, Goods goods, String url) {
		List<GoodsSpecProperty> specs = new ArrayList<GoodsSpecProperty>();
		if (goods != null) {
			specs = goods.getGoods_specs();
		}
		if (gdc != null) {
			specs = gdc.getGoodsDC_specs();
		}
		StringBuffer htmlSpec = new StringBuffer();
		Map<Long, List<GoodsSpecProperty>> map = new HashMap<Long, List<GoodsSpecProperty>>();
		for (GoodsSpecProperty goodsSpecProperty : specs) {
			Long specId = goodsSpecProperty.getSpec().getId();
			if (!map.containsKey(specId)) {
				List<GoodsSpecProperty> list = new ArrayList<GoodsSpecProperty>();
				list.add(goodsSpecProperty);
				map.put(specId, list);
			} else {
				map.get(specId).add(goodsSpecProperty);
			}
		}
		for (Long long1 : map.keySet()) {
			List<GoodsSpecProperty> list = map.get(long1);
			htmlSpec.append("<tr class='loadRemove' id='gs_" + long1
					+ "' gs_name='" + list.get(0).getSpec().getName()
					+ "'><td align='right' valign='top'>"
					+ list.get(0).getSpec().getName()
					+ ":</td><td class='sptable' ><ul class='color_chose'>");
			for (GoodsSpecProperty goodsSpecProperty : list) {
				htmlSpec.append("<li><span class='cc_sp1'></span> <label for='spec_"
						+ goodsSpecProperty.getId() + "'>");
				if (goodsSpecProperty.getSpec()!=null&&goodsSpecProperty.getSpec().getType()!=null&&goodsSpecProperty.getSpec().getType().equals("img")) {
					htmlSpec.append("<span class='cc_sp2'><img src='" + url
							+ "/" + goodsSpecProperty.getSpecImage().getPath()
							+ "/" + goodsSpecProperty.getSpecImage().getName()
							+ "' width='16' height='16' /> </span>");
				}
				htmlSpec.append("<span class='cc_sp3'>"
						+ goodsSpecProperty.getValue() + "</span></label></li>");
			}
			htmlSpec.append("</ul></td></tr>");
		}
		return htmlSpec.toString();
	}

	/**
	 * private String getGoodsdcSpec(GoodsDC gdc, Goods goods, String url) {
	 * Map<Long, Object> goodsSpec = new HashMap<Long, Object>(); if (goods !=
	 * null) { List<GoodsSpecProperty> spec = goods.getGoods_specs(); for
	 * (GoodsSpecProperty goodsSpecProperty : spec) {
	 * goodsSpec.put(goodsSpecProperty.getId(), goodsSpecProperty); } }
	 * StringBuffer htmlSpec = new StringBuffer(); List<GoodsSpecProperty> specs
	 * = gdc.getGoodsDC_specs(); Map<Long, List<GoodsSpecProperty>> map = new
	 * HashMap<Long, List<GoodsSpecProperty>>(); for (GoodsSpecProperty
	 * goodsSpecProperty : specs) { Long specId =
	 * goodsSpecProperty.getSpec().getId(); if (!map.containsKey(specId)) {
	 * List<GoodsSpecProperty> list = new ArrayList<GoodsSpecProperty>();
	 * list.add(goodsSpecProperty); map.put(specId, list); } else {
	 * map.get(specId).add(goodsSpecProperty); } } for (Long long1 :
	 * map.keySet()) { List<GoodsSpecProperty> list = map.get(long1);
	 * htmlSpec.append("
	 * <tr class='loadRemove' id='gs_" + long1 + "' gs_name='" + list.get(0).getSpec().getName() + "'>
	 * <td align='right' valign='top'>" + list.get(0).getSpec().getName() + ":</td>
	 * <td class='sptable' >
	 * <ul class='color_chose'>
	 * "); for (GoodsSpecProperty goodsSpecProperty : list) { String ck =
	 * goodsSpec.containsKey(goodsSpecProperty.getId()) ? "checked='checked'" :
	 * ""; htmlSpec.append("
	 * <li><span class='cc_sp1'><input name='spec_" + long1 +
	 * "' type='radio' id='spec_" + goodsSpecProperty.getId() + "' gs_id='" +
	 * long1 + "' gsp_val='" + goodsSpecProperty.getValue() + "' value='" +
	 * goodsSpecProperty.getId() + "'" + ck + "/></span> <label for='spec_" +
	 * goodsSpecProperty.getId() + "'>"); if
	 * (goodsSpecProperty.getSpec().getType().equals("img")) {
	 * htmlSpec.append("<span class='cc_sp2'><img src='" + url + "/" +
	 * goodsSpecProperty.getSpecImage().getPath() + "/" +
	 * goodsSpecProperty.getSpecImage().getName() +
	 * "' width='16' height='16' /> </span>"); }
	 * htmlSpec.append("<span class='cc_sp3'>" + goodsSpecProperty.getValue() +
	 * "</span></label></li>"); } htmlSpec.append("
	 * </ul>
	 * </td>
	 * </tr>
	 * "); } return htmlSpec.toString(); }
	 **/
	@SecurityMapping(title = "发布商品第二步", value = "/seller/add_goods_second.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/add_goods_second.htm")
	public ModelAndView add_goods_second(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType,String goods_serial,String goods_name) {
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Store store = user.getStore();
		int store_status = store.getStore_status();
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		if (store_status == 2) {
			mv = new JModelAndView(
					"user/default/usercenter/add_goods_second.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			// 商品图片处理
			String default_image_url = url
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getPath()
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getName();
			mv.addObject("goods_main_photo", default_image_url);
			mv.addObject("goods_image_1", default_image_url);
			for (int i = 0; i < 4; i++) {
				mv.addObject("goods_image_" + (i + 2), default_image_url);
			}
			if (request.getSession(false).getAttribute("goods_class_info") != null) {
				GoodsClass gc = (GoodsClass) request.getSession(false)
						.getAttribute("goods_class_info");
				gc = this.goodsClassService.getObjById(gc.getId());
				// 查询goodsd分页列表
				if (url == null || url.equals("")) {
					url = CommUtil.getURL(request);
				}
				// 分页需要传递的参数
				String params = "";
				GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv,
						orderBy, orderType);		
				qo.addQuery("obj.gc.id", new SysMap("gcid", gc.getId()), "=");
				if(goods_name!=null&&!goods_name.equals("")){
				qo.addQuery("obj.goods_name", new SysMap("goods_name","%"+goods_name+"%"), "like ");
				}
				if(goods_serial!=null&&!goods_serial.equals(""))
				qo.addQuery("obj.goods_serial", new SysMap("goods_serial", goods_serial), "=");				
				qo.addQuery("obj.goodsdc_status", new SysMap("gdc_status", 0),
						"=");
				qo.addINQueryStart(
						" obj.goods_serial not in ",
						"select ob.goods_serial from Goods ob where ob.deleteStatus=false and ob.goods_store.id",
						new SysMap("storeId", user.getStore().getId()), "=");
				qo.addINQueryEnd();
				qo.setOrderBy("goods_name");
				qo.setOrderType("desc");
				// qo.setPageSize(2);
				IPageList pList = this.goodsServiceDC.list(qo);
				List<GoodsDC> objs = pList.getResult();
				if (objs != null && objs.size() != 0)
					mv.addObject("hasGoods", "true");
				else
					mv.addObject("hasGoods", "false");
				CommUtil.saveIPageList2ModelAndView(url
						+ "/seller/add_goods_second.htm", "", params, pList, mv);
				mv.addObject("storeTools", storeTools);
				mv.addObject("goodsViewTools", goodsViewTools);
				String goods_class_info = this.generic_goods_class_info(gc);
				mv.addObject("goods_class", gc);
				mv.addObject("ceshi",goods_name);
				mv.addObject("goods_class_info", goods_class_info.substring(0,
						goods_class_info.length() - 1));
			}
			/**
			 * String path = request.getSession().getServletContext()
			 * .getRealPath("/") + File.separator + "upload" + File.separator +
			 * "store" + File.separator + user.getStore().getId();
			 **/
			if (user.getStore().getGrade().getSpaceSize() > 0) {
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("user_id", user.getId());
			params.put("display", true);
			List<UserGoodsClass> ugcs = this.userGoodsClassService
					.query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
							params, -1, -1);
			// List<GoodsBrand> gbs = this.goodsBrandService.query(
			// "select obj from GoodsBrand obj order by obj.sequence asc",
			// null, -1, -1);
			// mv.addObject("gbs", gbs);
			mv.addObject("ugcs", ugcs);
			// mv.addObject("img_remain_size", img_remain_size);
			// mv.addObject("imageSuffix", this.storeViewTools
			// .genericImageSuffix(this.configService.getSysConfig()
			// .getImageSuffix()));
			String goods_session = CommUtil.randomString(32);
			mv.addObject("goods_session", goods_session);
			request.getSession(false).setAttribute("goods_session",
					goods_session);
		}
		if (store_status == 0) {
			mv.addObject("op_title", "您尚未开通店铺，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		if (store_status == 1) {
			mv.addObject("op_title", "您的店铺在审核中，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		if (store_status == 3) {
			mv.addObject("op_title", "您的店铺已被关闭，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "产品规格显示", value = "/seller/goods_inventory.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_inventory.htm")
	public ModelAndView goods_inventory(HttpServletRequest request,
			HttpServletResponse response, String goods_spec_ids, String goods_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/goods_inventory.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String[] spec_ids = goods_spec_ids.split(",");
		List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();
		for (String spec_id : spec_ids) {
			if (!spec_id.equals("")) {
				GoodsSpecProperty gsp = this.specPropertyService
						.getObjById(Long.parseLong(spec_id));
				gsps.add(gsp);
			}
		}
		Set<GoodsSpecification> specs = new HashSet<GoodsSpecification>();
		for (GoodsSpecProperty gsp : gsps) {
			specs.add(gsp.getSpec());
		}
		for (GoodsSpecification spec : specs) {
			spec.getProperties().clear();
			for (GoodsSpecProperty gsp : gsps) {
				if (gsp.getSpec().getId().equals(spec.getId())) {
					spec.getProperties().add(gsp);
				}
			}
		}
		GoodsSpecification[] spec_list = specs
				.toArray(new GoodsSpecification[specs.size()]);
		Arrays.sort(spec_list, new Comparator() {
			@Override
			public int compare(Object obj1, Object obj2) {
				GoodsSpecification a = (GoodsSpecification) obj1;
				GoodsSpecification b = (GoodsSpecification) obj2;
				if (a.getSequence() == b.getSequence()) {
					return 0;
				} else {
					return a.getSequence() > b.getSequence() ? 1 : -1;
				}
			}
		});
		List<List<GoodsSpecProperty>> gsp_list = this
				.generic_spec_property(specs);
		mv.addObject("specs", Arrays.asList(spec_list));
		List<List<GoodsSpecProperty>> gsp_list2 = new ArrayList<List<GoodsSpecProperty>>();
		if (!goods_id.equals("")) {
			for (List<GoodsSpecProperty> list2 : gsp_list) {
				Long[] longs = new Long[list2.size()];
				Map<Long, GoodsSpecProperty> map1 = new HashMap<Long, GoodsSpecProperty>();
				for (int i = 0; i < list2.size(); i++) {
					longs[i] = list2.get(i).getId();
					map1.put(list2.get(i).getId(), list2.get(i));
				}
				longs = sort(longs);
				List<GoodsSpecProperty> list1 = new ArrayList<GoodsSpecProperty>();
				for (Long long1 : longs) {
					list1.add(map1.get(long1));
				}
				gsp_list2.add(list1);
			}
		} else {
			gsp_list2 = gsp_list;
		}
		mv.addObject("gsps", gsp_list2);
		return mv;
	}

	/**
	 * @param ints
	 * @return 數組排序
	 */
	private Long[] sort(Long[] ints) {
		for (int i = ints.length - 2; i >= 0; i--) {
			for (int j = i; j < ints.length - 1; j++) {
				if (ints[j] > ints[j + 1]) {
					Long k = ints[j + 1];
					ints[j + 1] = ints[j];
					ints[j] = k;
				} else
					break;
			}

		}
		return ints;
	}

	/**
	 * arraylist转化为二维数组
	 * 
	 * @param list
	 * @return
	 */
	public static GoodsSpecProperty[][] list2group(
			List<List<GoodsSpecProperty>> list) {
		GoodsSpecProperty[][] gps = new GoodsSpecProperty[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			gps[i] = list.get(i).toArray(
					new GoodsSpecProperty[list.get(i).size()]);
		}
		return gps;
	}

	/**
	 * 生成库存组合 *
	 * 
	 * @param specs
	 * @return
	 */
	private List<List<GoodsSpecProperty>> generic_spec_property(
			Set<GoodsSpecification> specs) {
		List<List<GoodsSpecProperty>> result_list = new ArrayList<List<GoodsSpecProperty>>();
		List<List<GoodsSpecProperty>> list = new ArrayList<List<GoodsSpecProperty>>();
		int max = 1;
		for (GoodsSpecification spec : specs) {
			list.add(spec.getProperties());
		}
		// 将List<List<GoodsSpecProperty>> 转换为二维数组
		GoodsSpecProperty[][] gsps = this.list2group(list);
		for (int i = 0; i < gsps.length; i++) {
			max *= gsps[i].length;
		}
		for (int i = 0; i < max; i++) {
			List<GoodsSpecProperty> temp_list = new ArrayList<GoodsSpecProperty>();
			int temp = 1; // 注意这个temp的用法。
			for (int j = 0; j < gsps.length; j++) {
				temp *= gsps[j].length;
				temp_list.add(j, gsps[j][i / (max / temp) % gsps[j].length]);
			}
			GoodsSpecProperty[] temp_gsps = temp_list
					.toArray(new GoodsSpecProperty[temp_list.size()]);
			Arrays.sort(temp_gsps, new Comparator() {
				public int compare(Object obj1, Object obj2) {
					GoodsSpecProperty a = (GoodsSpecProperty) obj1;
					GoodsSpecProperty b = (GoodsSpecProperty) obj2;
					if (a.getSpec().getSequence() == b.getSpec().getSequence()) {
						return 0;
					} else {
						return a.getSpec().getSequence() > b.getSpec()
								.getSequence() ? 1 : -1;
					}
				}
			});
			result_list.add(Arrays.asList(temp_gsps));
		}
		return result_list;
	}

	@RequestMapping("/seller/swf_upload.htm")
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
		if (user.getStore().getGrade().getSpaceSize() != 0) {
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
				params.put("store_id", user.getStore().getId());
				List<WaterMark> wms = this.waterMarkService
						.query("select obj from WaterMark obj where obj.store.id=:store_id",
								params, -1, -1);
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
				json_map.put("url", CommUtil.getURL(request) + "/" + url + "/"
						+ image.getName());
				json_map.put("id", image.getId());
				json_map.put("remainSpace", remainSpace == 10000 ? 0
						: remainSpace);
				// 同步生成小图片
				String ext = image.getExt().indexOf(".") < 0 ? "."
						+ image.getExt() : image.getExt();
				String source = request.getSession().getServletContext()
						.getRealPath("/")
						+ image.getPath() + File.separator + image.getName();
				String target = source + "_small" + ext;
				CommUtil.createSmall(source, target, this.configService
						.getSysConfig().getSmallWidth(), this.configService
						.getSysConfig().getSmallHeight());
				// 同步生成中等图片
				/**
				 * String midext = image.getExt().indexOf(".") < 0 ? "." +
				 * image.getExt() : image.getExt();
				 **/
				String midtarget = source + "_middle" + ext;
				CommUtil.createSmall(source, midtarget, this.configService
						.getSysConfig().getMiddleWidth(), this.configService
						.getSysConfig().getMiddleHeight());
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

	@SecurityMapping(title = "商品图片删除", value = "/seller/goods_image_del.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_image_del.htm")
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
			if (user.getStore().getGrade().getSpaceSize() != 0) {
				remainSpace = CommUtil.div(user.getStore().getGrade()
						.getSpaceSize()
						* 1024 - csize, 1024);
			}
			map.put("result", ret);
			map.put("remainSpace", remainSpace);
			writer = response.getWriter();
			writer.print(Json.toJson(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 过滤商品名称中的html代码
	 * 
	 * @param inputString
	 * @return
	 */
	/**
	 * private String clearContent(String inputString) { String htmlStr =
	 * inputString; // 含html标签的字符串 String textStr = ""; java.util.regex.Pattern
	 * p_script; java.util.regex.Matcher m_script; java.util.regex.Pattern
	 * p_style; java.util.regex.Matcher m_style; java.util.regex.Pattern p_html;
	 * java.util.regex.Matcher m_html;
	 * 
	 * java.util.regex.Pattern p_html1; java.util.regex.Matcher m_html1;
	 * 
	 * try { String regEx_script =
	 * "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>"; //
	 * 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script> String regEx_style =
	 * "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>"; //
	 * 定义style的正则表达式{或<style[^>]*?>[//s//S]*?<///style> String regEx_html =
	 * "<[^>]+>"; // 定义HTML标签的正则表达式 String regEx_html1 = "<[^>]+"; p_script =
	 * Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE); m_script =
	 * p_script.matcher(htmlStr); htmlStr = m_script.replaceAll(""); //
	 * 过滤script标签
	 * 
	 * p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE); m_style
	 * = p_style.matcher(htmlStr); htmlStr = m_style.replaceAll(""); //
	 * 过滤style标签
	 * 
	 * p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE); m_html =
	 * p_html.matcher(htmlStr); htmlStr = m_html.replaceAll(""); // 过滤html标签
	 * 
	 * p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE); m_html1
	 * = p_html1.matcher(htmlStr); htmlStr = m_html1.replaceAll(""); // 过滤html标签
	 * 
	 * textStr = htmlStr; } catch (Exception e) {
	 * System.err.println("Html2Text: " + e.getMessage()); } return textStr;//
	 * 返回文本字符串 }
	 **/
	@SecurityMapping(title = "发布商品第三步", value = "/seller/add_goods_finish.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/add_goods_finish.htm")
	public ModelAndView add_goods_finish(HttpServletRequest request,
			HttpServletResponse response, String goods_id,
			String intentory_details, String goods_spec_ids,
			String goods_session, String goodsdc_id, String goods_promotion,
			String user_class_ids, String store_price, String inventory_type,
			String goods_inventory, String goods_status, String goods_recommend) {
		// 获取Goodsdc的数据
		String id = goods_id;
		String branch_code = SecurityUserHolder.getCurrentUser().getStore()
				.getBranch_code();
		Goods goods = new Goods();
		if (!goodsdc_id.equals("")) {
			// 判断商品是否已经发布
			JModelAndView mv = new JModelAndView("error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("dcid", Integer.valueOf(goodsdc_id).longValue());
			map.put("store_id", SecurityUserHolder.getCurrentUser().getStore()
					.getId());
			List<Goods> listgoods = this.goodsService
					.query("select obj from Goods obj where obj.goodsDc_id=:dcid and obj.goods_store.id=:store_id",
							map, -1, -1);
			if (listgoods.size() > 0) {
				mv.addObject("op_title", "禁止重复提交表单");
				return mv;
			}
			// 发布商品
			GoodsDC goodsDc = this.goodsServiceDC.getObjById(Integer.valueOf(
					goodsdc_id).longValue());
			goodsDc.getGoodsDC_photos().size();
			for (Field field : goods.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				try {
					Field dcField = goodsDc.getClass().getDeclaredField(
							field.getName());
					dcField.setAccessible(true);
					field.set(goods, dcField.get(goodsDc));
				} catch (Exception e) {
				}
			}
			goods.setGoodsDc_id(Integer.valueOf(goodsdc_id).longValue());
			List<GoodsSpecProperty> list = new ArrayList<GoodsSpecProperty>(
					goodsDc.getGoodsDC_specs());
			goods.setGoods_specs(list);
			List<Accessory> goodsphotos = new ArrayList<Accessory>(
					goodsDc.getGoodsDC_photos());
			goods.setGoods_photos(goodsphotos);
		} else {
			goods = this.goodsService.getObjById(Integer.valueOf(id)
					.longValue());
		}
		/*
		 * goods.setGoods_name(goodsDc.getGoods_name());
		 * goods.setSeo_keywords(seo_keywords)
		 * goods.setSeo_description(seo_description)
		 * goods.setGoods_salenum(goods_salenum)
		 * goods.setGoods_serial(goods_serial)
		 * goods.setGoods_weight(goods_weight)
		 * goods.setGoods_details(goods_details)
		 * goods.setGoods_photos(goods_photos) }else { goods =
		 * this.goodsService.getObjById(Integer.valueOf(id) .longValue()); }
		 */
		// 为Goods赋值
		BigDecimal promotion = goods_promotion.equals("") ? null
				: new BigDecimal(goods_promotion);
		BigDecimal goods_original_price=goods.getGoods_current_price();//原始商品当前价格
		if (promotion == null) {
			if (goods.getGoods_promotion() != null) {
				goods.setGoods_current_price(new BigDecimal(store_price));
			} else {
				if (goods.getStore_price() != null) {
					if (goods.getStore_price().compareTo(
							goods.getGoods_current_price()) == 0)
						goods.setGoods_current_price(new BigDecimal(store_price));
				} else {
					goods.setGoods_current_price(new BigDecimal(store_price));
				}
			}
		} else {
			goods.setGoods_current_price(promotion);
		}
		goods.setGoods_promotion(promotion);
		goods.setGoods_price(new BigDecimal(store_price));
		goods.setStore_price(new BigDecimal(store_price));
		goods.setInventory_type(inventory_type == null ? "all" : inventory_type);
		goods.setGoods_inventory(Integer.valueOf(goods_inventory));
		try {
			goods.setGoods_status(Integer.valueOf(CommUtil
					.null2String(goods_status)));
		} catch (Exception e) {
		}
		goods.setGoods_recommend(Boolean.parseBoolean(goods_recommend));
		goods.setGoods_seller_time(new Date());
		// 规格库存
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		String[] inventory_list = intentory_details.split(";");
		for (String inventory : inventory_list) {
			if (!inventory.equals("")) {
				String[] list = inventory.split(",");
				Map<String, Object> map = new HashMap<String, Object>();
				String ids = list[0];
				String[] arrayIds = ids.split("_");
				Long[] arrayId = new Long[arrayIds.length];
				for (int i = 0; i < arrayIds.length; i++) {
					arrayId[i] = Integer.valueOf(arrayIds[i]).longValue();
				}
				StringBuffer sb = new StringBuffer();
				for (Long long1 : sort(arrayId)) {
					sb.append(long1.toString() + "_");
				}
				map.put("id", sb.toString());
				map.put("count", list[1]);
				map.put("price", list[2]);
				maps.add(map);
			}
		}
		goods.setGoods_inventory_detail(Json.toJson(maps, JsonFormat.compact()));
		// 商品所在店铺分类
		String[] ugc_ids = user_class_ids.split(",");
		goods.getGoods_ugcs().removeAll(goods.getGoods_ugcs());
		for (String ugc_id : ugc_ids) {
			if (!ugc_id.equals("")) {
				UserGoodsClass ugc = this.userGoodsClassService.getObjById(Long
						.parseLong(ugc_id));
				goods.getGoods_ugcs().add(ugc);
			}
		}
		// 商品规格
		/**
		 * goods.getGoods_specs().clear(); String[] spec_ids =
		 * goods_spec_ids.split(","); for (String spec_id : spec_ids) { if
		 * (!spec_id.equals("")) { GoodsSpecProperty gsp =
		 * this.specPropertyService .getObjById(Long.parseLong(spec_id));
		 * goods.getGoods_specs().add(gsp); } }
		 **/
		ModelAndView mv = null;
		String goods_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("goods_session"));
		if (goods_session1.equals("")) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "禁止重复提交表单");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		} else {
			if (goods_session1.equals(goods_session)) {
				if (id == null || id.equals("")) {
					mv = new JModelAndView(
							"user/default/usercenter/add_goods_finish.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
				} else {
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "商品编辑成功");
					mv.addObject("url", CommUtil.getURL(request) + "/goods_"
							+ id + ".htm");
				}
				if (id.equals("")) {
					goods.setAddTime(new Date());
					goods.setBranch_code(branch_code);
					User user = this.userService.getObjById(SecurityUserHolder
							.getCurrentUser().getId());
					goods.setGoods_store(user.getStore());
				} else {
					// 更新操作
				}
				/**
				 * if (goods.getCombin_status() == 2 ||
				 * goods.getDelivery_status() == 2 || goods.getBargain_status()
				 * == 2 || goods.getActivity_status() == 2) { } else { }
				 **/
				if (id != null && !id.equals("")) {
					goods.setGoods_transfee(1);
					this.goodsService.update(goods);
					BigDecimal goods_current_price = goods
							.getGoods_current_price();
					if (goods_original_price.compareTo(goods_current_price) != 0) {// 原始当前价格与当前价格不同时
						modifyGoodsCart(goods,goods_current_price);
					}
					// 更新lucene索引
					LuceneUtil.instance().ruLuceneIndex(goods, "u");
				} else {
					goods.setGoods_transfee(1);
					this.goodsService.save(goods);
					// 新增lucene索引
					LuceneUtil.instance().ruLuceneIndex(goods, "r");
				}
				mv.addObject("obj", goods);
				request.getSession(false).removeAttribute("goods_session");
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "参数错误");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/index.htm");
			}
		}

		return mv;
	}

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
	@SecurityMapping(title = "加载商品分类", value = "/seller/load_goods_class.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/load_goods_class.htm")
	public void load_goods_class(HttpServletRequest request,
			HttpServletResponse response, String pid, String session) {
		GoodsClass obj = this.goodsClassService.getObjById(CommUtil
				.null2Long(pid));
		Map<String, Object> mapreturn = new HashMap<String, Object>();
		List<Map> list = new ArrayList<Map>();
		String isLast = "true";
		if (obj != null) {
			if (obj.getChilds() == null || obj.getChilds().size() == 0)
				isLast = "false";
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

	@SecurityMapping(title = "添加用户常用商品分类", value = "/seller/load_goods_class.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/add_goods_class_staple.htm")
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

	@SecurityMapping(title = "删除用户常用商品分类", value = "/seller/del_goods_class_staple.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/del_goods_class_staple.htm")
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

	@SecurityMapping(title = "根据用户常用商品分类加载分类信息", value = "/seller/del_goods_class_staple.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/load_goods_class_staple.htm")
	public void load_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response, String id, String name)
			throws UnsupportedEncodingException {
		GoodsClass obj = null;
		name = URLDecoder.decode(name, "utf-8");
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

	private String generic_goods_class_info(GoodsClass gc) {
		String goods_class_info = gc.getClassName() + ">";
		if (gc.getParent() != null) {
			String class_info = generic_goods_class_info(gc.getParent());
			goods_class_info = class_info + goods_class_info;
		}
		return goods_class_info;
	}

	@SecurityMapping(title = "出售中的商品列表", value = "/seller/goods.htm*", rtype = "seller", rname = "出售中的商品", rcode = "goods_list_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods.htm")
	public ModelAndView goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String user_class_id) {
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if(user.getStore().getStore_status()==2){		
		mv = new JModelAndView(
				"user/default/usercenter/goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);		
		qo.addQuery("obj.deleteStatus", new SysMap("goods_deleteStatus", false), "=");//判断商品是否已经删除
		qo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		qo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", user
				.getStore().getId()), "=");
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		qo.setPageSize(12);
		if (goods_name != null && !goods_name.equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
					+ goods_name + "%"), "like");
		}
		qo=add_ugc(user_class_id,qo);//添加店铺分类查询
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/seller/goods.htm", "",
				params, pList, mv);
		mv.addObject("storeTools", storeTools);
		mv.addObject("store",user.getStore());
		mv.addObject("goodsViewTools", goodsViewTools);
		}else if (user.getStore().getStore_status() == 0) {
			mv.addObject("op_title", "您尚未开通店铺，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}else	if (user.getStore().getStore_status() == 1) {
			mv.addObject("op_title", "您的店铺在审核中，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}else	if (user.getStore().getStore_status() == 3) {
			mv.addObject("op_title", "您的店铺已被关闭，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "仓库中的商品列表", value = "/seller/goods_storage.htm*", rtype = "seller", rname = "仓库中的商品", rcode = "goods_storage_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_storage.htm")
	public ModelAndView goods_storage(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String user_class_id) {
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if(user.getStore().getStore_status()==2){		
		mv = new JModelAndView(
				"user/default/usercenter/goods_storage.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.addQuery("obj.deleteStatus", new SysMap("goods_deleteStatus", false), "=");//判断商品是否已经删除
		qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), ">");
		qo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "<>");
		qo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", user
				.getStore().getId()), "=");
		qo.setOrderBy("goods_seller_time");
		qo.setOrderType("desc");
		if (goods_name != null && !goods_name.equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
					+ goods_name + "%"), "like");
		}
		qo=add_ugc(user_class_id,qo);//添加店铺分类查询
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/seller/goods_storage.htm",
				"", params, pList, mv);
		mv.addObject("storeTools", storeTools);
		mv.addObject("store",user.getStore());
		mv.addObject("goodsViewTools", goodsViewTools);
		}else if (user.getStore().getStore_status() == 0) {
			mv.addObject("op_title", "您尚未开通店铺，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}else	if (user.getStore().getStore_status() == 1) {
			mv.addObject("op_title", "您的店铺在审核中，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}else	if (user.getStore().getStore_status() == 3) {
			mv.addObject("op_title", "您的店铺已被关闭，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		return mv;
	}
	/**
	 * 根据店铺分类id构造店铺分类查询器
	 * @param id
	 * @param qo
	 * @return
	 */
	private GoodsQueryObject add_ugc(String id,GoodsQueryObject qo){
		if (id != null && !id.equals("")) {
			UserGoodsClass ugc = this.userGoodsClassService.getObjById(Long.parseLong(id));
			if (ugc != null) {
					Set<Long> ids = this.genericUserGcIds(ugc);
					List<UserGoodsClass> ugc_list = new ArrayList<UserGoodsClass>();
					for (Long g_id : ids) {
							UserGoodsClass temp_ugc = this.userGoodsClassService.getObjById(g_id);
							ugc_list.add(temp_ugc);
					}
					if(ugc_list.size()>0)//判断该分类是否存在子分类
							qo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of","and (");
					else
							qo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of","and");
					for (int i = 0; i < ugc_list.size(); i++) {//拼接子分类HQL查询条件
							if(i==ugc_list.size()-1){
									qo.addQuery("ugc" + i, ugc_list.get(i), "obj.goods_ugcs)","member of", "or");	
							}else{
									qo.addQuery("ugc" + i, ugc_list.get(i), "obj.goods_ugcs","member of", "or");					
							}
					}			
			}
	}
		return qo;	
	}
	@SecurityMapping(title = "违规下架商品", value = "/seller/goods_out.htm*", rtype = "seller", rname = "违规下架商品", rcode = "goods_out_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_out.htm")
	public ModelAndView goods_out(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String user_class_id) {
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if(user.getStore().getStore_status()==2){		
		mv = new JModelAndView(
				"user/default/usercenter/goods_out.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.addQuery("obj.deleteStatus", new SysMap("goods_deleteStatus", false), "=");//判断商品是否已经删除
		qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), "=");
		qo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", user
				.getStore().getId()), "=");
		qo.setOrderBy("goods_seller_time");
		qo.setOrderType("desc");
		if (goods_name != null && !goods_name.equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
					+ goods_name + "%"), "like");
		}
		qo=add_ugc(user_class_id,qo);//添加店铺分类查询
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/seller/goods_out.htm", "",
				params, pList, mv);
		mv.addObject("storeTools", storeTools);
		mv.addObject("store",user.getStore());
		mv.addObject("goodsViewTools", goodsViewTools);
		}else if (user.getStore().getStore_status() == 0) {
			mv.addObject("op_title", "您尚未开通店铺，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}else	if (user.getStore().getStore_status() == 1) {
			mv.addObject("op_title", "您的店铺在审核中，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}else	if (user.getStore().getStore_status() == 3) {
			mv.addObject("op_title", "您的店铺已被关闭，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
			return mv;
		}

	@SecurityMapping(title = "商品编辑", value = "/seller/goods_edit.htm*", rtype = "seller", rname = "商品编辑", rcode = "goods_edit_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_edit.htm")
	public ModelAndView goods_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/add_goods_second.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Goods obj = this.goodsService.getObjById(Long.parseLong(id));
		GoodsClass gc = obj.getGc();
		// 加载商品规格数据
		GoodsDC goodsDC = this.goodsServiceDC.getObjById(obj.getGoodsDc_id());
		String htmlSpec = getGoodsdcSpec(goodsDC, obj, url);
		mv.addObject("specHTML", htmlSpec);
		// 商品详细库存
		if (obj.getGoods_store().getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			Map params = new HashMap();
			params.put("user_id", user.getId());
			params.put("display", true);
			List<UserGoodsClass> ugcs = this.userGoodsClassService
					.query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
							params, -1, -1);
			AccessoryQueryObject aqo = new AccessoryQueryObject();
			aqo.setPageSize(8);
			aqo.addQuery("obj.user.id", new SysMap("user_id", user.getId()),
					"=");
			aqo.setOrderBy("addTime");
			aqo.setOrderType("desc");
			IPageList pList = this.accessoryService.list(aqo);
			String photo_url = CommUtil.getURL(request)
					+ "/seller/load_photo.htm";
			List<GoodsBrand> gbs = this.goodsBrandService.query(
					"select obj from GoodsBrand obj order by obj.sequence asc",
					null, -1, -1);
			mv.addObject("gbs", gbs);
			mv.addObject("photos", pList.getResult());
			mv.addObject(
					"gotoPageAjaxHTML",
					CommUtil.showPageAjaxHtml(photo_url, "",
							pList.getCurrentPage(), pList.getPages()));
			mv.addObject("ugcs", ugcs);
			mv.addObject("obj", obj);
			// 运费显示
			String transfeeHtml = "";
			if (obj.getGoods_transfee() == 0) {
				// 判断是否使用运费模板 0:不使用运费模板1：使用运费模板
				if (obj.getTransport() == null) {
					transfeeHtml = getTrans_fee(obj.getMail_trans_fee(),
							obj.getExpress_trans_fee(), obj.getEms_trans_fee());
					mv.addObject("transfeeHtml", transfeeHtml);
					mv.addObject("transport", "");
				} else {
					mv.addObject("transport", obj.getTransport());
				}
			}
			for (UserGoodsClass goodsBrand : obj.getGoods_ugcs()) {
				System.out.println(goodsBrand);
			}
			// 商品类型属性
			try {
				JSONArray array = (JSONArray) JSONValue.parse(goodsDC
						.getGoods_property());
				StringBuffer html = new StringBuffer();
				for (Object object : array) {
					HashMap<String, String> map = (HashMap<String, String>) object;
					html.append("<tr class='loadRemove'><td align='right' valign='top'>"
							+ map.get("name")
							+ "：</td><td class='sptable'><span class='tabtxt1 size2'><input value='"
							+ map.get("val")
							+ "' disabled='disabled' /> </span></td></tr>");
				}
				mv.addObject("goods_property", html.toString());
			} catch (Exception e) {
			}
			// 获取商品分类字符串
			if (request.getSession(false).getAttribute("goods_class_info") != null) {
				// GoodsClass gc = this.goodsClassService.getObjById(session_gc
				// .getId());
				mv.addObject("goods_class_info",
						this.storeTools.generic_goods_class_info(gc));
				mv.addObject("goods_class", gc);
				request.getSession(false).removeAttribute("goods_class_info");
			} else {
				if (obj.getGc() != null) {
					mv.addObject("goods_class_info", this.storeTools
							.generic_goods_class_info(obj.getGc()));
					mv.addObject("goods_class", gc);
				}
			}
			// 商品图片处理
			// 默认图片路径（一张不存在的图片用以触发默认图）
			String default_image_url = url + "/"
					+ this.configService.getSysConfig().getGoodsImage().getPath()
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage().getName()
					+ ".jpg";
			if (obj.getGoods_main_photo() != null) {
				mv.addObject("goods_main_photo", url + "/"
						+ obj.getGoods_main_photo().getPath() + "/"
						+ obj.getGoods_main_photo().getName());
				mv.addObject("goods_image_1", url + "/"
						+ obj.getGoods_main_photo().getPath() + "/"
						+ obj.getGoods_main_photo().getName() + "_small."
						+ obj.getGoods_main_photo().getExt());
			} else {
				mv.addObject("goods_main_photo", default_image_url);
				mv.addObject("goods_image_1", default_image_url);
			}
			for (int i = 0; i < 4; i++) {
				try {
					mv.addObject("goods_image_" + (i + 2), url + "/"
							+ obj.getGoods_photos().get(i).getPath() + "/"
							+ obj.getGoods_photos().get(i).getName()
							+ "_small." + obj.getGoods_photos().get(i).getExt());
				} catch (Exception e) {
					mv.addObject("goods_image_" + (i + 2), default_image_url);
				}
			}
			// 添加session验证
			String goods_session = CommUtil.randomString(32);
			mv.addObject("goods_session", goods_session);
			request.getSession(false).setAttribute("goods_session",
					goods_session);
			// 未知
			mv.addObject("imageSuffix", this.storeViewTools
					.genericImageSuffix(this.configService.getSysConfig()
							.getImageSuffix()));
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有该商品信息！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "商品上下架", value = "/seller/goods_sale.htm*", rtype = "seller", rname = "商品上下架", rcode = "goods_sale_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_sale.htm")
	public String goods_sale(HttpServletRequest request,
			HttpServletResponse response, String mulitId) {
		String url = "/seller/goods.htm";
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Goods goods = this.goodsService.getObjById(Long.parseLong(id));
				if (goods.getGoods_store().getUser().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
					int goods_status = goods.getGoods_status() == 0 ? 1 : 0;
					goods.setGoods_status(goods_status);
					this.goodsService.update(goods);
					if (goods_status == 0) {
						url = "/seller/goods_storage.htm";
						// 更新lucene索引
						LuceneUtil.instance().ruLuceneIndex(goods, "u");
					} else {
						// 删除lucene索引
						LuceneUtil.instance().removeLuceneIndex(id);
					}
				}
			}
		}
		return "redirect:" + url;
	}

	@SecurityMapping(title = "商品删除", value = "/seller/goods_del.htm*", rtype = "seller", rname = "商品删除", rcode = "goods_del_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_del.htm")
	public String goods_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String op) {
		String url = "/seller/goods.htm";
		if (CommUtil.null2String(op).equals("storage")) {
			url = "/seller/goods_storage.htm";
		}
		if (CommUtil.null2String(op).equals("out")) {
			url = "/seller/goods_out.htm";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(id));
				if (goods.getGoods_store().getUser().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
					Map map = new HashMap();
					map.put("gid", goods.getId());
					List<GoodsCart> goodCarts = this.goodsCartService
							.query("select obj from GoodsCart obj where obj.goods.id = :gid",
									map, -1, -1);
					for (GoodsCart gc : goodCarts) {
						Long of_id = gc.getOf() == null ? -1 : gc.getOf()
								.getId();
						this.goodsCartService.delete(gc.getId());
						OrderForm of = this.orderFormService.getObjById(of_id);
						if (of != null && of.getGcs().size() <= 0) {
							this.orderFormService.delete(of_id);
						}
					}

					List<Evaluate> evaluates = goods.getEvaluates();
					for (Evaluate e : evaluates) {
						this.evaluateService.delete(e.getId());
					}
					goods.getGoods_ugcs().clear();
					goods.getGoods_photos().clear();
					goods.getGoods_specs().clear();
					this.goodsService.delete(goods.getId());
					// 删除lucene索引
					LuceneUtil.instance().removeLuceneIndex(id);
				}
			}
		}
		return "redirect:" + url;
	}

	@SecurityMapping(title = "被举报禁售商品", value = "/seller/goods_report.htm*", rtype = "seller", rname = "被举报禁售商品", rcode = "goods_report_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_report.htm")
	public ModelAndView goods_report(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/goods_report.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ReportQueryObject qo = new ReportQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.addQuery("obj.deleteStatus", new SysMap("goods_deleteStatus", false), "=");//判断商品是否已经删除
		qo.addQuery("obj.goods.goods_store.user.id", new SysMap("user_id",
				SecurityUserHolder.getCurrentUser().getId()), "=");
		qo.addQuery("obj.result", new SysMap("result", 1), "=");
		IPageList pList = this.reportService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "举报图片查看", value = "/seller/report_img.htm*", rtype = "seller", rname = "被举报禁售商品", rcode = "goods_report_seller", rgroup = "商品管理")
	@RequestMapping("/seller/report_img.htm")
	public ModelAndView report_img(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/report_img.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Report obj = this.reportService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		return mv;
	}

	@RequestMapping("/seller/goods_img_album.htm")
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
	
	/**
	 * 用于商品当前价修改时，购物车模块中，修改goodsCart的price
	 */
	private void modifyGoodsCart(Goods goods, BigDecimal goods_current_price) {
		List<GoodsCart> goodsCartList = this.goodsCartService
				.queryByGoods(goods);
		boolean result_goodsCart = false;
		try {
			for (GoodsCart goodsCart : goodsCartList) {
				goodsCart.setPrice(goods_current_price);
				result_goodsCart = this.goodsCartService.update(goodsCart);
				if (result_goodsCart) {
					modifyStoreCart(goodsCart);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用于商品当前价修改时，购物车模块中，修改storeCart的total_price
	 */
	private void modifyStoreCart(GoodsCart goodsCart) {
		StoreCart storeCart = goodsCart.getSc();
		if (storeCart.getSc_status() == 0 && !storeCart.isDeleteStatus()) {// sc_status=0,DELETESTATUS=0
			List<GoodsCart> goodsCartList = this.goodsCartService
					.queryByStoreCart(storeCart);
			BigDecimal total_price = BigDecimal.ZERO;
			for (GoodsCart goodsCartTemp : goodsCartList) {
				total_price = total_price.add(goodsCartTemp.getPrice()
						.multiply(new BigDecimal(goodsCartTemp.getCount())));
			}
			storeCart.setTotal_price(total_price);
			this.storeCartService.update(storeCart);
		}
	}
	private Set<Long> genericUserGcIds(UserGoodsClass ugc) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(ugc.getId());
		for (UserGoodsClass child : ugc.getChilds()) {
			Set<Long> cids = genericUserGcIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}
}
