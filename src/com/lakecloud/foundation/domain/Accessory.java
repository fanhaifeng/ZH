package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 系统附件管理类，用来管理系统所有附件信息，包括图片附件、rar附件等等
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "accessory")
public class Accessory extends IdEntity {
	private String name;// 附件名称
	private String path;// 存放路径
	private float size;// 附件大小
	private int width;// 宽度
	private int height;// 高度
	private String ext;// 扩展名，不包括.
	private String info;// 附件说明
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 附件对应的用户，可以精细化管理用户附件
	@ManyToOne(fetch = FetchType.LAZY)
	private Album album;// 图片对应的相册
	@OneToOne(mappedBy = "album_cover", fetch = FetchType.LAZY)
	private Album cover_album;//
	@ManyToOne(fetch = FetchType.LAZY)
	private SysConfig config;// 对应登录页左侧图片
	@OneToMany(mappedBy = "goods_main_photo")
	private List<Goods> goods_main_list = new ArrayList<Goods>();
	@OneToMany(mappedBy = "goods_main_photo")
	private List<GoodsDC> goodsDC_main_list = new ArrayList<GoodsDC>();
	@ManyToMany(mappedBy = "goods_photos")
	private List<Goods> goods_list = new ArrayList<Goods>();// 商品列表
	@ManyToMany(mappedBy = "goodsDC_photos")
	private List<GoodsDC> goodsDC_list = new ArrayList<GoodsDC>();// 商品列表
	
	public List<Goods> getGoods_main_list() {
		return goods_main_list;
	}

	public void setGoods_main_list(List<Goods> goods_main_list) {
		this.goods_main_list = goods_main_list;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public Album getCover_album() {
		return cover_album;
	}

	public void setCover_album(Album cover_album) {
		this.cover_album = cover_album;
	}

	public SysConfig getConfig() {
		return config;
	}

	public void setConfig(SysConfig config) {
		this.config = config;
	}

	public void setGoods_list(List<Goods> goods_list) {
		this.goods_list = goods_list;
	}

	public List<Goods> getGoods_list() {
		return goods_list;
	}

	public void setGoodsDC_list(List<GoodsDC> goodsDC_list) {
		this.goodsDC_list = goodsDC_list;
	}

	public List<GoodsDC> getGoodsDC_list() {
		return goodsDC_list;
	}

	public void setGoodsDC_main_list(List<GoodsDC> goodsDC_main_list) {
		this.goodsDC_main_list = goodsDC_main_list;
	}

	public List<GoodsDC> getGoodsDC_main_list() {
		return goodsDC_main_list;
	}

}
