package com.lakecloud.foundation.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.annotation.Lock;
import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * 
* <p>Title: Audit.java</p>

* <p>Description:用户注册审核</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-5-7

* @version LakeCloud_C2C 1.4
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "audit")
public class Audit extends IdEntity {
	private static final long serialVersionUID = 8026813053768023527L;

	@Column(length=15) 
	private String mobile;// 手机移动电话（改：推荐用户手机号--20150910）
	
	@Lock
	@Column(length=40) 
	private String password;// 密码
	
	@Column(length=15) 
	private String telephone;// 电话号码

	@Column(length=20) 
	private String trueName;// 真实姓名
	
	@Column(length=20) 
	private String userName;// 用户名称

	@OneToOne
	private Area area;// 所属区域
	
	@Column(length=255) 
	private String store_name;//店铺名称
	
	@OneToOne
	private Area store_area;// 店铺地区
	
	@OneToOne
	private StoreClass store_class;//店铺分类
	
	@Column(length=255) 
	private String store_addr;//店铺地址
	
	@Column(length=18) 
	private String id_card; //身份证
	
	@Column(length=2) 
	private String status; //状态 0尚未审核 1 审核通过 2 审核不通过
	
	private int check_person; //审核人ID
	private Date check_time;  //审核时间
	
	private String option;//意见
	
	private String care;//关心作物
	private String plant;//种植作物
	
	@OneToOne
	private Branch branch;// 用户分公司
	
	private String branch_code;//分公司代号
	
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory file1;// 认证身份证正面
	
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory file2;// 认证身份证反面
	
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory file3;// 店铺营业执照
	

	
	public Accessory getFile1() {
		return file1;
	}

	public void setFile1(Accessory file1) {
		this.file1 = file1;
	}

	public Accessory getFile2() {
		return file2;
	}

	public void setFile2(Accessory file2) {
		this.file2 = file2;
	}

	public Accessory getFile3() {
		return file3;
	}

	public void setFile3(Accessory file3) {
		this.file3 = file3;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public String getBranch_code() {
		return branch_code;
	}

	public void setBranch_code(String branch_code) {
		this.branch_code = branch_code;
	}

	private  String cul_area; //种植面积
	
	
	public String getCare() {
		return care;
	}

	public void setCare(String care) {
		this.care = care;
	}

	public String getPlant() {
		return plant;
	}

	public void setPlant(String plant) {
		this.plant = plant;
	}


	public String getCul_area() {
		return cul_area;
	}

	public void setCul_area(String cul_area) {
		this.cul_area = cul_area;
	}

	public StoreClass getStore_class() {
		return store_class;
	}

	public void setStore_class(StoreClass store_class) {
		this.store_class = store_class;
	}

	public Area getStore_area() {
		return store_area;
	}
	
	public void setStore_area(Area store_area) {
		this.store_area = store_area;
	}
	
	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getTelephone() {
		return telephone;
	}
	
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getTrueName() {
		return trueName;
	}
	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
	public String getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	public String getStore_addr() {
		return store_addr;
	}
	public void setStore_addr(String store_addr) {
		this.store_addr = store_addr;
	}
	public String getId_card() {
		return id_card;
	}
	public void setId_card(String id_card) {
		this.id_card = id_card;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getCheck_person() {
		return check_person;
	}
	public void setCheck_person(int check_person) {
		this.check_person = check_person;
	}
	public Date getCheck_time() {
		return check_time;
	}
	public void setCheck_time(Date check_time) {
		this.check_time = check_time;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
}
