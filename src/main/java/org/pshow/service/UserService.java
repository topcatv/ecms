package org.pshow.service;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;
import org.pshow.common.JackrabbitUtils;
import org.pshow.common.ShiroUtils;
import org.pshow.common.page.Pagination;
import org.pshow.domain.Role;
import org.pshow.domain.User;

@IocBean(args = { "refer:dao" })
public class UserService extends BaseService<User> {
	private final static Logger log = Logger.getLogger(UserService.class);
	
	public UserService(Dao dao) {
		super(dao);
	}

	public User login(User user, HttpSession session) throws LoginException, RepositoryException {
		Subject currentUser = ShiroUtils.getSubject();
		if (!currentUser.isAuthenticated()) {
			UsernamePasswordToken token = new UsernamePasswordToken(
					user.getName(), user.getPassword());
			token.setRememberMe(false);
			currentUser.login(token);
			session.setAttribute(JackrabbitUtils.JCR_SESSION, loginToJcr(user));
		}
		return user;
	}

	private Session loginToJcr(User user) throws LoginException,
			RepositoryException {
		Repository repository = JcrUtils.getRepository();
		Session session = repository.login(new SimpleCredentials(
				user.getName(), "admin".equals(user.getName()) ? "admin"
						.toCharArray() : user.getPassword().toCharArray()));
		return session;
	}

	public List<User> list() {
		return query(null, null);
	}

	public void update(final User user) {
		log.info(String.format("update user[%s]", user));
		FieldFilter.create(User.class,"^locked|description$", true).run(new Atom(){
		    public void run(){
		    	dao().update(user);
		    }
		});
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
		log.info(String.format("updateLock user[%s], isLocked[%s]", ids, isLocked));
		Trans.exec(new Atom(){
			@Override
			public void run() {
				String[] idArray = ids.split(",");
				for (String id : idArray) {
					Long lId = Long.valueOf(id);
					dao().update(
							User.class,
							Chain.make("is_locked", isLocked), Cnd.where("id", "=", lId));
				}
			}
		});
	}

	public void update(long uid, String password, boolean isLocked,
			Integer[] ids) {
		User user = fetch(uid);
		dao().clearLinks(user, "roles");
		if (!Lang.isEmptyArray(ids)) {
			user.setRoles(dao().query(Role.class, Cnd.where("id", "in", ids)));
		}
		if (StringUtils.isNotBlank(password)) {
			String salt = new SecureRandomNumberGenerator().nextBytes()
					.toBase64();
			user.setSalt(salt);
			user.setPassword(new Sha256Hash(password, salt, 1024).toBase64());
		}
		user.setLocked(isLocked);
		dao().update(user);
		if (!Lang.isEmpty(user.getRoles())) {
			dao().insertRelation(user, "roles");
		}
	}

	public void updatePwd(Object uid, String password) {
		String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
		dao().update(
				User.class,
				Chain.make("password",
						new Sha256Hash(password, salt, 1024).toBase64()).add(
						"salt", salt), Cnd.where("id", "=", uid));
	}

	public void insert(final User user) {
		log.info(String.format("insert user[%s]", user));
		Trans.exec(new Atom(){
			public void run(){
				RandomNumberGenerator rng = new SecureRandomNumberGenerator();
				String salt = rng.nextBytes().toBase64();
				String hashedPasswordBase64 = new Sha256Hash(user.getPassword(), salt,
						1024).toBase64();
				user.setSalt(salt);
				user.setPassword(hashedPasswordBase64);
				dao().insert(user);
		//		dao().insertRelation(user, "roles");
				try {
					Session manageSession = JackrabbitUtils.getManageSession();
					JackrabbitSession jcrSession = (JackrabbitSession) manageSession;
					UserManager userManager = jcrSession.getUserManager();
					if (userManager.getAuthorizable(user.getName()) == null) {
						userManager.createUser(user.getName(), user.getPassword());
					}
					manageSession.save();
				} catch (RepositoryException re) {
					throw Lang.wrapThrow(re);
				}
			}
		});
	}
	
	public void save(User user) {
		int count = dao().count(User.class, Cnd.where("name", "=", user.getName()));
		if (count > 0) {
			throw new IllegalArgumentException(String.format("user name[%s] already exists!", user.getName()));
		}
		insert(user);
	}

	public boolean save(String username, String password, boolean isEnabled,
			String addr, int[] roleIds) {
		User user = new User();
		user.setCreateDate(Times.now());
		user.setDescription("--");
		user.setLocked(!isEnabled);
		user.setName(username);
		user.setRegisterIp(addr);
		user.setSex("man");
		user.setRoles(dao().query(Role.class, Cnd.where("id", "in", roleIds)));
		String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
		user.setSalt(salt);
		user.setPassword(new Sha256Hash(password, salt, 1024).toBase64());
		insert(user);
		return true;
	}

