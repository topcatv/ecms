Ext.define('ECM.store.ContentTreeForPermission', {
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
