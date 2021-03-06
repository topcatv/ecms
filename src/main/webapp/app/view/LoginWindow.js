Ext.define('ECM.view.LoginWindow', {
	extend : 'Ext.Window',
	alias : 'widget.loginwindow',

	title : '请登陆',
	width : 400,
	autoHeight : true,
	closable : false,
	resizable : false,
	draggable : false,
	autoHeight : true,
	layout : 'fit',
	border : false,
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
					xtype : 'textfield',
					fieldLabel : '用户名',
					value: 'admin',
					name : 'name',
					allowBlank : false,
					anchor : '100%',
					selectOnFocus : true
				}, {
					xtype : 'textfield',
					fieldLabel : '密码',
					name : 'password',
					value: '123',
					allowBlank : false,
					inputType : 'password',
					anchor : '100%',
					selectOnFocus : true
				}],
			}]
		});
		this.callParent(arguments);
	},

	buttons : [{
		text : "登陆",
		type : "submit",
		action : "login",
		formBind : true,
	}],
	defaultFocus : 'userName',
});