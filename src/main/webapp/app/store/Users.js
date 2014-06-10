Ext.define('ECM.store.Users', {
	extend : 'Ext.data.Store',
	model : 'ECM.model.User',
	autoLoad : true,
	proxy : {
		type : 'ajax',
		api : {
			read : 'user/list',
			update : 'user/update',
			create : 'user/create'
		},
		reader : {
			type : 'json',
			root : 'list',
			successProperty : 'success'
		},
		writer : {
			encode : true,
			type : 'json',
			root : 'user'// 服务器端直接用 Request.Form('data')接收
		}
	}
});
