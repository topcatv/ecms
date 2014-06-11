Ext.define('ECM.controller.SystemManagement', {
	extend : 'Ext.app.Controller',
	stores : [ 'SystemManagement', 'Users' ],
	views : [ 'SystemManagement', 'user.List' ],
	refs : [ {
		ref : 'systemManagement',
		selector : 'systemManagement'
	}, {
		ref : 'userList',
		selector : 'userlist'
	} ],
	init : function() {
		Ext.create('ECM.view.user.List', {});
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
		}
	}
});