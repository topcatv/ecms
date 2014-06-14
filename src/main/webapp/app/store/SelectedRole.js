Ext.define('ECM.store.SelectedRole', {
	extend : 'Ext.data.Store',
	model : 'ECM.model.Role',
	//autoLoad : true,
	proxy : {
		type : 'ajax',
		api : {
			read : 'user/getSelectedRoleList',
		},
		reader : {
			type : 'json'
		}
	}
});
