Ext.define('ECM.view.user.Edit', {
	extend : 'Ext.window.Window',
	alias : 'widget.useredit',
	title : '编辑用户',
	autoHeight : true,
	closable : true,
	closeAction : 'hide',
	resizable : false,
	draggable : true,
	autoHeight : true,
	layout : 'fit',
	border : true,
	modal : true,

	initComponent : function() {
		Ext.apply(this, {
			items : [ {
				xtype : 'form',
				plain : true,
				frame : true,
				border : 0,
				bodyPadding : 5,
				items : [ {
					name : 'id',
					xtype : 'hiddenfield'
				}, {
					name : 'description',
					fieldLabel : '描述',
					xtype : 'textfield'
				}]
			} ]
		});

		this.buttons = [{
			text : '保存',
			action : 'save'
		}, {
			text : '取消',
			scope : this,
			handler : this.close
		}];

		this.callParent(arguments);
	}
});