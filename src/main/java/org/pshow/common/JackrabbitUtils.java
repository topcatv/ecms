package org.pshow.common;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;

public class JackrabbitUtils {

	public static Session getManageSession() throws LoginException, RepositoryException{
		Repository repository = JcrUtils.getRepository();
		Session session = repository.login(new SimpleCredentials("admin", "admin"
				.toCharArray()));
		return session;
	}
}
