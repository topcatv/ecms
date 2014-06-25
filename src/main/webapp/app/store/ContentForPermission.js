Ext.define('ECM.store.ContentForPermission', {
	extend : 'Ext.data.Store',
	model : 'ECM.model.Content',

	proxy : {
		type : 'ajax',
		url : 'content/children',
		reader : {
			type : 'json',
			root : 'children',
			successProperty : 'success'
		}
	}
});