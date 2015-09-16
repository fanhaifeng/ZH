package com.lakecloud.core.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.core.tools.CommUtil;
/**
 * 
* <p>Title: QueryObject.java</p>

* <p>Description:基础查询对象，封装基础查询条件，包括页大小、当前页、排序信息等 </p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
 */
public class QueryObject implements IQueryObject {

	protected Integer pageSize = 12;

	protected Integer currentPage = 0;

	protected String orderBy;

	protected String orderType;

	protected Map params = new HashMap();

	protected String queryString = "1=1";

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	protected void setParams(Map params) {
		this.params = params;
	}

	public String getOrderType() {
		return orderType;
	}

	public Integer getCurrentPage() {
		if (currentPage == null) {
			currentPage = -1;
		}
		return currentPage;
	}

	public String getOrder() {
		return orderType;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public Integer getPageSize() {
		if (pageSize == null) {
			pageSize = -1;
		}
		return pageSize;
	}

	public QueryObject() {

	}

	public QueryObject(String currentPage, ModelAndView mv, String orderBy,
			String orderType) {
		if (currentPage != null && !currentPage.equals(""))
			this.setCurrentPage(CommUtil.null2Int(currentPage));
		this.setPageSize(this.pageSize);
		if (orderBy == null || orderBy.equals("")) {
			this.setOrderBy("addTime");
			mv.addObject("orderBy", "addTime");
		} else {
			this.setOrderBy(orderBy);
			mv.addObject("orderBy", orderBy);
		}
		if (orderType == null || orderType.equals("desc")) {
			this.setOrderType("desc");
			mv.addObject("orderType", "desc");
		} else {
			this.setOrderType(orderType);
			mv.addObject("orderType", orderType);
		}
	}

	public PageObject getPageObj() {
		PageObject pageObj = new PageObject();
		pageObj.setCurrentPage(this.getCurrentPage());
		pageObj.setPageSize(this.getPageSize());
		if (this.currentPage == null || this.currentPage <= 0) {
			pageObj.setCurrentPage(1);
		}
		return pageObj;
	}

	public String getQuery() {
		customizeQuery();
		return queryString + orderString();
	}

	protected String orderString() {
		String orderString = " ";
		if (this.getOrderBy() != null && !"".equals(getOrderBy())) {
			orderString += " order by obj." + this.getOrderBy();
		}
		if (this.getOrderType() != null && !"".equals(getOrderType())) {
			orderString = orderString + " " + getOrderType();
		}
		return orderString;
	}

	public Map getParameters() {
		return this.params;
	}

	public IQueryObject addQuery(String field, SysMap para, String expression) {
		if (field != null && para != null) {
			queryString += " and " + field + " " + handleExpression(expression)
					+ ":" + para.getKey().toString();
			params.put(para.getKey(), para.getValue());
		}
		return this;
	}
	
	/**
	 * not in 或 in 仅限其他表处理 
	 * @param field
	 * @param query 其他表条件
	 * @param para
	 * @param expression
	 * @return
	 */
	public IQueryObject addINQueryStart(String field,String query ,SysMap para, String expression) {
		if (field != null && para != null) {
			if(query!=null && !"".equals(query)){
				queryString += " and " + field +" ( "+query+" "+ handleExpression(expression)
				+ ":" + para.getKey().toString();
			}else{
				queryString += " and " + field +" ( "+ ":" + para.getKey().toString();
			}
			params.put(para.getKey(), para.getValue());
		}
		return this;
	}
	
	/**
	 * in 或not in 结束符号
	 * @return
	 */
	public IQueryObject addINQueryEnd() {
		queryString +=" )";
		return this;
	}

	public IQueryObject addQuery(String field, SysMap para, String expression,
			String logic) {
		if (field != null && para != null) {
			queryString += " " + logic + " " + field + " "
					+ handleExpression(expression) + ":"
					+ para.getKey().toString();
			params.put(para.getKey(), para.getValue());
		}
		return this;
	}

	public IQueryObject addQuery(String scope, Map paras) {
		if (scope != null) {
			if (scope.trim().indexOf("and") == 0
					|| scope.trim().indexOf("or") == 0) {
				queryString += " " + scope;
			} else
				queryString += " and " + scope;
			if (paras != null && paras.size() > 0) {
				for (Object key : paras.keySet()) {
					params.put(key, paras.get(key));
				}
			}
		}
		return this;
	}

	@Override
	public IQueryObject addQuery(String para, Object obj, String field,
			String expression) {
		// TODO Auto-generated method stub
		if (field != null && para != null) {
			queryString += " and :" + para + " " + expression + " " + field;
			params.put(para, obj);
		}
		return this;
	}

	public IQueryObject addQuery(String para, Object obj, String field,
			String expression, String logic) {
		if (field != null && para != null) {
			queryString += " " + logic + " :" + para + " " + expression + " "
					+ field;
			params.put(para, obj);
		}
		return this;
	}

	private String handleExpression(String expression) {
		if (expression == null)
			return "=";
		else
			return expression;
	}

	public void customizeQuery() {

	}

}
