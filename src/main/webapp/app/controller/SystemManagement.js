Ext.define('ECM.controller.SystemManagement', {
	extend : 'Ext.app.Controller',
	stores : [ 'SystemManagement', 'Users', 'Roles' ],
	views : [ 'SystemManagement', 'user.List', 'role.List' ],
	refs : [ {
		ref : 'systemManagement',
		selector : 'systemManagement'
	}, {
		ref : 'userList',
		selector : 'userlist'
	} , {
		ref : 'roleList',
		selector : 'rolelist'
	} ],
	init : function() {
		Ext.create('ECM.view.user.List', {});
		Ext.create('ECM.view.role.List', {});
		this.control({
			'systemManagement' : {
				itemclick : this.itemclick,
			}
		});
	},
	itemclick : function(view, record, item, index, e) {
		var id = record.get('id');
		if (id == 'userManagement') {
			var tabPanel = Ext.getCmp('mainTab');
			var userList = this.getUserList();
			if(!tabPanel.contains(userList)){
				userList.getStore().load();
				tabPanel.add(userList);
			}
			tabPanel.setActiveTab(userList);
		} else if (id == 'roleManagement') {
			var tabPanel = Ext.getCmp('mainTab');
			var userList = this.getRoleList();
			if(!tabPanel.contains(userList)){
				userList.getStore().load();
				tabPanel.add(userList);
			}
			tabPanel.setActiveTab(userList);
		}
	}
});