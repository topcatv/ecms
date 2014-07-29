Ext.define('ECM.view.privilege.AuthorizeWindow', {
	extend : 'Ext.Window',
	alias : 'widget.authorize_window',

	title : '授权',
	width : 600,
	autoHeight : true,
	closable : true,
	closeAction : 'hide',
	resizable : false,
	draggable : true,
	layout : 'column',
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
	    			format:'Y-m-d H:i'
	    		}],
		        columnWidth: 0.7
		    }, {
	            xtype: 'fieldset',
	            title: '权限',
	            hideLabels: true,
	            layout: 'hbox',
	            margin: '0 5px 0 5px',
	            defaults: {
	            	flex: 1
	            },
	            defaultType: 'checkbox',
	            items: [{
                    boxLabel  : '所有',
                    name      : 'right',
                    inputValue: '0',
                    id        : 'cb_all'
                }, {
                    boxLabel  : '写',
                    name      : 'right',
                    inputValue: '1',
                    id        : 'cb_write'
                }, {
                    boxLabel  : '读',
                    name      : 'right',
                    inputValue: '2',
                    id        : 'cb_read'
                }],
		        columnWidth: 0.3
		    }]
		});
		this.callParent(arguments);
	},
	
	buttons : [{
		text : "授权",
		type : "button",
		action : "authorize"
	}]
});
