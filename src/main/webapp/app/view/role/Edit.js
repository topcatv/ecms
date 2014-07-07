Ext.define('ECM.view.role.Edit', {
	extend : 'Ext.window.Window',
	alias : 'widget.roleedit',
	title : '编辑用户',
	layout : 'fit',
	autoShow : true,
	initComponent : function() {
		this.items = [ {
			xtype : 'form',
			items : [ {
				name : 'id',
				fieldLabel : 'id'
			}, {
				name : 'Name',
				fieldLabel : '角色名'
			}, {
				name : 'description',
				fieldLabel : '描述',
				xtype : 'textfield'
			}, {
				name : 'createDate',
				fieldLabel : '创建日期',
				xtype : 'textfield'
			}/*, {
				name : 'locked',
				fieldLabel : '是否锁定',
				xtype : 'textfield'
			}*/ ]
		} ];

		this.buttons = [ {
			text : '保存',
			action : 'save'
		}, {
			text : '取消',
			scope : this,
			handler : this.close
		} ];

		this.callParent(arguments);
	}
});