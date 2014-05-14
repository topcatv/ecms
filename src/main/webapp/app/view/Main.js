Ext.define('ECM.view.Main', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.mainview',
	requires : ['ECM.view.LeftContainer', 'Ext.layout.container.Border'],

	activeItem : 0,
	defaults : {
		border : false
	},
	layout : 'card',
	items : [{
		id : 'card0',
		html : ''
	}, {
		id : 'card1',
		xtype : 'panel',
		layout : 'border',
		items : [{
			xtype : 'leftcontainer',
			region : 'west',
			width : '20%',
			margins : '5 0 5 0'
		}, {
			title : 'Center Panel',
			region : 'center',
			xtype : 'panel',
			layout : 'fit',
			margins : '5 5 0 0',

		}, {
			region : 'north',
			xtype : 'panel',
			layout : 'fit',
			margins : '5 5 0 0',
			height : '15%',
			html : 'ECMS'
		}]
	}],

	initComponent : function() {
		this.callParent(arguments);
	},
});
