package org.pshow.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.pshow.domain.Permission;
import org.pshow.domain.Role;
import org.pshow.domain.User;
import org.pshow.repository.DynamicSpecifications;
import org.pshow.repository.RoleDao;
import org.pshow.repository.SearchFilter;
import org.pshow.repository.SearchFilter.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
	private final static Logger log = Logger.getLogger(RoleService.class);
	@Autowired
	private RoleDao roleDao;

	public List<Role> list() {
		return roleDao.findAll(DynamicSpecifications.bySearchFilter(Role.class,
				new SearchFilter[] {}));
	}

	public void save(Role role) {
		Specification<Role> searchFilter = DynamicSpecifications
				.bySearchFilter(Role.class, new SearchFilter("name",
						Operator.EQ, role.getName()));
		long count = roleDao.count(searchFilter);
		if (count > 0) {
			throw new IllegalArgumentException(String.format(
					"role name[%s] already exists!", role.getName()));
		}
		insert(role);
	}

	public void insert(final Role role) {
		log.info(String.format("insert role[%s]", role));
		roleDao.save(role);
		// if (role.getPermissions() != null) {
		// newRole.setPermissions(role.getPermissions());
		// dao().insertRelation(newRole, "permissions");
		// }
		// if (role.getUsers() != null) {
		// newRole.setUsers(role.getUsers());
		// dao().insertRelation(newRole, "users");
		// }
	}

	public void delete(final String ids) {
		log.info(String.format("delete role[%s]", ids));
		String[] idArray = ids.split(",");
		for (String id : idArray) {
			Long lId = Long.valueOf(id);

			roleDao.delete(lId);
			// dao().clear("ecm_role_permission", Cnd.where("roleid", "=",
			// lId));
			// dao().clear("ecm_user_role", Cnd.where("roleid", "=", lId));
		}
	}

	public Role view(Long id) {
		// return dao().fetchLinks(fetch(id), "permissions");
		return roleDao.findOne(id);
	}

	public void update(final Role role) {
		log.info(String.format("update role[%s]", role));
		// FieldFilter.create(User.class, "^locked|description$", true).run(
		// new Atom() {
		// public void run() {
		// dao().update(role);
		// }
		// });
		Role one = roleDao.findOne(role.getId());
		one.setDescription(role.getDescription());
	}

	public List<Permission> getUnselectedPermissionList(Long roleId) {
//		Sql sql = Sqls
//				.queryEntity("select * from ecm_permission r where not exists (select 1 from ecm_role_permission eur where eur.permissionid = r.id and eur.roleid = @roleId)");
//		sql.params().set("roleId", roleId);
//		sql.setEntity(dao().getEntity(Permission.class));
//		dao().execute(sql);
		return null;//sql.getList(Permission.class);
	}

	public List<Permission> getSelectedPermissionList(Long roleId) {
		Role role = roleDao.findOne(roleId);
		// Role role = dao().fetchLinks(fetch(roleId), "permissions");
		return role.getPermissions();
	}

	public void updatePermission(final Long roleId,
			final String addPermissionIds, final String removePermissionIds) {
		log.info(String
				.format("updatePermission roleId[%s], addPermissionIds[%s], removePermissionIds[%s]",
						roleId, addPermissionIds, removePermissionIds));
		Role role = roleDao.findOne(roleId);
		if (StringUtils.isNotEmpty(addPermissionIds)) {
			String[] idArray = addPermissionIds.split(",");
			for (String id : idArray) {
				Long lId = Long.valueOf(id);
				Permission permission = new Permission();
				permission.setId(lId);
				role.getPermissions().add(permission);
			}
		}
		if (StringUtils.isNotEmpty(removePermissionIds)) {
			String[] removeIdArray = removePermissionIds.split(",");
			log.debug("removeSize: " + removeIdArray.length);
			List<Permission> permissions = role.getPermissions();
			for (Permission permission : permissions) {
				for (String permission_id : removeIdArray) {
					if (Long.parseLong(permission_id) == permission.getId()) {
						permissions.remove(permission);
					}
				}
			}
		}
		// Trans.exec(new Atom() {
		// public void run() {
		// Role role = fetch(roleId);
		// if (StringUtils.isNotEmpty(addPermissionIds)) {
		// String[] idArray = addPermissionIds.split(",");
		// List<Permission> permissionList = new ArrayList<Permission>();
		// for (String id : idArray) {
		// Long lId = Long.valueOf(id);
		// Permission permission = new Permission();
		// permission.setId(lId);
		// permissionList.add(permission);
		// }
		// role.setPermissions(permissionList);
		// dao().insertRelation(role, "permissions");
		// }
		// if (StringUtils.isNotEmpty(removePermissionIds)) {
		// String[] removeIdArray = removePermissionIds.split(",");
		// log.debug("removeSize: " + removeIdArray.length);
		// dao().clear(
		// "ecm_role_permission",
		// Cnd.where("roleid", "=", roleId)
		// .and("permissionid",
		// "in",
		// Lang.array2array(removeIdArray,
		// Long.class)));
		// }
		// }
		// });
	}

	public Role fetchByName(String name) {
		Specification<Role> filter = DynamicSpecifications.bySearchFilter(
				Role.class, new SearchFilter("name", Operator.EQ, name));
		return roleDao.findOne(filter);
	}

	public List<String> getPermissionNameList(Role role) {
		List<Permission> permissions = roleDao.findOne(role.getId())
				.getPermissions();
		List<String> permissionNameList = new ArrayList<String>();
		for (Permission permission : permissions) {
			permissionNameList.add(permission.getName());
		}
		return permissionNameList;
	}

	public void updateUserRelation(final long roleId, final List<User> addList,
			final List<User> removeList) {
		Role role = roleDao.findOne(roleId);
		if (removeList != null && removeList.size() > 0) {
			role.getUsers().removeAll(removeList);
		}
		if (addList != null && addList.size() > 0) {
			role.getUsers().addAll(addList);
		}
		// Trans.exec(new Atom() {
		// public void run() {
		// Dao dao = dao();
		// Role role = new Role();
		// role.setId(roleId);
		// if (!Lang.isEmpty(removeList)) {
		// dao.clearLinks(role, "users");
		// }
		// if (!Lang.isEmpty(addList)) {
		// role.setUsers(addList);
		// dao.insertRelation(role, "users");
		// }
		// }
		// });
	}

	public Page<Role> listRoleByPage(int pageNumber, int pageSize) {
		Pageable pageable = new PageRequest(pageNumber, pageSize);
		return roleDao.findAll(pageable);
//		Cnd cnd = null;
//		if (!Lang.isEmpty(roleId)) {
//			cnd = Cnd.where("id", "=", roleId);
//		} else if (!Lang.isEmpty(name)) {
//			cnd = Cnd.where("name", "LIKE", "%" + name + "%");
//		}
//		return getObjListByPager(dao(), pageNumber, pageSize, cnd, Role.class);
	}

	public Page<Permission> listPermissionByPage(Long roleId, String name,
			Integer pageNumber, int pageSize) {
//		Specification<Permission> spec = DynamicSpecifications.bySearchFilter(
//				Permission.class, new SearchFilter("id", Operator.EQ, roleId),
//				new SearchFilter("name", Operator.LIKE, name));
//		Pageable pageable = new PageRequest(pageNumber, pageSize);
		return null;
//		Cnd cnd = null;
//		if (!Lang.isEmpty(roleId)) {
//			cnd = Cnd.where("roleid", "=", roleId);
//		} else if (!Lang.isEmpty(name)) {
//			cnd = Cnd.where("name", "LIKE", "%" + name + "%");
//		}
//		return getListByPage(pageNumber, pageSize, cnd, "ecm_role_permission");
	}

	public Page<User> listUserByPage(Long roleId, String name,
			Integer pageNumber, int pageSize) {
		return null;
//		Cnd cnd = null;
//		if (!Lang.isEmpty(roleId)) {
//			cnd = Cnd.where("roleid", "=", roleId);
//		} else if (!Lang.isEmpty(name)) {
//			cnd = Cnd.where("name", "LIKE", "%" + name + "%");
//		}
//		return getListByPage(pageNumber, pageSize, cnd, "ecm_user_role");
	}

	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}
}
