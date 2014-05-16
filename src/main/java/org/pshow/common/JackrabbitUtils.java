package org.pshow.common;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;

public class JackrabbitUtils {
	private static Repository repository;
	private static Session manageSession;

	public static synchronized Session getManageSession()
			throws LoginException, RepositoryException {
		if (manageSession == null || !manageSession.isLive()) {
			if (repository == null) {
				repository = JcrUtils.getRepository();
			}
			manageSession = repository.login(new SimpleCredentials("admin",
					"admin".toCharArray()));
		}
		return manageSession;
	}
	
	public static Repository getRepository() throws RepositoryException{
		if (repository == null) {
			repository = JcrUtils.getRepository();
		}
		return repository;
	}
}
