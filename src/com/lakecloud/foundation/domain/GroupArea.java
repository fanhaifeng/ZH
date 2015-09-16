package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 团购区域,团购商品申请可以选择允许团购的区域
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "group_area")
public class GroupArea extends IdEntity {
	private String ga_name;// 区域名称
	private int ga_sequence;// 区域序号，升序排序
	@ManyToOne(fetch = FetchType.LAZY)
	private GroupArea parent;// 父级区域
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	@OrderBy(value = "ga_sequence asc")
	private List<GroupArea> childs = new ArrayList<GroupArea>();// 子集区域
	private int ga_level;// 区域层级，用在区域显示梯次

	public int getGa_level() {
		return ga_level;
	}

	public void setGa_level(int ga_level) {
		this.ga_level = ga_level;
	}

	public String getGa_name() {
		return ga_name;
	}

	public void setGa_name(String ga_name) {
		this.ga_name = ga_name;
	}

	public int getGa_sequence() {
		return ga_sequence;
	}

	public void setGa_sequence(int ga_sequence) {
		this.ga_sequence = ga_sequence;
	}

	public GroupArea getParent() {
		return parent;
	}

	public void setParent(GroupArea parent) {
		this.parent = parent;
	}

	public List<GroupArea> getChilds() {
		return childs;
	}

	public void setChilds(List<GroupArea> childs) {
		this.childs = childs;
	}

}
