Ext.define('ECM.view.content.CreateFolderWindow', {
	extend : 'Ext.Window',
	alias : 'widget.create_folder_window',

	title : '创建文件夹',
	width : 400,
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
			items : [{
				xtype : 'form',
				plain : true,
				frame : true,
				border : 0,
				bodyPadding : 5,
				items : [{
					xtype : 'label',
					itemId : 'msg',
					text : '',
					margin : '0 0 5 0'
				}, {
					xtype : 'hiddenfield',
					name : 'parent'
				}, {
					xtype : 'textfield',
					fieldLabel : '文件夹名',
					name : 'name',
					allowBlank : false,
					anchor : '100%',
					selectOnFocus : true
				}],
			}]
		});
		this.callParent(arguments);
	},

	buttons : [{
		text : "创建",
		type : "submit",
		action : "create",
		formBind : true
	}],
	defaultFocus : 'name'
}); 