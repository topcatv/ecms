package org.pshow.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
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
	private final static Logger log = Logger.getLogger(RoleService.class);

	public RoleService(Dao dao) {
		super(dao);
	}

	public List<Role> list() {
		return query(null, null);
	}
	
	public void save(Role role) {
		int count = dao().count(User.class, Cnd.where("name", "=", role.getName()));
		if (count > 0) {
			throw new IllegalArgumentException(String.format("role name[%s] already exists!", role.getName()));
		}
		insert(role);
	}

	public void insert(final Role role) {
		log.info(String.format("insert role[%s]", role));
		Trans.exec(new Atom(){
			public void run(){
				Role newRole = dao().insert(role);
				if (role.getPermissions() != null) {
					newRole.setPermissions(role.getPermissions());
					dao().insertRelation(newRole, "permissions");
				}
				if (role.getUsers() != null) {
					newRole.setUsers(role.getUsers());
					dao().insertRelation(newRole, "users");
				}
			}
		});
	}

	public void delete(final String ids) {
		log.info(String.format("delete role[%s]", ids));
		Trans.exec(new Atom(){
			public void run(){
				String[] idArray = ids.split(",");
				for (String id : idArray) {
					Long lId = Long.valueOf(id);
					
					dao().delete(Role.class, lId);
					dao().clear("ecm_role_permission", Cnd.where("roleid", "=", lId));
					dao().clear("ecm_user_role", Cnd.where("roleid", "=", lId));
				}
			}
		});
	}
	public Role view(Long id) {
		return dao().fetchLinks(fetch(id), "permissions");
	}

	public void update(final Role role) {
		log.info(String.format("update role[%s]", role));
		FieldFilter.create(User.class,"^locked|description$", true).run(new Atom(){
		    public void run(){
		    	dao().update(role);
		    }
		});
	}
	
	public List<Permission> getUnselectedPermissionList(Long roleId) {
		Sql sql = Sqls.queryEntity("select * from ecm_permission r where not exists (select 1 from ecm_role_permission eur where eur.permissionid = r.id and eur.roleid = @roleId)");
		sql.params().set("roleId", roleId);
		sql.setEntity(dao().getEntity(Permission.class));
		dao().execute(sql);
		return sql.getList(Permission.class);
	}

	public List<Permission> getSelectedPermissionList(Long roleId) {
		Role role = dao().fetchLinks(fetch(roleId), "permissions");
		return role.getPermissions();
	}
	
	public void updatePermission(final Long roleId, final String addPermissionIds, final String removePermissionIds) {
		log.info(String.format("updatePermission roleId[%s], addPermissionIds[%s], removePermissionIds[%s]", roleId, addPermissionIds, removePermissionIds));
		Trans.exec(new Atom(){
			public void run(){
				Role role = fetch(roleId);
				if(StringUtils.isNotEmpty(addPermissionIds)) {
				String[] idArray = addPermissionIds.split(",");
					List<Permission> permissionList = new ArrayList<Permission>();
					for (String id : idArray) {
						Long lId = Long.valueOf(id);
						Permission permission = new Permission();
						permission.setId(lId);
						permissionList.add(permission);
					}
					role.setPermissions(permissionList);
					dao().insertRelation(role, "permissions");
				}
				if (StringUtils.isNotEmpty(removePermissionIds)) {
					String[] removeIdArray = removePermissionIds.split(",");
					log.debug("removeSize: " + removeIdArray.length);
					dao().clear("ecm_role_permission",
							Cnd.where("roleid", "=", roleId).and("permissionid", "in", Lang.array2array(removeIdArray, Long.class)));
				}
			}
		});
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
