package org.pshow.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Before;
import org.junit.Test;
import org.pshow.domain.Permission;
import org.pshow.domain.Role;
import org.pshow.domain.User;
import org.springframework.data.domain.Page;

public class RoleControllerTest extends BaseTest {
	private RoleController roleController;
	
	private Long roleId;

	@Before
	public void setUp() throws Exception {
		roleController = getBean(RoleController.class);
		
		Page<Role> page = roleController.list(1, 1);
		assertNotNull("no result return", page);
		assertEquals(1, page.getTotalElements());
		roleId = page.getContent().get(0).getId();
	}

	@Test
	public void testCreate() {
		Role role = new Role();
		role.setName("testRole");
		role.setDescription("测试的Role");
		roleController.create(role);
		
		Page<Role> pagination = roleController.list(1, 1);
		assertNotNull("no result return", pagination);
		assertEquals(1, pagination.getTotalElements());
		roleId = pagination.getContent().get(0).getId();
	}

	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testDelete() {
//		roleController.delete(roleId);
		
		Page<Role> pagination = roleController.list(1,1);
		assertNotNull("no result return", pagination);
		assertEquals(0, pagination.getTotalElements());
	}

	@Test
	public void testList() {
		Page<Role> pagination = roleController.list(1,1);
		assertNotNull("no result return", pagination);
		
		pagination = roleController.list(1,1);
		assertNotNull("no result return", pagination);
		assertEquals(1, pagination.getTotalElements());
		
		pagination = roleController.list(1,1);
		assertNotNull("no result return", pagination);
		assertEquals(0, pagination.getTotalElements());
		
		pagination = roleController.list(1,1);
		assertNotNull("no result return", pagination);
		assertEquals(2, pagination.getTotalElements());
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
		
// 		roleController.updatePermissionRelation(roleId, addList, removeList);
 		Page<Permission> pagination = roleController.listPermissionByPage(roleId, null, null, 0);
 		assertEquals(2, pagination.getTotalElements());
	}
	
	@Test
	public void testListPermissionByPage() {
		Page<Permission> pagination = roleController.listPermissionByPage(roleId, null, null, 0);
 		assertEquals(2, pagination.getTotalElements());
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
		Page<User> pagination = roleController.listUserByPage(roleId, null, null, 0);
 		assertEquals(2, pagination.getTotalElements());
	}

}
