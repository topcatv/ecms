Ext.define('ECM.model.Version', {
	extend : 'Ext.data.Model',
	fields : [ {
		name : 'name',
		type : 'string'
	}, {
		name : 'label',
		type : 'string'
	}, {
		name : 'created',
		type : 'date'
	} ]
});
