Ext.define('ECM.view.user.List', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.userlist',
	title : '用户列表',
	store : 'Users',
	dockedItems : [ {
		xtype : 'toolbar',
		dock : 'top',
		items : [ {
			text : '添加用户',
			action : 'showAddUser'
		} ]
	} ],
	initComponent : function() {
		this.columns = [ {
			header : 'id',
			dataIndex : 'id'
		}, {
			header : '登录名',
			dataIndex : 'name'
		}, {
			header : '描述',
			dataIndex : 'description'
		}, {
			header : '创建日期',
			dataIndex : 'createDate',
			xtype:'datecolumn', 
			format:'Y年m月d日'
		}, {
			header : '是否锁定',
			dataIndex : 'locked'
		} ];
		this.callParent(arguments);
	}
});
