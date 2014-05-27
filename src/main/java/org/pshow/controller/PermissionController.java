/**
 * 
 */
package org.pshow.controller;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.pshow.common.page.Pagination;
import org.pshow.service.PermissionService;

/**
 * @author Sin
 *
 */
@IocBean
@At("/permission")
public class PermissionController {
	@Inject
	private PermissionService permissionService;
	
	@At
	public Pagination list(Integer pageNumber, int pageSize) {
		return permissionService.getListByPager(pageNumber, pageSize);
	}
}
