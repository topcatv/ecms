package org.pshow.shiro.realm;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.mvc.Mvcs;
import org.pshow.domain.Role;
import org.pshow.domain.User;
import org.pshow.service.RoleService;
import org.pshow.service.UserService;

public abstract class AbstractNutAuthoRealm extends AuthorizingRealm {

	private UserService userService;
	private RoleService roleService;

	protected UserService getUserService() {
		if (Lang.isEmpty(userService)) {
			Ioc ioc = Mvcs.getIoc();
			userService = ioc.get(UserService.class);
		}
		return userService;
	}

	protected RoleService getRoleService() {
		if (Lang.isEmpty(roleService)) {
			Ioc ioc = Mvcs.getIoc();
			roleService = ioc.get(RoleService.class);
		}
		return roleService;
	}

	/**
	 * 更新用户授权信息缓存.
	 */
	public void clearCachedAuthorizationInfo(String principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}

	/**
	 * 清除所有用户授权信息缓存.
	 */
	public void clearAllCachedAuthorizationInfo() {
		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
		if (cache != null) {
			for (Object key : cache.keys()) {
				cache.remove(key);
			}
		}
	}
	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		User user = (User) principals.getPrimaryPrincipal();
		if (user != null) {
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			info.addRoles(getUserService().getRoleNameList(user));
			for (Role role : user.getRoles()) {
				info.addStringPermissions(getRoleService().getPermissionNameList(role));
			}
			return info;
		} else {
			return null;
		}
	}
}
