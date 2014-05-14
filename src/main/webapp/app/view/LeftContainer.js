var store = Ext.create('Ext.data.TreeStore', {
	root : {
		expanded : true,
		children : [{
			text : "用户管理",
			leaf : true
		}, {
			text : "角色管理",
			leaf : true
		}, {
			text : "功能授权",
			leaf : true
		}, {
			text : "数据授权",
			leaf : true
		}]
	}
});
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
		xtype : 'treepanel',
		width : 100,
		store : store,
		lines : false,
		rootVisible : false
	}],
	initComponent : function() {
		this.callParent(arguments);
	}
});
