Ext.define('ECM.view.content.HistoryWindow', {
	extend : 'Ext.Window',
	alias : 'widget.history_window',

	title : '文档历史版本',
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
				xtype : 'grid',
				store : "Version",
				emptyText: '无数据',
		        reserveScrollOffset: true,
		        hideHeaders: false,        //是否隐藏标题
		        tbar : [{
		    		text : '查看',
		    		iconCls : 'fa fa-eye',
		    		action : 'delete'
		    	}, {
		    		text : '恢复',
		    		iconCls : 'fa fa-undo',
		    		action : 'show_history'
		    	}],
		        columns: [{
		            header: "版本号",
		            dataIndex: 'name'
		        }, {
		            header: "版本标签",
		            dataIndex: 'label'
		        }, {
		            header: "创建时间",
		            dataIndex: 'created',
		            align: 'right',
		            xtype: 'datecolumn',
		            flex: 1,
		            format: 'Y-m-d H:i'
		        }]
			}]
		});
		this.callParent(arguments);
	}
});
