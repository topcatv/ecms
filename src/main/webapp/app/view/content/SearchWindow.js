Ext.define('ECM.view.content.SearchWindow', {
	extend : 'Ext.Window',
	alias : 'widget.search_window',

	title : '查询',
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
				layout : {
			        type: 'table',
			        columns: 3
			    },
				bodyPadding : 5,
				items : [{
					xtype : 'textfield',
					name : 'name',
					fieldLabel : '文件或文件夹名',
					colspan: 2
				}, {
					xtype : 'checkboxfield',
					name : 'isFolder',
					fieldLabel : '是否是文件夹'
				}, {
					xtype : 'textfield',
					name : 'creator',
					fieldLabel : '创建人',
					colspan: 3
				}, {
					xtype : 'datefield',
					name : 'created_start',
					format : 'Y-m-d',
					fieldLabel : '创建时间'
				}, {
					xtype : 'label',
					text: '-',
			        margin: '0 10 0 10'
				}, {
					xtype : 'datefield',
					format : 'Y-m-d',
					name : 'created_end'
				}, {
					xtype : 'textfield',
					name : 'lastModifiedBy',
					fieldLabel : '修改人',
					colspan: 3
				}, {
					xtype : 'datefield',
					name : 'lastModified_start',
					format : 'Y-m-d',
					fieldLabel : '修改时间'
				}, {
					xtype : 'label',
					text: '-',
			        margin: '0 10 0 10'
				}, {
					xtype : 'datefield',
					format : 'Y-m-d',
					name : 'lastModified_end'
				}]
			}]
		});
		this.callParent(arguments);
	},

	buttons : [{
		text : "查询",
		type : "submit",
		action : "search",
		formBind : true
	}]
});
