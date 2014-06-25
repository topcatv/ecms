Ext.define('ECM.store.UsersForPermission', {
	extend : 'Ext.data.Store',
	model : 'ECM.model.User',
	autoLoad : true,
	proxy : {
		type : 'ajax',
		api : {
			read : 'user/list'
		},
		reader : {
			type : 'json',
			root : 'list',
			successProperty : 'success'
		}
	}
});
