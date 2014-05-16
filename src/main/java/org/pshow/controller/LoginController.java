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
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.pshow.domain.User;
import org.pshow.service.UserService;

@IocBean
@At("/auth")
public class LoginController {
	@Inject
	private UserService userService;

	@At
	@Ok("json")
	@Fail("exception:403")
	public User login(@Param("..") User user, HttpSession session)
			throws LoginException, RepositoryException {
		return userService.login(user, session);
	}

	@At
	public void logout(HttpSession session) {
		Subject currentUser = SecurityUtils.getSubject();
		if (currentUser.isAuthenticated()) {
			Session jcrSession = (Session) session
					.getAttribute(UserService.JCR_SESSION);
			if (jcrSession != null) {
				jcrSession.logout();
			}
			currentUser.logout();
		}
	}
}
