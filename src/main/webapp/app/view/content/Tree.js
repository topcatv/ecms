Ext.define('ECM.view.content.Tree', {
	extend : 'Ext.tree.Panel',
	alias : 'widget.contenttree',

	title : '导航',
	store : 'ContentTree',
	rootVisible: true,

	initComponent : function() {
		this.callParent(arguments);
	}
});
