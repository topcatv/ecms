Ext.define('ECM.store.UnselectedPermission', {
	extend : 'Ext.data.Store',
	model : 'ECM.model.Permission',
	//autoLoad : true,
	proxy : {
		type : 'ajax',
		api : {
			read : 'role/getUnselectedPermissionList',
		},
		reader : {
			type : 'json'
		}
	}
});
