package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 个人动态管理类，商城用户可发布自己的动态、查看好友的动态以及对好友动态的评论、赞、转发（分享）
 * @since V1.3
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net hezeng 20130725
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "dynamic")
public class Dynamic extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods goods;// 动态中对应的商品类
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;// 动态中对应的店铺类
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 发布动态的用户
	/**
	 * @info 数据库移植兼容性修改
	 * @author chenyong
	 * @see
	 * 	@Column(columnDefinition = "bit default false")
	 *  private boolean locked;// 是否加密，加密后别人不可见，默认为否，
	 */
	@Column(name="locked",columnDefinition = "int default 0")
	private boolean locked;
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory img;// 动态中分享的图片
	/**
	 * @info 数据库移植兼容性修改
	 * @author chenyong
	 * @see
	 * 	修改@Column(columnDefinition = "LongText")
	 */
	@Lob
	@Column(length = 1048576)
	private String content;// 动态内容
	@Column(columnDefinition = "int default 0")
	private int turnNum;// 转发次数
	@Column(columnDefinition = "int default 0")
	private int discussNum;// 评论次数
	@Column(columnDefinition = "int default 0")
	private int praiseNum;// 赞次数
	@ManyToOne(fetch = FetchType.LAZY)
	private Dynamic dissParent;// 被评论的动态（父类）
	@ManyToOne(fetch = FetchType.LAZY)
	private Dynamic turnParent;// 被转发的动态（父类）
	// 评论子类
	@OneToMany(mappedBy = "dissParent", cascade = CascadeType.REMOVE)
	List<Dynamic> childs = new ArrayList<Dynamic>();

	
	/**
	 * @info 数据库移植兼容性修改
	 * @author chenyong
	 * @see
	 * 	@Column(columnDefinition = "bit default true")
	 *  private boolean display;// 管理员后台设置是否可见，默认为可见，
	 */
	@Column(columnDefinition = "SMALLINT DEFAULT 1")
	private boolean display;
	
	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public Accessory getImg() {
		return img;
	}

	public void setImg(Accessory img) {
		this.img = img;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public List<Dynamic> getChilds() {
		return childs;
	}

	public void setChilds(List<Dynamic> childs) {
		this.childs = childs;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getTurnNum() {
		return turnNum;
	}

	public void setTurnNum(int turnNum) {
		this.turnNum = turnNum;
	}

	public int getDiscussNum() {
		return discussNum;
	}

	public void setDiscussNum(int discussNum) {
		this.discussNum = discussNum;
	}

	public int getPraiseNum() {
		return praiseNum;
	}

	public void setPraiseNum(int praiseNum) {
		this.praiseNum = praiseNum;
	}

	public Dynamic getDissParent() {
		return dissParent;
	}

	public void setDissParent(Dynamic dissParent) {
		this.dissParent = dissParent;
	}

	public Dynamic getTurnParent() {
		return turnParent;
	}

	public void setTurnParent(Dynamic turnParent) {
		this.turnParent = turnParent;
	}

}
