Ext.application({
	requires : ['Ext.container.Viewport'],
	name : 'ECM',

	appFolder : 'app',
	controllers : ['LoginController', 'SystemManagement', 'Content', 'TabPanel', 'Users'],

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
