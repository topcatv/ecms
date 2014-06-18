Ext.define('ECM.store.Permissions', {
	extend : 'Ext.data.Store',
	model : 'ECM.model.Permission',
	autoLoad : true,
	proxy : {
		type : 'ajax',
		api : {
			read : 'permission/list',
			update : 'permission/update',
			create : 'permission/create'
		},
		reader : {
			type : 'json',
			root : 'list',
			successProperty : 'success'
		},
		writer : {
			encode : true,
			type : 'json',
			root : 'permission'// 服务器端直接用 Request.Form('data')接收
		}
	}
});
