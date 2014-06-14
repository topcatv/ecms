Ext.define('ECM.view.user.List', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.userlist',
	title : '用户列表',
	store : 'Users',
	selModel : {
	    selType : 'checkboxmodel',
	    mode : 'SIMPLE'
	},
	dockedItems : [ {
		xtype : 'toolbar',
		dock : 'top',
		items : [ {
			text : '添加用户',
			action : 'showAddUser'
		}, {
			text : '编辑用户',
			action : 'showEditUser'
		}, {
			text : '删除用户',
			action : 'showDelUser'
		}, {
			text : '解锁',
			action : 'showUnlockUser'
		}, {
			text : '锁定',
			action : 'showLockUser'
		}, {
			text : '角色',
			action : 'showRoles'
		}]
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
