package org.pshow.controller;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
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
	@Ok("redirect:/index.html")
	public void logout(HttpSession session) {
		session.invalidate();
	}
}
