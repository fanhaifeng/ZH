package com.lakecloud.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.core.tools.database.DatabaseTools;
import com.lakecloud.foundation.domain.Accessory;
import com.lakecloud.foundation.domain.GoodsDC;
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.GoodsSpecification;
import com.lakecloud.foundation.domain.GoodsType;
import com.lakecloud.foundation.domain.query.GoodsSpecificationQueryObject;
import com.lakecloud.foundation.service.IAccessoryService;
import com.lakecloud.foundation.service.IGoodsServiceDC;
import com.lakecloud.foundation.service.IGoodsSpecPropertyService;
import com.lakecloud.foundation.service.IGoodsSpecificationService;
import com.lakecloud.foundation.service.IGoodsTypeService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.manage.admin.tools.StoreTools;

@Controller
public class GoodsSpecificationManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoodsSpecificationService goodsSpecService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	private StoreTools shopTools;
	@Autowired
	private IGoodsTypeService goodsTypeService;
	@Autowired
	private IGoodsServiceDC goodsServiceDC;

	/**
	 * GoodsSpecification列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品规格列表", value = "/admin/goods_spec_list.htm*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "商品")
	@RequestMapping("/admin/goods_spec_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType,String message,String flag) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_spec_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		GoodsSpecificationQueryObject qo = new GoodsSpecificationQueryObject(
				currentPage, mv, orderBy, orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, GoodsSpecification.class, mv);
		qo.setOrderBy("sequence");
		qo.setOrderType("asc");
		IPageList pList = this.goodsSpecService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("shopTools", shopTools);
		
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
	 * goodsSpecification添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "商品规格添加", value = "/admin/goods_spec_add.htm*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "商品")
	@RequestMapping("/admin/goods_spec_add.htm")
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_spec_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * goodsSpecification编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "商品规格编辑", value = "/admin/goods_spec_edit.htm*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "商品")
	@RequestMapping("/admin/goods_spec_edit.htm")
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_spec_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			GoodsSpecification goodsSpecification = this.goodsSpecService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", goodsSpecification);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * goodsSpecification保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品规格保存", value = "/admin/goods_spec_save.htm*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "商品")
	@RequestMapping("/admin/goods_spec_save.htm")
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response, String id, String cmd, String count,
			String add_url, String list_url, String currentPage) {
		WebForm wf = new WebForm();
		GoodsSpecification goodsSpecification = null;
		if (id.equals("")) {
			goodsSpecification = wf.toPo(request, GoodsSpecification.class);
			goodsSpecification.setAddTime(new Date());
		} else {
			GoodsSpecification obj = this.goodsSpecService.getObjById(Long
					.parseLong(id));
			goodsSpecification = (GoodsSpecification) wf.toPo(request, obj);
		}
		if (id.equals("")) {
			this.goodsSpecService.save(goodsSpecification);
		} else
			this.goodsSpecService.update(goodsSpecification);
		this.genericProperty(request, goodsSpecification, count);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url + "?currentPage=" + currentPage);
		mv.addObject("op_title", "保存商品规格成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url);
		}
		return mv;
	}

	private void clearProperty(HttpServletRequest request,
			GoodsSpecification spec) {
		for (GoodsSpecProperty property : spec.getProperties()) {
			property.setSpec(null);
			Accessory img = property.getSpecImage();
			CommUtil.del_acc(request, img);
			property.setSpecImage(null);
			this.goodsSpecPropertyService.delete(property.getId());
		}
	}

	private void genericProperty(HttpServletRequest request,
			GoodsSpecification spec, String count) {
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			Integer sequence = CommUtil.null2Int(request
					.getParameter("sequence_" + i));
			String value = CommUtil.null2String(request.getParameter("value_"
					+ i));
			if (sequence != null && !sequence.equals("") && value != null
					&& !value.equals("")) {
				String id = CommUtil.null2String(request
						.getParameter("id_" + i));
				GoodsSpecProperty property = null;
				if (id != null && !id.equals("")) {
					property = this.goodsSpecPropertyService.getObjById(Long
							.parseLong(id));
				} else
					property = new GoodsSpecProperty();
				property.setAddTime(new Date());
				property.setSequence(sequence);
				property.setSpec(spec);
				property.setValue(value);
				String uploadFilePath = this.configService.getSysConfig()
						.getUploadFilePath();
				String saveFilePathName = request.getSession()
						.getServletContext().getRealPath("/")
						+ uploadFilePath + File.separator + "spec";
				Map map = new HashMap();
				try {
					String fileName = property.getSpecImage() == null ? ""
							: property.getSpecImage().getName();
					map = CommUtil.saveFileToServer(request, "specImage_" + i,
							saveFilePathName, fileName, null);
					if (fileName.equals("")) {
						if (map.get("fileName") != "") {
							Accessory specImage = new Accessory();
							specImage.setName(CommUtil.null2String(map
									.get("fileName")));
							specImage.setExt(CommUtil.null2String(map
									.get("mime")));
							specImage.setSize(CommUtil.null2Float(map
									.get("fileSize")));
							specImage.setPath(uploadFilePath + "/spec");
							specImage.setWidth(CommUtil.null2Int(map
									.get("width")));
							specImage.setHeight(CommUtil.null2Int(map
									.get("height")));
							specImage.setAddTime(new Date());
							this.accessoryService.save(specImage);
							property.setSpecImage(specImage);
						}
					} else {
						if (map.get("fileName") != "") {
							Accessory specImage = property.getSpecImage();
							specImage.setName(CommUtil.null2String(map
									.get("fileName")));
							specImage.setExt(CommUtil.null2String(map
									.get("mime")));
							specImage.setSize(CommUtil.null2Float(map
									.get("fileSize")));
							specImage.setPath(uploadFilePath + "/spec");
							specImage.setWidth(CommUtil.null2Int(map
									.get("width")));
							specImage.setHeight(CommUtil.null2Int(map
									.get("height")));
							specImage.setAddTime(new Date());
							this.accessoryService.update(specImage);
						}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (id.equals("")) {
					this.goodsSpecPropertyService.save(property);
				} else {
					this.goodsSpecPropertyService.update(property);
				}
			}
		}
	}

	@SecurityMapping(title = "商品规格删除", value = "/admin/goods_spec_del.htm*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "商品")
	@RequestMapping("/admin/goods_spec_del.htm")
	public String delete(HttpServletRequest request, String mulitId,
			String currentPage) throws UnsupportedEncodingException {
		String[] ids = mulitId.split(",");
		int flag= 0 ;
		String requestString = ""; 
		for (String id : ids) {
			if (!id.equals("")) {
				GoodsSpecification obj = this.goodsSpecService.getObjById(Long
						.parseLong(id));
				List<GoodsType> goodsTypeList = obj.getTypes();
				if(goodsTypeList.size() > 0 ){
					flag=1;
					requestString +=obj.getName()+"有上级关联，不能删除！！\\n";
				}else{
					if(getSpecSize(Long.parseLong(id))>0){
						flag=1;
						requestString +=obj.getName()+"被商品使用，不能删除！！\\n";
					}else{
					this.clearProperty(request, obj);
					obj.getTypes().clear();
					this.goodsSpecService.delete(Long.parseLong(id));
					}
				}
				
			}
		}
		return "redirect:goods_spec_list.htm?currentPage="+currentPage+"&message="+ URLEncoder.encode(requestString, "UTF-8")+"&flag="+flag;
	}

	/**
	 * 删除规格名称调用方法
	 * @param request	  
	 */
	@SuppressWarnings("unchecked")
	private int getSpecSize(Long id){
		String query = "select count(1) from lakecloud_goodsDC_spec where SPEC_ID  in (select id from lakecloud_goodsspecproperty where SPEC_ID="+id+")";
		List<Integer> specSize = this.goodsServiceDC.getPrice(query,null,-1,-1);	
		int specNum  =0;
		if(specSize.size()>0){
		for(int i =0;i<specSize.size();i++){
			specNum  = specSize.get(0);
		}
		return specNum;	
		}else{
		return 0;
		}
	}
	
	@SecurityMapping(title = "商品规格属性AJAX删除", value = "/admin/goods_property_delete.htm*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "商品")
	@RequestMapping("/admin/goods_property_delete.htm")
	public void goods_property_delete(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = true;
		if (!id.equals("")) {
			if(getSpecValSize(Long.parseLong(id))>0){
				ret = false;
			}else{
			this.databaseTools.execute("delete from "
					+ Globals.DEFAULT_TABLE_SUFFIX
					+ "goods_spec where spec_id=" + id);
			this.databaseTools.execute("delete from "
					+ Globals.DEFAULT_TABLE_SUFFIX + "cart_gsp where gsp_id="
					+ id);
			GoodsSpecProperty property = this.goodsSpecPropertyService
					.getObjById(Long.parseLong(id));
			property.setSpec(null);
			Accessory img = property.getSpecImage();
			CommUtil.del_acc(request, img);
			property.setSpecImage(null);
			ret = this.goodsSpecPropertyService.delete(property.getId());
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
	 * 删除规格值调用方法
	 * @param request	  
	 */
	@SuppressWarnings("unchecked")
	private int getSpecValSize(Long id){
		String query = "select count(1) from lakecloud_goodsDC_spec where SPEC_ID="+id;
		List<Integer> specSize = this.goodsServiceDC.getPrice(query,null,-1,-1);	
		int specNum  =0;
		if(specSize.size()>0){
		for(int i =0;i<specSize.size();i++){
			specNum  = specSize.get(0);
		}
		return specNum;	
		}else{
		return 0;
		}
	}
	
	@SecurityMapping(title = "商品规格AJAX更新", value = "/admin/goods_spec_ajax.htm*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "商品")
	@RequestMapping("/admin/goods_spec_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		GoodsSpecification obj = this.goodsSpecService.getObjById(Long
				.parseLong(id));
		Field[] fields = GoodsSpecification.class.getDeclaredFields();
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
		this.goodsSpecService.update(obj);
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

	@RequestMapping("/admin/goods_spec_verify.htm")
	public void goods_spec_verify(HttpServletRequest request,
			HttpServletResponse response, String name, String id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("name", name);
		params.put("id", CommUtil.null2Long(id));
		List<GoodsSpecification> gss = this.goodsSpecService
				.query(
						"select obj from GoodsSpecification obj where obj.name=:name and obj.id!=:id",
						params, -1, -1);
		if (gss != null && gss.size() > 0) {
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
}