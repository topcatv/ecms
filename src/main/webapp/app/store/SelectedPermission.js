Ext.define('ECM.store.SelectedPermission', {
	extend : 'Ext.data.Store',
	model : 'ECM.model.Permission',
	//autoLoad : true,
	proxy : {
		type : 'ajax',
		api : {
			read : 'role/getSelectedPermissionList',
		},
		reader : {
			type : 'json'
		}
	}
});
