package org.pshow.service;
import java.util.List;

import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.lang.Lang;
import org.nutz.service.IdEntityService;
import org.pshow.common.page.Pagination;


public class BaseService<T> extends IdEntityService<T> {

	public BaseService() {
		super();
	}

	public BaseService(Dao dao) {
		super(dao);
	}

	public Pagination getObjListByPager(Dao dao, Integer pageNumber, int pageSize, Condition cnd, Class<T> entityType) {
		pageNumber = getPageNumber(pageNumber);
		Pager pager = dao.createPager(pageNumber, pageSize);
		List<T> list = dao.query(entityType, cnd, pager);
		pager.setRecordCount(dao.count(entityType, cnd));
		Pagination pagination = new Pagination(pageNumber, pageSize, pager.getRecordCount(), list);
		return pagination;
	}
	
	public Pagination getListByPage(Integer pageNumber, int pageSize, Condition cnd, String tableName) {
		Dao dao = dao();
		pageNumber = getPageNumber(pageNumber);
		Pager pager = dao.createPager(pageNumber, pageSize);
		List<Record> list = dao.query(tableName, cnd, pager);
		pager.setRecordCount(dao.count(tableName, cnd));
		Pagination pagination = new Pagination(pageNumber, pageSize, pager.getRecordCount(), list);
		return pagination;
	}

	protected int getPageNumber(Integer pageNumber) {
		return Lang.isEmpty(pageNumber) ? 1 : pageNumber;
	}
}