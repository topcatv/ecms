Ext.define('ECM.view.privilege.Tree', {
	extend : 'Ext.tree.Panel',
	alias : 'widget.permissiontree',

	title : '导航',
	store : 'ContentTreeForPermission',
	rootVisible: true,

	initComponent : function() {
		this.callParent(arguments);
	}
});
