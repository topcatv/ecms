package org.pshow.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.shiro.subject.Subject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.pshow.common.JackrabbitUtils;
import org.pshow.common.ShiroUtils;
import org.pshow.domain.User;

public class LoginControllerTest extends BaseTest {

	private LoginController loginController;
	private UserController userController;
	private User user;
	
	@Before
	public void setUp() throws Exception {
		loginController = getBean(LoginController.class);
		userController = getBean(UserController.class);
		user = new User();
		user.setName("roy");
		user.setPassword("topcat");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@AfterClass
	public static void tearAferClassDown() throws Exception {
		JackrabbitUtils.getManageSession().logout();
		JackrabbitRepository repository = (JackrabbitRepository)JackrabbitUtils.getRepository();
		repository.shutdown();
	}
	
	@Test
	public void testLogin() throws LoginException, RepositoryException {
		userController.regist(user);
		user.setPassword("topcat");
		HttpSession session = mock(HttpSession.class);
		loginController.login(user, session);
		Subject currentUser = ShiroUtils.getSubject();
		
		assertTrue(currentUser.isAuthenticated());
		Map<String, String> msg = userController.unregist(user);
		assertEquals("user 'roy' unregisted", msg.get("message"));
	}

	@Test
	public void testLogout() {
		HttpSession session = mock(HttpSession.class);
		Session jcrSession = mock(Session.class);
		when(session.getAttribute(JackrabbitUtils.JCR_SESSION)).thenReturn(jcrSession);
		HttpServletResponse response =  mock(HttpServletResponse.class);
		loginController.logout(session, response);
		verify(jcrSession, times(1)).logout();
		try {
			verify(response, times(1)).sendRedirect("/");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Subject currentUser = ShiroUtils.getSubject();
		assertFalse(currentUser.isAuthenticated());
	}

}
