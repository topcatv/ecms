Ext.define('ECM.view.user.Create', {
	extend : 'Ext.window.Window',
	alias : 'widget.usercreate',
	title : '添加用户',
	layout : 'fit',
	autoShow : true,
	initComponent : function() {
		this.items = [ {
			xtype : 'form',
			items : [ {
				name : 'id',
				fieldLabel : 'id'
			}, {
				name : 'name',
				fieldLabel : '登录名',
				xtype : 'textfield'
			}, {
				name : 'password',
				fieldLabel : '密码',
				xtype : 'textfield',
				inputType: 'password'
			}, {
				name : 'confirmPassword',
				fieldLabel : '确认密码',
				xtype : 'textfield',
				inputType: 'password'
			}, {
				name : 'description',
				fieldLabel : '描述',
				xtype : 'textfield'
			}, {
				name : 'createDate',
				fieldLabel : '创建日期',
				xtype : 'textfield'
			}, {
				name : 'locked',
				fieldLabel : '是否锁定',
				xtype : 'textfield'
			} ]
		} ];

		this.buttons = [ {
			text : '保存',
			action : 'addUser'
		}, {
			text : '取消',
			scope : this,
			handler : this.close
		} ];

		this.callParent(arguments);
	}
});