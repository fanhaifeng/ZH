package com.lakecloud.lucene;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * 全文检索控制类，通过该类完成索引的建议以及搜索的完成 V1.3版开始
 * 
 * @author erikchang
 * 
 */
public class LuceneVo {
	public static final String ID = "id";
	public static final String TITLE = "title";//商品名称
	public static final String TYPE = "type";
	public static final String CONTENT = "content";//商品描述
	public static final String URL = "url";
	public static final String STORE_PRICE = "store_price";
	public static final String ADD_TIME = "add_time";
	public static final String GOODS_SALENUM = "goods_salenum";
	public static final String SEO_KEYWORDS = "seo_keywords";//商品搜索关键字
	public static final String GOODS_PROMOTION = "goods_promotion";//促销价格
	public static final String GOODS_PRICE = "goods_price";//商品原价格
	public static final String GOODS_SERIAL = "goods_serial";//货号
	public static final String GOODS_STORE = "goods_store_name";//店铺名称
	public static final String GOODS_BRAND = "goods_brand";//商品品牌名称
	public static final String GOODS_SPEC = "goods_sepc";//商品规格
	public static final String GOODS_INVENTORY_DETAIL = "goods_inventory_detail";//商品规格库存情况
	public static final String GOODS_CURRENT_PRICE ="goods_current_price";//当前价格
	public static final String DESCRIPTION_EVALUATE ="description_evaluate";// 商品描述相符评分，默认为5分
	public static final String STORE_ID ="store_id";// 店铺id
	public static final String USER_ID ="user_id";// 店主ID
	public static final String STORE_OWER ="store_ower";// 店主姓名
	public static final String GOODS_CLICK ="goods_click";// 浏览次数
	public static final String GOODS_COLLECT ="goods_collect";// 收藏次数
	public static final String IMG_PATH ="img_path";//图片路径
	public static final String IMG_NAME ="img_name";//图片名称
	public static final String IMG_EXT ="img_ext";//图片名称
	public static final String CIT_ID="city_id";//城市ID商品商店所在城市ID
	public static final String EVALUATES_NUM="evaluates_num";//累加评价次数
	public static final String STORE_GRADE="store_grade";//店铺等级
	public static final String INVENTORY_TYPE="inventory_type";//库存类型 all是全局库存，其他是规格库存
	public static final String GOODS_INVENTORY="goods_inventory";//商品库存
	public static final String GOODS_STATUS="goods_status";//商品状态，0为上架，1为在仓库中，-1为手动下架状态，-2为违规下架状态,-3被举报禁售
	public static final String CITY_NAME="city_name";//城市名称
	public static final String WEIXIN_STATUS="weixin_status";//app状态 1为可见 0为不可见
	public static final String GC_ID="gc_id";// 商品对应的大分类id
	public static final String BRAND_ID="brand_id";// 品牌id
	public static final String GOODS_SPECIFICATION="goods_specification";//商品规格
	public static final String GOODS_TYPE_PROPERTY="goods_type_property";//商品属性
	
	
	
	

	
	
	
	
	
	
	
	private Long vo_id;
	private String vo_type;
	private String vo_title;
	private String vo_content;
	private String vo_url;
	private double vo_store_price;
	private long vo_add_time;
	private int vo_goods_salenum;
	private String vo_seo_keywords;
	private BigDecimal vo_goods_promotion;
	private double vo_goods_price;
	private String vo_goods_serial;
	private String vo_goods_store;
	private String vo_goods_brand;
	private String vo_goods_sepc;
	private String vo_goods_inventory_detail;
	private double vo_goods_current_price;
	private BigDecimal vo_description_evaluate;
	private String vo_store_id;
	private Long vo_user_id;
	private String vo_store_ower;
	private int vo_goods_click;
	private int vo_goods_collect;
	private String vo_img_path;
	private String vo_img_name;
	private String vo_img_ext;
	private Long vo_city_id;
	private int vo_evalutes_num;
	private String vo_store_grade; 
	private String vo_inventory_type;
	private int vo_goods_inventory;
	private String vo_goods_status;
	private String vo_city_name;
	private String vo_weixin_status;
	private String vo_gc_id;
	private String vo_brand_id;
	private Map<String,String> vo_goods_specification =new HashMap<String,String>();
	private Map<String,String> vo_goods_type_property = new HashMap<String,String>();
	
	public long getVo_add_time() {
		return vo_add_time;
	}

	public void setVo_add_time(long vo_add_time) {
		this.vo_add_time = vo_add_time;
	}

	public int getVo_goods_salenum() {
		return vo_goods_salenum;
	}

	public void setVo_goods_salenum(int vo_goods_salenum) {
		this.vo_goods_salenum = vo_goods_salenum;
	}


	public double getVo_store_price() {
		return vo_store_price;
	}

	public void setVo_store_price(double vo_store_price) {
		this.vo_store_price = vo_store_price;
	}

	public Long getVo_id() {
		return vo_id;
	}

	public void setVo_id(Long vo_id) {
		this.vo_id = vo_id;
	}

	public String getVo_type() {
		return vo_type;
	}

	public void setVo_type(String vo_type) {
		this.vo_type = vo_type;
	}

	public String getVo_title() {
		return vo_title;
	}

	public void setVo_title(String vo_title) {
		this.vo_title = vo_title;
	}

	public String getVo_content() {
		return vo_content;
	}

	public void setVo_content(String vo_content) {
		this.vo_content = vo_content;
	}

	public String getVo_url() {
		return vo_url;
	}

	public void setVo_url(String vo_url) {
		this.vo_url = vo_url;
	}

	
	public String getVo_seo_keywords() {
		return vo_seo_keywords;
	}

