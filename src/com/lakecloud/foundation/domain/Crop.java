package com.lakecloud.foundation.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.omg.CORBA.PRIVATE_MEMBER;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * 用户作物信息
 * @author Administrator
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "crop")
public class Crop extends IdEntity {
	private String care;//关心作物
	private String plant;//种植作物
	private String cul_area; //种植面积	
	
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
}
