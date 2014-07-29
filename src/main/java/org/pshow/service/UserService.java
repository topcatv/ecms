package org.pshow.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.pshow.common.JackrabbitUtils;
import org.pshow.common.ShiroUtils;
import org.pshow.domain.Role;
import org.pshow.domain.User;
import org.pshow.repository.DynamicSpecifications;
import org.pshow.repository.SearchFilter;
import org.pshow.repository.SearchFilter.Operator;
import org.pshow.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sun.star.uno.RuntimeException;

@Service("userService")
@Transactional
public class UserService {
	public static final String SUBJECT = "subject";
	private final static Logger log = Logger.getLogger(UserService.class);
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private UserDao userDao;

	public UserService() {
	}

	public User login(User user, HttpSession session) throws LoginException,
			RepositoryException {
		Subject currentUser = ShiroUtils.getSubject();
		if (!currentUser.isAuthenticated()) {
			UsernamePasswordToken token = new UsernamePasswordToken(
					user.getName(), user.getPassword());
			token.setRememberMe(false);
			currentUser.login(token);
			session.setAttribute(SUBJECT, currentUser);
			session.setAttribute("d_user", user);
		}
		return user;
	}

	public List<User> list() {
		// return query(null, null);
		return null;
	}

	public void update(User user) {
		log.info(String.format("update user[%s]", user));
		User u = userDao.findOne(user.getId());
		u.setDescription(user.getDescription());
	}

	public void lock(String ids) {
		log.info(String.format("lock user[%s]", ids));
		updateLock(ids, true);
	}

	public void unlock(String ids) {
		log.info(String.format("unlock user[%s]", ids));
		updateLock(ids, false);
	}

	private void updateLock(final String ids, final boolean isLocked) {
		log.info(String.format("updateLock user[%s], isLocked[%s]", ids,
				isLocked));
		if (StringUtils.isNotEmpty(ids)) {
			String[] idArray = StringUtils.split(ids, ",");
			List<Long> l_ids = new ArrayList<Long>(idArray.length);
			for (String id : idArray) {
				l_ids.add(Long.valueOf(id));
			}
			userDao.updateLock(l_ids, isLocked);
		}
	}

	public void update(long uid, String password, boolean isLocked,
			Integer[] ids) {
		// User user = fetch(uid);
		// dao().clearLinks(user, "roles");
		// if (!Lang.isEmptyArray(ids)) {
		// user.setRoles(dao().query(Role.class, Cnd.where("id", "in", ids)));
		// }
		// if (StringUtils.isNotBlank(password)) {
		// String salt = new SecureRandomNumberGenerator().nextBytes()
		// .toBase64();
		// user.setSalt(salt);
		// user.setPassword(new Sha256Hash(password, salt, 1024).toBase64());
		// }
		// user.setLocked(isLocked);
		// dao().update(user);
		// if (!Lang.isEmpty(user.getRoles())) {
		// dao().insertRelation(user, "roles");
		// }
	}

	public void updatePwd(Object uid, String password) {
		String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
		// dao().update(
		// User.class,
		// Chain.make("password",
		// new Sha256Hash(password, salt, 1024).toBase64()).add(
		// "salt", salt), Cnd.where("id", "=", uid));
	}

	public void regist(User user) throws LoginException,
			RepositoryException {
		if (userDao.findByName(user.getName()) != null) {
			throw new RuntimeException(String.format("user '%s' exist", user.getName()));
		}
		
		log.info(String.format("insert user[%s]", user));
		
		Session manageSession = JackrabbitUtils.getManageSession();
		JackrabbitSession jcrSession = (JackrabbitSession) manageSession;
		UserManager userManager = jcrSession.getUserManager();
		if (userManager.getAuthorizable(user.getName()) == null) {
			userManager.createUser(user.getName(), user.getPassword());
		}
		// 保证jcr用户创建在前，否则下面的密码会被修改
		RandomNumberGenerator rng = new SecureRandomNumberGenerator();
		String salt = rng.nextBytes().toBase64();
		String hashedPasswordBase64 = new Sha256Hash(user.getPassword(), salt,
				1024).toBase64();
		user.setSalt(salt);
		user.setPassword(hashedPasswordBase64);
		user.setCreateDate(new Date());
		userDao.save(user);
		manageSession.save();
	}

	public void save(User user) {
		Specification<User> spec = DynamicSpecifications.bySearchFilter(
				User.class,
				new SearchFilter("name", Operator.EQ, user.getName()));
		long count = userDao.count(spec);
		if (count > 0) {
			throw new IllegalArgumentException(String.format(
					"user name[%s] already exists!", user.getName()));
		}
		userDao.save(user);
	}

	public boolean save(String username, String password, boolean isEnabled,
			String addr, int[] roleIds) {
		// User user = new User();
		// user.setCreateDate(Times.now());
		// user.setDescription("--");
		// user.setLocked(!isEnabled);
		// user.setName(username);
		// user.setRegisterIp(addr);
		// user.setSex("man");
		// user.setRoles(dao().query(Role.class, Cnd.where("id", "in",
		// roleIds)));
		// String salt = new
		// SecureRandomNumberGenerator().nextBytes().toBase64();
		// user.setSalt(salt);
		// user.setPassword(new Sha256Hash(password, salt, 1024).toBase64());
		// insert(user);
		return true;
	}

