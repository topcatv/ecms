Ext.define('ECM.view.content.UpdateFolderWindow', {
	extend : 'Ext.Window',
	alias : 'widget.update_folder_window',

	title : '修改文件夹名',
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
					xtype : 'hiddenfield',
					name : 'id'
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
		text : "修改",
		type : "submit",
		action : "folder_rename",
		formBind : true
	}],
	defaultFocus : 'name'
}); 