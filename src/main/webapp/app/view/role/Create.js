Ext.define('ECM.view.role.Create', {
	extend : 'Ext.window.Window',
	alias : 'widget.rolecreate',
	title : '添加角色',
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
				fieldLabel : '角色名',
				xtype : 'textfield'
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
			action : 'addRole'
		}, {
			text : '取消',
			scope : this,
			handler : this.close
		} ];

		this.callParent(arguments);
	}
});