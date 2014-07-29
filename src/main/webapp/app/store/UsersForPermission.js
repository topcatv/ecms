Ext.define('ECM.store.UsersForPermission', {
	extend : 'Ext.data.Store',
	model : 'ECM.model.User',
	autoLoad : true,
	proxy : {
		type : 'ajax',
		url : 'user',
//		headers : { 'Content-Type' : 'application/json; charset=UTF-8' },
		actionMethods : {
			create : 'POST',
			read : 'GET',
			update : 'PUT',
			destroy : 'DELETE'
		},
		api : {
			read : 'user/list'
		},
		reader : {
			type : 'json',
			root : 'page.content'
		}
	}
});