	public void setVo_seo_keywords(String voSeoKeywords) {
		vo_seo_keywords = voSeoKeywords;
	}

	public BigDecimal getVo_goods_promotion() {
		return vo_goods_promotion;
	}

	public void setVo_goods_promotion(BigDecimal voGoodsPromotion) {
		vo_goods_promotion = voGoodsPromotion;
	}

	

	public String getVo_goods_serial() {
		return vo_goods_serial;
	}

	public void setVo_goods_serial(String voGoodsSerial) {
		vo_goods_serial = voGoodsSerial;
	}

	public String getVo_goods_store() {
		return vo_goods_store;
	}

	public void setVo_goods_store(String voGoodsStore) {
		vo_goods_store = voGoodsStore;
	}

	public String getVo_goods_brand() {
		return vo_goods_brand;
	}

	public void setVo_goods_brand(String voGoodsBrand) {
		vo_goods_brand = voGoodsBrand;
	}

	public String getVo_goods_sepc() {
		return vo_goods_sepc;
	}

	public void setVo_goods_sepc(String voGoodsSepc) {
		vo_goods_sepc = voGoodsSepc;
	}

	public String getVo_goods_inventory_detail() {
		return vo_goods_inventory_detail;
	}

	public void setVo_goods_inventory_detail(String voGoodsInventoryDetail) {
		vo_goods_inventory_detail = voGoodsInventoryDetail;
	}


	public double getVo_goods_price() {
		return vo_goods_price;
	}

	public void setVo_goods_price(double voGoodsPrice) {
		vo_goods_price = voGoodsPrice;
	}

	public double getVo_goods_current_price() {
		return vo_goods_current_price;
	}

	public void setVo_goods_current_price(double voGoodsCurrentPrice) {
		vo_goods_current_price = voGoodsCurrentPrice;
	}

	public BigDecimal getVo_description_evaluate() {
		return vo_description_evaluate;
	}

	public void setVo_description_evaluate(BigDecimal voDescriptionEvaluate) {
		vo_description_evaluate = voDescriptionEvaluate;
	}

	public String getVo_store_id() {
		return vo_store_id;
	}

	public void setVo_store_id(String voStoreId) {
		vo_store_id = voStoreId;
	}

	public Long getVo_user_id() {
		return vo_user_id;
	}

	public void setVo_user_id(Long voUserId) {
		vo_user_id = voUserId;
	}

	public String getVo_store_ower() {
		return vo_store_ower;
	}

	public void setVo_store_ower(String voStoreOwer) {
		vo_store_ower = voStoreOwer;
	}

	public int getVo_goods_click() {
		return vo_goods_click;
	}

	public void setVo_goods_click(int voGoodsClick) {
		vo_goods_click = voGoodsClick;
	}

	public int getVo_goods_collect() {
		return vo_goods_collect;
	}

	public void setVo_goods_collect(int voGoodsCollect) {
		vo_goods_collect = voGoodsCollect;
	}

	public String getVo_img_path() {
		return vo_img_path;
	}

	public void setVo_img_path(String voImgPath) {
		vo_img_path = voImgPath;
	}


	public String getVo_img_name() {
		return vo_img_name;
	}

	public void setVo_img_name(String voImgName) {
		vo_img_name = voImgName;
	}

	public String getVo_img_ext() {
		return vo_img_ext;
	}

	public void setVo_img_ext(String voImgExt) {
		vo_img_ext = voImgExt;
	}

	public Long getVo_city_id() {
		return vo_city_id;
	}

	public void setVo_city_id(Long voCityId) {
		vo_city_id = voCityId;
	}

	public String getVo_store_grade() {
		return vo_store_grade;
	}

	public void setVo_store_grade(String voStoreGrade) {
		vo_store_grade = voStoreGrade;
	}

	public int getVo_evalutes_num() {
		return vo_evalutes_num;
	}

	public void setVo_evalutes_num(int voEvalutesNum) {
		vo_evalutes_num = voEvalutesNum;
	}

	public int getVo_goods_inventory() {
		return vo_goods_inventory;
	}

	public void setVo_goods_inventory(int voGoodsInventory) {
		vo_goods_inventory = voGoodsInventory;
	}

	public String getVo_inventory_type() {
		return vo_inventory_type;
	}

	public void setVo_inventory_type(String voInventoryType) {
		vo_inventory_type = voInventoryType;
	}


	public String getVo_city_name() {
		return vo_city_name;
	}

	public void setVo_city_name(String voCityName) {
		vo_city_name = voCityName;
	}
	

	public String getVo_goods_status() {
		return vo_goods_status;
	}

	public void setVo_goods_status(String voGoodsStatus) {
		vo_goods_status = voGoodsStatus;
	}

	public String getVo_weixin_status() {
		return vo_weixin_status;
	}

	public void setVo_weixin_status(String voWeixinStatus) {
		vo_weixin_status = voWeixinStatus;
	}

	public String getVo_gc_id() {
		return vo_gc_id;
	}

	public void setVo_gc_id(String voGcId) {
		vo_gc_id = voGcId;
	}

	public String getVo_brand_id() {
		return vo_brand_id;
	}

	public void setVo_brand_id(String voBrandId) {
		vo_brand_id = voBrandId;
	}

	public Map<String, String> getVo_goods_specification() {
		return vo_goods_specification;
	}

	public void setVo_goods_specification(Map<String, String> voGoodsSpecification) {
		vo_goods_specification = voGoodsSpecification;
	}

	public Map<String, String> getVo_goods_type_property() {
		return vo_goods_type_property;
	}

	public void setVo_goods_type_property(Map<String, String> voGoodsTypeProperty) {
		vo_goods_type_property = voGoodsTypeProperty;
	}

	
}
