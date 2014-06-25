package org.pshow.common;

import javax.jcr.Session;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.apache.shiro.subject.Subject;
import org.pshow.service.UserService;

/**
 * Application Lifecycle Listener implementation class LogoutListener
 *
 */
public class LogoutListener implements HttpSessionListener {
	private static Logger log = Logger.getLogger(LogoutListener.class);

    /**
     * Default constructor. 
     */
    public LogoutListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent se) {
    	HttpSession session = se.getSession();
    	Subject currentUser = (Subject) session.getAttribute(UserService.SUBJECT);
    	Object username = currentUser.getPrincipal();
		if (currentUser.isAuthenticated()) {
			Session jcrSession = JackrabbitUtils.getJcrSessionFromHttpSession(session);
			if (jcrSession != null) {
				jcrSession.logout();
			}
			currentUser.logout();
			log.debug(String.format("user %s logout", username));
		}
    }

	/**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent se) {
        // TODO Auto-generated method stub
    }
	
}
