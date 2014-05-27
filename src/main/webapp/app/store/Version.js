Ext.define('ECM.store.Version', {
	extend : 'Ext.data.Store',
	model : 'ECM.model.Version',

	proxy : {
		type : 'ajax',
		api : {
			read : 'content/history'
		},
		reader : {
			type : 'json',
			root : 'history',
			successProperty : 'success'
		}
	}
});