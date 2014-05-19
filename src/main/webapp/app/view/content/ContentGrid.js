Ext.define('ECM.view.content.ContentGrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.content_grid',

	title : '文件',
	store : "Content",
	selType : 'checkboxmodel',
	tbar : [ {
		text : '创建',
		xtype : 'splitbutton',
		menu : [ {
			text : '文件',
			action : 'createFile'
		}, {
			text : '文件夹',
			action : 'createFolder'
		} ]
	}, {
		text : '删除'
	}, {
		text : '检出'
	}, {
		text : '检入'
	}, {
		text : '版本恢复'
	}, {
		text : '查看版本历史'
	}, {
		text : '精确查询'
	}, '->', '全文检索', {
		xtype : 'textfield',
		name : 'fullsearch',
		emptyText : '请输入你要查询的关字'
	}, {
		xtype : 'tbspacer',
		width : 25
	} ],
	bbar : {
		xtype : 'pagingtoolbar',
		store : 'Content',
		displayInfo : true
	},

	initComponent : function() {
		this.columns = [ {
			header : 'Name',
			dataIndex : 'name',
			flex : 1
		} ];

		this.callParent(arguments);
	}
});
