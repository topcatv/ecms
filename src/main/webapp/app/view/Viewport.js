Ext.define('ECM.view.Viewport', {
	extend : 'Ext.container.Viewport',

	requires : ['ECM.view.Main'],
	layout: 'fit',

    items: [
        {
          xtype: 'mainview',
        },
    ],
});
