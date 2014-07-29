/**
 * 
 */
package org.pshow.common;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.jackrabbit.commons.JcrUtils;
import org.pshow.domain.User;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @author topcat
 *
 */
public class JcrSessionInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("d_user");
		if (user != null) {
			JackrabbitUtils.jcrSession.set(loginToJcr(user));
		}
		return true;
	}

	private Session loginToJcr(User user) throws LoginException,
			RepositoryException {
		Repository repository = JcrUtils.getRepository();
		Session session = repository.login(new SimpleCredentials(
				user.getName(), "admin".equals(user.getName()) ? "admin"
						.toCharArray() : user.getPassword().toCharArray()));
		return session;
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		Session jcrSession = JackrabbitUtils.jcrSession.get();
		if (jcrSession != null) {
			if (jcrSession.isLive()) {
				jcrSession.logout();
			}
		}
		JackrabbitUtils.jcrSession.remove();
	}
}
