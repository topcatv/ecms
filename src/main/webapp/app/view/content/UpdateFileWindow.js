Ext.define('ECM.view.content.UpdateFileWindow', {
	extend : 'Ext.Window',
	alias : 'widget.update_file_window',

	title : '修改文件',
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
					name : 'fileName',
					fieldLabel : '文件名',
					allowBlank: false,
					anchor : '100%'
				}, {
					xtype : 'filefield',
					name : 'file',
					fieldLabel : '文件',
					anchor : '100%'
				}]
			}]
		});
		this.callParent(arguments);
	},

	buttons : [{
		text : "修改",
		type : "submit",
		action : "file_modify",
		formBind : true
	}]
});
