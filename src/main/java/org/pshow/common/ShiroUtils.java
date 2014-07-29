package org.pshow.common;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;

public class ShiroUtils{
	
	public static Subject getSubject() {
		IniSecurityManagerFactory factory = new IniSecurityManagerFactory("classpath:shiro.ini");

		SecurityManager securityManager = factory.getInstance();

	    SecurityUtils.setSecurityManager(securityManager);
		Subject currentUser = SecurityUtils.getSubject();
		return currentUser;
	}
}
