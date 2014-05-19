Ext.define('ECM.view.content.CreateFileWindow', {
	extend : 'Ext.Window',
	alias : 'widget.create_file_window',

	title : '创建文件',
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
					name : 'fileName',
					fieldLabel : '文件名',
					anchor : '100%'
				}, {
					xtype : 'filefield',
					name : 'file',
					allowBlank: false,
					fieldLabel : '文件',
					anchor : '100%'
				}]
			}]
		});
		this.callParent(arguments);
	},

	buttons : [{
		text : "上传",
		type : "submit",
		action : "upload",
		formBind : true
	}]
});
