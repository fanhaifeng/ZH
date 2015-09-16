package com.lakecloud.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsBrand;
import com.lakecloud.foundation.domain.GoodsBrandCategory;
import com.lakecloud.foundation.domain.GoodsClass;
import com.lakecloud.foundation.domain.GoodsDC;
import com.lakecloud.foundation.domain.GoodsSpecification;
import com.lakecloud.foundation.domain.GoodsType;
import com.lakecloud.foundation.domain.GoodsTypeProperty;
import com.lakecloud.foundation.domain.query.GoodsTypeQueryObject;
import com.lakecloud.foundation.service.IGoodsBrandCategoryService;
import com.lakecloud.foundation.service.IGoodsBrandService;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.IGoodsServiceDC;
import com.lakecloud.foundation.service.IGoodsSpecificationService;
import com.lakecloud.foundation.service.IGoodsTypePropertyService;
import com.lakecloud.foundation.service.IGoodsTypeService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.manage.admin.tools.StoreTools;

@Controller
public class GoodsTypeManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsTypeService goodsTypeService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsBrandCategoryService goodsBrandCategoryService;
	@Autowired
	private IGoodsSpecificationService goodsSpecificationService;
	@Autowired
	private IGoodsTypePropertyService goodsTypePropertyService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private StoreTools shopTools;
	@Autowired
	private IGoodsServiceDC goodsServiceDC;

	
	/**
	 * GoodsType列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品类型列表", value = "/admin/goods_type_list.htm*", rtype = "admin", rname = "类型管理", rcode = "goods_type", rgroup = "商品")
	@RequestMapping("/admin/goods_type_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType,String message,String flag) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_type_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		GoodsTypeQueryObject qo = new GoodsTypeQueryObject(currentPage, mv,
				orderBy, orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, GoodsType.class, mv);
		qo.setOrderBy("sequence");
		qo.setOrderType("asc");
		IPageList pList = this.goodsTypeService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		if(null==flag){
			flag="2";
		}
		if(null==message){
			message="";
		}
		mv.addObject("msg", message);
		mv.addObject("flag", flag);
		return mv;
	}

	/**
	 * goodsType添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "商品类型添加", value = "/admin/goods_type_add.htm*", rtype = "admin", rname = "类型管理", rcode = "goods_type", rgroup = "商品")
	@RequestMapping("/admin/goods_type_add.htm")
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_type_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		List<GoodsBrandCategory> gbcs = this.goodsBrandCategoryService.query(
				"select obj from GoodsBrandCategory obj order by sequence asc",
				null, -1, -1);
		List<GoodsSpecification> gss = this.goodsSpecificationService
				.query(
						"select obj from GoodsSpecification obj order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gss", gss);
		mv.addObject("gbcs", gbcs);
		mv.addObject("shopTools", shopTools);
		return mv;
	}

	/**
	 * goodsType编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "商品类型编辑", value = "/admin/goods_type_edit.htm*", rtype = "admin", rname = "类型管理", rcode = "goods_type", rgroup = "商品")
	@RequestMapping("/admin/goods_type_edit.htm")
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_type_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			GoodsType goodsType = this.goodsTypeService.getObjById(Long
					.parseLong(id));
			List<GoodsBrandCategory> gbcs = this.goodsBrandCategoryService
					.query(
							"select obj from GoodsBrandCategory obj order by sequence asc",
							null, -1, -1);
			List<GoodsSpecification> gss = this.goodsSpecificationService
					.query(
							"select obj from GoodsSpecification obj order by obj.sequence asc",
							null, -1, -1);
			mv.addObject("gss", gss);
			mv.addObject("gbcs", gbcs);
			mv.addObject("shopTools", shopTools);
			mv.addObject("obj", goodsType);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * goodsType保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品类型保存", value = "/admin/goods_type_save.htm*", rtype = "admin", rname = "类型管理", rcode = "goods_type", rgroup = "商品")
	@RequestMapping("/admin/goods_type_save.htm")
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response, String id, String cmd,
			String currentPage, String list_url, String add_url,
			String spec_ids, String brand_ids, String count) {
		WebForm wf = new WebForm();
		GoodsType goodsType = null;
		if (id.equals("")) {
			goodsType = wf.toPo(request, GoodsType.class);
			goodsType.setAddTime(new Date());
		} else {
			GoodsType obj = this.goodsTypeService
					.getObjById(Long.parseLong(id));
			goodsType = (GoodsType) wf.toPo(request, obj);
		}
		goodsType.getGss().clear();
		goodsType.getGbs().clear();
		String[] gs_ids = spec_ids.split(",");
		for (String gs_id : gs_ids) {
			if (!gs_id.equals("")) {
				GoodsSpecification gs = this.goodsSpecificationService
						.getObjById(Long.parseLong(gs_id));
				goodsType.getGss().add(gs);
			}
		}
		String[] gb_ids = brand_ids.split(",");
		for (String gb_id : gb_ids) {
			if (!gb_id.equals("")) {
				GoodsBrand gb = this.goodsBrandService.getObjById(Long
						.parseLong(gb_id));
				goodsType.getGbs().add(gb);
			}
		}
		if (id.equals("")) {
			this.goodsTypeService.save(goodsType);
		} else
			this.goodsTypeService.update(goodsType);
		this.genericProperty(request, goodsType, count);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url + "?currentPage=" + currentPage);
		mv.addObject("op_title", "保存商品类型成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url);
		}
		return mv;
	}

	public void genericProperty(HttpServletRequest request, GoodsType type,
			String count) {
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			int sequence = CommUtil.null2Int(request
					.getParameter("gtp_sequence_" + i));
			String name = CommUtil.null2String(request.getParameter("gtp_name_"
					+ i));
			String value = CommUtil.null2String(request
					.getParameter("gtp_value_" + i));
			boolean display = CommUtil.null2Boolean(request
					.getParameter("gtp_display_" + i));
			if (!name.equals("") && !value.equals("")) {
				GoodsTypeProperty gtp = null;
				String id = CommUtil.null2String(request.getParameter("gtp_id_"
						+ i));
				if (id.equals("")) {
					gtp = new GoodsTypeProperty();
				} else {
					gtp = this.goodsTypePropertyService.getObjById(Long
							.parseLong(id));
				}
				gtp.setAddTime(new Date());
				gtp.setDisplay(display);
				gtp.setGoodsType(type);
				gtp.setName(name);
				gtp.setSequence(sequence);
				gtp.setValue(value);
				if (id.equals("")) {
					this.goodsTypePropertyService.save(gtp);
				} else {
					this.goodsTypePropertyService.update(gtp);
				}
			}
		}
	}

	@SecurityMapping(title = "商品类型删除", value = "/admin/goods_type_del.htm*", rtype = "admin", rname = "类型管理", rcode = "goods_type", rgroup = "商品")
	@RequestMapping("/admin/goods_type_del.htm")
	public String delete(HttpServletRequest request, String mulitId,
			String currentPage) throws UnsupportedEncodingException {
		String[] ids = mulitId.split(",");
		int flag= 0 ;
		String requestString = "";
		for (String id : ids) {
			if (!id.equals("")) {
				GoodsType goodsType = this.goodsTypeService.getObjById(Long
						.parseLong(id));
				//判断该类型是否能被删除
				if(goodsType!=null){
					if(goodsType.getGcs().size()>0 || goodsType.getGss().size()>0 || goodsType.getGbs().size()>0){
						flag=1;
						requestString +=goodsType.getName()+"被使用，不能删除。\\n";	
					}else{
				//未被使用
				goodsType.getGbs().clear();
				goodsType.getGss().clear();
				for (GoodsClass gc : goodsType.getGcs()) {
					gc.setGoodsType(null);
					this.goodsClassService.update(gc);
				}
				this.goodsTypeService.delete(Long.parseLong(id));
					}
				}
			}
		}
		return "redirect:goods_type_list.htm?currentPage=" + currentPage+"&message="+ URLEncoder.encode(requestString, "UTF-8")+"&flag="+flag;
	}

	@SecurityMapping(title = "商品类型属性AJAX删除", value = "/admin/goods_type_property_delete.htm*", rtype = "admin", rname = "类型管理", rcode = "goods_type", rgroup = "商品")
	@RequestMapping("/admin/goods_type_property_delete.htm")
	public void goods_type_property_delete(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = true;
		if (!id.equals("")) {
			//判断类型是否被使用
			if(getSpecTypeSize(Long.parseLong(id))>0){
				ret=false;
			}else{
			GoodsTypeProperty property = this.goodsTypePropertyService
					.getObjById(Long.parseLong(id));
			property.setGoodsType(null);
			ret = this.goodsTypePropertyService.delete(property.getId());
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
	 * 类型删除判断
	 * @param request	  
	 */
	@SuppressWarnings("unchecked")
	private int getSpecTypeSize(Long id){
		List<GoodsDC> goodsList = this.goodsServiceDC.query(
				"select obj from GoodsDC obj",
				null, -1, -1);
		int specNum  =0;
		if(goodsList!=null && goodsList.size()>0){
		for(int i =0;i<goodsList.size();i++){
			GoodsDC goodsdc=goodsList.get(i);
			JSONArray array = (JSONArray) JSONValue.parse(goodsdc.getGoods_property());//获取规格的json数据
			for (Object object : array) {
				HashMap<String, String> map = (HashMap<String, String>) object;
			    if (map.get("id").equals(id.toString())){
			    	specNum++;
			    }
			}
		}
		return specNum;	
		}else{
		return 0;
		}
	}
	
	@SecurityMapping(title = "商品类型AJAX更新", value = "/admin/goods_type_ajax.htm*", rtype = "admin", rname = "类型管理", rcode = "goods_type", rgroup = "商品")
	@RequestMapping("/admin/goods_type_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		GoodsType obj = this.goodsTypeService.getObjById(Long.parseLong(id));
		Field[] fields = GoodsType.class.getDeclaredFields();
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
		this.goodsTypeService.update(obj);
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

	@RequestMapping("/admin/goods_type_verify.htm")
	public void goods_type_verify(HttpServletRequest request,
			HttpServletResponse response, String name, String id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("name", name);
		params.put("id", CommUtil.null2Long(id));
		List<GoodsType> gts = this.goodsTypeService
				.query(
						"select obj from GoodsType obj where obj.name=:name and obj.id!=:id",
						params, -1, -1);
		if (gts != null && gts.size() > 0) {
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
	
	//验证类型属性
	@RequestMapping("/admin/goods_property_check.htm")
	public void goods_property_check(HttpServletRequest request,
			HttpServletResponse response, String val, String id) {
		int num = 0;
		GoodsTypeProperty gtp = this.goodsTypePropertyService.getObjById(CommUtil.null2Long(id));
		if(!val.contains(gtp.getValue())){
			num++;	
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(num);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}