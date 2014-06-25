Ext.define('ECM.view.content.ShareWindow', {
	extend : 'Ext.Window',
	alias : 'widget.share_window',

	title : '共享',
	width : 600,
	autoHeight : true,
	closable : true,
	closeAction : 'hide',
	resizable : false,
	draggable : true,
	layout : 'fit',
	border : true,
	modal : true,

	initComponent : function() {
		Ext.apply(this, {
			items : [{
	            xtype: 'gridpanel',
	            store:'UsersForPermission',
	            selType: 'checkboxmodel',
	            columns: [{
	    			header : '登录名',
	    			dataIndex : 'name'
	    		}, {
	    			header : '描述',
	    			dataIndex : 'description'
	    		}, {
	    			header : '创建日期',
	    			dataIndex : 'createDate',
	    			xtype:'datecolumn', 
	    			format:'Y年m月d日'
	    		}]
		    }]
		});
		this.callParent(arguments);
	},
	
	buttons : [{
		text : "共享",
		type : "button",
		action : "share"
	}]
});
