/**
 * 
 */
package org.pshow.controller;

import java.util.List;

import org.pshow.domain.Permission;
import org.pshow.domain.Role;
import org.pshow.domain.User;
import org.pshow.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Sin
 *
 */
@Controller
@RequestMapping("/role")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@RequestMapping(method = RequestMethod.POST)
	public void create(Role role) {
		roleService.insert(role);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public void delete(String ids) {
		roleService.delete(ids);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public void update(Role role) {
		roleService.update(role);
	}

	@RequestMapping("/getUnselectedPermissionList")
	public List<Permission> getUnselectedPermissionList(Long roleId) {
		return roleService.getUnselectedPermissionList(roleId);
	}

	@RequestMapping("/getSelectedPermissionList")
	public List<Permission> getSelectedPermissionList(Long roleId) {
		return roleService.getSelectedPermissionList(roleId);
	}

	@RequestMapping("/updatePermission")
	public void updatePermission(Long roleId, String addPermissionIds,
			String removePermissionIds) {
		roleService.updatePermission(roleId, addPermissionIds,
				removePermissionIds);
	}

	@RequestMapping("/updateUserRelation")
	public void updateUserRelation(long roleId, List<User> addList,
			List<User> removeList) {
		roleService.updateUserRelation(roleId, addList, removeList);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Page<Role> list(
			@RequestParam(required = false, defaultValue = "0") int pageNumber,
			@RequestParam(required = false, defaultValue = "20") int pageSize) {
		return roleService.listRoleByPage(pageNumber, pageSize);
	}

	@RequestMapping(value = "/listAll", method = RequestMethod.GET)
	public List<Role> listAll() {
		return roleService.list();
	}

	@RequestMapping(value = "/listPermissionByPage", method = RequestMethod.GET)
	public Page<Permission> listPermissionByPage(Long roleId, String name,
			Integer pageNumber, int pageSize) {
		return roleService.listPermissionByPage(roleId, name, pageNumber,
				pageSize);
	}

	@RequestMapping(value = "/listUserByPage", method = RequestMethod.GET)
	public Page<User> listUserByPage(Long roleId, String name,
			Integer pageNumber, int pageSize) {
		return roleService.listUserByPage(roleId, name, pageNumber, pageSize);
	}
}
