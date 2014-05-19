Ext.define('ECM.store.ContentTree', {
	extend : 'Ext.data.TreeStore',
	proxy : {
		type : 'ajax',
		url : 'content/tree',
		reader : {
			type : 'json',
			root : 'folders',
			successProperty : 'success'
		}
	},
	root : {
		text : 'Root',
		id : 'root'
	}
});
