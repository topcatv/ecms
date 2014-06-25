Ext.define('ECM.view.privilege.Authorize', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.authorize',
	title : '数据授权',
	layout : 'border',
	initComponent : function() {
		this.items = [{
			xtype:'permissiontree',
			region: 'west',
			width : '20%'
		},{
			xtype:'content_grid_for_permission',
			html:'privilege',
			region: 'center'
		}];
		this.callParent(arguments);
	}
});
