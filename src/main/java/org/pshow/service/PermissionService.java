/**
 * 
 */
package org.pshow.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.LockException;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.AccessControlPolicyIterator;
import javax.jcr.security.Privilege;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.pshow.common.JackrabbitUtils;
import org.pshow.common.page.Pagination;
import org.pshow.domain.Permission;
import org.pshow.domain.User;

/**
 * @author Sin
 *
 */
@IocBean(args = { "refer:dao" })
public class PermissionService extends BaseService<Permission> {
	@Inject
	private UserService userService;

	public PermissionService(Dao dao) {
		super(dao);
	}

	public Pagination getListByPager(Integer pageNumber, int pageSize) {
		return getObjListByPager(dao(), getPageNumber(pageNumber), pageSize,
				null, Permission.class);
	}

	public void authorize(String cid, String userName, boolean isAllow,
			String... privileges) throws RepositoryException {
		Session manageSession = JackrabbitUtils.getManageSession();
		// 首先获取AccessManager实例
		AccessControlManager acm = manageSession.getAccessControlManager();
		Node node = manageSession.getNodeByIdentifier(cid);
		// AccessManager会将授权操作下发给AccessControlPolicy
		Principal principal = getPrincipal(userName, manageSession);
		Privilege[] _privileges = getPrivileges(acm, privileges);
		String path = node.getPath();
		AccessControlPolicyIterator applicablePolicies = acm
				.getApplicablePolicies(path);
		if (applicablePolicies.getSize() > 0) {
			while (applicablePolicies.hasNext()) {
				setPolicy(isAllow, acm, principal, _privileges, path,
						applicablePolicies.nextAccessControlPolicy());
			}
		} else {
			AccessControlPolicy[] policies = acm.getPolicies(path);
			if (policies != null) {
				for (AccessControlPolicy acp : policies) {
					setPolicy(isAllow, acm, principal, _privileges, path, acp);
				}
			}
		}
		// 调用session.save()方法将授权信息持久化到存储层
		manageSession.save();
	}

	private void setPolicy(boolean isAllow, AccessControlManager acm,
			Principal principal, Privilege[] _privileges, String path,
			AccessControlPolicy acp) throws AccessControlException,
			RepositoryException, PathNotFoundException, AccessDeniedException,
			LockException, VersionException {
		// 将AccessControlPolicy转换成JackrabbitAccessControlList类型
		JackrabbitAccessControlList jacl = (JackrabbitAccessControlList) acp;
		// clearAce(jacl);
		// 调用JackrabbitAccessControlList类的addEntry方法，为节点添加授权记录
		jacl.addEntry(principal, _privileges, isAllow);
		// 最后调用setPolicy方法,完成授权操作
		acm.setPolicy(path, jacl);
	}

	private Privilege[] getPrivileges(AccessControlManager acm,
			String... privileges) throws AccessControlException,
			RepositoryException {
		List<Privilege> _privileges = new ArrayList<Privilege>(
				privileges.length);
		for (String privilege : privileges) {
			Privilege p = acm.privilegeFromName(privilege);
			_privileges.add(p);
		}
		Privilege[] array = _privileges.toArray(new Privilege[] {});
		return array;
	}

	private Principal getPrincipal(String userName, Session manageSession)
			throws AccessDeniedException,
			UnsupportedRepositoryOperationException, RepositoryException {
		PrincipalManager pMgr = ((JackrabbitSession) manageSession)
				.getPrincipalManager();
		Principal principal = pMgr.getPrincipal(userName);
		return principal;
	}

	public void authorize(int[] uids, String[] cids, int permission)
			throws RepositoryException {
		List<User> users = userService.getUsersByIds(uids);
		String[] privilege = getPrivilege(permission);
		for (User user : users) {
			for (String cid : cids) {
				authorize(cid, user.getName(), true, privilege);
			}
		}
	}

	private String[] getPrivilege(int permission) {
		if (permission == 3) // All
			return new String[] { Privilege.JCR_ALL };
		if (permission == 1) // Read
			return new String[] { Privilege.JCR_READ };
		if (permission == 2) { // Write
			return new String[] { Privilege.JCR_WRITE,
					Privilege.JCR_NODE_TYPE_MANAGEMENT,
					Privilege.JCR_ADD_CHILD_NODES,
					Privilege.JCR_REMOVE_CHILD_NODES,
					Privilege.JCR_REMOVE_NODE, Privilege.JCR_VERSION_MANAGEMENT };
		}
		return null;
	}
}
