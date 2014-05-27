package org.pshow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.nodetype.InvalidNodeTypeDefinitionException;
import javax.jcr.nodetype.NodeTypeExistsException;

import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Files;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.pshow.common.JackrabbitUtils;
import org.pshow.domain.Permission;
import org.pshow.domain.Role;
import org.pshow.domain.User;
import org.pshow.domain.UserGroup;

public class MvcSetup implements Setup {

	@Override
	public void init(NutConfig config) {
		Ioc ioc = config.getIoc();
		Dao dao = ioc.get(Dao.class);
		try {
			Session manageSession = JackrabbitUtils.getManageSession();
			NamespaceRegistry namespaceRegistry = manageSession.getWorkspace()
					.getNamespaceRegistry();
			namespaceRegistry.registerNamespace("ps", "http://www.pshow.org/");
			// TODO 需要定义常量
			if (!manageSession.getWorkspace().getNodeTypeManager()
					.hasNodeType("ps:file")) {
				File file = Files.findFile("pshow.cnd");
				CndImporter.registerNodeTypes(new FileReader(file),
						manageSession);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidNodeTypeDefinitionException e) {
			e.printStackTrace();
		} catch (NodeTypeExistsException e) {
			e.printStackTrace();
		} catch (UnsupportedRepositoryOperationException e) {
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			e.printStackTrace();
		} catch (NamespaceException e) {
			// e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		// 若必要的数据表不存在，则初始化数据库
		if (!dao.exists(Permission.class)) {
			dao.create(User.class, true);
			dao.create(Role.class, true);
			dao.create(Permission.class, true);
			dao.create(UserGroup.class, true);
			FileSqlManager fm = new FileSqlManager("init_system_h2.sql");
			List<Sql> sqlList = fm.createCombo(fm.keys());
			dao.execute(sqlList.toArray(new Sql[sqlList.size()]));
			// 初始化用户密码（全部都是123）及salt
			List<User> userList = dao.query(User.class, null);
			for (User user : userList) {
				RandomNumberGenerator rng = new SecureRandomNumberGenerator();
				String salt = rng.nextBytes().toBase64();
				String hashedPasswordBase64 = new Sha256Hash("123", salt, 1024)
						.toBase64();
				user.setSalt(salt);
				user.setPassword(hashedPasswordBase64);
				dao.update(user);
			}
		}
	}

	@Override
	public void destroy(NutConfig config) {

	}
}
