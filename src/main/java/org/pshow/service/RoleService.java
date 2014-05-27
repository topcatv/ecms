package org.pshow.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;
import org.pshow.common.page.Pagination;
import org.pshow.domain.Permission;
import org.pshow.domain.Role;
import org.pshow.domain.User;

@IocBean(args = { "refer:dao" })
public class RoleService extends BaseService<Role> {

	public RoleService(Dao dao) {
		super(dao);
	}

	public List<Role> list() {
		return query(null, null);
	}

	public void insert(final Role role) {
		Trans.exec(new Atom(){
			public void run(){
				Role newRole = dao().insert(role);
				newRole.setPermissions(role.getPermissions());
				dao().insertRelation(newRole, "permissions");
				newRole.setUsers(role.getUsers());
				dao().insertRelation(newRole, "users");
			}
		});
	}

	public void delete(final Long id) {
		Trans.exec(new Atom(){
			public void run(){
				dao().delete(Role.class, id);
				dao().clear("ecm_role_permission", Cnd.where("roleid", "=", id));
				dao().clear("ecm_user_role", Cnd.where("roleid", "=", id));
			}
		});
	}
	public Role view(Long id) {
		return dao().fetchLinks(fetch(id), "permissions");
	}

	public void update(Role role) {
		dao().updateWith(role, "permissions");
	}

	public Role fetchByName(String name) {
		return fetch(Cnd.where("name", "=", name));
	}

	public List<String> getPermissionNameList(Role role) {
		dao().fetchLinks(role, "permissions");
		List<String> permissionNameList = new ArrayList<String>();
		for (Permission permission : role.getPermissions()) {
			permissionNameList.add(permission.getName());
		}
		return permissionNameList;
	}

	@Deprecated
	public void updatePermissionRelation(final Role role, final List<Permission> perms) {
		Trans.exec(new Atom(){
			public void run(){
				dao().clear("ecm_role_permission", Cnd.where("roleid", "=", role.getId()));
				dao().update(role);
				if (!Lang.isEmpty(perms)) {
					role.setPermissions(perms);
					dao().insertRelation(role, "permissions");
				}
			}
		});
	}
	
	public void updatePermissionRelation(final long roleId, final List<Permission> addList, final List<Permission> removeList) {
		Trans.exec(new Atom(){
			public void run(){
				Dao dao = dao();
				Role role = new Role();
				role.setId(roleId);
				if (!Lang.isEmpty(removeList)) {
					dao.clearLinks(role, "permissions");
				}
				if (!Lang.isEmpty(addList)) {
					role.setPermissions(addList);
					dao.insertRelation(role, "permissions");
				}
			}
		});
	}
	
	public void updateUserRelation(final long roleId, final List<User> addList, final List<User> removeList) {
		Trans.exec(new Atom(){
			public void run(){
				Dao dao = dao();
				Role role = new Role();
				role.setId(roleId);
				if (!Lang.isEmpty(removeList)) {
					dao.clearLinks(role, "users");
				}
				if (!Lang.isEmpty(addList)) {
					role.setUsers(addList);
					dao.insertRelation(role, "users");
				}
			}
		});
	}

	public Map<Long, String> map() {
		Map<Long, String> map = new HashMap<Long, String>();
		List<Role> roles = query(null, null);
		for (Role role : roles) {
			map.put(role.getId(), role.getName());
		}
		return map;
	}

	public void addPermission(Long roleId, Long permissionId) {
		dao().insert("ecm_role_permission", Chain.make("roleid", roleId).add("permissionid", permissionId));
	}

	public void removePermission(Long roleId, Long permissionId) {
		dao().clear("ecm_role_permission", Cnd.where("roleid", "=", roleId).and("permissionid", "=", permissionId));
	}

	public Pagination listRoleByPage(Long roleId, String name, Integer pageNumber, int pageSize) {
		Cnd cnd = null;
		if (!Lang.isEmpty(roleId)) {
			cnd = Cnd.where("id", "=", roleId);
		} else if (!Lang.isEmpty(name)) {
			cnd = Cnd.where("name", "LIKE", "%" + name + "%");
		}
		return getObjListByPager(dao(), pageNumber, pageSize, cnd, Role.class);
	}
	
	@Deprecated
	public List<Permission> listPermission(Long roleId, String name, Integer pageNumber, int pageSize) {
		Role role = dao().fetchLinks(dao().fetch(Role.class, roleId), "permissions", Cnd.where("name", "LIKE", name));
		return role.getPermissions();
	}
	
	public Pagination listPermissionByPage(Long roleId, String name, Integer pageNumber, int pageSize) {
		Cnd cnd = null;
		if (!Lang.isEmpty(roleId)) {
			cnd = Cnd.where("roleid", "=", roleId);
		} else if (!Lang.isEmpty(name)) {
			cnd = Cnd.where("name", "LIKE", "%" + name + "%");
		}
		return getListByPage(pageNumber, pageSize, cnd, "ecm_role_permission");
	}
	
	public Pagination listUserByPage(Long roleId, String name, Integer pageNumber, int pageSize) {
		Cnd cnd = null;
		if (!Lang.isEmpty(roleId)) {
			cnd = Cnd.where("roleid", "=", roleId);
		} else if (!Lang.isEmpty(name)) {
			cnd = Cnd.where("name", "LIKE", "%" + name + "%");
		}
		return getListByPage(pageNumber, pageSize, cnd, "ecm_user_role");
	}
}
