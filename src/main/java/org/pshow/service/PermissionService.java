/**
 * 
 */
package org.pshow.service;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.pshow.common.page.Pagination;
import org.pshow.domain.Permission;

/**
 * @author Sin
 *
 */
@IocBean(args = { "refer:dao" })
public class PermissionService extends BaseService<Permission> {
	public PermissionService(Dao dao) {
		super(dao);
	}
	
	public Pagination getListByPager(Integer pageNumber, int pageSize) {
		return getObjListByPager(dao(), getPageNumber(pageNumber), pageSize,
				null, Permission.class);
	}
}
