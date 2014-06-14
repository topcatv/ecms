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
			'win_userRoleList button[action=updateRole]' : {
				click : this.updateRole
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
				url : 'user/create',
				method : 'POST',
				type : 'ajax',
				waitMsg : '处理中...',
				success : function(f, action) {
					win.hide();
					f.reset();
					_this.getUsersStore().load();
				},
				failure : function(f, action) {
					var result = Ext.decode(action.response.responseText);
					Ext.Msg.show({
						title : '提示信息',
						msg : result.data,
						minWidth : 200,
						modal : true,
						buttons : Ext.Msg.OK
					});
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
			var roleList = _this.getRoleList();
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
	},
	updateUser : function(button) {
		var win = button.up('window'),
		form = win.down('form'),
		record = form.getRecord(),
		values = form.getValues();

		record.set(values);
		win.close();
		this.getUsersStore().sync();
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
	}, 
	updateRole : function(button) {
		console.debug('updateRole click');
		var _this = this;
		var roleList = _this.getRoleList();
		var selectionList = roleList.down('#selectedRoles');
		var store = selectionList.getStore();
		var newRecords = store.getNewRecords();
		var addRoleIds = [];
		Ext.Array.forEach(newRecords, function(modle){
			addRoleIds.push(modle.get('id'));
		});
		var removedRecords = store.getRemovedRecords(); 
		var removeRoleIds = [];
		Ext.Array.forEach(removeRoleIds, function(modle){
			removedRecords.push(modle.get('id'));
		});
		var userId = _this._getGridSelectedIds();
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
		button.up('window').close();
	}
});
