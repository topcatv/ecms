package org.pshow.controller;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpSession;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.view.ForwardView;
import org.nutz.mvc.view.ViewWrapper;
import org.pshow.common.ShiroUtils;
import org.pshow.domain.User;

@IocBean
@At("/auth")
public class LoginController {

	public static final String JCR_SESSION = "jcr_session";

	public View login(@Param("..") User user, boolean remeberMe,
			HttpSession session) {
		Subject currentUser = ShiroUtils.getSubject();
		if (!currentUser.isAuthenticated()) {
			UsernamePasswordToken token = new UsernamePasswordToken(
					user.getName(), user.getPassword());
			token.setRememberMe(remeberMe);
			try {
				currentUser.login(token);
				session.setAttribute(JCR_SESSION, loginToJcr(user));
				// if no exception, that's it, we're done!
				return new ViewWrapper(new ForwardView("/index.jsp"), "welcome");
			} catch (UnknownAccountException e) {
				// username wasn't in the system, show them an error message?
				e.printStackTrace();
				return new ViewWrapper(new ForwardView("/index.jsp"),
						e.getMessage());
			} catch (IncorrectCredentialsException e) {
				// password didn't match, try again?
				e.printStackTrace();
				return new ViewWrapper(new ForwardView("/index.jsp"),
						e.getMessage());
			} catch (LockedAccountException e) {
				// account for that username is locked - can't login. Show them
				// a message?
				e.printStackTrace();
				return new ViewWrapper(new ForwardView("/index.jsp"),
						e.getMessage());
			} catch (AuthenticationException e) {
				// unexpected condition - error?
				e.printStackTrace();
				return new ViewWrapper(new ForwardView("/index.jsp"),
						e.getMessage());
			} catch (LoginException e) {
				e.printStackTrace();
				return new ViewWrapper(new ForwardView("/index.jsp"),
						e.getMessage());
			} catch (RepositoryException e) {
				e.printStackTrace();
				return new ViewWrapper(new ForwardView("/index.jsp"),
						e.getMessage());
			}
		}
		return new ViewWrapper(new ForwardView("/index.jsp"), "");
	}

	private Session loginToJcr(User user) throws LoginException,
			RepositoryException {
		Repository repository = JcrUtils.getRepository();
		Session session = repository.login(new SimpleCredentials(
				user.getName(), user.getPassword().toCharArray()));
		return session;
	}

	public void logout(HttpSession session) {
		Subject currentUser = SecurityUtils.getSubject();
		if (currentUser.isAuthenticated()) {
			Session jcrSession = (Session)session.getAttribute(JCR_SESSION);
			if (jcrSession != null) {
				jcrSession.logout();
			}
			currentUser.logout();
		}
	}
}
