Ext.define('ECM.store.UnselectedRole', {
	extend : 'Ext.data.Store',
	model : 'ECM.model.Role',
	//autoLoad : true,
	proxy : {
		type : 'ajax',
		api : {
			read : 'user/getUnselectedRoleList',
		},
		reader : {
			type : 'json'
		}
	}
});
