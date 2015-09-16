package com.lakecloud.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "xconf")
public class XConf extends IdEntity {
	@Column(length = 128, nullable = false)
	private String xconfkey;
	@Column(length = 255)
	private String xconfvalue;

	public String getXconfkey() {
		return xconfkey;
	}

	public void setXconfkey(String xconfkey) {
		this.xconfkey = xconfkey;
	}

	public String getXconfvalue() {
		return xconfvalue;
	}

	public void setXconfvalue(String xconfvalue) {
		this.xconfvalue = xconfvalue;
	}
}