	public User view(Long id) {
		// return dao().fetchLinks(fetch(id), "roles");
		return null;
	}

	public User fetchByName(String name) {
		return userDao.findByName(name);
	}

	public List<Role> getUnselectedRoleList(Long userId) {
		// Sql sql =
		// Sqls.queryEntity("select * from ecm_role r where not exists (select 1 from ecm_user_role eur where eur.roleid = r.id and eur.userid = @userId)");
		// sql.params().set("userId", userId);
		// // sql.setPager(dao().createPager(2,20));
		// sql.setEntity(dao().getEntity(Role.class));
		// dao().execute(sql);
		// return sql.getList(Role.class);
		return null;
	}

	public List<Role> getSelectedRoleList(Long userId) {
		// User user = dao().fetchLinks(fetch(userId), "roles");
		// return user.getRoles();
		return null;
	}

	public void updateRole(final Long userId, final String addRoleIds,
			final String removeRoleIds) {
		log.info(String.format(
				"updateRole userId[%s], addRoleIds[%s], removeRoleIds[%s]",
				userId, addRoleIds, removeRoleIds));
		// Trans.exec(new Atom(){
		// public void run(){
		// User user = fetch(userId);
		// if(StringUtils.isNotEmpty(addRoleIds)) {
		// String[] idArray = addRoleIds.split(",");
		// List<Role> roleList = new ArrayList<Role>();
		// for (String id : idArray) {
		// Long lId = Long.valueOf(id);
		// Role role = new Role();
		// role.setId(lId);
		// roleList.add(role);
		// }
		// user.setRoles(roleList);
		// dao().insertRelation(user, "roles");
		// }
		// if (StringUtils.isNotEmpty(removeRoleIds)) {
		// String[] removeIdArray = removeRoleIds.split(",");
		// log.debug("removeSize: " + removeIdArray.length);
		// dao().clear("ecm_user_role",
		// Cnd.where("userid", "=", userId).and("roleid", "in",
		// Lang.array2array(removeIdArray, Long.class)));
		// }
		// }
		// });
	}

	public void removeRole(Long userId, Long roleId) {
		// dao().clear("system_user_role",
		// Cnd.where("userid", "=", userId).and("roleid", "=", roleId));
	}

	public List<String> getRoleNameList(User user) {
		// dao().fetchLinks(user, "roles");
		// for (Role role : user.getRoles()) {
		// roleNameList.add(role.getName());
		// return roleNameList;
		// }
		// return roleNameList;
		List<Role> roles = userDao.findOne(user.getId()).getRoles();
		List<String> roleNameList = new ArrayList<String>();
		for (Role role : roles) {
			roleNameList.add(role.getName());
		}
		return roleNameList;
	}

	public Page<User> getUserListByPager(Integer pageNumber, int pageSize) {
		// return getObjListByPager(dao(), getPageNumber(pageNumber), pageSize,
		// null, User.class);
		return null;
	}

	public Page<User> listUserByPage(int pageNumber, int pageSize) {
		Pageable pageable = new PageRequest(pageNumber, pageSize);
		return userDao.findAll(pageable);
	}

	public User initUser(String name, String openid, String providerid,
			String addr, boolean isUpdated) {
		// User user = new User();
		// user.setCreateDate(Times.now());
		// user.setName(name);
		// user.setOpenid(openid);
		// user.setProviderid(providerid);
		// user.setRegisterIp(addr);
		// user.setLocked(false);
		// user.setUpdated(isUpdated);
		// return dao().insert(user);
		return null;
	}

	public User fetchByOpenID(String openid) {
		// User user = fetch(Cnd.where("openid", "=", openid));
		// if (!Lang.isEmpty(user) && !user.isLocked()) {
		// dao().fetchLinks(user, "servers");
		// dao().fetchLinks(user, "roles");
		// }
		// return user;
		return null;
	}

	public void delete(String ids) {
		log.info(String.format("delete user[%s]", ids));
		if (StringUtils.isNotBlank(ids)) {
			String[] idArray = StringUtils.split(ids, ",");
			List<Long> l_ids = new ArrayList<Long>(idArray.length);
			try {
				Session manageSession = JackrabbitUtils.getManageSession();
				JackrabbitSession jsession = (JackrabbitSession) manageSession;
				UserManager userManager = jsession.getUserManager();
				for (String id : idArray) {
					Long l_id = Long.valueOf(id);
					l_ids.add(l_id);
					User user = userDao.findOne(l_id);
					org.apache.jackrabbit.api.security.user.User juser = (org.apache.jackrabbit.api.security.user.User) userManager
							.getAuthorizable(user.getName());
					if (juser != null) {
						juser.remove();
					}
				}
				userDao.delete(l_ids);
				manageSession.save();
			} catch (RepositoryException re) {
				throw new RuntimeException("user delete fail", re);
			}
		}
	}

	public List<User> getUsersByIds(int[] ids) {
		if (ids == null || ids.length <= 0) {
			return null;
		}
		List<Long> l_ids = new ArrayList<Long>(ids.length);
		for (int id : ids) {
			l_ids.add((long) id);
		}
		return userDao.findByIds(l_ids);
	}

	public void delete(User user) {
		// TODO Auto-generated method stub

	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
}
