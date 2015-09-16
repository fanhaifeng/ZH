package com.lakecloud.foundation.domain.query;

import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.query.QueryObject;

public class ThirdPaymentQueryObject extends QueryObject {
	public ThirdPaymentQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}

	public ThirdPaymentQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
