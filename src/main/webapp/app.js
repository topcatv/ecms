Ext.application({
	requires : ['Ext.container.Viewport'],
	name : 'ECM',

	appFolder : 'app',
	controllers : ['LoginController', 'SystemManagement', 'Content', 'Users', 'RoleManagement'],

	launch : function() {
		Ext.create('ECM.view.LoginWindow', {}).show();
		Ext.create('Ext.container.Viewport', {
			layout : 'fit',
			items : [{
				xtype : 'mainview',
			}]
		});
	}
});
