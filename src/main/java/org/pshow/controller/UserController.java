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
import org.pshow.common.JackrabbitUtils;
import org.pshow.domain.User;
import org.pshow.service.UserService;

@IocBean
@At("/users")
public class UserController {

	@Inject
	private UserService userService;

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
