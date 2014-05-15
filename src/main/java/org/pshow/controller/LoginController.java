package org.pshow.controller;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpSession;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.pshow.common.ShiroUtils;
import org.pshow.domain.User;

@IocBean
@At("/auth")
public class LoginController {

	public static final String JCR_SESSION = "jcr_session";

	@At
	@Ok("json")
	@Fail("exception:403")
	public User login(@Param("..") User user, boolean remeberMe,
			HttpSession session) throws LoginException, RepositoryException {
		Subject currentUser = ShiroUtils.getSubject();
		if (!currentUser.isAuthenticated()) {
			UsernamePasswordToken token = new UsernamePasswordToken(
					user.getName(), user.getPassword());
			token.setRememberMe(remeberMe);
			currentUser.login(token);
			session.setAttribute(JCR_SESSION, loginToJcr(user));
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

	public void logout(HttpSession session) {
		Subject currentUser = SecurityUtils.getSubject();
		if (currentUser.isAuthenticated()) {
			Session jcrSession = (Session) session.getAttribute(JCR_SESSION);
			if (jcrSession != null) {
				jcrSession.logout();
			}
			currentUser.logout();
		}
	}
}
