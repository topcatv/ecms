/**
 * 
 */
package org.pshow.controller;

import javax.jcr.RepositoryException;

import org.pshow.domain.Permission;
import org.pshow.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Sin
 *
 */
@Controller
@RequestMapping("/permission")
public class PermissionController {
	@Autowired
	private PermissionService permissionService;

	@RequestMapping("/list")
	public Page<Permission> list(Integer pageNumber, int pageSize) {
		return permissionService.getListByPager(pageNumber, pageSize);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/authorize")
	public void authorize(int[] uids, String[] cids, int permission)
			throws RepositoryException {
		permissionService.authorize(uids, cids, permission);
	}
}
