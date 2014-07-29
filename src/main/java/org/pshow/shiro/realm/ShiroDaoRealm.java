package org.pshow.shiro.realm;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.pshow.domain.Role;
import org.pshow.domain.User;
import org.pshow.service.RoleService;
import org.pshow.service.UserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ShiroDaoRealm extends AuthorizingRealm {
	private UserService userService;
	private RoleService roleService;
	private static ClassPathXmlApplicationContext applicationContext = new  ClassPathXmlApplicationContext("applicationContext.xml");

	public ShiroDaoRealm() {
		setAuthenticationTokenClass(UsernamePasswordToken.class);
		this.userService = applicationContext.getBean(UserService.class);
		this.roleService = applicationContext.getBean(RoleService.class);
	}

	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws DisabledAccountException {
		UsernamePasswordToken authcToken = (UsernamePasswordToken) token;
		String accountName = authcToken.getUsername();
		if (StringUtils.isBlank(accountName)) {
			throw new AuthenticationException("Account is empty");
		}
		User user = userService.fetchByName(authcToken.getUsername());
		if (null == user) {
			throw new UnknownAccountException(String.format("Account [ %s ] not found", authcToken.getUsername()));
		}
		if (user.isLocked()) {
			throw new LockedAccountException(String.format("Account [ %s ] is locked.", authcToken.getUsername()));
		}
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getPassword(), getName());
		ByteSource salt = ByteSource.Util.bytes(user.getSalt());
		info.setCredentialsSalt(salt);
		return info;
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		User user = (User) principals.getPrimaryPrincipal();
		if (user != null) {
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			info.addRoles(userService.getRoleNameList(user));
			for (Role role : user.getRoles()) {
				info.addStringPermissions(roleService.getPermissionNameList(role));
			}
			return info;
		} else {
			return null;
		}
	}
}
