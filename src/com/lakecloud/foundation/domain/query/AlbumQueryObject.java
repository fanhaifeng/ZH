package com.lakecloud.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.query.QueryObject;

public class AlbumQueryObject extends QueryObject {
	public AlbumQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public AlbumQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
