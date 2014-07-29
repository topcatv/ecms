package org.pshow.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.pshow.common.JackrabbitUtils;
import org.pshow.domain.Role;
import org.pshow.domain.User;
import org.pshow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserController {
	@InitBinder  
	public void initBinder(WebDataBinder binder) {  
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
	    dateFormat.setLenient(false);  
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));  
	} 

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Page<User> list(@RequestParam(defaultValue = "0", required = false)int pageNumber,
			@RequestParam(defaultValue = "20", required = false) int pageSize) {
		return userService.listUserByPage(pageNumber, pageSize);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public void delete(String ids) {
		userService.delete(ids);
	}

	@RequestMapping("/lock")
	public void lock(String ids) {
		userService.lock(ids);
	}

	@RequestMapping("/unlock")
	public void unlock(String ids) {
		userService.unlock(ids);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public void update(User user) {
		userService.update(user);
	}

	@RequestMapping("/getUnselectedRoleList")
	public List<Role> getUnselectedRoleList(Long userId) {
		return userService.getUnselectedRoleList(userId);
	}

	@RequestMapping("/getSelectedRoleList")
	public List<Role> getSelectedRoleList(Long userId) {
		return userService.getSelectedRoleList(userId);
	}

	@RequestMapping("/updateRole")
	public void updateRole(Long userId, String addRoleIds, String removeRoleIds) {
		userService.updateRole(userId, addRoleIds, removeRoleIds);
	}

	@RequestMapping(method = RequestMethod.POST)
	public void regist(User user) {
		try {
			userService.regist(user);
		} catch (LoginException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@RequestMapping("/unregist")
	public Map<String, String> unregist(User user) {

		final Map<String, String> msgs = new Hashtable<String, String>();
		userService.delete(user);
		try {
			Session manageSession = JackrabbitUtils.getManageSession();
			JackrabbitSession jsession = (JackrabbitSession) manageSession;
			UserManager userManager = jsession.getUserManager();
			org.apache.jackrabbit.api.security.user.User juser = (org.apache.jackrabbit.api.security.user.User) userManager
					.getAuthorizable(user.getName());
			juser.remove();
			manageSession.save();
			msgs.put("message",
					String.format("user '%s' unregisted", user.getName()));
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return msgs;
	}

}
