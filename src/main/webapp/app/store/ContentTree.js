Ext.define('ECM.store.ContentTree', {
	extend : 'Ext.data.TreeStore',
	proxy : {
		type : 'ajax',
		url : 'data/getTreeList.json',
		reader : {
			type : 'json',
			root : 'menu',
			successProperty : 'success'
		}
	},
	root : {
		text : 'Root',
		id : '00',
		expanded : true
	}
});
