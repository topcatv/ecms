Ext.define('ECM.view.SystemManagement', {
	extend : 'Ext.tree.Panel',
	alias : 'widget.systemManagement',

	title : '系统管理',
	store : 'SystemManagement',
	rootVisible: false,
	lines : false,

	initComponent : function() {
		this.callParent(arguments);
	}
});
