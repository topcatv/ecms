/* system_user */
INSERT INTO system_user(ID,NAME,PASSWORD,SALT,SEX,DESCRIPTION,IS_LOCKED,CREATE_DATE,REGISTER_IP,OPENID,PROVIDERID) VALUES 
(1,'admin',NULL,NULL,0,'超级管理员',false,'2014-02-01 00:12:40','127.0.0.1','admin','local');
/*  .system_role   		*/
INSERT INTO system_role(ID,NAME,DESCRIPTION) VALUES
(1,'admin','超级管理员：拥有全部权限的角色'),
(2,'viewer','审阅者：拥有任何对象的浏览权限的角色'),
(3,'user-superadmin','用户超级管理员：拥有对用户的任意操作权限的角色'),
(4,'user-admin','用户管理员：拥有对用户的浏览、增加和编辑(不包括删除)权限的角色'),
(5,'security-admin','安全管理员：拥有对角色和权限的任意操作，对用户分配角色及对角色分配权限的权限');
/*  .system_permission   		*/
INSERT INTO system_permission VALUES (1,'*:*:*','全部权限','1',true),(2,'admin:role','对用户分配角色 ','721fbbca1ae44716918fdb3921deacb1',false),(3,'admin:setting','系统设置 ','800a35a4c142413c9d902f4d40245ec3',false),(4,'admin:article','文章管理 ','0b24b967550543469941c7b29e27f277',false),(5,'admin:articleCategory','文章分类 ','0b24b967550543469941c7b29e27f277',false),(6,'admin:admin','添加管理员','721fbbca1ae44716918fdb3921deacb1',false),(7,'admin:permission','权限管理','721fbbca1ae44716918fdb3921deacb1',false),(8,'admin:permissionCategory','权限分类','721fbbca1ae44716918fdb3921deacb1',false);
/*  .permission_category   		*/
INSERT INTO permission_category VALUES ('0b24b967550543469941c7b29e27f277','文章管理',true),('1','超级权限',true),('721fbbca1ae44716918fdb3921deacb1','用户管理',true),('800a35a4c142413c9d902f4d40245ec3','系统设置',true);
/*  .system_user_role   		*/
INSERT INTO system_user_role(USERID,ROLEID) VALUES 
(1,1);
/*  .system_role_permission  		*/
INSERT INTO system_role_permission(ROLEID,PERMISSIONID) VALUES 
(1,1),
(1,2),
(1,3),
(1,4),
(1,5),
(1,6);