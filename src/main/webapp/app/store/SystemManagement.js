Ext.define('ECM.store.SystemManagement', {
	extend : 'Ext.data.TreeStore',
	autoLoad : true,
	proxy : {
		type : 'ajax',
		url : 'data/systemManagement.json',
		reader : {
			type : 'json',
			root : 'functions',
			successProperty : 'success'
		}
	}
});
