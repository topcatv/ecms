/**
 * 
 */
package org.pshow.controller;

import java.util.List;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.pshow.common.page.Pagination;
import org.pshow.domain.Permission;
import org.pshow.domain.Role;
import org.pshow.domain.User;
import org.pshow.mvc.Result;
import org.pshow.mvc.SuccessResult;
import org.pshow.service.RoleService;

/**
 * @author Sin
 *
 */
@IocBean
@At("/role")
public class RoleController {
	
	@Inject
	private RoleService roleService;
	
	@At
	public Result create(Role role) {
		roleService.insert(role);
		return new SuccessResult();
	}
	
	@At
	public Result delete(Long roleId) {
		roleService.delete(roleId);
		return new SuccessResult();
	}
	
	@At
	public Result update(Role role) {
		roleService.update(role);
		return new SuccessResult();
	}
	
	@At
	public Result updatePermissionRelation(long roleId, List<Permission> addList, List<Permission> removeList) {
		roleService.updatePermissionRelation(roleId, addList, removeList);
		return new SuccessResult();
	}
	
	@At
	public Result updateUserRelation(long roleId, List<User> addList, List<User> removeList) {
		roleService.updateUserRelation(roleId, addList, removeList);
		return new SuccessResult();
	}

	@At
	public Pagination list(Long roleId, String name, Integer pageNumber, int pageSize) {
		return roleService.listRoleByPage(roleId, name, pageNumber, pageSize);
	}
	
	@At
	public List<Role> listAll() {
		return roleService.list();
	}
	
	@At
	public Pagination listPermissionByPage(Long roleId, String name, Integer pageNumber, int pageSize) {
		return roleService.listPermissionByPage(roleId, name, pageNumber, pageSize);
	}
	
	@At
	public Pagination listUserByPage(Long roleId, String name, Integer pageNumber, int pageSize) {
		return roleService.listUserByPage(roleId, name, pageNumber, pageSize);
	}
}
