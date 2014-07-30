Ext.define('ECM.controller.Users', {
	extend : 'Ext.app.Controller',
	stores : ['Users', 'UnselectedRole', 'SelectedRole'],
	models : ['User', 'Role'],
	views : ['user.List', 'user.Edit', 'user.Create', 'user.RoleList'],
	refs : [ {
		ref : 'userList',
		selector : 'userlist'
	},  {
		ref : 'roleList',
		selector : 'win_userRoleList'
	}],
	init : function() {
		Ext.create('ECM.view.user.RoleList', {});
		this.control({
			'userlist' : {
				itemdblclick : this.editUser
			},
			'useredit button[action=save]' : {
				click : this.updateUser
			},
			'userlist button[action=showAddUser]' : {
				click : this.showAddUser
			},
			'userlist button[action=showEditUser]' : {
				click : this.showEditUser
			},
			'userlist button[action=showDelUser]' : {
				click : this.showDelUser
			},
			'userlist button[action=showUnlockUser]' : {
				click : this.showUnlockUser
			},
			'userlist button[action=showLockUser]' : {
				click : this.showLockUser
			},
			'userlist button[action=showRoles]' : {
				click : this.showRoles
			},
			'usercreate button[action=addUser]' : {
				click : this.addUser
			},
			'win_userRoleList #btnPanel button[action=addRole]' : {
				click : this.addRole
			},
			'win_userRoleList #btnPanel button[action=removeRole]' : {
				click : this.removeRole
			},
			'win_userRoleList button[action=updateRole]' : {
				click : this.updateRole
			},
			'win_userRoleList button[action=closeWin]' : {
				click : this.closeWin
			}
 		});
	},
	addUser : function(button, e, eOpts) {
		console.debug('save button click');
		var win = button.up('window'),
		form = win.down('form');
		var _this = this;
		if (form.isValid()) {
			form.submit({
				url : 'user',
				method : 'POST',
//				type : 'ajax',
				waitMsg : '处理中...',
				success : function(f, action) {
					win.hide();
					f.reset();
					_this.getUsersStore().load();
				},
				failure : function(f, action) {
					var result = Ext.decode(action.response.responseText);
					if(result.user){
						win.hide();
						f.reset();
						_this.getUsersStore().load();
					}else{
						Ext.Msg.show({
							title : '提示信息',
							msg : result.message,
							minWidth : 200,
							modal : true,
							buttons : Ext.Msg.OK
						});
					}
				}
			});
		}
	},
	showAddUser : function(button, e, eOpts) {
		console.debug('showAddUser button click');
		Ext.widget('usercreate').show();
	},
	showEditUser : function(button, e, eOpts) {
		console.debug('showEditUser button click');
		var selection = this.getUserList().getSelectionModel().getSelection();
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
			this.editUser(null, selection[0]);
		}
	},
	showDelUser : function(button, e, eOpts) {
		console.debug('showDelUser button click');
		var userList = this.getUserList();
		var selection = userList.getSelectionModel().getSelection();
		var count = selection.length;
		if (count == 0) {
			Ext.Msg.show({
				title : '提示信息',
				msg : '请选择要删除的用户',
				minWidth : 200,
				modal : true,
				buttons : Ext.Msg.OK
			});
		} else {
			var _this = this;
			Ext.Msg.confirm('系统提示', '是否删除选中的用户', function (button, text) {
				if (button == "yes") {
					Ext.Ajax.request({
						url: 'user/delete',
						params: {'ids': _this._getGridSelectedIds()},
						method: 'POST',
						success: function(response, opts) {
							var obj = Ext.decode(response.responseText);
							console.dir(obj);
							_this.getUserList().getStore().load();
						}
					});
				}
			}, _this);
		}
	},
	showUnlockUser : function(button, e, eOpts) {
		console.debug('showUnlockUser button click');
		var userList = this.getUserList();
		var selection = userList.getSelectionModel().getSelection();
		var count = selection.length;
		if (count == 0) {
			Ext.Msg.show({
				title : '提示信息',
				msg : '请选择要解锁的用户',
				minWidth : 200,
				modal : true,
				buttons : Ext.Msg.OK
			});
		} else {
			var _this = this;
			Ext.Msg.confirm('系统提示', '是否解锁选中的用户', function (button, text) {
				if (button == "yes") {
					Ext.Ajax.request({
						url: 'user/unlock',
						params: {'ids': _this._getGridSelectedIds()},
						method: 'POST',
						success: function(response, opts) {
							var obj = Ext.decode(response.responseText);
							console.dir(obj);
							_this.getUserList().getStore().load();
						}
					});
				}
			}, _this);
		}
	},
	showLockUser : function(button, e, eOpts) {
		console.debug('showLockUser button click');
		var userList = this.getUserList();
		var selection = userList.getSelectionModel().getSelection();
		var count = selection.length;
		if (count == 0) {
			Ext.Msg.show({
				title : '提示信息',
				msg : '请选择要锁定的用户',
				minWidth : 200,
				modal : true,
				buttons : Ext.Msg.OK
			});
		} else {
			var _this = this;
			Ext.Msg.confirm('系统提示', '是否锁定选中的用户', function (button, text) {
				if (button == "yes") {
					Ext.Ajax.request({
						url: 'user/lock',
						params: {'ids': _this._getGridSelectedIds()},
						method: 'POST',
						success: function(response, opts) {
							var obj = Ext.decode(response.responseText);
							console.dir(obj);
							_this.getUserList().getStore().load();
						}
					});
				}
			}, _this);
		}
	},
	showRoles : function(button, e, eOpts) {
		console.debug('showRoles button click');
		this.addedRoles = [];
		this.removedRoles = [];
		var userList = this.getUserList();
		var selection = userList.getSelectionModel().getSelection();
		var count = selection.length;
		if (count != 1) {
			Ext.Msg.show({
				title : '提示信息',
				msg : '请选择要角色维护的一个用户',
				minWidth : 200,
				modal : true,
				buttons : Ext.Msg.OK
			});
		} else {
			var _this = this;
			var roleList = Ext.widget('win_userRoleList');//_this.getRoleList();
			var unSelectionList = roleList.down('#unselectedRoles');
			var userId = _this._getGridSelectedIds();
			unSelectionList.getStore().load({params: {userId : userId}});
			var selectionList = roleList.down('#selectedRoles');
			selectionList.getStore().load({params: {userId : userId}});
			_this.getRoleList().show();
		}
	},
	editUser : function(grid, record) {
		console.debug('grid double click');
		var view = Ext.widget('useredit');
		view.down('form').loadRecord(record);
		view.show();
	},
	updateUser : function(button) {
		var win = button.up('window'), form = win.down('form');
		var _this = this;
		form.submit({
			url : 'user',
			method : 'PUT',
			type : 'ajax',
			waitMsg : '正在更新...',
			success : function(f, action) {
				win.hide();
				_this.getUsersStore().reload();
			},
			failure : function(f, action) {
				var result = Ext.decode(action.response.responseText);
				if(result.user){
					win.hide();
					_this.getUsersStore().load();
				}else{
					Ext.Msg.alert("提示信息",result.message);
				}
			}
		});
	},
	_getGridSelectedIds : function(){
		var seleted = this.getUserList().getSelectionModel().getSelection();
		var ids = [];
		Ext.Array.forEach(seleted, function(modle){
			ids.push(modle.get('id'));
		});
		return ids.join(',');
	},
	addRole : function() {
		console.debug('addRole click');
		var roleList = this.getRoleList();
		var unSelectionList = roleList.down('#unselectedRoles');
		var unList = unSelectionList.getSelectionModel().getSelection();
		unSelectionList.getStore().remove(unList);
		var selectionList = roleList.down('#selectedRoles');
		selectionList.getStore().add(unList);
		var _this = this;
		Ext.Array.forEach(unList, function(record){
			if (!Ext.Array.contains(_this.removedRoles, record)) {
				_this.addedRoles.push(record);
			} else {
				Ext.Array.remove(_this.removedRoles, record);
			}
		});
		console.dir(_this.addedRoles);
		console.dir(_this.removedRoles);
	}, 
	removeRole : function() {
		console.debug('removeRole click');
		var roleList = this.getRoleList();
		var unSelectionList = roleList.down('#unselectedRoles');
		var selectionList = roleList.down('#selectedRoles');
		var removeList = selectionList.getSelectionModel().getSelection();
		unSelectionList.getStore().add(removeList);
		selectionList.getStore().remove(removeList);
		var _this = this;
		Ext.Array.forEach(removeList, function(record){
			if (!Ext.Array.contains(_this.addedRoles, record)) {
				_this.removedRoles.push(record);
			} else {
				Ext.Array.remove(_this.addedRoles, record);
			}
		});
		console.dir(_this.addedRoles);
		console.dir(_this.removedRoles);
	}, 
	updateRole : function(button) {
		console.debug('updateRole click');
		var _this = this;
		var addRoleIds = [];
		Ext.Array.forEach(this.addedRoles, function(modle){
			addRoleIds.push(modle.get('id'));
		});
		var removeRoleIds = [];
		Ext.Array.forEach(this.removedRoles, function(modle){
			removeRoleIds.push(modle.get('id'));
		});
		var userId = _this._getGridSelectedIds();
		if (addRoleIds.length != 0 || removeRoleIds.length != 0) {
			Ext.Ajax.request({
				url: 'user/updateRole',
				params: {'userId' : userId, 'addRoleIds': addRoleIds.join(','), 'removeRoleIds' : removeRoleIds.join(',')},
				method: 'POST',
				success: function(response, opts) {
					var obj = Ext.decode(response.responseText);
					console.dir(obj);
					_this.getUserList().getStore().load();
				}
			});
		}
		button.up('window').hide();
	},
	closeWin : function(button) {
		button.up('window').hide();
	}
});