	public User view(Long id) {
		return dao().fetchLinks(fetch(id), "roles");
	}

	public User fetchByName(String name) {
		return fetch(Cnd.where("name", "=", name));
	}
	
	public List<Role> getUnselectedRoleList(Long userId) {
		Sql sql = Sqls.queryEntity("select * from ecm_role r where not exists (select 1 from ecm_user_role eur where eur.roleid = r.id and eur.userid = @userId)");
		sql.params().set("userId", userId);
//		sql.setPager(dao().createPager(2,20));
		sql.setEntity(dao().getEntity(Role.class));
		dao().execute(sql);
		return sql.getList(Role.class);
	}

	public List<Role> getSelectedRoleList(Long userId) {
		User user = dao().fetchLinks(fetch(userId), "roles");
		return user.getRoles();
	}

	public void updateRole(final Long userId, final String addRoleIds, final String removeRoleIds) {
		log.info(String.format("updateRole userId[%s], addRoleIds[%s], removeRoleIds[%s]", userId, addRoleIds, removeRoleIds));
		Trans.exec(new Atom(){
			public void run(){
				User user = fetch(userId);
				if(StringUtils.isNotEmpty(addRoleIds)) {
				String[] idArray = addRoleIds.split(",");
					List<Role> roleList = new ArrayList<Role>();
					for (String id : idArray) {
						Long lId = Long.valueOf(id);
						Role role = new Role();
						role.setId(lId);
						roleList.add(role);
					}
					user.setRoles(roleList);
					dao().insertRelation(user, "roles");
				}
				if (StringUtils.isNotEmpty(removeRoleIds)) {
					String[] removeIdArray = removeRoleIds.split(",");
					log.debug("removeSize: " + removeIdArray.length);
					dao().clear("ecm_user_role",
							Cnd.where("userid", "=", userId).and("roleid", "in", Lang.array2array(removeIdArray, Long.class)));
				}
			}
		});
	}

	public void removeRole(Long userId, Long roleId) {
		dao().clear("system_user_role",
				Cnd.where("userid", "=", userId).and("roleid", "=", roleId));
	}

	public List<String> getRoleNameList(User user) {
		dao().fetchLinks(user, "roles");
		List<String> roleNameList = new ArrayList<String>();
		for (Role role : user.getRoles()) {
			roleNameList.add(role.getName());
		}
		return roleNameList;
	}
	
	public Pagination getUserListByPager(Integer pageNumber, int pageSize) {
		return getObjListByPager(dao(), getPageNumber(pageNumber), pageSize,
				null, User.class);
	}
	
	public Pagination listUserByPage(Long roleId, String name, Integer pageNumber, int pageSize) {
		Cnd cnd = null;
		if (!Lang.isEmpty(roleId)) {
			cnd = Cnd.where("id", "=", roleId);
		} else if (!Lang.isEmpty(name)) {
			cnd = Cnd.where("name", "LIKE", "%" + name + "%");
		}
		return getObjListByPager(dao(), pageNumber, pageSize, cnd, User.class);
	}

	public User initUser(String name, String openid, String providerid,
			String addr, boolean isUpdated) {
		User user = new User();
		user.setCreateDate(Times.now());
		user.setName(name);
		user.setOpenid(openid);
		user.setProviderid(providerid);
		user.setRegisterIp(addr);
		user.setLocked(false);
		user.setUpdated(isUpdated);
		return dao().insert(user);
	}

	public User fetchByOpenID(String openid) {
		User user = fetch(Cnd.where("openid", "=", openid));
		if (!Lang.isEmpty(user) && !user.isLocked()) {
			dao().fetchLinks(user, "servers");
			dao().fetchLinks(user, "roles");
		}
		return user;
	}
	
	public void delete(final String ids) {
		log.info(String.format("delete user[%s]", ids));
		if (!Lang.isEmpty(ids)) {
			Trans.exec(new Atom(){
				public void run(){
					String[] idArray = ids.split(",");
					for (String id : idArray) {
						Long lId = Long.valueOf(id);
						User user = dao().fetch(User.class, lId);
						dao().delete(User.class, lId);
						dao().clear("ecm_user_role", Cnd.where("userid", "=", lId));
						try {
							Session manageSession = JackrabbitUtils.getManageSession();
							JackrabbitSession jsession = (JackrabbitSession) manageSession;
							UserManager userManager = jsession.getUserManager();
							org.apache.jackrabbit.api.security.user.User juser = (org.apache.jackrabbit.api.security.user.User) userManager
									.getAuthorizable(user.getName());
							juser.remove();
							manageSession.save();
						} catch (RepositoryException re) {
							throw Lang.wrapThrow(re);
						}
					}
				}
			});
		}
	}
}
