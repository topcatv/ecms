Ext.define('ECM.controller.LoginController', {
	extend : 'Ext.app.Controller',
	views : [ 'Main', 'LoginWindow' ],

	init : function() {
		this.control({
			'loginwindow' : {
				render : this.onPanelRendered
			},
			'loginwindow button[action=login]' : {
				click : this.login
			},
			'loginwindow textfield' : {
				specialkey : this.keyenter
			},
			'mainview button[action=logout]' : {
				click : this.logout
			}
		});
	},
	refs : [ {
		ref : 'mainview',
		selector : 'mainview'
	}, {
		ref : 'loginwindow',
		selector : 'loginwindow'
	} ],

	onPanelRendered : function(panel) {
		console.log('The panel was rendered');
	},

	login : function(button) {
		var win = button.up('window'), form = win.down('form'), values = form
				.getValues();
		var _this = this;

		if (form.isValid()) {
			form.submit({
				url : 'auth/login',
				method : 'POST',
				// url : 'data/users.json',
				// method : 'GET',
				type : 'ajax',
				waitMsg : '正在登陆中...',
				success : function(f, action) {
					var lay = _this.getMainview().getLayout();
					lay.setActiveItem(1);
					win.hide();
					f.reset();
				},
				failure : function(f, action) {
					var result = Ext.decode(action.response.responseText);
					Ext.Msg.show({
						title : '提示信息',
						msg : result.data,
						minWidth : 200,
						modal : true,
						buttons : Ext.Msg.OK
					})
				}
			});
		}
	},
	keyenter : function(field, e) {
		if (e.getKey() == e.ENTER) {
			var _this = this;
			var form = field.up('form').getForm(), win = field.up('window');
			if (form.isValid()) {
				form.submit({
					// url : 'auth/login',
					// method : 'POST',
					url : 'data/users.json',
					method : 'GET',
					type : 'ajax',
					waitMsg : '正在登陆中...',
					success : function(f, action) {
						var lay = _this.getMainview().getLayout();
						lay.setActiveItem(1);
						win.hide();
					},
					failure : function(f, action) {
						var result = Ext.decode(action.response.responseText);
						Ext.Msg.show({
							title : '提示信息',
							msg : result.data,
							minWidth : 200,
							modal : true,
							buttons : Ext.Msg.OK
						})
					}
				});
			}
		}
	},
	logout : function(button) {
		console.log('logout');
		var lay = this.getMainview().getLayout();
		lay.setActiveItem(0);
		var win = this.getLoginwindow();
		win.show();
	}
});
