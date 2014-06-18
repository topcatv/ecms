Ext.define('ECM.store.Roles', {
	extend : 'Ext.data.Store',
	model : 'ECM.model.Role',
	autoLoad : true,
	proxy : {
		type : 'ajax',
		api : {
			read : 'role/list',
			update : 'role/update',
			create : 'role/create'
		},
		reader : {
			type : 'json',
			root : 'list',
			successProperty : 'success'
		},
		writer : {
			encode : true,
			type : 'json',
			root : 'role'// 服务器端直接用 Request.Form('data')接收
		}
	}
});
