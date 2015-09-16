package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.annotation.Lock;
import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/*
UPDATE LAKECLOUD_AREA SET BRANCH_ID = 2  WHERE ID= 4522294;
UPDATE LAKECLOUD_AREA SET BRANCH_ID = 2  WHERE ID= 4522265;

UPDATE LAKECLOUD_AREA SET BRANCH_ID = 3  WHERE PARENT_ID= 4524292 AND LEVEL=1;
UPDATE LAKECLOUD_AREA SET BRANCH_ID = 4  WHERE PARENT_ID= 4524431 AND LEVEL=1;
UPDATE LAKECLOUD_AREA SET BRANCH_ID = 5  WHERE PARENT_ID= 4523850 AND LEVEL=1;
UPDATE LAKECLOUD_AREA SET BRANCH_ID = 0  WHERE PARENT_ID= 4522868 AND LEVEL=1;
UPDATE LAKECLOUD_AREA SET BRANCH_ID = 2  WHERE PARENT_ID= 4523655 AND LEVEL=1;

*/

/**
 * 
* <p>Title: Audit.java</p>

* <p>Description:分公司</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-5-7

* @version LakeCloud_C2C 1.4
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "branch")
public class Branch extends IdEntity {
	
	@Column(length=255) 
	private String name;//分公司名称
	
	private String code;//分公司代码
	
	@OneToMany(mappedBy = "branch")
	private List<Area> areaList = new ArrayList<Area>();//一个分公司对应多个区域

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<Area> getAreaList() {
		return areaList;
	}

	public void setAreaList(List<Area> areaList) {
		this.areaList = areaList;
	}
}
