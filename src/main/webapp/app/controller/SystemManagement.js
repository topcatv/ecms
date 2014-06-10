Ext.define('ECM.controller.SystemManagement', {
	extend : 'Ext.app.Controller',
	stores : [ 'SystemManagement' ],
	views : [ 'SystemManagement' ],
	refs : [ {
		ref : 'systemManagement',
		selector : 'systemManagement'
	} ],
	init : function() {
		this.control({
			'systemManagement' : {
				itemclick : this.itemclick,
			}
		});
	},
	itemclick : function(view, record, item, index, e) {
		console.debug('systemManagement itemclick');
		var id = record.get('id');
		if (id == 'userManagement') {
			var tabPanel = Ext.getCmp('ecmTabPanel');
			var userTab = tabPanel.getComponent('userTab');
			if (!userTab) {
				userTab = Ext.create('ECM.view.user.List', {});
				userTab.getStore().load();
				tabPanel.add(userTab);
			}
			tabPanel.setActiveTab(userTab);
		}
	}
});