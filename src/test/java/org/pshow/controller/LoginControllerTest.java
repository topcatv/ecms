package org.pshow.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import javax.jcr.Session;
import javax.servlet.http.HttpSession;

import org.apache.shiro.subject.Subject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.mvc.Mvcs;
import org.pshow.common.ShiroUtils;
import org.pshow.domain.User;

public class LoginControllerTest {

	private LoginController loginController;
	private UserController userController;
	private User user;
	
	public LoginControllerTest() {
		Ioc ioc = null;
		try {
			ioc = new NutIoc(new ComboIocLoader("*org.nutz.ioc.loader.json.JsonLoader", "ioc", "*org.nutz.ioc.loader.annotation.AnnotationIocLoader", "org.pshow"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		};
		Mvcs.setIoc(ioc);
	}

	@Before
	public void setUp() throws Exception {
		loginController = Mvcs.getIoc().get(LoginController.class);
		userController = Mvcs.getIoc().get(UserController.class);
		user = new User();
		user.setName("roy");
		user.setPassword("roy");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testLogin() {
		Map<String, String> msg = userController.regist(user);
		assertEquals("user 'roy' registed", msg.get("message"));
		user.setPassword("roy");
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
