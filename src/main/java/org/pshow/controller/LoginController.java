package org.pshow.controller;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Param;
import org.pshow.common.JackrabbitUtils;
import org.pshow.domain.User;
import org.pshow.mvc.Result;
import org.pshow.mvc.SuccessResult;
import org.pshow.service.UserService;

@IocBean
@At("/auth")
public class LoginController {
	@Inject
	private UserService userService;

	@At
	public Result login(@Param("..") User user, HttpSession session)
			throws LoginException, RepositoryException {
		SuccessResult success = new SuccessResult();
		success.put("user", userService.login(user, session));
		return success;
	}

	@At
	public void logout(HttpSession session) {
		Subject currentUser = SecurityUtils.getSubject();
		if (currentUser.isAuthenticated()) {
			Session jcrSession = JackrabbitUtils.getJcrSessionFromHttpSession(session);
			if (jcrSession != null) {
				jcrSession.logout();
			}
			currentUser.logout();
		}
	}
}
