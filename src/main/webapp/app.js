Ext.application({
	requires : ['Ext.container.Viewport'],
	name : 'ECM',

	appFolder : 'app',
	requires: ['ECM.view.Main'],
	controllers : ['LoginController', 'ContentTree', 'Users'],

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
