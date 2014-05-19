Ext.define('ECM.view.Main', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.mainview',
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
			width : '20%'
		}, {
			region : 'center',
			xtype : 'content_grid'
		}, {
			region : 'north',
			xtype : 'panel',
			layout : 'fit',
			height : '15%',
			html : 'ECMS'
		}]
	}],

	initComponent : function() {
		this.callParent(arguments);
	},
});
