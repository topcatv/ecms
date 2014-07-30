Ext.define('ECM.controller.RoleManagement', {
	extend : 'Ext.app.Controller',
	stores : ['Roles', 'UnselectedPermission', 'SelectedPermission'],
	models : ['Role', 'Permission'],
	views : ['role.List', 'role.Edit', 'role.Create', 'role.PermissionList'],
	refs : [ {
		ref : 'roleList',
		selector : 'rolelist'
	},  {
		ref : 'permissionList',
		selector : 'win_rolePermissionList'
	}],
	init : function() {
		Ext.create('ECM.view.role.PermissionList', {});
		this.control({
			'rolelist' : {
				itemdblclick : this.editRole
			},
			'roleedit button[action=save]' : {
				click : this.updateRole
			},
			'rolelist button[action=showAddRole]' : {
				click : this.showAddRole
			},
			'rolelist button[action=showEditRole]' : {
				click : this.showEditRole
			},
			'rolelist button[action=showDelRole]' : {
				click : this.showDelRole
			},
			/*'rolelist button[action=showUnlockUser]' : {
				click : this.showUnlockUser
			},
			'rolelist button[action=showLockUser]' : {
				click : this.showLockUser
			},*/
			'rolelist button[action=showPermissions]' : {
				click : this.showPermissions
			},
			'rolecreate button[action=addRole]' : {
				click : this.addRole
			},
			'win_rolePermissionList #btnPanel button[action=addPermission]' : {
				click : this.addPermission
			},
			'win_rolePermissionList #btnPanel button[action=removePermission]' : {
				click : this.removePermission
			},
			'win_rolePermissionList button[action=updatePermission]' : {
				click : this.updatePermission
			},
			'win_rolePermissionList button[action=closeWin]' : {
				click : this.closeWin
			}
 		});
	},
	addRole : function(button, e, eOpts) {
		console.debug('addRole button click');
		var win = button.up('window'),
		form = win.down('form');
		var _this = this;
		if (form.isValid()) {
			form.submit({
				url : 'role/create',
				method : 'POST',
				type : 'ajax',
				waitMsg : '处理中...',
				success : function(f, action) {
					win.hide();
					f.reset();
					_this.getRolesStore().load();
				},
				failure : function(f, action) {
					var result = Ext.decode(action.response.responseText);
					Ext.Msg.show({
						title : '提示信息',
						msg : result.message,
						minWidth : 200,
						modal : true,
						buttons : Ext.Msg.OK
					});
				}
			});
		}
	},
	showAddRole : function(button, e, eOpts) {
		console.debug('showAddRole button click');
		Ext.widget('rolecreate').show();
	},
	showEditRole : function(button, e, eOpts) {
		console.debug('showEditRole button click');
		var selection = this.getRoleList().getSelectionModel().getSelection();
		var count = selection.length;
		if (count == 0 || count > 1) {
			Ext.Msg.show({
				title : '提示信息',
				msg : '请选择要编辑的一行，或者双击要编辑的行',
				minWidth : 200,
				modal : true,
				buttons : Ext.Msg.OK
			});
		} else {
			this.editRole(null, selection[0]);
		}
	},
	showDelRole : function(button, e, eOpts) {
		console.debug('showDelRole button click');
		var list = this.getRoleList();
		var selection = list.getSelectionModel().getSelection();
		var count = selection.length;
		if (count == 0) {
			Ext.Msg.show({
				title : '提示信息',
				msg : '请选择要删除的角色',
				minWidth : 200,
				modal : true,
				buttons : Ext.Msg.OK
			});
		} else {
			var _this = this;
			Ext.Msg.confirm('系统提示', '是否删除选中的角色', function (button, text) {
				if (button == "yes") {
					Ext.Ajax.request({
						url: 'role/delete',
						params: {'ids': _this._getGridSelectedIds()},
						method: 'POST',
						success: function(response, opts) {
							var obj = Ext.decode(response.responseText);
							console.dir(obj);
							_this.getRoleList().getStore().load();
						}
					});
				}
			}, _this);
		}
	},
	showUnlockUser : function(button, e, eOpts) {
		console.debug('showUnlockUser button click');
		var list = this.getRoleList();
		var selection = list.getSelectionModel().getSelection();
		var count = selection.length;
		if (count == 0) {
			Ext.Msg.show({
				title : '提示信息',
				msg : '请选择要解锁的角色',
				minWidth : 200,
				modal : true,
				buttons : Ext.Msg.OK
			});
		} else {
			var _this = this;
			Ext.Msg.confirm('系统提示', '是否解锁选中的角色', function (button, text) {
				if (button == "yes") {
					Ext.Ajax.request({
						url: 'role/unlock',
						params: {'ids': _this._getGridSelectedIds()},
						method: 'POST',
						success: function(response, opts) {
							var obj = Ext.decode(response.responseText);
							console.dir(obj);
							_this.getRoleList().getStore().load();
						}
					});
				}
			}, _this);
		}
	},
	showLockUser : function(button, e, eOpts) {
		console.debug('showLockUser button click');
		var list = this.getRoleList();
		var selection = list.getSelectionModel().getSelection();
		var count = selection.length;
		if (count == 0) {
			Ext.Msg.show({
				title : '提示信息',
				msg : '请选择要锁定的角色',
				minWidth : 200,
				modal : true,
				buttons : Ext.Msg.OK
			});
		} else {
			var _this = this;
			Ext.Msg.confirm('系统提示', '是否锁定选中的角色', function (button, text) {
				if (button == "yes") {
					Ext.Ajax.request({
						url: 'role/lock',
						params: {'ids': _this._getGridSelectedIds()},
						method: 'POST',
						success: function(response, opts) {
							var obj = Ext.decode(response.responseText);
							console.dir(obj);
							_this.getRoleList().getStore().load();
						}
					});
				}
			}, _this);
		}
	},
	showPermissions : function(button, e, eOpts) {
		console.debug('showPermissions button click');
		this.addedPermissions = [];
		this.removedPermissions = [];
		var list = this.getRoleList();
		var selection = list.getSelectionModel().getSelection();
		var count = selection.length;
		if (count != 1) {
			Ext.Msg.show({
				title : '提示信息',
				msg : '请选择要角色维护的一个角色',
				minWidth : 200,
				modal : true,
				buttons : Ext.Msg.OK
			});
		} else {
			var _this = this;
			var roleList = Ext.widget('win_rolePermissionList');//_this.getRoleList();
			var unSelectionList = roleList.down('#unselectedPermissions');
			var roleId = _this._getGridSelectedIds();
			unSelectionList.getStore().load({params: {roleId : roleId}});
			var selectionList = roleList.down('#selectedPermissions');
			selectionList.getStore().load({params: {roleId : roleId}});
			_this.getPermissionList().show();
		}
	},
	editRole : function(grid, record) {
		console.debug('grid double click');
		var view = Ext.widget('roleedit');
		view.down('form').loadRecord(record);
	},
	updateRole : function(button) {
		var win = button.up('window'),
		form = win.down('form'),
		record = form.getRecord(),
		values = form.getValues();

		record.set(values);
		win.hide();
		this.getRolesStore().sync();
	},
	_getGridSelectedIds : function(){
		var seleted = this.getRoleList().getSelectionModel().getSelection();
		var ids = [];
		Ext.Array.forEach(seleted, function(modle){
			ids.push(modle.get('id'));
		});
		return ids.join(',');
	},
	addPermission : function() {
		console.debug('addPermission click');
		var roleList = this.getPermissionList();
		var unSelectionList = roleList.down('#unselectedPermissions');
		var unList = unSelectionList.getSelectionModel().getSelection();
		unSelectionList.getStore().remove(unList);
		var selectionList = roleList.down('#selectedPermissions');
		selectionList.getStore().add(unList);
		var _this = this;
		Ext.Array.forEach(unList, function(record){
			if (!Ext.Array.contains(_this.removedPermissions, record)) {
				_this.addedPermissions.push(record);
			} else {
				Ext.Array.remove(_this.removedPermissions, record);
			}
		});
		console.dir(_this.addedPermissions);
		console.dir(_this.removedPermissions);
	}, 
	removePermission : function() {
		console.debug('removePermission click');
		var roleList = this.getPermissionList();
		var unSelectionList = roleList.down('#unselectedPermissions');
		var selectionList = roleList.down('#selectedPermissions');
		var removeList = selectionList.getSelectionModel().getSelection();
		unSelectionList.getStore().add(removeList);
		selectionList.getStore().remove(removeList);
		var _this = this;
		Ext.Array.forEach(removeList, function(record){
			if (!Ext.Array.contains(_this.addedPermissions, record)) {
				_this.removedPermissions.push(record);
			} else {
				Ext.Array.remove(_this.addedPermissions, record);
			}
		});
		console.dir(_this.addedPermissions);
		console.dir(_this.removedPermissions);
	}, 
	updatePermission : function(button) {
		console.debug('updatePermission click');
		var _this = this;
		var addPermissionIds = [];
		Ext.Array.forEach(this.addedPermissions, function(modle){
			addPermissionIds.push(modle.get('id'));
		});
		var removePermissionIds = [];
		Ext.Array.forEach(this.removedPermissions, function(modle){
			removePermissionIds.push(modle.get('id'));
		});
		var roleId = _this._getGridSelectedIds();
		if (addPermissionIds.length != 0 || removePermissionIds.length != 0) {
			Ext.Ajax.request({
				url: 'role/updatePermission',
				params: {'roleId' : roleId, 'addPermissionIds': addPermissionIds.join(','), 'removePermissionIds' : removePermissionIds.join(',')},
				method: 'POST',
				success: function(response, opts) {
					var obj = Ext.decode(response.responseText);
					console.dir(obj);
					_this.getRoleList().getStore().load();
				}
			});
		}
		button.up('window').hide();
	},
	closeWin : function(button) {
		button.up('window').hide();
	}
});
