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
					xtype: 'label',
					itemId : 'msg',
					text: '',
					margin: '0 0 5 0'
				},{
					xtype : 'textfield',
					fieldLabel : '用户名',
					name : 'name',
					allowBlank : false,
					anchor : '100%',
					selectOnFocus : true
				}, {
					xtype : 'textfield',
					fieldLabel : '密码',
					name : 'password',
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
		text : "Login",
		type : "submit",
		action : "login",
		formBind : true,
	}],
	defaultFocus : 'userName',
});