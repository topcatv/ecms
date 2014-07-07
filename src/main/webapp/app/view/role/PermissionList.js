Ext.define('ECM.view.role.PermissionList', {
	extend : 'Ext.window.Window',
	alias : 'widget.win_rolePermissionList',
	title : '用户权限',
	layout : {
		type : 'hbox'
	},
	autoHeight : true,
	items : [ {
		xtype : 'gridpanel',
		itemId : 'unselectedPermissions',
		store : 'UnselectedPermission',
		title : '所有权限',
		width : 300,
		height : 500,
		selModel : {
			selType : 'checkboxmodel',
			mode : 'SIMPLE'
		},
		columns : [ {
			header : 'id',
			dataIndex : 'id',
			hidden : true
		}, {
			header : '权限名',
			dataIndex : 'name'

		}, {
			header : '描述',
			dataIndex : 'description'

		} ]
	}, {
		xtype : 'panel',
		height : 500,
		itemId : 'btnPanel',
		layout : {
			type : 'vbox',
			align : 'middle',
			pack: 'center'
		},
		items : [ {
			xtype : 'button',
			text : '>',
			action : 'addPermission'
		}, {
			xtype : 'button',
			text : '<',
			action : 'removePermission'
		} ]
	}, {
		xtype : 'gridpanel',
		itemId : 'selectedPermissions',
		title : '已分配权限',
		store : 'SelectedPermission',
		width : 300,
		height : 500,
		selModel : {
			selType : 'checkboxmodel',
			mode : 'SIMPLE'
		},
		columns : [ {
			header : 'id',
			dataIndex : 'id'
		}, {
			header : '权限名',
			dataIndex : 'name'
		} ]
	} ],
	buttonAlign : 'center',
	buttons : [ {
		text : '保存',
		action : 'updatePermission'
	}, {
		text : '取消',
		scope : this,
		action : 'closeWin'
	} ],
	initComponent : function() {
		this.callParent(arguments);
	},
});