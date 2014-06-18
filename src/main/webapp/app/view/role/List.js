Ext.define('ECM.view.role.List', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.rolelist',
	title : '角色列表',
	store : 'Roles',
	selModel : {
	    selType : 'checkboxmodel',
	    mode : 'SIMPLE'
	},
	dockedItems : [ {
		xtype : 'toolbar',
		dock : 'top',
		items : [ {
			text : '添加角色',
			action : 'showAddRole'
		}, {
			text : '编辑角色',
			action : 'showEditRole'
		}, {
			text : '删除角色',
			action : 'showDelRole'
		},/* {
			text : '解锁',
			action : 'showUnlockRole'
		}, {
			text : '锁定',
			action : 'showLockUser'
		}, */{
			text : '权限',
			action : 'showPermissions'
		}]
	} ],
	initComponent : function() {
		this.columns = [ {
			header : 'id',
			dataIndex : 'id'
		}, {
			header : '角色名',
			dataIndex : 'name'
		}, {
			header : '描述',
			dataIndex : 'description'
		}, {
			header : '创建日期',
			dataIndex : 'createDate',
			xtype:'datecolumn', 
			format:'Y年m月d日'
		}/*, {
			header : '是否锁定',
			dataIndex : 'locked'
		}*/ ];
		this.callParent(arguments);
	}
});
