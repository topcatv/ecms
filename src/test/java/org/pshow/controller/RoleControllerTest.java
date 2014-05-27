package org.pshow.controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Before;
import org.junit.Test;
import org.nutz.mvc.Mvcs;
import org.pshow.common.page.Pagination;
import org.pshow.domain.Permission;
import org.pshow.domain.Role;
import org.pshow.domain.User;

public class RoleControllerTest extends BaseTest {
	private RoleController roleController;
	
	private Long roleId;

	@Before
	public void setUp() throws Exception {
		roleController = Mvcs.getIoc().get(RoleController.class);
		
		Pagination pagination = roleController.list(null, "testRole", null, 0);
		assertNotNull("no result return", pagination);
		assertEquals(1, pagination.getTotalCount());
		roleId = ((Role)pagination.getList().get(0)).getId();
	}

	@Test
	public void testCreate() {
		Role role = new Role();
		role.setName("testRole");
		role.setDescription("测试的Role");
		roleController.create(role);
		
		Pagination pagination = roleController.list(null, "testRole", null, 0);
		assertNotNull("no result return", pagination);
		assertEquals(1, pagination.getTotalCount());
		roleId = ((Role)pagination.getList().get(0)).getId();
	}

	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testDelete() {
		roleController.delete(roleId);
		
		Pagination pagination = roleController.list(roleId, null, null, 0);
		assertNotNull("no result return", pagination);
		assertEquals(0, pagination.getTotalCount());
	}

	@Test
	public void testList() {
		Pagination pagination = roleController.list(null, null, null, 0);
		assertNotNull("no result return", pagination);
		
		pagination = roleController.list(roleId, null, null, 0);
		assertNotNull("no result return", pagination);
		assertEquals(1, pagination.getTotalCount());
		
		pagination = roleController.list(Long.valueOf(0), null, null, 0);
		assertNotNull("no result return", pagination);
		assertEquals(0, pagination.getTotalCount());
		
		pagination = roleController.list(null, "user", null, 0);
		assertNotNull("no result return", pagination);
		assertEquals(2, pagination.getTotalCount());
	}
	
	@Test
	public void testListAll() {
		System.err.println(ToStringBuilder.reflectionToString(roleController.listAll()));
	}
	
	@Test
	public void testUpdatePermissionRelation() {
		List<Permission> addList = new ArrayList<>();
		Permission permission = new Permission();
		permission.setId(Long.valueOf(1));
		addList.add(permission);
		
		permission = new Permission();
		permission.setId(Long.valueOf(2));
		addList.add(permission);
		
		List<Permission> removeList = new ArrayList<>();
		permission = new Permission();
		permission.setId(Long.valueOf(3));
		removeList.add(permission);
		
		permission = new Permission();
		permission.setId(Long.valueOf(4));
		removeList.add(permission);
		
 		roleController.updatePermissionRelation(roleId, addList, removeList);
 		Pagination pagination = roleController.listPermissionByPage(roleId, null, null, 0);
 		assertEquals(2, pagination.getTotalCount());
	}
	
	@Test
	public void testListPermissionByPage() {
		Pagination pagination = roleController.listPermissionByPage(roleId, null, null, 0);
 		assertEquals(2, pagination.getTotalCount());
	}
	
	@Test
	public void testUpdateUserRelation() {
		List<User> addList = new ArrayList<>();
		User user = new User();
		user.setId(Long.valueOf(1));
		addList.add(user);
		
		user = new User();
		user.setId(Long.valueOf(2));
		addList.add(user);
		
		List<User> removeList = new ArrayList<>();
		user = new User();
		user.setId(Long.valueOf(3));
		removeList.add(user);
		
		user = new User();
		user.setId(Long.valueOf(4));
		removeList.add(user);
		
 		roleController.updateUserRelation(roleId, addList, removeList);
 		testListUserByPage();
	}
	
	@Test
	public void testListUserByPage() {
		Pagination pagination = roleController.listUserByPage(roleId, null, null, 0);
 		assertEquals(2, pagination.getTotalCount());
	}

}
