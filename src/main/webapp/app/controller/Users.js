Ext.define('ECM.controller.Users', {
	extend : 'Ext.app.Controller',
	stores : ['Users'],
	models : ['User'],
	views : ['user.List', 'user.Edit', 'user.Create'],

	init : function() {
		this.control({
			'userlist' : {
				itemdblclick : this.editUser
			},
			'useredit button[action=save]' : {
				click : this.updateUser
			},
			'userlist button[action=showAddUser]' : {
				click : this.showAddUser
			},
			'usercreate button[action=addUser]' : {
				click : this.addUser
			}
 		});
	},
	addUser : function(button, e, eOpts) {
		console.debug('save button click');
		var win = button.up('window'),
		form = win.down('form');
		var _this = this;
		if (form.isValid()) {
			form.submit({
				url : 'user/create',
				method : 'POST',
				type : 'ajax',
				waitMsg : '处理中...',
				success : function(f, action) {
					win.hide();
					f.reset();
					_this.getUsersStore().load();
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
	showAddUser : function(button, e, eOpts) {
		console.debug('showAddUser button click');
		Ext.widget('usercreate').show();
	},
	editUser : function(grid, record) {
		console.debug('grid double click');
		var view = Ext.widget('useredit');

		view.down('form').loadRecord(record);
	},
	updateUser : function(button) {
		var win = button.up('window'),
		form = win.down('form'),
		record = form.getRecord(),
		values = form.getValues();

		record.set(values);
		win.close();
		this.getUsersStore().sync();
	}
});
