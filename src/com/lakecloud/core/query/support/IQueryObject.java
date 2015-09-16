package com.lakecloud.core.query.support;

import java.util.Map;

import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.query.PageObject;

/**
 * 
* <p>Title: IQueryObject.java</p>

* <p>Description: 查询对象接口</p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
 */
public interface IQueryObject {
	/**
	 * 得到一个查询条件语句,其中参数用命名参数title=:title and id=:id
	 * 
	 * @return 返回的条件语句
	 */
	String getQuery();

	/**
	 * 得到查询语句需要的参数对象列表
	 * 
	 * @return 参数对象列表
	 */
	Map getParameters();

	/**
	 * 得到关于分页页面信息对象
	 * 
	 * @return 包装分页信息对象
	 */
	PageObject getPageObj();

	/**
	 * 批量往查询对象中添加查询选项，可以是一个完整的具体查询语句，如title='111' and
	 * status>0，也可以是包括命名参数的的语句，如title=:title and status>0 <code>
	 * NewsDocQueryObject query=new NewsDocQueryObject();
	 * Map map=new HashMap();
	 * map.put("title","xxxx");
	 * query.addQuery("title=:title",map);
	 * </code>
	 * 
	 * @param scope
	 *            查询条件
	 * @param paras
	 *            参数值，如果
	 */
	IQueryObject addQuery(String scope, Map paras);

	/**
	 * 往查询条件中逐个加入查询条件
	 * 
	 * @param field
	 *            属性名称
	 * @param para
	 *            参数值
	 * @param expression
	 *            表达式,如果为null，则使用=。
	 */
	IQueryObject addQuery(String field, SysMap para, String expression);

	/**
	 * 往查询条件中逐个加入集合查询条件，包括 member of 等
	 * 
	 * @param para
	 *            集合参数
	 * @param obj
	 *            需要传递的参数值
	 * @param field
	 *            查询参数
	 * @param expression
	 *            集合表达式 如 member of
	 * @return
	 */
	IQueryObject addQuery(String para, Object obj, String field,
			String expression);

}
