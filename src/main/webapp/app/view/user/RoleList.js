Ext.define('ECM.view.user.RoleList', {
	extend : 'Ext.window.Window',
	alias : 'widget.win_userRoleList',
	title : '用户角色',
	layout : {
		type : 'hbox'
	},
	autoHeight : true,
	items : [ {
		xtype : 'gridpanel',
		itemId : 'unselectedRoles',
		store : 'UnselectedRole',
		title : '所有角色',
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
			header : '角色名',
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
			action : 'addRole'
		}, {
			xtype : 'button',
			text : '<',
			action : 'removeRole'
		} ]
	}, {
		xtype : 'gridpanel',
		itemId : 'selectedRoles',
		title : '已分配角色',
		store : 'SelectedRole',
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
			header : '角色名',
			dataIndex : 'name'
		} ]
	} ],
	buttonAlign : 'center',
	buttons : [ {
		text : '保存',
		action : 'updateRole'
	}, {
		text : '取消',
		scope : this,
		action : 'closeWin'
	} ],
	initComponent : function() {
		this.callParent(arguments);
	},
});