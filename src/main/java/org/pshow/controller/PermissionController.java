/**
 * 
 */
package org.pshow.controller;

import javax.jcr.RepositoryException;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.POST;
import org.pshow.common.page.Pagination;
import org.pshow.mvc.Result;
import org.pshow.mvc.SuccessResult;
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
	
	@At("/authorize")
	@POST
	public Result authorize(int[] uids, String[] cids, int permission ) throws RepositoryException {
		permissionService.authorize(uids, cids, permission);
		return new SuccessResult();
	}
}
