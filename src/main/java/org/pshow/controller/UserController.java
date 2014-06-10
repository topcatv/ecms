package org.pshow.controller;

import java.util.Hashtable;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Param;
import org.pshow.common.JackrabbitUtils;
import org.pshow.common.page.Pagination;
import org.pshow.domain.User;
import org.pshow.mvc.Result;
import org.pshow.mvc.SuccessResult;
import org.pshow.service.UserService;

@IocBean
@At("/user")
public class UserController {

	@Inject
	private UserService userService;
	
	@At
	public Pagination list(Long userId, String name, Integer pageNumber, int pageSize) {
		return userService.listUserByPage(userId, name, pageNumber, pageSize);
	}
	
	@At
	public Result create(@Param("..") User user) {
		userService.save(user);
		return new SuccessResult();
	}
	
	@At
	public Result delete(User user) {
		userService.delete(user);
		return new SuccessResult();
	}
	
	@At
	public Result lock(Long userId) {
		userService.lock(userId);
		return new SuccessResult();
	}
	
	@At
	public Result unlock(Long userId) {
		userService.unlock(userId);
		return new SuccessResult();
	}
	
	@At
	public Result update(User user) {
		userService.update(user);
		return new SuccessResult();
	}
	

	@At
	public Map<String, String> regist(final User user) {
		final Map<String, String> msgs = new Hashtable<String, String>();
		if (userService.fetchByName(user.getName()) != null) {
			msgs.put("message",
					String.format("user '%s' exist", user.getName()));
			return msgs;
		}
		try {
			Session manageSession = JackrabbitUtils.getManageSession();
			JackrabbitSession jcrSession = (JackrabbitSession) manageSession;
			UserManager userManager = jcrSession.getUserManager();
			if (userManager.getAuthorizable(user.getName()) == null) {
				userManager.createUser(user.getName(), user.getPassword());
			}
			manageSession.save();
			msgs.put("message",
					String.format("user '%s' registed", user.getName()));
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		RandomNumberGenerator rng = new SecureRandomNumberGenerator();
		String salt = rng.nextBytes().toBase64();
		String hashedPasswordBase64 = new Sha256Hash(user.getPassword(), salt,
				1024).toBase64();
		user.setSalt(salt);
		user.setPassword(hashedPasswordBase64);
		userService.insert(user);
		return msgs;
	}

	@At
	public Map<String, String> unregist(final User user) {

		final Map<String, String> msgs = new Hashtable<String, String>();
		userService.delete(userService.fetchByName(user.getName()).getId());
		try {
			Session manageSession = JackrabbitUtils.getManageSession();
			JackrabbitSession jsession = (JackrabbitSession) manageSession;
			UserManager userManager = jsession.getUserManager();
			org.apache.jackrabbit.api.security.user.User juser = (org.apache.jackrabbit.api.security.user.User) userManager
					.getAuthorizable(user.getName());
			juser.remove();
			manageSession.save();
			msgs.put("message",
					String.format("user '%s' unregisted", user.getName()));
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return msgs;
	}

}
