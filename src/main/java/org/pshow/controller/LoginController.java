package org.pshow.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.pshow.domain.User;
import org.pshow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class LoginController {
	@Autowired
	private UserService userService;

	@RequestMapping("/login")
	public Map<String, Object> login(User user, HttpSession session)
			throws LoginException, RepositoryException {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("user", userService.login(user, session));
		result.put("success", true);
		return result;
	}

	@RequestMapping("/logout")
	public void logout(HttpSession session, HttpServletResponse request) {
		session.invalidate();
		try {
			request.sendRedirect("/");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
