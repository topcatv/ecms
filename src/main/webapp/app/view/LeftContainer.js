Ext.define('ECM.view.LeftContainer', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.leftcontainer',
	collapsible : true, // make collapsible
	layout : {
		type : 'accordion',
		animate : true
	},
	items : [{
		title : '导航',
		xtype : 'contenttree'
	}, {
		title : '系统管理',
		xtype : 'systemManagement'
	}],
	initComponent : function() {
		this.callParent(arguments);
	}
});
