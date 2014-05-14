package org.pshow.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpSession;

import org.apache.shiro.subject.Subject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.mvc.Mvcs;
import org.pshow.common.ShiroUtils;
import org.pshow.domain.User;

public class LoginControllerTest extends BaseTest {

	private LoginController loginController;
	private UserController userController;
	private User user;
	
	@Before
	public void setUp() throws Exception {
		loginController = Mvcs.getIoc().get(LoginController.class);
		userController = Mvcs.getIoc().get(UserController.class);
		user = new User();
		user.setName("roy");
		user.setPassword("topcat");
	}

	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testLogin() throws LoginException, RepositoryException {
		Map<String, String> msg = userController.regist(user);
		assertEquals("user 'roy' registed", msg.get("message"));
		user.setPassword("topcat");
		HttpSession session = mock(HttpSession.class);
		loginController.login(user, true, session);
		Subject currentUser = ShiroUtils.getSubject();
		assertTrue(currentUser.isAuthenticated());
		msg = userController.unregist(user);
		assertEquals("user 'roy' unregisted", msg.get("message"));
	}

	@Test
	public void testLogout() {
		HttpSession session = mock(HttpSession.class);
		Session jcrSession = mock(Session.class);
		when(session.getAttribute(LoginController.JCR_SESSION)).thenReturn(jcrSession);
		loginController.logout(session);
		verify(jcrSession, times(1)).logout();
		Subject currentUser = ShiroUtils.getSubject();
		assertFalse(currentUser.isAuthenticated());
	}

}
