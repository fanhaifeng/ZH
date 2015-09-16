package com.lakecloud.core.base;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.lakecloud.core.tools.JdbcBaseTemplate;

/**
 * 
* <p>Title: GenericDAO.java</p>

* <p>Description:统所有数据库操作的基类，采用JDK5泛型接口完成POJO类型注入</p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.0
 */
public class GenericDAO<T> extends JdbcBaseTemplate<T> implements com.lakecloud.core.dao.IGenericDAO<T> {
	protected Class<T> entityClass;// DAO所管理的Entity类型
	@Autowired
	@Qualifier("genericEntityDao")
	private GenericEntityDao geDao;

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public GenericEntityDao getGeDao() {
		return geDao;
	}

	public void setGeDao(GenericEntityDao geDao) {
		this.geDao = geDao;
	}

	public GenericDAO() {
		this.entityClass = (Class<T>) ((ParameterizedType) (this.getClass()
				.getGenericSuperclass())).getActualTypeArguments()[0];
		// System.out.println("执行GenericDAO不带参数构造方法！");
		// System.out.println("注入类：" + this.entityClass);
	}

	public GenericDAO(Class<T> type) {
		this.entityClass = type;
	}

	public int batchUpdate(String jpql, Object[] params) {
		// TODO Auto-generated method stub
		return this.geDao.batchUpdate(jpql, params);
	}
	
	/**
     * 批量更新
     * @param publishSql
     */
    public void batchInsert(String[] sqlList){
        super.batchUpdate(sqlList);
    }

	public List executeNamedQuery(String queryName, Object[] params, int begin,
			int max) {
		// TODO Auto-generated method stub
		return this.geDao.executeNamedQuery(queryName, params, begin, max);
	}

	public List executeNativeNamedQuery(String nnq) {
		// TODO Auto-generated method stub
		return this.geDao.executeNativeNamedQuery(nnq);
	}

	public List executeNativeQuery(String nnq, Object[] params, int begin,
			int max) {
		// TODO Auto-generated method stub
		return this.geDao.executeNativeQuery(nnq, params, begin, max);
	}

	public int executeNativeSQL(String nnq) {
		// TODO Auto-generated method stub
		return this.geDao.executeNativeSQL(nnq);
	}

	public List find(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.getGeDao()
				.find(this.entityClass, query, params, begin, max);
	}

	public void flush() {
		// TODO Auto-generated method stub
		this.geDao.flush();
	}

	public T get(Serializable id) {
		// TODO Auto-generated method stub
		return (T) this.getGeDao().get(this.entityClass, id);
	}

	public T getBy(String propertyName, Object value) {
		// TODO Auto-generated method stub
		return (T) this.getGeDao().getBy(this.entityClass, propertyName, value);
	}

	public List query(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.getGeDao().query(query, params, begin, max);
	}

	public void remove(Serializable id) {
		// TODO Auto-generated method stub
		this.getGeDao().remove(this.entityClass, id);
	}

	public void save(Object newInstance) {
		// TODO Auto-generated method stub
		this.getGeDao().save((T) newInstance);
	}

	public void update(Object transientObject) {
		// TODO Auto-generated method stub
		this.getGeDao().update((T) transientObject);
	}

	@Override
	public List executeNativeQuery(String nnq, Map sparams, int begin, int max) {
		// TODO Auto-generated method stub
		return this.getGeDao().executeNativeQuery(nnq,sparams,begin,max);
	}
	

	
}
