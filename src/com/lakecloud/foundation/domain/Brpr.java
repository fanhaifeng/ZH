package com.lakecloud.foundation.domain;

public class Brpr {

	private String goodsId;//商品ID
	
	private String compname;//分公司名称
 
	private String brId;//分公司ID
	
	private String reprice;//指导价
	
	private String lowprice;//经销商最低价



	public String getBrId() {
		return brId;
	}

	public void setBrId(String brId) {
		this.brId = brId;
	}

	public String getReprice() {
		return reprice;
	}

	public void setReprice(String reprice) {
		this.reprice = reprice;
	}

	public void setCompname(String compname) {
		this.compname = compname;
	}

	public String getCompname() {
		return compname;
	}

	public void setLowprice(String lowprice) {
		this.lowprice = lowprice;
	}

	public String getLowprice() {
		return lowprice;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsId() {
		return goodsId;
	}
	
	
	
}
